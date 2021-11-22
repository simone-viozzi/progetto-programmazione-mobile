// importing main components
import 'package:flutter/material.dart';

// import data widgets
import 'package:flutter_app/DataWidgets/main_fragment_data.dart';

// import widgets
import 'package:flutter_app/Widgets/floating_action_button.dart';
import 'package:flutter_app/Widgets/bottom_navigation_drawer.dart';
import 'package:flutter_app/Widgets/home_settings_menu.dart';

// importing themes
import 'package:flutter_app/Styles/recipteapp_theme.dart';

class HomeFragment extends StatelessWidget{

  final String title;

  const HomeFragment({Key? key, required this.title}) : super(key: key);

  @override
  Widget build(BuildContext context){
    print('rebuild HomeFragment()');
    return Scaffold(
      // HEADER -------------------------
      extendBody: true,
      appBar: AppBar(
        title: Text(title),
      ),
      // BODY ---------------------------
      body: const Center(
        child: null,
      ),
      // BOTTOM -------------------------
      floatingActionButton: AdaptiveFab(
        state: FABstate.addRecipt,
      ),
      floatingActionButtonLocation: AdaptiveFab.location(context),
      bottomNavigationBar: BottomAppBar(
        elevation: 1.0,
        notchMargin: 10,
        shape: const CircularNotchedRectangle(),
        color: ThemeColors.matPrimary,
        child: Row(
          children: [
            IconButton(
                color: Colors.white,
                icon: const Icon(Icons.menu),
                onPressed: () {
                  openBottomNavigationDrawer(context);
                }
            ),
            const Spacer(),
            const HomeSettings(),
          ],
        ),
      ),
    );
  }
}