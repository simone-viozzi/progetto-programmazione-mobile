import 'package:flutter/material.dart';
import 'package:charts_flutter/flutter.dart' as charts;
import 'package:flutter_app/Charts/data_series.dart';

class SimpleIntBarChart extends StatelessWidget {
  
  final List<IntDataSeries> data;
  final String chartId;
  final String chartName;

  SimpleIntBarChart({
    Key? key,
    required this.chartName,
    required this.chartId,
    required this.data
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {

    List<charts.Series<IntDataSeries, String>> series = [
      charts.Series(
          id: chartId,
          data: data,
          domainFn: (IntDataSeries series, _) => series.label,
          measureFn: (IntDataSeries series, _) => series.data,
          colorFn: (IntDataSeries series, _) => series.barColor
      )
    ];

    return Container(
      height: 300,
      padding: const EdgeInsets.all(10),
      child: Card(
        child: Padding(
          padding: const EdgeInsets.all(9.0),
          child: Column(
            children: <Widget>[
              Text(
                chartName,
                style: Theme.of(context).textTheme.bodyText1,
              ),
              Expanded(
                child: charts.BarChart(series, animate: true),
              )
            ],
          ),
        ),
      ),
    );

  }

}

class SimpleDoubleBarChart extends StatelessWidget {

  final List<DoubleDataSeries> data;
  final String chartId;
  final String chartName;
  final bool littleLabels;

  const SimpleDoubleBarChart({
    Key? key,
    required this.chartName,
    required this.chartId,
    required this.data,
    this.littleLabels = false
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {

    List<charts.Series<DoubleDataSeries, String>> series = [
      charts.Series(
          id: chartId,
          data: data,
          domainFn: (DoubleDataSeries series, _) => series.label,
          measureFn: (DoubleDataSeries series, _) => series.data,
          colorFn: (DoubleDataSeries series, _) => series.barColor
      )
    ];

    return Container(
      height: 350,
      padding: const EdgeInsets.all(5),
      child: Card(
        child: Padding(
          padding: const EdgeInsets.all(9.0),
          child: Column(
            children: <Widget>[
              Text(
                chartName,
                style: Theme.of(context).textTheme.bodyText1,
              ),
              Expanded(
                child: charts.BarChart(
                    series,
                    animate: true,
                    domainAxis: charts.OrdinalAxisSpec(
                      renderSpec: charts.SmallTickRendererSpec(
                        labelRotation: 70,
                        labelStyle: charts.TextStyleSpec(
                            fontSize: (littleLabels) ? 8 : 14, // size in Pts.
                            color: charts.MaterialPalette.black)
                      ),
                    )
                ),
              )
            ],
          ),
        ),
      ),
    );

  }

}