import 'db_aggregate_manager.dart';
import 'db_element_manager.dart';
import 'db_tag_manager.dart';
import 'dataModels/element.dart';
import 'dataModels/aggregate.dart';
import 'dataModels/tag.dart';
import 'dart:math';

class DbRepository {

  ///////////////////////////////////////////////////////////////////////
  //// INSERT

  Future<int> insertAggregate(Aggregate aggregate, List<Element> elements) async {
    
    aggregate.id = null;

    if(aggregate.tag != ""){

      int? tagId = await DbTagMng.instance.readTagId(aggregate.tag);

      if( tagId != null ){
        // if the tag exist it will be recovered and the id will be added to the aggregate
        aggregate.tag_id = tagId;
      }else{
        // if the tag dosen't exist it will be created and the id will be added to the aggregate
        Tag newTag = Tag(
          tag_name: aggregate.tag
        );
        aggregate.tag_id = await DbTagMng.instance.insert(newTag);
      }
    }

    int id = await DbAggregateMng.instance.insert(aggregate);
    for(final e in elements){
      e.aggregate_id = id;
    }
    DbElementMng.instance.insertList(elements);

    return id;
  }

  ///////////////////////////////////////////////////////////////////////
  //// GET

  Future<Map<Aggregate, List<Element>>> getAggregateById(int id) async
  {

    Aggregate aggregate = await DbAggregateMng.instance.read(id);
    List<Element> elements = await DbElementMng.instance.readByAggrId(id);

    if(aggregate.tag_id != null) {
      aggregate.tag = (await DbTagMng.instance.read(aggregate.tag_id!)).tag_name;
    }

    return {
      aggregate: elements
    };
  }

  Future<Map<Aggregate, List<Element>>> getAllAggregatesAndElements() async
  {

    final Map<Aggregate, List<Element>> result = {};

    List<Aggregate> aggregates = await DbAggregateMng.instance.readAll();

    for (var aggregate in aggregates) {
      result[aggregate] = await DbElementMng.instance.readByAggrId(aggregate.id!);
    }

    return result;
  }

  Future<List> getAllAggregates() async
  {
    List<Aggregate> aggregates = await DbAggregateMng.instance.readAll();

    for (var element in aggregates)
    {

      final tagId = element.tag_id;
      if (tagId == null)
        {
          continue;
        }
      var tag =  (await DbTagMng.instance.read(tagId)).tag_name;

      print(tag);

      element.tag = tag;
    }

    print(aggregates);

    return aggregates;
  }


  ///////////////////////////////////////////////////////////////////////
  //// DELETE

  void deleteAggregateById(int aggrId) async {

    final aggregate = await DbAggregateMng.instance.read(aggrId);

    if(aggregate.tag_id != null){
      // if the aggregate has a tag, shuld be verified if there is another aggregate associated
      // if this one is the only tag associated the tag will be eleminated
      if(await DbAggregateMng.instance.countByTagId(aggregate.tag_id!) <= 1){
        // if there is only this aggregate attached to the tag delete the tag
        DbTagMng.instance.delete(aggregate.tag_id!);
      }
    }

    final elements = await DbElementMng.instance.readByAggrId(aggrId);
    for( var elem in elements){
      DbElementMng.instance.delete(elem.elem_id!);
    }

    DbAggregateMng.instance.delete(aggregate.id!);
  }

  void deleteAll(){
    // reset only records inside each table
    DbAggregateMng.instance.deleteAll();
    DbElementMng.instance.deleteAll();
    DbTagMng.instance.deleteAll();
  }

  void resetDatabase(){
    // delete completely every table
    DbAggregateMng.instance.resetTable();
    DbElementMng.instance.resetTable();
    DbTagMng.instance.resetTable();
  }

  ///////////////////////////////////////////////////////////////////////
  //// DEBUG

  List<String> tag_list = ["spesa", "cinema", "ristorante", "bollette", "affitto", "auto"];

  void generateFakeData({
    int aggregates_num = 10,
    int elements_num = 10
  }) async {

    var rng = Random();
    var date = DateTime.now();

    for(int i = 0; i < aggregates_num; i++){

      //print("inserrting element " + i.toString());
      //await inspectDatabase();

      List<Element> element_list = [];
      double totCost = 0.0;

      for(int j = 0; j < elements_num; j++){

        final newNum = rng.nextInt(5);
        final newCost = rng.nextDouble()*100.0;

        totCost += newNum * newCost;

        element_list.add(
          Element(
            num: newNum,
            cost: newCost,
            name: "elem_" + j.toString()
          )
        );
      }

      await insertAggregate(
        Aggregate(
          date: date.millisecondsSinceEpoch,
          tag: tag_list[rng.nextInt(tag_list.length)],
          total_cost: totCost
        ), element_list
      );

      date = date.add(const Duration(days: 1));
    }
  }

  Future inspectDatabase() async {

    final tagList = await DbTagMng.instance.readAll();
    final aggregateList = await DbAggregateMng.instance.readAll();
    final elementList = await DbElementMng.instance.readAll();

    print("##################################################");
    print("## TAGS:");
    tagList.forEach((element) {print(element);});
    print("\n##################################################");
    print("## AGGREGATES:");
    aggregateList.forEach((element) {print(element);});
    print("\n##################################################");
    print("## ELEMENTS:");
    elementList.forEach((element) {print(element);});
    print("##################################################\n");

  }

}