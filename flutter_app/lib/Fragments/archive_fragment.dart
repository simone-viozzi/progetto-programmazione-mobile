// importing main components
import 'package:flutter/material.dart';

// importing themes
import 'package:flutter_app/Styles/recipteapp_theme.dart';
import 'package:flutter_app/Widgets/bottom_navigation_drawer.dart';

// import widgets
import 'package:flutter_app/Widgets/floating_action_button.dart';
import 'package:flutter_app/Widgets/home_settings_menu.dart';

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
      floatingActionButton: AdaptiveFab(),
      floatingActionButtonLocation: AdaptiveFab.location(context),
      bottomNavigationBar: BottomAppBar(
        elevation: 1.0,
        notchMargin: 10,
        shape: const CircularNotchedRectangle(),
        color: ThemeColors.matPrimary,
        child: Row(
          children: [
            IconButton(
                color: Colors.white,
                icon: const Icon(Icons.menu),
                onPressed: () {
                  openBottomNavigationDrawer(context);
                }),
            const Spacer(),
            const HomeSettings(),
          ],
        ),
      ),
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
  List elements = [
    ArchiveDataModel(index: 0, date: DateTime.now(), tag: "tag 1", total: 20),
    ArchiveDataModel(index: 1, date: DateTime.now(), tag: "tag 2", total: 40)
  ];

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      padding: const EdgeInsets.all(8),
      itemCount: elements.length,
      itemBuilder: (BuildContext context, int index) {
        return ArchiveElement(
          data: elements[index],
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