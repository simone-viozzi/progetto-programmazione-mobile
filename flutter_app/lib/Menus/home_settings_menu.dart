// importing main components
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

class HomeSettings extends StatelessWidget {

  final changeModStateFun;

  const HomeSettings({this.changeModStateFun});

  @override
  Widget build(BuildContext context) {
    return PopupMenuButton(
      icon: const Icon(
        Icons.more_vert,
        color: Colors.white,
      ),
      itemBuilder: (BuildContext context) => <PopupMenuEntry>[
        const PopupMenuItem(
          child: ListTile(
            leading: Icon(Icons.info_outline),
            title: Text('About'),
          ),
        ),
        PopupMenuItem(
          onTap:() {
            changeModStateFun(true);
          },
          child: const ListTile(
            leading: Icon(Icons.edit),
            title: Text('Settings'),
          ),
        ),
      ],
    );
  }
}

