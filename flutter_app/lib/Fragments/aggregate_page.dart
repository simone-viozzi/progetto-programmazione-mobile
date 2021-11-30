


import 'package:flutter/material.dart';
import 'package:flutter_app/DataWidgets/main_fragment_data.dart';
import 'package:flutter_app/Widgets/bottom_app_bar.dart';
// import widgets
import 'package:flutter_app/Widgets/floating_action_button.dart';

import '../data_models.dart';
import '../definitions.dart';

class AggregatePage extends StatefulWidget {
  const AggregatePage({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => AggregatePageState();
}

class AggregatePageState extends State<AggregatePage>
{
  List elements = [
    AggregateDataModel(index: 0, date: DateTime.now(), tag: ""),
    ElementDataModel(index: 1, name: "", tag: "", cost: 0, num: 0)
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // HEADER -------------------------
      extendBody: true,
      appBar: AppBar(
        title: const Text("aggregate"),
          leading: BackButton(
            color: Colors.white,
            onPressed: () {
              MainFragDataWidget.of(context).changePage(PageMap.archiveId);
            },
          ),
        ),
        // BODY ---------------------------
        body: AggregatePageMainList(elements: elements),
        // BOTTOM -------------------------
        floatingActionButton: AdaptiveFab(),
        floatingActionButtonLocation: AdaptiveFab.location(context),
        bottomNavigationBar: MyBottomAppBar(
          displayHamburger: false,
        ));
  }
}

class AggregatePageMainList extends StatelessWidget
{
  final List elements;

  const AggregatePageMainList({Key? key, required this.elements}) : super(key: key);


  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      padding: const EdgeInsets.all(8),
      itemCount: elements.length,
      itemBuilder: (BuildContext context, int index) {
        return Text(
          elements[index].toString(),
        );
      },
    );
  }

}