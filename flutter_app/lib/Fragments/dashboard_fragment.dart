// importing main components
import 'package:flutter/material.dart';
import 'package:flutter_app/Charts/charts_builder.dart';
import 'package:flutter_app/Charts/simple_barchart.dart';
import 'package:flutter_app/DataWidgets/main_fragment_data.dart';
import 'package:flutter_app/Database/dataModels/aggregate.dart';
import 'package:flutter_app/Database/dataModels/tag.dart';
import 'package:flutter_app/Database/db_repository.dart';
import 'package:flutter_app/Widgets/bottom_app_bar.dart';

// import widgets
import 'package:flutter_app/Widgets/floating_action_button.dart';
import 'package:flutter_staggered_grid_view/flutter_staggered_grid_view.dart';

import '../definitions.dart';
import '../utils.dart';

class DashboardFragment extends StatelessWidget {
  final String title;

  const DashboardFragment({Key? key, required this.title}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    print('rebuild HomeFragment()');
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
            child: DashboardContent(),
          ),
          // BOTTOM -------------------------
          floatingActionButton: AdaptiveFab(
        icon: Icons.add,
            position: FloatingActionButtonLocation.centerDocked,
            onPressed: () {
              MainFragDataWidget.of(context).changePage(PageMap.editAgrId);
            },
          ),
          floatingActionButtonLocation: AdaptiveFab.location(context),
          bottomNavigationBar: const MyBottomAppBar(
              displayHamburger: true, displayOptionMenu: true),
        ));
  }
}

class DashboardContent extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => DashboardContentState();
}

class DashboardContentState extends State<DashboardContent> {
  DashboardContentState({Key? key});

  DashboardRepository repository = DashboardRepository();

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<bool>(
        future: repository.loadData(),
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            return StaggeredGridView.count(
                crossAxisCount: 2,
                staggeredTiles: const <StaggeredTile>[
                  StaggeredTile.count(2, 1.5),
                  StaggeredTile.count(1, 0.5),
                  StaggeredTile.count(1, 0.5),
                ],
                mainAxisSpacing: 2,
                crossAxisSpacing: 2,
                padding: const EdgeInsets.all(4),
                children: <Widget>[
                  SimpleDoubleBarChart(
                    chartId: "chart2",
                    chartName: "Month expenses by tag",
                    data: repository.charts.monthTagExpenses,
                  ),
                  Card(
                      clipBehavior: Clip.antiAlias,
                      child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            ListTile(
                              title: Text(repository.sumLastMonth.toString(),
                                  textAlign: TextAlign.center),
                              subtitle: Text(
                                'This month expenses',
                                textAlign: TextAlign.center,
                                style: TextStyle(
                                    color: Colors.black.withOpacity(0.6)),
                              ),
                            ),
                          ])),
                  Card(
                      clipBehavior: Clip.antiAlias,
                      child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            ListTile(
                              title: Text(repository.sumLastYear.toString(),
                                  textAlign: TextAlign.center),
                              subtitle: Text(
                                'This year expenses',
                                textAlign: TextAlign.center,
                                style: TextStyle(
                                    color: Colors.black.withOpacity(0.6)),
                              ),
                            ),
                          ])),
                ],
                shrinkWrap: false);
          } else {
            return const CircularProgressIndicator();
          }
        });
  }
}

class DashboardRepository {
  List<Tag> tagList = [];
  List<Aggregate> aggregatesList = [];

  DbRepository dbRepository = DbRepository();
  ChartsBuilder charts = ChartsBuilder();

  double sumLastMonth = 0;
  double sumLastYear = 0;

  Future<bool> loadData() async {
    tagList = await dbRepository.getAllTags();
    aggregatesList = await dbRepository.getAllAggregates();

    charts
      ..tagList = tagList
      ..aggregatesList = aggregatesList
      ..monthLabels = charts.generateMonthLabels()
      ..generateAggregatesSublists()
      ..generateChartsSeries();

    DateTime date = DateTime.now();
    date = DateTime(date.year, date.month, date.day);

    // creation of a list of aggregates contained inside the last 30 days
    DateTime endDate = date.add(const Duration(days: 1));
    DateTime startDate = date.subtract(const Duration(days: 30));

    for (var i = 0; i < aggregatesList.length; i++) {
      if (aggregatesList[i].date > startDate.millisecondsSinceEpoch &&
          aggregatesList[i].date <= endDate.millisecondsSinceEpoch) {
        sumLastMonth = sumLastMonth + aggregatesList[i].total_cost;
      }
    }

    // creation of a list of aggregates contained inside the last 365 days
    endDate = date.add(const Duration(days: 1));
    startDate = date.subtract(const Duration(days: 365));

    for(var i = 0; i < aggregatesList.length; i++){
      if(aggregatesList[i].date > startDate.millisecondsSinceEpoch &&
          aggregatesList[i].date <= endDate.millisecondsSinceEpoch) {
        sumLastYear = sumLastYear + aggregatesList[i].total_cost;
      }
    }

    return true;
  }
}