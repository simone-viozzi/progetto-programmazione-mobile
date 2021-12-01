import 'package:charts_flutter/flutter.dart' as charts;
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

class IntDataSeries {

  final String label;
  final int data;
  final charts.Color barColor;

  IntDataSeries({
        required this.label,
        required this.data,
        required this.barColor
      });
}

class DoubleDataSeries {

  final String label;
  final double data;
  final charts.Color barColor;

  DoubleDataSeries({
    required this.label,
    required this.data,
    required this.barColor
  });
}

List<IntDataSeries> testIntList = [
  IntDataSeries(
      label: "1",
      data: 1,
      barColor: charts.ColorUtil.fromDartColor(Colors.green)
  ),
  IntDataSeries(
      label: "2",
      data: 2,
      barColor: charts.ColorUtil.fromDartColor(Colors.green)
  ),
  IntDataSeries(
      label: "3",
      data: 3,
      barColor: charts.ColorUtil.fromDartColor(Colors.green)
  ),
  IntDataSeries(
      label: "4",
      data: 4,
      barColor: charts.ColorUtil.fromDartColor(Colors.green)
  ),
  IntDataSeries(
      label: "5",
      data: 5,
      barColor: charts.ColorUtil.fromDartColor(Colors.green)
  )
];

List<DoubleDataSeries> testDoubleList = [
  DoubleDataSeries(
      label: "1",
      data: 1.5,
      barColor: charts.ColorUtil.fromDartColor(Colors.green)
  ),
  DoubleDataSeries(
      label: "2",
      data: 2.5,
      barColor: charts.ColorUtil.fromDartColor(Colors.green)
  ),
  DoubleDataSeries(
      label: "3",
      data: 3.5,
      barColor: charts.ColorUtil.fromDartColor(Colors.green)
  ),
  DoubleDataSeries(
      label: "4",
      data: 4.5,
      barColor: charts.ColorUtil.fromDartColor(Colors.green)
  ),
  DoubleDataSeries(
      label: "5",
      data: 5.5,
      barColor: charts.ColorUtil.fromDartColor(Colors.green)
  )
];