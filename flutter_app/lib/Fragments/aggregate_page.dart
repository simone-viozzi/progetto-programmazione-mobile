


import 'package:flutter/material.dart';
import 'package:flutter_app/DataWidgets/main_fragment_data.dart';

// importing themes
import 'package:flutter_app/Styles/recipteapp_theme.dart';
import 'package:flutter_app/Widgets/bottom_navigation_drawer.dart';

// import widgets
import 'package:flutter_app/Widgets/floating_action_button.dart';
import 'package:flutter_app/Widgets/home_settings_menu.dart';

class AggregatePage extends StatefulWidget {
  const AggregatePage({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => AggregatePageState();
}

class AggregatePageState extends State<AggregatePage>
{
  var elements = [
    "bla", "blu"
  ];


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // HEADER -------------------------
      extendBody: true,
      appBar: AppBar(
        title: Text("aggregate"),
      ),
      // BODY ---------------------------
      body: AggregatePageMainList(elements: elements),
      // BOTTOM -------------------------
      floatingActionButton: AdaptiveFab(
        state: FABstate.addRecipt,
      ),
      floatingActionButtonLocation: AdaptiveFab.location(context),
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
                  openBottomNavigationDrawer(context);
                }),
            const Spacer(),
            const HomeSettings(),
          ],
        ),
      ),
    );
  }
}

class AggregatePageMainList extends StatelessWidget
{
  final List elements;

  const AggregatePageMainList({Key? key, required this.elements}) : super(key: key);


  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      padding: const EdgeInsets.all(8),
      itemCount: elements.length,
      itemBuilder: (BuildContext context, int index) {
        return Text(
          elements[index],
        );
      },
    );
  }

}