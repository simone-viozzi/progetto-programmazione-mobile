


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
  Future<List> readData() async {
    var aggrId = MainFragDataScope.of(context).selectedAggregate;

    var data = await MainFragDataScope.of(context)
        .dbRepository
        .getAggregateById(aggrId);

    var dbAggregate = data.a;
    var dbElements = data.b;

    var aggregate = AggregateDataModel(
        index: 0,
        date: DateTime.fromMillisecondsSinceEpoch(dbAggregate.date),
        tag: dbAggregate.tag,
        totalCost: dbAggregate.total_cost);
    var elements = dbElements.map((e) {
      return ElementDataModel(name: e.name, cost: e.cost, num: e.num);
    });

    var list = [(aggregate as EditDataModel)];
    list.addAll(elements);

    return list;
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
        body: FutureBuilder(
          future: readData(),
          builder: (context, snapshot) {
            if (snapshot.connectionState == ConnectionState.done) {
              final data = snapshot.data as List?;

              if (data == null) {
                return const Center(
                  child: Text("errrorr"),
                );
              }

              return AggregatePageMainList(elements: data);
            } else if (snapshot.hasError) {
              throw snapshot.error ?? Error();
            } else {
              return const Center(child: CircularProgressIndicator());
            }
          },
        ),
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

  Widget buildSingleElement(EditDataModel data) {
    if (data is AggregateDataModel)
      {
        return StaticAggregateWidget(data: data);
      }
    
    if (data is ElementDataModel)
      {
        return StaticElementWidget(data: data);
      }

    throw UnsupportedError("");
  }
  

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      padding: const EdgeInsets.all(8),
      itemCount: elements.length,
      itemBuilder: (BuildContext context, int index) {
        return buildSingleElement(
          elements[index],
        );
      },
    );
  }
}



class StaticAggregateWidget extends StatelessWidget {

  final AggregateDataModel data;

  const StaticAggregateWidget({Key? key, required this.data}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return  Card(
        child:Padding(
          padding: const EdgeInsets.fromLTRB(16, 8, 8, 8),
          child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.all(4),
            child: Row(children: [
              const Text(
                "tag: \t   ",
                style: TextStyle(fontWeight: FontWeight.bold),
              ),
              Text(data.tag)
            ]),
          ),
          Padding(
            padding: const EdgeInsets.all(4),
            child: Row(children: [
              const Text(
                "date: \t ",
                style: TextStyle(fontWeight: FontWeight.bold),
              ),
              Text(data.date.toString())
            ]),
          ),
          Padding(
              padding: const EdgeInsets.all(4),
              child: Row(children: [
                const Text(
                  "total: \t ",
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
                Text(data.totalCost.toString())
              ]))
        ],
        ),
      ),
    );
  }
}


class StaticElementWidget extends StatelessWidget {
  final ElementDataModel data;

  const StaticElementWidget({Key? key, required this.data}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return  Card(
      child:Padding(
        padding: const EdgeInsets.fromLTRB(16, 8, 8, 8),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Padding(
              padding: const EdgeInsets.all(4),
              child: Row(children: [
                const Text(
                  "name: \t",
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
                Text(data.name)
              ]),
            ),
            Padding(
              padding: const EdgeInsets.all(4),
              child: Row(children: [
                const Text(
                  "num: \t  ",
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
                Text(data.num.toString())
              ]),
            ),
            Padding(
                padding: const EdgeInsets.all(4),
                child: Row(children: [
                  const Text(
                    "cost: \t  ",
                    style: TextStyle(fontWeight: FontWeight.bold),
                  ),
                  Text(data.cost.toString())
                ]))
          ],
        ),
      ),
    );
  }
}