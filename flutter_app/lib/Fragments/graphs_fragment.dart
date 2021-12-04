// importing main components
import 'package:flutter/material.dart';
import 'package:flutter_app/Charts/charts_builder.dart';

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

class GraphsFragment extends StatefulWidget{
  const GraphsFragment({Key? key}) : super(key: key);

  @override
  GraphsFragmentState createState() => GraphsFragmentState();
}

class GraphsFragmentState extends State<GraphsFragment>
{
  ChartsBuilder chartsBuilder = ChartsBuilder();

  @override
  Widget build(BuildContext context) {

    // test graphs
    return Scaffold(
      // HEADER -------------------------
      extendBody: false, // if true graphs remain behind the bottom bar
      appBar: AppBar(
        title: const Text("Graphs page"),
      ),
      // BODY ---------------------------
      body: Center(
        child: FutureBuilder<bool>(
          future: chartsBuilder.loadData(),
          builder: (BuildContext context, AsyncSnapshot<bool> snapshot) {
            if(snapshot.hasData){
              return ListView(
                padding: const EdgeInsets.all(8),
                children: <Widget>[
                  SimpleDoubleBarChart(
                    chartId: "chart1",
                    chartName: "Month expenses by day",
                    data: chartsBuilder.monthExpenses,
                    littleLabels: true,
                  ),
                  SimpleDoubleBarChart(
                    chartId: "chart2",
                    chartName: "Month expenses by tag",
                    data: chartsBuilder.monthTagExpenses,
                  ),
                  SimpleDoubleBarChart(
                    chartId: "chart3",
                    chartName: "Year expenses by month",
                    data: chartsBuilder.yearExpenses,
                  ),
                  SimpleDoubleBarChart(
                    chartId: "chart4",
                    chartName: "Year expenses by tag",
                    data: chartsBuilder.yearTagExpenses,
                  )
                ]
              );
            }else{
              return CircularProgressIndicator();
            }
          }
        )
      ),
      // BOTTOM -------------------------
      floatingActionButton: null,
      bottomNavigationBar: const MyBottomAppBar(
        displayHamburger: true,
        displayOptionMenu: false,
      )
    );
  }

}