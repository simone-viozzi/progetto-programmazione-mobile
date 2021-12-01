import 'dart:async';
import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';
import 'package:flutter_app/Database/dataModels/element.dart';

class DbElementMng{

  static final DbElementMng instance = DbElementMng._init();

  static Database? _database;

  DbElementMng._init();

  Future<Database> get database async {
    if (_database != null) return _database!;

    _database = await _initDB('elements.db');
    return _database!;
  }

  Future<Database> _initDB(String filePath) async {
    final dbPath = await getDatabasesPath();
    final path = join(dbPath, filePath);

    return await openDatabase(path, version: 1, onCreate: _createDB);
  }

  Future _createDB(Database db, int version) async {
    final idType = 'INTEGER PRIMARY KEY AUTOINCREMENT';
    final textType = 'TEXT';
    final boolType = 'BOOLEAN';
    final integerType = 'INTEGER';
    final doubleType = 'DOUBLE';
    final notNull = 'NOT NULL';

    await db.execute('''
      CREATE TABLE element (
      elem_id $idType,
      aggregate_id $integerType $notNull,
      num $integerType $notNull,
      elem_tag_id $integerType,
      cost $doubleType $notNull,
      name $textType
      )'''
    );
  }

  Future<int> insert (Element element) async {
    final db = await instance.database;
    final id = await db.insert("element", element.toMap());
    return id;
  }

  Future<List<int>> insertList(List<Element> elements) async {

    List<int> ids = [];

    final db = await instance.database;
    elements.forEach((element) async {
      ids.add(await db.insert("element", element.toMap()));
    });
    return ids;
  }

  Future<Element> read (int elem_id) async {
    final db = await instance.database;
    final maps = await db.query(
      'element',
      //columns: [],
      where: 'elem_id = ?',
      whereArgs: [elem_id],
    );

    if (maps.isNotEmpty) {
      return Element.fromMap(maps.first);
    } else {
      throw Exception('ID $elem_id not found');
    }
  }

  Future<List<Element>> readByAggrId(int aggr_id) async {
    // Not tested
    final db = await instance.database;

    // final result =
    //     await db.rawQuery('SELECT * FROM element WHERE aggregate_id = aggr_id');

    final result = await db.query(
        'element',
        where: 'aggregate_id = ?',
        whereArgs: [aggr_id],
    );

    return result.map((json) => Element.fromMap(json)).toList();
  }

  Future<List<Element>> readAll() async {
    // Not tested
    final db = await instance.database;

    // final result =
    //     await db.rawQuery('SELECT * FROM tag');

    final result = await db.query("element");

    return result.map((json) => Element.fromMap(json)).toList();
  }

  Future<int> update(Element element) async {
    // Not tested
    final db = await instance.database;

    return db.update(
      "element",
      element.toMap(),
      where: 'elem_id = ?',
      whereArgs: [element.elem_id],
    );
  }

  Future<int> delete(int id) async {
    final db = await instance.database;

    return await db.delete(
      "element",
      where: 'elem_id = ?',
      whereArgs: [id],
    );
  }

  Future<int> deleteAll() async {
    final db = await instance.database;
    return await db.delete("element");
  }

  Future close() async {
    final db = await instance.database;
    db.close();
  }
}