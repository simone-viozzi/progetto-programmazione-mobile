// importing main components
import 'package:flutter/material.dart';

// import data widgets
import 'package:flutter_app/DataWidgets/main_fragment_data.dart';
import 'package:flutter_app/Database/db_tag_manager.dart';
import 'package:flutter_app/Database/dataModels/tag.dart';

// import widgets
import 'package:flutter_app/Widgets/floating_action_button.dart';
import 'package:flutter_app/Widgets/bottom_navigation_drawer.dart';
import 'package:flutter_app/Widgets/home_settings_menu.dart';

// importing themes
import 'package:flutter_app/Styles/recipteapp_theme.dart';


class TestFragment extends StatelessWidget
{

  const TestFragment({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // HEADER -------------------------
      extendBody: true,
      appBar: AppBar(
        title: const Text("Tests"),
      ),
      // BODY ---------------------------
      body: Center(
        child: Column(
          children: [
            const Text("TEST PAGE"),
            ElevatedButton(
                child: const Text("generate fake data"),
                onPressed: () async {
                  WidgetsFlutterBinding.ensureInitialized();
                  print("executed: generateFakeData()");
                  MainFragDataScope.of(context).dbRepository.generateFakeData();
                }
            ),
            ElevatedButton(
                child: const Text("Inspect database"),
                onPressed: () async {
                  WidgetsFlutterBinding.ensureInitialized();
                  print("executed: inspectDatabase()");
                  MainFragDataScope.of(context).dbRepository.inspectDatabase();
                }
            ),
            ElevatedButton(
                child: const Text("Database hard reset"),
                onPressed: () async {
                  WidgetsFlutterBinding.ensureInitialized();
                  print("executed: resetDatabase()");
                  MainFragDataScope.of(context).dbRepository.resetDatabase();
                }
            ),
            ElevatedButton(
                child: const Text("Database values reset"),
                onPressed: () async {
                  WidgetsFlutterBinding.ensureInitialized();
                  print("executed: deleteAll()");
                  MainFragDataScope.of(context).dbRepository.deleteAll();
                }
            ),
          ],
        ),
      ),
      // BOTTOM -------------------------
      floatingActionButton: null,
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