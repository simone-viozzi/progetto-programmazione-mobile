// importing main components
import 'package:flutter/material.dart';
// import data widgets
import 'package:flutter_app/DataWidgets/main_fragment_data.dart';

import '../definitions.dart';


class AdaptiveFab extends StatelessWidget
{
  // completely modular fab
  final Function onPressed;
  final FloatingActionButtonLocation position;
  static late FloatingActionButtonLocation staticPosition;
  final IconData icon;

  AdaptiveFab(
      {Key? key,
      required this.onPressed,
      required this.position,
      required this.icon,})
      : super(key: key)
  {
    staticPosition = position;
  }

  // to set the gravity as a fab property and than take it back statically
  // to set it into the floatingActionButtonLocation of the scaffold
  static FloatingActionButtonLocation location(BuildContext context) {
    return staticPosition;
  }

  @override
  Widget build(BuildContext context) {
      return FloatingActionButton(
          child: Icon(
            icon,
            color: Colors.white,
          ),
          onPressed: () {
            onPressed();
          },
      );
  }
}