import 'package:flutter/material.dart';
// importing themes
import 'package:flutter_app/Styles/recipteapp_theme.dart';
import 'package:flutter_app/Widgets/bottom_navigation_drawer.dart';
import 'package:flutter_app/Widgets/home_settings_menu.dart';

class MyBottomAppBar extends StatelessWidget {
  // can choose if to display the hamburger or/and the option menu
  final bool displayHamburger;
  final bool displayOptionMenu;

  const MyBottomAppBar({
    Key? key,
    required this.displayHamburger,
    required this.displayOptionMenu
  })
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
      // if displayHamburger == false than display a transparent box so that the
      // appbar does not collapse
      return const SizedBox(
        width: 48,
        height: 48,
      );
    }
  }

  Widget optionMenu()
  {
    if (displayOptionMenu) {
      return const HomeSettings();
    } else {
      return const SizedBox(
        width: 48,
        height: 48,
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
          hamburger(context),
          const Spacer(),
          optionMenu(),
        ],
      ),
    );
  }
}
