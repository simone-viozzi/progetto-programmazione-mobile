import 'package:flutter/material.dart';

// importing themes
import 'package:flutter_app/Styles/recipteapp_theme.dart';
import 'package:flutter_app/Widgets/bottom_navigation_drawer.dart';
import 'package:flutter_app/Widgets/home_settings_menu.dart';

class MyBottomAppBar extends StatelessWidget {
  final bool displayHamburger;

  const MyBottomAppBar({Key? key, required this.displayHamburger})
      : super(key: key);

  Widget hamburger(BuildContext context) {
    if (displayHamburger) {
      return IconButton(
          color: Colors.white,
          icon: const Icon(Icons.menu),
          onPressed: () {
            openBottomNavigationDrawer(context);
          });
    } else {
      return const SizedBox(
        width: 5,
        height: 5,
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return BottomAppBar(
      elevation: 1.0,
      notchMargin: 10,
      shape: const CircularNotchedRectangle(),
      color: ThemeColors.matPrimary,
      child: Row(
        children: [
          hamburger(
            context,
          ),
          const Spacer(),
          const HomeSettings(),
        ],
      ),
    );
  }
}
