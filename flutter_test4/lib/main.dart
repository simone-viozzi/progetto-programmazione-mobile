import 'package:flutter/material.dart';

import 'package:gallery/studies/reply/colors.dart';


void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: Home(),
    );
  }
}

class Home extends StatefulWidget {
  @override
  _HomeState createState() => _HomeState();
}

class _HomeState extends State<Home> {
  List<Widget> _widgetOptions = <Widget>[
    Page(),
    Page(),
    Page(),
  ];
  int _currentSelected = 0;
  GlobalKey<ScaffoldState> _drawerKey = GlobalKey();

  void _onItemTapped(int index) {
    index == 3
        ? _drawerKey.currentState?.openDrawer()
        : setState(() {
      _currentSelected = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      key: _drawerKey,
      body: _widgetOptions.elementAt(_currentSelected),
      drawer: Drawer(),
      bottomNavigationBar: BottomNavigationBar(
        type: BottomNavigationBarType.fixed,
        onTap: _onItemTapped,
        currentIndex: _currentSelected,
        showUnselectedLabels: true,
        unselectedItemColor: Colors.grey[800],
        selectedItemColor: Color.fromRGBO(10, 135, 255, 1),
        items: <BottomNavigationBarItem>[
          BottomNavigationBarItem(
            label: 'Page 1',
            icon: Icon(Icons.access_alarm),
          ),
          BottomNavigationBarItem(
            label: 'Page 2',
            icon: Icon(Icons.accessible),
          ),
          BottomNavigationBarItem(
            label: 'Page 3',
            icon: Icon(Icons.adb),
          ),
          BottomNavigationBarItem(
            label: 'Drawer',
            icon: Icon(Icons.more_vert),
          )
        ],
      ),
    );
  }
}

class Page extends StatelessWidget {
  const Page({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container();
  }
}


class BottomDrawer extends StatelessWidget {
  const BottomDrawer({
    Key key,
    this.onVerticalDragUpdate,
    this.onVerticalDragEnd,
    this.leading,
    this.trailing,
  }) : super(key: key);

  final GestureDragUpdateCallback onVerticalDragUpdate;
  final GestureDragEndCallback onVerticalDragEnd;
  final Widget leading;
  final Widget trailing;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return GestureDetector(
      behavior: HitTestBehavior.opaque,
      onVerticalDragUpdate: onVerticalDragUpdate,
      onVerticalDragEnd: onVerticalDragEnd,
      child: Material(
        color: theme.bottomSheetTheme.backgroundColor,
        borderRadius: const BorderRadius.only(
          topLeft: Radius.circular(12),
          topRight: Radius.circular(12),
        ),
        child: ListView(
          padding: const EdgeInsets.all(12),
          physics: const NeverScrollableScrollPhysics(),
          children: [
            const SizedBox(height: 28),
            leading,
            const SizedBox(height: 8),
            const Divider(
              color: ReplyColors.blue200,
              thickness: 0.25,
              indent: 18,
              endIndent: 160,
            ),
            const SizedBox(height: 16),
            Padding(
              padding: const EdgeInsetsDirectional.only(start: 18),
              child: Text(
                'FOLDERS',
                style: theme.textTheme.caption.copyWith(
                  color:
                  theme.navigationRailTheme.unselectedLabelTextStyle.color,
                ),
              ),
            ),
            const SizedBox(height: 4),
            trailing,
          ],
        ),
      ),
    );
  }
}

