// importing main components
import 'package:flutter/material.dart';

// import data widgets
import 'package:flutter_app/DataWidgets/main_fragment_data.dart';

import '../definitions.dart';

class HomeSettings extends StatelessWidget {
  const HomeSettings({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {

    return PopupMenuButton(
      icon: const Icon(
        Icons.more_vert,
        color: Colors.white,
      ),
      itemBuilder: (BuildContext context) => <PopupMenuEntry>[
        PopupMenuItem(
          onTap:() {
            MainFragDataWidget.of(context).changePage(PageMap.tests);
          },
          child: const ListTile(
            leading: Icon(Icons.warning),
            title: Text('Tests'),
          ),
        ),
      ],
    );
  }

}

