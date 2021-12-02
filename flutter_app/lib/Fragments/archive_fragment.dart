// importing main components
import 'package:flutter/material.dart';
import 'package:flutter_app/DataWidgets/main_fragment_data.dart';
import 'package:flutter_app/Widgets/bottom_app_bar.dart';

import '../definitions.dart';

class ArchiveFragment extends StatelessWidget {
  final String title;

  const ArchiveFragment({Key? key, required this.title}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // HEADER -------------------------
      extendBody: true,
      appBar: AppBar(
        title: Text(title),
      ),
      // BODY ---------------------------
      body: ArchiveMainList(),
      // BOTTOM -------------------------
      bottomNavigationBar: MyBottomAppBar(displayHamburger: true,)
    );
  }
}

class ArchiveMainList extends StatefulWidget {
  const ArchiveMainList({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => ArchiveMainListState();
}

class ArchiveDataModel {
  @override
  int index;
  DateTime date;
  String tag;
  double total;

  ArchiveDataModel({
    this.index = 0,
    required this.date,
    required this.tag,
    required this.total,
  });

  @override
  String toString() {
    return "ArchiveDataModel -> {index: $index; date: $date; tag: $tag; total: $total}";
  }
}

class ArchiveMainListState extends State<ArchiveMainList> {

  List aggregates = [];

  void getAggregates() {
    var dbAggregates =
        MainFragDataWidget.of(context).getRepository().getAllAggregates();

    dbAggregates.then((value) {
      var dbAggregates = value.keys;

      var aggregates = dbAggregates
          .map((e) {
            return ArchiveDataModel(
                  index: e.id ?? 0,
                  date: DateTime.fromMillisecondsSinceEpoch(e.date),
                  total: e.total_cost,
                  tag: e.tag,
                );
          });
      setState(() {
        this.aggregates = aggregates.toList();
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    getAggregates();

    return ListView.builder(
      padding: const EdgeInsets.all(8),
      itemCount: aggregates.length,
      itemBuilder: (BuildContext context, int index) {
        return ArchiveElement(
          data: aggregates[index],
        );
      },
    );
  }
}

class ArchiveElement extends StatelessWidget {
  const ArchiveElement({Key? key, required this.data}) : super(key: key);

  final ArchiveDataModel data;

  @override
  Widget build(BuildContext context) {
    return Card(
        child: InkWell(
            onTap: () {
              print(data);
              MainFragDataWidget.of(context).changePage(PageMap.agrViewId);
            },
            child: Padding(
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
                        Text(data.total.toString())
                      ]))
                ],
              ),
            )));
  }
}