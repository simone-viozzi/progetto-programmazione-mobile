import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_app/DataWidgets/main_fragment_data.dart';
import 'package:flutter_app/Database/dataModels/aggregate.dart';
import 'package:flutter_app/Database/dataModels/element.dart' as DbElement;
import 'package:flutter_app/Widgets/bottom_app_bar.dart';
import 'package:flutter_app/Widgets/floating_action_button.dart';

import '../data_models.dart';
import '../definitions.dart';
import '../utils.dart';

class EditFragment extends StatelessWidget {
  EditFragment({Key? key}) : super(key: key);

  final GlobalKey<EditMainListState> mainListKey = GlobalKey();

  // dialog to be sure the user want to go back to the home
  Future<bool> sureToExitEdit(BuildContext context) {
    return sureToExit(context, "", () {
      MainFragDataWidget.of(context).changePage(PageMap.homeId);
      Navigator.of(context).pop(false);
    }, () => Navigator.of(context).pop(false));
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
        onWillPop: () {
          return sureToExitEdit(context);
        },
        child: Scaffold(
          extendBody: true,
          appBar: AppBar(
            title: const Text("aggregate"),
            leading: BackButton(
              color: Colors.white,
              onPressed: () {
                sureToExitEdit(context);
              },
            ),
          ),
          // i use the key to the sole purpose of accessing it's variable
          body: EditMainList(key: mainListKey),
          floatingActionButton: AdaptiveFab(
            icon: Icons.check,
            position: FloatingActionButtonLocation.endDocked,
            onPressed: () {
              // the data is in a child widget so i use the key to get it.
              List? list = mainListKey.currentState?.elements;
              if (list == null) {
                ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
                  content: Text("there was an error in the data"),
                ));
                return;
              }
              // the data need to be converted into the db format
              var aggregate = list[0] as AggregateDataModel;
              var elements = list
                  .getRange(1, list.length - 1)
                  .map((e) => e as ElementDataModel);

              // need to calculate the total cost
              double totalCost = 0;

              // map is lazy, the toList need to be called here otherwise the
              //  total cost would be wrong.
              var dbElements = elements.map((e) {
                totalCost = totalCost + (e.cost * e.num);

                return DbElement.Element(
                    num: e.num, cost: e.cost, name: e.name);
              }).toList();

              print("totalCost -> $totalCost");

              var dbAggregate = Aggregate(
                  date: aggregate.date.millisecondsSinceEpoch,
                  tag: aggregate.tag,
                  total_cost: totalCost);

              // i can get the repository instance from the inheritedWidget
              MainFragDataScope.of(context)
                  .dbRepository
                  .insertAggregate(dbAggregate, dbElements);
              MainFragDataWidget.of(context).changePage(PageMap.homeId);
            },
          ),
          floatingActionButtonLocation: AdaptiveFab.location(context),
          bottomNavigationBar: const MyBottomAppBar(
              displayHamburger: false, displayOptionMenu: false),
        ));
  }
}

class EditMainList extends StatefulWidget {
  const EditMainList({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => EditMainListState();
}

class EditMainListState extends State<EditMainList> {
  // the list will always start with a blank aggregate and an element
  List elements = [
    AggregateDataModel(index: 0, date: DateTime.now(), tag: ""),
    ElementDataModel(index: 1, name: "", cost: 0, num: 0)
  ];

  // callback called every time the user writes something
  void updateList(EditDataModel value) {
    var index = (value as HasIndex).index;
    setState(() {
      elements[index] = value;
      // if the user wrote on the last element, add an element
      if (index == elements.length - 1) {
        elements.add(ElementDataModel(
            index: elements.length, name: "", cost: 0, num: 0));
      }
    });
  }

  // show the date picker, if the user selected something save it.
  Future<void> _selectDate(BuildContext context) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: DateTime.now(),
      firstDate: DateTime(1970),
      lastDate: DateTime.now(),
    );
    if (picked != null) {
      setState(() {
        var aggregate = elements[0] as AggregateDataModel;
        aggregate.date = picked;
        elements[0] = aggregate;
        print(aggregate);
      });
    }
  }

  // basically the on bind view holder.
  Widget buildSingleElement(EditDataModel data) {
    if (data is AggregateDataModel) {
      return AggregateWidget(data: data, update: updateList, selectDate: _selectDate);
    }

    if (data is ElementDataModel) {
      return ElementWidget(data: data, update: updateList);
    }
    throw UnsupportedError("");
  }

  @override
  Widget build(BuildContext context) {
    return ListView.separated(
      padding: const EdgeInsets.all(8),
      itemCount: elements.length,
      itemBuilder: (BuildContext context, int index) {
        return buildSingleElement(elements[index]);
      },
      separatorBuilder: (BuildContext context, int index) => const Divider(),
    );
  }
}

class AggregateWidget extends StatelessWidget {
  final AggregateDataModel data;

  final void Function(BuildContext context) selectDate;

  final void Function(EditDataModel value) update;

  // to keep the ui harmonic i use a TextField fro the date too, to change the
  // text of a TextField i need a controller
  final TextEditingController dateController = TextEditingController();

  AggregateWidget(
      {Key? key,
      required this.data,
      required this.update,
      required this.selectDate})
      : super(key: key) {
    dateController.text = data.date.toString();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Padding(
            padding: const EdgeInsets.only(bottom: 6),
            child: TextField(
              decoration: const InputDecoration(
                border: OutlineInputBorder(),
                labelText: 'Tag',
              ),
              onChanged: (text) {
                data.tag = text;
                update(data);
              },
            )),
        TextField(
            decoration: const InputDecoration(
              border: OutlineInputBorder(),
              labelText: 'Date',
            ),
            controller: dateController,
            // with readOnly it can't take focus
            readOnly: true,
            onTap: () => selectDate(context)),
      ],
    );
  }
}

class ElementWidget extends StatelessWidget {
  const ElementWidget({Key? key, required this.data, required this.update})
      : super(key: key);

  final void Function(EditDataModel value) update;
  final ElementDataModel data;

  @override
  Widget build(BuildContext context) {
    return Flex(direction: Axis.vertical, children: [
      Padding(
          padding: const EdgeInsets.only(bottom: 6),
          child: Padding(
              padding: const EdgeInsets.only(right: 2),
              child: TextField(
                decoration: const InputDecoration(
                  border: OutlineInputBorder(),
                  labelText: 'name',
                ),
                onChanged: (text) {
                  data.name = text;
                  update(data);
                },
              ))),
      Row(children: [
        Expanded(
            flex: 60,
            child: Padding(
                padding: const EdgeInsets.only(left: 2),
                child: TextField(
                  decoration: const InputDecoration(
                    border: OutlineInputBorder(),
                    labelText: 'cost',
                  ),
                  keyboardType: TextInputType.number,
                  onChanged: (text) {
                    data.cost = double.tryParse(text) ?? 0;
                    update(data);
                  },
                ))),
        Expanded(
            flex: 40,
            child: Padding(
                padding: const EdgeInsets.only(left: 2),
                child: TextField(
                  decoration: const InputDecoration(
                    border: OutlineInputBorder(),
                    labelText: 'num',
                  ),
                  keyboardType: TextInputType.number,
                  onChanged: (text) {
                    data.num = int.tryParse(text) ?? 0;
                    update(data);
                  },
                ))),
      ])
    ]);
  }
}
