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

enum HomePageStates { editMode, readOnlyMode }

class _MyHomePageState extends State<MyHomePage> {
  final GlobalKey<EditTextListState> _editTextList = GlobalKey();

  HomePageStates state = HomePageStates.editMode;
  List? list;

  Widget currentState() {
    switch (state) {
      case HomePageStates.editMode:
        return EditTextList(key: _editTextList);
      case HomePageStates.readOnlyMode:
        return ReadOnlyList(list);
    }
  }

  void switchToReadOnlyState(List? list) {
    this.list = list;
    setState(() {
      state = HomePageStates.readOnlyMode;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: currentState(),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          var list = _editTextList.currentState?.saveElements();
          switchToReadOnlyState(list);
        },
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}

class ReadOnlyList extends StatelessWidget {
  final List? list;

  const ReadOnlyList(this.list, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return ListView.separated(
      padding: const EdgeInsets.all(8),
      itemCount: list?.length ?? 0,
      itemBuilder: (BuildContext context, int index) {
        return SizedBox(
            height: 50,
            child: Center(child: Text(list?.elementAt(index) ?? "")));
      },
      separatorBuilder: (BuildContext context, int index) => const Divider(),
    );
  }
}

class EditTextList extends StatefulWidget {
  const EditTextList({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => EditTextListState();
}

class EditTextListState extends State<EditTextList> {
  List elements = [""];

  void updateList(int index, String value) {
    setState(() {
      elements[index] = value;
      if (index == elements.length - 1) {
        elements.add("");
      }
      print(elements);
    });
  }

  List saveElements() {
    return elements;
  }

  @override
  Widget build(BuildContext context) {
    return ListView.separated(
      padding: const EdgeInsets.all(8),
      itemCount: elements.length,
      itemBuilder: (BuildContext context, int index) {
        return ListSingleElement(
            index: index, value: elements[index], update: updateList);
      },
      separatorBuilder: (BuildContext context, int index) => const Divider(),
    );
  }
}

class ListSingleElement extends StatelessWidget {
  final int index;
  final String value;
  final void Function(int index, String value) update;

  const ListSingleElement({
    Key? key,
    required this.index,
    required this.value,
    required this.update,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 50,
      child: TextField(
        decoration: const InputDecoration(
          border: OutlineInputBorder(),
        ),
        onChanged: (text) {
          update(index, text);
        },
      ),
    );
  }
}
