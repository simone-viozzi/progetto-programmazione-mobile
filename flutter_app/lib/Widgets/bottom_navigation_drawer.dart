import 'package:flutter/material.dart';
import 'package:flutter_app/main.dart';

// importing themes
import 'package:flutter_app/styles/recipteapp_theme.dart';

// import definitions
import 'package:flutter_app/definitions.dart';


void openBottomNavigationDrawer(BuildContext context, int page, changestate){

  showModalBottomSheet<void>(
    elevation: 2.0,
    context: context,
    builder: (BuildContext context) {
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
                if(page != PageMap.homeId){
                  changestate(true);
                  //Navigator.pushNamed(context, '/');
                  // change route and discard the last page state
                  // Navigator.of(context).pushNamedAndRemoveUntil('/', (Route<dynamic> route) => false);
                  /*Navigator.pushReplacement(
                    context,
                    MaterialPageRoute(
                      builder: (context) => const MyHomePage(title: 'Home', pageId: PageMap.homeId),
                    ),
                  );*/
                }
              },
                child: Row(
                  children:[
                    Icon(
                      Icons.home,
                      size: 40,
                      color: (page == PageMap.homeId) ? ThemeColors.matPrimary : Colors.black,
                    ),
                    Padding(
                      padding: const EdgeInsets.fromLTRB(35.0, 0, 0, 0),
                      child: Text(
                        'Home',
                        style: TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                          color: (page == PageMap.homeId) ? ThemeColors.matPrimary : Colors.black,
                        ),
                      ),
                    )
                  ],
                ),
              ),
              GestureDetector(
                onTap: (){
                  if(page != PageMap.archiveId){
                    //Navigator.pushNamed(context, '/archive');
                    // change route and discard the last page state
                    Navigator.of(context).pushNamedAndRemoveUntil('/archive', (Route<dynamic> route) => false);

                    /*
                    Navigator.pushReplacement(
                      context,
                      MaterialPageRoute(
                        builder: (context) => const MyHomePage(title: 'Archive', pageId: PageMap.archiveId),
                      ),
                    );*/
                  }
                },
                child: Row(
                  children:[
                    Icon(
                      Icons.archive,
                      size: 40,
                      color: (page == PageMap.archiveId) ? ThemeColors.matPrimary : Colors.black,
                    ),
                    Padding(
                      padding: const EdgeInsets.fromLTRB(35.0, 0, 0, 0),
                      child: Text(
                        'Archive',
                        style: TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                            color: (page == PageMap.archiveId) ? ThemeColors.matPrimary : Colors.black,
                        ),
                      ),
                    )
                  ],
                ),
              ),
              GestureDetector(
                onTap: (){
                  if(page != PageMap.graphsId){
                    // change route and put the last page in stack
                    //Navigator.pushNamed(context, '/graphs');

                    // change route and discard the last page state
                    Navigator.of(context).pushNamedAndRemoveUntil('/graphs', (Route<dynamic> route) => false);

                  }
                },
                child: Row(
                  children:[
                    Icon(
                      Icons.insights,
                      size: 40,
                      color: (page == PageMap.graphsId) ? ThemeColors.matPrimary : Colors.black,
                    ),
                    Padding(
                      padding: const EdgeInsets.fromLTRB(35.0, 0, 0, 0),
                      child: Text(
                        'Graphs',
                        style: TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                          color: (page == PageMap.graphsId) ? ThemeColors.matPrimary : Colors.black,
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
