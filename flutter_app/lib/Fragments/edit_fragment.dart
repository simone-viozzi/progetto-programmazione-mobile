import 'dart:async';

import 'package:flutter/material.dart';

import '../data_models.dart';

class EditFragment extends StatelessWidget {
  const EditFragment({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        extendBody: true,
        appBar: AppBar(
          title: const Text("edit"),
        ),
        body: EditMainList()

    );
  }
}

class EditMainList extends StatefulWidget {
  const EditMainList({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => EditMainListState();
}

class EditMainListState extends State<EditMainList> {
  List elements = [
    AggregateDataModel(index: 0, date: DateTime.now(), tag: ""),
    ElementDataModel(index: 1, name: "", tag: "", cost: 0, num: 0)
  ];

  DateTime selectedDate = DateTime.now();

  void updateList(EditDataModel value) {
    var index = (value as HasIndex).index;
    setState(() {
      elements[index] = value;
      if (index == elements.length - 1) {
        elements.add(ElementDataModel(
            index: elements.length, name: "", tag: "", cost: 0, num: 0));
      }
      print(elements);
    });

  }

  List saveElements() {
    return elements;
  }

  Future<void> _selectDate(BuildContext context) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: selectedDate,
      firstDate: DateTime(1970),
      lastDate: DateTime.now(),
    );
    if (picked != null && picked != selectedDate) {
      setState(() {
        selectedDate = picked;
        var aggregate = elements[0] as AggregateDataModel;
        aggregate.date = picked;
        elements[0] = aggregate;
        print(aggregate);
      });
    }
  }

  Widget buildSingleElement(EditDataModel data) {
    if (data is AggregateDataModel) {
      return Aggregate(data: data, update: updateList, selectDate: _selectDate);
    }

    if (data is ElementDataModel) {
      return Element(data: data, update: updateList);
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

class Aggregate extends StatelessWidget {
  final AggregateDataModel data;

  final void Function(BuildContext context) selectDate;

  final void Function(EditDataModel value) update;

  final TextEditingController dateController = TextEditingController();

  Aggregate(
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
            readOnly: true,
            onTap: () => selectDate(context)),
      ],
    );
  }
}

class Element extends StatelessWidget {
  const Element({Key? key, required this.data, required this.update})
      : super(key: key);

  final void Function(EditDataModel value) update;
  final ElementDataModel data;

  @override
  Widget build(BuildContext context) {
    return Flex(direction: Axis.vertical, children: [
      Padding(
          padding: const EdgeInsets.only(bottom: 6),
          child: Row(children: [
            Expanded(
                flex: 60,
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
            Expanded(
                flex: 40,
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
          ])),
      Row(children: [
        Expanded(
            flex: 85,
            child: Padding(
                padding: const EdgeInsets.only(right: 2),
                child: TextField(
                  decoration: const InputDecoration(
                    border: OutlineInputBorder(),
                    labelText: 'Tag',
                  ),
                  onChanged: (text) {
                    data.tag = text;
                    update(data);
                  },
                ))),
        Expanded(
            flex: 25,
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
