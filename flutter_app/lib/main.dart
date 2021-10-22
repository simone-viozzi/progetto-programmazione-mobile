// importing main components
import 'package:flutter/material.dart';

// import external libraries

// import constants
import 'package:flutter_app/definitions.dart';

// importing UI components
import 'Widgets/card_test.dart';
import 'Widgets/bottom_navigation_drawer.dart';
import 'package:flutter_app/Menus/home_settings_menu.dart';

// importing themes
import 'package:flutter_app/styles/recipteapp_theme.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'receiptApp',
      theme: ThemeData(
        primarySwatch: ThemeColors.matPrimary,
      ),
      initialRoute: '/',
      routes: {
        '/': (context) =>                   const MyHomePage(title: 'Home',           pageId: PageMap.homeId),
        '/archive': (context) =>            const MyHomePage(title: 'Archive',        pageId: PageMap.archiveId),
        '/archive/element': (context) =>    const MyHomePage(title: 'Element',        pageId: PageMap.agrViewId),
        '/graphs': (context) =>             const MyHomePage(title: 'Graphs',         pageId: PageMap.graphsId),
        '/add_aggregate': (context) =>      const MyHomePage(title: 'Add aggregate',  pageId: PageMap.addAgrId),
        '/camera_view': (context) =>        const MyHomePage(title: 'Dashboard',      pageId: PageMap.cameraViewId),
      }
    );
  }
}

class MyHomePage extends StatefulWidget{
  const MyHomePage({Key? key, required this.title, required this.pageId}) : super(key: key);
  final String title;
  final int pageId;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage>{
  bool modificationState = false;
  int state_update = 0;

  void changeModState(bool state){
    setState(() {
      modificationState = state;
    });
  }

  void reload(){
    setState(() {
      state_update++;
    });
  }

  @override
  Widget build(BuildContext context) {

    print("on create pageId is: ${widget.pageId}");

    return Scaffold(
      extendBody: true, // this enable the listView to swipe under the notch
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(

        child: ListView(
          padding: const EdgeInsets.all(8),
          children: <Widget>[
            buildCard(context),
            buildCard(context),
            buildCard(context),
            buildCard(context),
            buildCard(context),
          ],
        )
      ),
      floatingActionButton: (modificationState) ?
      FloatingActionButton(
          child: const Icon(
            Icons.check,
            color: Colors.white,
          ),
          onPressed: () {
            changeModState(false);
          }
      ) :
      FloatingActionButton(
          hoverElevation: 10,
          child: const Icon(
            Icons.add,
            color: Colors.white,
          ),
          onPressed: () {}
      ),
      floatingActionButtonLocation: (modificationState) ?
      FloatingActionButtonLocation.endDocked :
      FloatingActionButtonLocation.centerDocked,
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
                  openBottomNavigationDrawer(context, widget.pageId, changeModState);
                }
              ),
            const Spacer(),
            HomeSettings(
                changeModStateFun: changeModState
            ),
          ],
        ),
      ),
    );
  }
}