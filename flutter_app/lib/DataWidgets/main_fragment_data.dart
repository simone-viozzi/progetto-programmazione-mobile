// importing main components
import 'package:flutter/material.dart';
import 'package:flutter_app/Database/db_repository.dart';
import 'package:flutter_app/definitions.dart';

////////////////////////////////////////////////////////////////////////////////
/////////////////////Data managements objects///////////////////////////////////
// Object that can handle the data
class MainFragData {
  // class that contain all MainFrag states

  // states
  int pageSelected; //
  bool dashModify; // se true indica che la dashboard è in stato di modifica

  DbRepository dbRepository = DbRepository();


  // constructor
  MainFragData({
    this.pageSelected = PageMap.homeId,
    this.dashModify = false,
  });

  // getter/setter methods
}

// object that give methods to retrieve the data
// from each part of the widget tree
class MainFragDataScope extends InheritedWidget{
  // data
  final MainFragData data;

  // constructor
  const MainFragDataScope(
      this.data,
      {
        Key? key,
        required Widget child,
      }
      ):super(key: key, child: child);

  // define static method for data structures retrieving of the field data of the InheritedWidget
  static MainFragData of(BuildContext context) {
    return context.dependOnInheritedWidgetOfExactType<MainFragDataScope>()!.data;
  }

  @override
  bool updateShouldNotify(MainFragDataScope oldWidget) {
    return data != oldWidget.data;
  }
}

// stateful widget that hold the state
class MainFragDataWidget extends StatefulWidget {
  // variables
  final Widget child;

  // constructor
  const MainFragDataWidget({Key? key, required this.child}) : super(key: key);

  // static access method
  static MainFragDataWidgetState of(BuildContext context){
    return context.findAncestorStateOfType<MainFragDataWidgetState>()!;
  }

  @override
  MainFragDataWidgetState createState() => MainFragDataWidgetState();
}

// state of the principal stateful widget
class MainFragDataWidgetState extends State<MainFragDataWidget>{
  // state variables /////////////
  // Nota per accedere alle variabili: MainFragData.of(context).variable
  // TODO aggiungi qui i dati che devono essere accessibili in tutto il subtree

  final MainFragData _data = MainFragData();

  // methods /////////////////////
  // Nota: per accedere ai metodi: MainFragDataWidget.of(context).method()
  // TODO aggiungi qui i metodi per modificare le variabili da tutto il subtree

  void changePage(int pageId) {
    // method that change the page in the mainFrag
    print('changePage()');
    setState(() {
      _data.pageSelected = pageId;
    });
  }

  void modifyDash(bool newState) {
    // method that change the modification state of the dashboard
    // do anything if pageSelected != homeId
    print('modifyDash()');
    setState(() {
      _data.dashModify = newState;
    });
  }

  DbRepository getRepository()
  {
    return _data.dbRepository;
  }

  // overrides ///////////////////
  @override
  Widget build(BuildContext context){
    print('rebuild MainFragDataWidget()');
    rebuildAllChildren(context);
    return MainFragDataScope(
        _data,
        child: widget.child
    );
  }

  void rebuildAllChildren(BuildContext context) {
    void rebuild(Element el) {
      el.markNeedsBuild();
      el.visitChildren(rebuild);
    }
    (context as Element).visitChildren(rebuild);
  }

}

////////////////////////////////////////////////////////////////////////////////