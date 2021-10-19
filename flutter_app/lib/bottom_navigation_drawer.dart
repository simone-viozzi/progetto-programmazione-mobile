import 'package:flutter/material.dart';


void openBottomNavigationDrawer(BuildContext context){
  showModalBottomSheet<void>(
    context: context,
    builder: (BuildContext context) {
      return Container(
        height: 200,
        color: Colors.grey[350],
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            mainAxisSize: MainAxisSize.min,
            children: <Widget>[
              const Text('Modal BottomSheet'),
              ElevatedButton(
                child: const Text('Close BottomSheet'),
                onPressed: () => Navigator.pop(context),
              )
            ],
          ),
        ),
      );
    },
  );
}
