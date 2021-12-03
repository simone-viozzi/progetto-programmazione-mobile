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

// import charts
import 'package:flutter_app/Charts/data_series.dart';
import 'package:flutter_app/Charts/simple_barchart.dart';

import '../utils.dart';

class GraphsFragment extends StatelessWidget
{
  final String title;

  const GraphsFragment({Key? key, required this.title}) : super(key: key);

  @override
  Widget build(BuildContext context) {

    // test graphs


    return WillPopScope(
        onWillPop: () {
      return sureToExit(
          context,
          'Do you want to exit an App',
              () => Navigator.of(context).pop(true),
              () => Navigator.of(context).pop(false)
      );
    },
    child: Scaffold(
      // HEADER -------------------------
      extendBody: true,
      appBar: AppBar(
        title: Text(title),
      ),
      // BODY ---------------------------
      body: Center(
        child: Column(
          children: [
            SimpleIntBarChart(
              chartId: "chart1",
              chartName: "Test chart this is a test",
              data: testIntList,
            ),
            SimpleDoubleBarChart(
              chartId: "chart2",
              chartName: "Test chart this is a test with doubles",
              data: testDoubleList,
            )
          ],
        )
      ),
      // BOTTOM -------------------------
      floatingActionButton: null,
      bottomNavigationBar: const MyBottomAppBar(
        displayHamburger: true,
        displayOptionMenu: true
      )
    ));
  }

}