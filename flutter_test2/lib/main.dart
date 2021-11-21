import 'dart:developer';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

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
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
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
  final GlobalKey<_AddListState> _key = GlobalKey();

  FloatingActionButtonLocation gravity =
      FloatingActionButtonLocation.centerFloat;

  void toggleGravity() {
    setState(() {
      if (gravity == FloatingActionButtonLocation.centerFloat) {
        gravity = FloatingActionButtonLocation.endFloat;
      } else {
        gravity = FloatingActionButtonLocation.centerFloat;
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: AddList(key: _key, toggleGravity: toggleGravity),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          _key.currentState?.addElement();
          debugPrint('onPressed');
        },
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ),
      floatingActionButtonLocation: gravity,
    );
  }
}

class AddList extends StatefulWidget {
  const AddList({Key? key, required this.toggleGravity}) : super(key: key);

  final Function toggleGravity;

  @override
  State<StatefulWidget> createState() => _AddListState();
}

class _AddListState extends State<AddList> {
  List<String> elements = <String>[];
  int counter = 0;

  void addElement() {
    log('data');
    setState(() {
      elements.add("$counter");
      counter += 1;
    });
  }

  @override
  Widget build(BuildContext context) {
    return ListView.separated(
      padding: const EdgeInsets.all(8),
      itemCount: elements.length,
      itemBuilder: (BuildContext context, int index) {
        return Container(
          height: 50,
          child: ElevatedButton(
            child: Center(child: Text('Entry ${elements[index]}')),
            onPressed: () => widget.toggleGravity(),
          ),
        );
      },
      separatorBuilder: (BuildContext context, int index) => const Divider(),
    );
  }
}

