import 'package:flutter/material.dart';

Widget buildCard(BuildContext context){

  return Card(
    elevation: 15.0,
    clipBehavior: Clip.antiAlias,
    shape: RoundedRectangleBorder(
      borderRadius: BorderRadius.circular(35.0),
      side: const BorderSide(
        style: BorderStyle.none,
        width: 5.0,
      ),
    ),
    child: Column(
      children: [
        Padding(
          padding: const EdgeInsets.only(left: 38.0),
          child: ListTile(
            title: const Text('Card title 1'),
            subtitle: Text(
              'Secondary Text',
              style: TextStyle(color: Colors.black.withOpacity(0.6)),
            ),
          ),
        ),
        Padding(
          padding: const EdgeInsets.all(16.0),
          child: Text(
            'Greyhound divisively hello coldly wonderfully marginally far upon excluding.',
            style: TextStyle(color: Colors.black.withOpacity(0.6)),
          ),
        ),
        Image.asset('images/pie_chart.png'),
      ],
    ),
  );
}