// importing main components
import 'package:flutter/material.dart';
import 'package:flutter_app/Fragments/archive_fragment.dart';
import 'package:flutter_app/Fragments/test_fragment.dart';

// import constants
import 'package:flutter_app/definitions.dart';

// import main data
import 'package:flutter_app/DataWidgets/main_fragment_data.dart';

// import fragments
import 'package:flutter_app/Fragments/dashboard_fragment.dart';

import 'aggregate_page.dart';
import 'edit_fragment.dart';
import 'graphs_fragment.dart';


////////////////////////////////////////////////////////////////////////////////
///////////////////// Main Fragment manager ////////////////////////////////////
// stateful widget that hold the state
class MainFrag extends StatefulWidget {
  // variables

  // constructor
  const MainFrag({Key? key}) : super(key: key);

  // static access method
  static MainFragState of(BuildContext context){
    return context.findAncestorStateOfType<MainFragState>()!;
  }

  @override
  MainFragState createState() => MainFragState();
}

// stateful widget state
class MainFragState extends State<MainFrag>{
  // state variables /////////////

  // methods /////////////////////

  // overrides ///////////////////
  @override
  Widget build(BuildContext context){
    print('rebuild MainFrag()');
    switch(MainFragDataScope.of(context).pageSelected){
      case PageMap.homeId:
        return DashboardFragment(
            title: 'home'
        );
      case PageMap.archiveId:
        return ArchiveFragment(
            title: 'archive'
        );
      case PageMap.graphsId:
        return GraphsFragment();

      case PageMap.editAgrId:
        return EditFragment();

	  case PageMap.agrViewId:
        return AggregatePage();

      case PageMap.tests:
        return TestFragment();

      default:
        return DashboardFragment(
            title: 'home'
        );
    }
  }
}

////////////////////////////////////////////////////////////////////////////////

/*
*Example of main:

void main() {
  runApp(
      MainFragDataWidget( // (!) object that own the states, and make it reachable from all the widgets
          child: MaterialApp(
            debugShowCheckedModeBanner: false,
            title: 'Store',
            home: MainFrag(), // (!) fragments manager that rebuild the page based on the state.
          )
      )
  );
}

* Scaffold reference:
* return const Scaffold(
      // HEADER -------------------------
      extendBody: true,
      // BODY ---------------------------
      body: Center(
          child: null,
      ),
      // BOTTOM -------------------------
      floatingActionButton: null,
      floatingActionButtonLocation: null,
      bottomNavigationBar: null,
    );
*
 */