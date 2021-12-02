import 'dart:async';
import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';
import 'package:flutter_app/Database/dataModels/aggregate.dart';

class DbAggregateMng{

  static final DbAggregateMng instance = DbAggregateMng._init();

  static Database? _database;

  DbAggregateMng._init();

  Future<Database> get database async {
    if (_database != null) return _database!;

    _database = await _initDB('aggregates.db');
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
      CREATE TABLE aggregate (
      id $idType,
      tag_id $integerType,
      date $integerType $notNull,
      attachment $textType,
      total_cost $doubleType $notNull
      )'''
    );
  }

  Future<int> insert (Aggregate aggregate) async {
    final db = await instance.database;
    final id = await db.insert("aggregate", aggregate.toMap());
    return id;
  }

  Future<Aggregate> read (int id) async {
    final db = await instance.database;
    final maps = await db.query(
      'aggregate',
      //columns: [],
      where: 'id = ?',
      whereArgs: [id],
    );

    if (maps.isNotEmpty) {
      return Aggregate.fromMap(maps.first);
    } else {
      throw Exception('ID $id not found');
    }
  }

  Future<List<Aggregate>> readAll() async {
    // Not tested
    final db = await instance.database;

    // final result =
    //     await db.rawQuery('SELECT * FROM tag');

    final result = await db.query("aggregate");

    return result.map((json) => Aggregate.fromMap(json)).toList();
  }

  Future<int> update(Aggregate aggregate) async {
    // Not tested
    final db = await instance.database;

    return db.update(
      "aggregate",
      aggregate.toMap(),
      where: 'id = ?',
      whereArgs: [aggregate.id],
    );
  }

  Future<int> delete(int id) async {
    final db = await instance.database;

    return await db.delete(
      "aggregate",
      where: 'id = ?',
      whereArgs: [id],
    );
  }

  Future<int> deleteAll() async {
    final db = await instance.database;
    return await db.delete("aggregate");
  }

  Future<int> countByTagId(int tag_id) async {
    final db = await instance.database;

    final result = Sqflite.firstIntValue(
        await db.rawQuery(
            'SELECT COUNT(*) FROM aggregate WHERE aggregate.tag_id = ?',
            [tag_id]
        )
    );

    if(result != null) {
      return result;
    }else{
      return 0;
    }
  }

  Future close() async {
    final db = await instance.database;
    db.close();
  }
}