// importing main components
import 'package:flutter/material.dart';

// import data widgets
import 'package:flutter_app/DataWidgets/main_fragment_data.dart';

// importing themes
import 'package:flutter_app/Styles/recipteapp_theme.dart';

// import definitions
import 'package:flutter_app/definitions.dart';


void openBottomNavigationDrawer(BuildContext context){

  showModalBottomSheet<void>(
    elevation: 2.0,
    context: context,
    builder: (BuildContext context) {

      // retrieving data from main data object
      int pageId = MainFragDataScope.of(context).pageSelected;

      return Container(
        height: 250,
        color: Colors.white,
        child: Padding(
          padding: const EdgeInsets.only(left: 35.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            mainAxisSize: MainAxisSize.min,

            children: <Widget>[
              GestureDetector(
              onTap: (){
                if(pageId != PageMap.homeId){
                  print('open home');
                  MainFragDataWidget.of(context).changePage(PageMap.homeId);
                  Navigator.pop(context); // close the bottom sheet
                }
              },
                child: Row(
                  children:[
                    Icon(
                      Icons.home,
                      size: 40,
                      color: (pageId == PageMap.homeId) ? ThemeColors.matPrimary : Colors.black,
                    ),
                    Padding(
                      padding: const EdgeInsets.fromLTRB(35.0, 0, 0, 0),
                      child: Text(
                        'Home',
                        style: TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                          color: (pageId == PageMap.homeId) ? ThemeColors.matPrimary : Colors.black,
                        ),
                      ),
                    )
                  ],
                ),
              ),
              GestureDetector(
                onTap: (){
                  if(pageId != PageMap.archiveId){
                    print('open archive');
                    MainFragDataWidget.of(context).changePage(PageMap.archiveId);
                    Navigator.pop(context); // close the bottom sheet
                  }
                },
                child: Row(
                  children:[
                    Icon(
                      Icons.archive,
                      size: 40,
                      color: (pageId == PageMap.archiveId) ? ThemeColors.matPrimary : Colors.black,
                    ),
                    Padding(
                      padding: const EdgeInsets.fromLTRB(35.0, 0, 0, 0),
                      child: Text(
                        'Archive',
                        style: TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                            color: (pageId == PageMap.archiveId) ? ThemeColors.matPrimary : Colors.black,
                        ),
                      ),
                    )
                  ],
                ),
              ),
              GestureDetector(
                onTap: (){
                  if(pageId != PageMap.graphsId){
                    print('open graphs');
                    MainFragDataWidget.of(context).changePage(PageMap.graphsId);
                    Navigator.pop(context); // close the bottom sheet
                  }
                },
                child: Row(
                  children:[
                    Icon(
                      Icons.insights,
                      size: 40,
                      color: (pageId == PageMap.graphsId) ? ThemeColors.matPrimary : Colors.black,
                    ),
                    Padding(
                      padding: const EdgeInsets.fromLTRB(35.0, 0, 0, 0),
                      child: Text(
                        'Graphs',
                        style: TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                          color: (pageId == PageMap.graphsId) ? ThemeColors.matPrimary : Colors.black,
                        ),
                      ),
                    )
                  ],
                ),
              ),
            ],
          ),
        ),
      );
    },
  );
}
