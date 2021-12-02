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
                child: const Text("test 1 button"),
                onPressed: () async {

                  WidgetsFlutterBinding.ensureInitialized();

                  Tag tag = Tag(
                      tag_name: "test",
                      //aggregate: 1
                  );

                  final id = null;//await DbTagMng.instance.insertTag(tag);

                  final readedTag = null;//await DbTagMng.instance.readTag(id);

                  print(await readedTag);

                  if(tag.tag_name !=  readedTag.tag_name){
                    print("${tag.tag_name} while exected is ${readedTag.tag_name}");
                  }else{
                    print("query executed correctly");
                  }
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