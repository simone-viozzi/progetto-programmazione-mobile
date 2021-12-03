


import 'package:flutter/material.dart';
import 'package:flutter_app/DataWidgets/main_fragment_data.dart';
import 'package:flutter_app/Widgets/bottom_app_bar.dart';

import '../data_models.dart';
import '../definitions.dart';

class AggregatePage extends StatefulWidget {
  const AggregatePage({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => AggregatePageState();
}

class AggregatePageState extends State<AggregatePage>
{
  List elements = [];

  void readData()
  {
    setState(() {
      var aggrId = MainFragDataScope.of(context).selectedAggregate;

      var data = MainFragDataScope.of(context).dbRepository.getAggregateById(aggrId);

      data.then((value) {

      });
    });
  }

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
        floatingActionButton: null,
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