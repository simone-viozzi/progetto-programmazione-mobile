// importing main components
import 'package:flutter/material.dart';

// import data widgets
import 'package:flutter_app/DataWidgets/main_fragment_data.dart';

import '../definitions.dart';


class AdaptiveFab extends StatelessWidget
{
  const AdaptiveFab({Key? key}) : super(key: key);

  // variables

  static FloatingActionButtonLocation location(BuildContext context) {
    // se la dashboard è in modifica:
    if(MainFragDataScope.of(context).dashModify){
      // allora il fab si trova in fondo a destra
      return FloatingActionButtonLocation.endDocked;
    }else{
      // altriment si trova nel centro
      return FloatingActionButtonLocation.centerDocked;
    }
  }

  @override
  Widget build(BuildContext context) {
    // se la dashboard è in stato di modifica
    if (MainFragDataScope.of(context).dashModify) {
      return FloatingActionButton(
          child: const Icon(
            Icons.check,
            color: Colors.white,
          ),
          onPressed: () {
            MainFragDataWidget.of(context).modifyDash(false);
          }
      );
    }else{
      return FloatingActionButton(
          hoverElevation: 10,
          child: const Icon(
            Icons.add,
            color: Colors.white,
          ),
          onPressed: () {
            MainFragDataWidget.of(context).changePage(PageMap.editAgrId);
          }
      );
    }

  }
}