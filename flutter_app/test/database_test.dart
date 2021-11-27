import 'package:test/test.dart';
import 'package:flutter_app/Database/db_manager.dart';
import 'package:flutter_app/Database/dataModels/tag.dart';
import 'package:flutter/widgets.dart';

void main() {

  test('tags instance should be the same', () async {

    // Avoid errors caused by flutter upgrade.
    // Importing 'package:flutter/widgets.dart' is required.
    WidgetsFlutterBinding.ensureInitialized();

    final database = databaseTest();

    Tag tag = Tag(
        tag_id: 0,
        tag_name: "test",
        aggregate: true
    );

    database.insertTag(tag);

    final allTags = database.getAllTags();

    print(await allTags);

    expect(tag, (await allTags)[0]);
  });

}