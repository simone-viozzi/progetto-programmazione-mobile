import 'package:flutter/material.dart';

class Pair<T1, T2> {
  final T1 a;
  final T2 b;

  Pair(this.a, this.b);
}

Future<bool> sureToExit(BuildContext context, String content,
    Function positiveButton, Function negativeButton) async {
  return (await showDialog(
        context: context,
        builder: (context) => AlertDialog(
          title: const Text('Are you sure?'),
          content: Text(content),
          actions: <Widget>[
            TextButton(
              onPressed: () {
                print("sureToExit neg");
                negativeButton();
              },
              child: const Text('No'),
            ),
            TextButton(
              onPressed: () {
                print("sureToExit pos");
                positiveButton();
              },
              child: const Text('Yes'),
            ),
          ],
        ),
      )) ??
      false;
}
