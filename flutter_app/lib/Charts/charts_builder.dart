import 'package:flutter/material.dart' as material;
import 'package:flutter_app/Database/db_repository.dart';
import 'package:flutter_app/Database/dataModels/aggregate.dart';
import 'package:flutter_app/Database/dataModels/element.dart';
import 'package:flutter_app/Database/dataModels/tag.dart';
import 'package:charts_flutter/flutter.dart' as charts;
import 'package:flutter_app/Charts/data_series.dart';
import 'package:quiver/time.dart';
import 'package:intl/intl.dart';


class ChartsBuilder{

  List<Tag> tagList = [];
  List<Aggregate> aggregatesList = [];
  List<Aggregate> aggregatesMonthList = [];
  List<Aggregate> aggregatesYearList = [];

  DbRepository dbRepository = DbRepository();

  List<DoubleDataSeries> monthTagExpenses = [];
  List<DoubleDataSeries> yearTagExpenses = [];
  List<DoubleDataSeries> monthExpenses = [];
  List<DoubleDataSeries> yearExpenses = [];

  List<String> yearLabels = ["January", "February", "March", "April", "May", "June", "July",
                             "August", "September", "October", "November", "December"];

  List<String> monthLabels = [];

  Future<bool> loadData() async {

    tagList = await dbRepository.getAllTags();
    aggregatesList = await dbRepository.getAllAggregates();
    monthLabels = generateMonthLabels();

    generateAggregatesSublists();

    generateChartsSeries();

    return true;
  }

  void generateAggregatesSublists(){

    DateTime date = DateTime.now();
    date = DateTime(date.year, date.month, date.day);

    // creation of a list of aggregates contained inside the last 30 days
    DateTime endDate = date.add(const Duration(days: 1));
    DateTime startDate = date.subtract(const Duration(days: 30));

    for(var i = 0; i < aggregatesList.length; i++){
      if(aggregatesList[i].date > startDate.millisecondsSinceEpoch &&
          aggregatesList[i].date <= endDate.millisecondsSinceEpoch) {
        aggregatesMonthList.add(
            aggregatesList[i]
        );
      }
    }

    // creation of a list of aggregates contained inside the last 365 days
    endDate = date.add(const Duration(days: 1));
    startDate = date.subtract(const Duration(days: 365));

    for(var i = 0; i < aggregatesList.length; i++){
      if(aggregatesList[i].date > startDate.millisecondsSinceEpoch &&
          aggregatesList[i].date <= endDate.millisecondsSinceEpoch) {
        aggregatesYearList.add(
            aggregatesList[i]
        );
      }
    }
  }

  void generateChartsSeries(){

    // generate yearTagExpenses
    for(var i = 0; i < tagList.length; i++){

      var tagExpense = 0.0;
      aggregatesYearList.forEach((aggregate) {
        if(aggregate.tag_id == tagList[i].tag_id) tagExpense += aggregate.total_cost;
      });

      yearTagExpenses.add(
          DoubleDataSeries(
              label: tagList[i].tag_name,
              data: tagExpense,
              barColor: charts.ColorUtil.fromDartColor(material.Colors.deepPurpleAccent)
          )
      );
    }

    // generate yearExpenses
    final yearIntervals = generateYearIntervals();

    for(var i = 0; i < yearLabels.length; i++){

      var monthExpenses = 0.0;
      aggregatesYearList.forEach((aggregate) {
        if(aggregate.date > yearIntervals[i].millisecondsSinceEpoch &&
            aggregate.date <= yearIntervals[i+1].millisecondsSinceEpoch) {
          monthExpenses += aggregate.total_cost;
        }
      });

      yearExpenses.add(
          DoubleDataSeries(
              label: yearLabels[i],
              data: monthExpenses,
              barColor: charts.ColorUtil.fromDartColor(material.Colors.deepPurpleAccent)
          )
      );
    }

    // generate monthTagExpenses
    for(var i = 0; i < tagList.length; i++){

      var tagExpense = 0.0;
      aggregatesMonthList.forEach((aggregate) {
        if(aggregate.tag_id == tagList[i].tag_id) tagExpense += aggregate.total_cost;
      });

      monthTagExpenses.add(
          DoubleDataSeries(
              label: tagList[i].tag_name,
              data: tagExpense,
              barColor: charts.ColorUtil.fromDartColor(material.Colors.deepPurpleAccent)
          )
      );
    }

    // generate monthExpenses
    final monthIntervals = generateMonthIntervals();
    for(var i = 0; i < monthLabels.length; i++){

      var dayExpenses = 0.0;
      aggregatesMonthList.forEach((aggregate) {
        if(aggregate.date > monthIntervals[i].millisecondsSinceEpoch &&
            aggregate.date <= monthIntervals[i+1].millisecondsSinceEpoch) {
          dayExpenses += aggregate.total_cost;
        }
      });

      monthExpenses.add(
          DoubleDataSeries(
              label: monthLabels[i],
              data: dayExpenses,
              barColor: charts.ColorUtil.fromDartColor(material.Colors.deepPurpleAccent)
          )
      );
    }
  }

  List<String> generateMonthLabels(){

    DateFormat format = DateFormat('yyyy/MM/dd');

    DateTime date = DateTime.now();
    date = DateTime(
      date.year,
      date.month,
      1
    );

    var numOfDays = daysInMonth(date.year, date.month);

    List<String> dateLabels = [];

    for(var i = 0; i < numOfDays; i++){

      dateLabels.add(
          format.format(date)
      );

      date = date.add(Duration(days: 1));
    }

    return dateLabels;
  }

  List<DateTime> generateMonthIntervals(){

    DateTime date = DateTime.now();
    date = DateTime(
        date.year,
        date.month,
        1
    );

    var numOfDays = daysInMonth(date.year, date.month);

    List<DateTime> dateIntervals = [];

    for(var i = 0; i < numOfDays; i++){
      dateIntervals.add(
          date
      );

      date = date.add(const Duration(days: 1));
      if(i >= numOfDays-1){
        dateIntervals.add(
            date
        );
      }
    }

    return dateIntervals;
  }

  List<DateTime> generateYearIntervals(){

    DateTime date = DateTime.now();
    date = DateTime(
        date.year,
        1,
        1
    );

    var numOfmonths = 12;

    List<DateTime> dateIntervals = [];

    for(var i = 0; i < numOfmonths; i++){
      dateIntervals.add(
          date
      );

      if(i < numOfmonths-1) {
        date = DateTime(date.year, date.month+1, 1);

      } else {
        date = DateTime(date.year+1, 1, 1);
        dateIntervals.add(
            date
        );
      }

    }

    return dateIntervals;
  }

}