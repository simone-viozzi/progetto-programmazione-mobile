import 'package:flutter/material.dart';
import 'card_test.dart';
import 'bottom_navigation_drawer.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'reciptApp',
      theme: ThemeData(
        primarySwatch: Colors.grey,
      ),
      initialRoute: '/',
      routes: {
        '/': (context) => const MyHomePage(title: 'Dashboard'),
        '/archive': (context) => const MyHomePage(title: 'Dashboard'),
        '/charts': (context) => const MyHomePage(title: 'Dashboard'),
        '/add_agregate': (context) => const MyHomePage(title: 'Dashboard'),
      }
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);
  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;

  void _incrementCounter() {
    setState(() {
      _counter++;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[350],
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
      bottomNavigationBar: BottomAppBar(
        notchMargin: 10,

        shape: const CircularNotchedRectangle(),
        color: Colors.black,
        child: Row(
          children: [
            IconButton(
                color: Colors.white,
                icon: const Icon(Icons.menu),
                onPressed: () => openBottomNavigationDrawer(context)
                ),
            const Spacer(),
            IconButton(
                color: Colors.white,
                icon: const Icon(Icons.more_vert),
                onPressed: () {}
                ),
          ],
        ),
      ),
      floatingActionButton:
      FloatingActionButton(
          hoverElevation: 10,
          child: const Icon(
            Icons.add,
            color: Colors.white,
          ),
          onPressed: () {}
          ),
      floatingActionButtonLocation: FloatingActionButtonLocation.centerDocked
    );
  }
}
