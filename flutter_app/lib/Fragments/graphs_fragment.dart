// importing main components
import 'package:flutter/material.dart';

// import data widgets
import 'package:flutter_app/DataWidgets/main_fragment_data.dart';
import 'package:flutter_app/Database/db_tag_manager.dart';
import 'package:flutter_app/Database/dataModels/tag.dart';
import 'package:flutter_app/Widgets/bottom_app_bar.dart';

// import widgets
import 'package:flutter_app/Widgets/floating_action_button.dart';
import 'package:flutter_app/Widgets/bottom_navigation_drawer.dart';
import 'package:flutter_app/Widgets/home_settings_menu.dart';

// importing themes
import 'package:flutter_app/Styles/recipteapp_theme.dart';


class GraphsFragment extends StatelessWidget
{
  final String title;

  const GraphsFragment({Key? key, required this.title}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // HEADER -------------------------
      extendBody: true,
      appBar: AppBar(
        title: Text(title),
      ),
      // BODY ---------------------------
      body: Center(
        child: Column(
          children: [
            Container(
                child: Text("graph fragment"),
                margin: EdgeInsets.symmetric(vertical: 20.0)
            )
          ],
        ),
      ),
      // BOTTOM -------------------------
      floatingActionButton: null,
      bottomNavigationBar: MyBottomAppBar(displayHamburger: true,)
    );
  }

}