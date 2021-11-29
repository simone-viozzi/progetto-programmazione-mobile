import 'dart:async';
import 'dart:io';
import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';

import 'package:flutter_app/Database/dataModels/aggregate.dart';
import 'package:flutter_app/Database/dataModels/element.dart';
import 'package:flutter_app/Database/dataModels/tag.dart';

class databaseTest{

  static final databaseTest instance = databaseTest._init();

  static Database? _database;

  databaseTest._init();

  Future<Database> get database async {
    if (_database != null) return _database!;

    _database = await _initDB('tags.db');
    return _database!;
  }

  Future<Database> _initDB(String filePath) async {
    final dbPath = await getDatabasesPath();
    final path = join(dbPath, filePath);

    return await openDatabase(path, version: 1, onCreate: _createDB);
  }

  Future _createDB(Database db, int version) async {
    final idType = 'INTEGER PRIMARY KEY AUTOINCREMENT';
    final textType = 'TEXT NOT NULL';
    final boolType = 'BOOLEAN NOT NULL';
    final integerType = 'INTEGER NOT NULL';

    await db.execute('''
      CREATE TABLE tag (
      tag_id $idType, 
      tag_name $textType,
      aggregate $boolType
      )'''
    );
  }

  Future<int> insertTag (Tag tag) async {
    final db = await instance.database;
    final id = await db.insert("tag", tag.toMap());
    return id;
  }

  Future<Tag> readTag (int tag_id) async {
    final db = await instance.database;
    final maps = await db.query(
      'tag',
      //columns: [],
      where: 'tag_id = ?',
      whereArgs: [tag_id],
    );

    if (maps.isNotEmpty) {
      return Tag.fromMap(maps.first);
    } else {
      throw Exception('ID $tag_id not found');
    }
  }

  Future close() async {
    final db = await instance.database;
    db.close();
  }
}

/*
class databaseTest{

  static late Future<Database> _database;
  static bool db_init = false;

  Future<Database> get database async{
    if(db_init){
      return _database;
    }else{
      _database = await createDatabase();
      db_init = true;
      return _database;
    }
  }

  createDatabase() async{

    String path = join(await getDatabasesPath(), 'tag_database.db');
    //database creation
    return openDatabase(
      // Set the path to the database. Note: Using the `join` function from the
      // `path` package is best practice to ensure the path is correctly
      // constructed for each platform.
      path,
      onOpen: (db){},
      // When the database is first created, create a table to store dogs.
      onCreate: (db, version) {
        // Run the CREATE TABLE statement on the database.
        return db.execute(
          'CREATE TABLE tag(tag_id INTEGER PRIMARY KEY, tag_name TEXT, aggregate BIT)',
        );
      },
      // Set the version. This executes the onCreate function and provides a
      // path to perform database upgrades and downgrades.
      version: 1,
    );
  }

  // Define a function that inserts dogs into the database
  Future<void> insertTag(Tag tag) async {
    // Get a reference to the database.
    final db = await database;

    // Insert the Dog into the correct table. You might also specify the
    // `conflictAlgorithm` to use in case the same dog is inserted twice.
    //
    // In this case, replace any previous data.
    await db.insert(
      'tag',
      tag.toMap(),
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
  }

  Future<List<Tag>> getAllTags() async{
    final db = await database;

    final List<Map<String, dynamic>> maps = await db.query('tag');

    // Convert the List<Map<String, dynamic> into a List<Tag>.
    return List.generate(maps.length, (i) {
      return Tag(
        tag_id: maps[i]['tag_id'],
        tag_name: maps[i]['tag_name'],
        aggregate: maps[i]['aggregate'],
      );
    });
  }

  void test_1() async{

    Tag tag = Tag(
      tag_id: 0,
      tag_name: "test",
      aggregate: true
    );

    insertTag(tag);

    print(await getAllTags());
  }

}
*/

