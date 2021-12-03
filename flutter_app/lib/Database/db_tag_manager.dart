import 'dart:async';
import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';
import 'package:flutter_app/Database/dataModels/tag.dart';

class DbTagMng{

  static final DbTagMng instance = DbTagMng._init();

  static Database? _database;

  DbTagMng._init();

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
      tag_name $textType
      )'''
    );
  }

  Future<int> insert (Tag tag) async {
    final db = await instance.database;
    final id = await db.insert("tag", tag.toMap());
    return id;
  }

  Future<Tag> read (int tag_id) async {
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

  Future<Tag?> readByName (String tag_name) async {
    final db = await instance.database;
    final maps = await db.query(
      'tag',
      //columns: [],
      where: 'tag_name = ?',
      whereArgs: [tag_name]
    );

    if (maps.isNotEmpty) {
      return Tag.fromMap(maps.first);
    }else{
      return null;
    }
  }

  Future<int?> readTagId(String tagName) async {
    Tag? tag = await readByName(tagName);
    if(tag != null) {
      return tag.tag_id;
    } else {
      return null;
    }
  }

  Future<List<Tag>> readAll() async {
    // Not tested
    final db = await instance.database;

    // final result =
    //     await db.rawQuery('SELECT * FROM tag');

    final result = await db.query("tag");

    return result.map((json) => Tag.fromMap(json)).toList();
  }

  Future<int> update(Tag tag) async {
    // Not tested
    final db = await instance.database;

    return db.update(
      "tag",
      tag.toMap(),
      where: 'tag_id = ?',
      whereArgs: [tag.tag_id],
    );
  }

  Future<int> delete(int id) async {
    final db = await instance.database;

    return await db.delete(
      "tag",
      where: 'tag_id = ?',
      whereArgs: [id],
    );
  }

  Future<int> deleteAll() async {
    final db = await instance.database;
    return await db.delete("tag");
  }

  Future resetTable() async {
    final db = await instance.database;
    await db.execute("DROP TABLE tag;");
    await _createDB(db, 1);
  }

  Future close() async {
    final db = await instance.database;
    db.close();
  }
}
