// importing main components
import 'package:flutter/material.dart';

// importing themes
import 'package:flutter_app/Styles/recipteapp_theme.dart';
import 'package:flutter_app/Widgets/bottom_navigation_drawer.dart';

// import widgets
import 'package:flutter_app/Widgets/floating_action_button.dart';
import 'package:flutter_app/Widgets/home_settings_menu.dart';
import 'package:flutter_staggered_grid_view/flutter_staggered_grid_view.dart';

class DashboardFragment extends StatelessWidget {
  final String title;

  const DashboardFragment({Key? key, required this.title}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    print('rebuild HomeFragment()');
    return Scaffold(
      // HEADER -------------------------
      extendBody: true,
      appBar: AppBar(
        title: Text(title),
      ),
      // BODY ---------------------------
      body: const Center(
        child: DashboardContent(),
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
                }),
            const Spacer(),
            const HomeSettings(),
          ],
        ),
      ),
    );
  }
}

class DashboardContent extends StatelessWidget {
  const DashboardContent({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {

    return Column(children: [
        Expanded(
            child: StaggeredGridView.count(
                crossAxisCount: 2,
                staggeredTiles: _staggeredTiles,
                mainAxisSpacing: 2,
                crossAxisSpacing: 2,
                padding: const EdgeInsets.all(4),
                children: _tiles,
                shrinkWrap: true
            )
    )]
    );
  }
}

const List<StaggeredTile> _staggeredTiles = <StaggeredTile>[
  StaggeredTile.count(2, 1.5),
  StaggeredTile.count(1, 0.5),
  StaggeredTile.count(1, 1),
];

const List<Widget> _tiles = <Widget>[
  _Example01Tile(Colors.green, Icons.widgets),
  _Example01Tile(Colors.lightBlue, Icons.wifi),
  _Example01Tile(Colors.amber, Icons.panorama_wide_angle),
];

class _Example01Tile extends StatelessWidget {
  const _Example01Tile(this.backgroundColor, this.iconData);

  final Color backgroundColor;
  final IconData iconData;

  @override
  Widget build(BuildContext context) {
    return Card(
      color: backgroundColor,
      child: Center(
        child: Padding(
          padding: const EdgeInsets.all(4),
          child: Icon(
            iconData,
            color: Colors.white,
          ),
        ),
      ),
    );
  }
}
