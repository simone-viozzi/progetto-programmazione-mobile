// importing main components
import 'package:flutter/material.dart';

// import external libraries

// import constants
import 'package:flutter_app/definitions.dart';

// import data widgets
import 'DataWidgets/main_fragment_data.dart';

// importing UI components
import 'Fragments/main_fragment.dart';

// importing themes
import 'package:flutter_app/styles/recipteapp_theme.dart';

void main() {
  runApp(
    MainFragDataWidget(
      child: MaterialApp(
        title: 'receiptApp',
        theme: ThemeData(
          primarySwatch: ThemeColors.matPrimary,
        ),
        home: MainFrag(),
      ),
    )
  );
}