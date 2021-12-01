import 'db_aggregate_manager.dart';
import 'db_element_manager.dart';
import 'db_tag_manager.dart';
import 'dataModels/element.dart';
import 'dataModels/aggregate.dart';
import 'dataModels/tag.dart';

class dbRepository{

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
        Tag new_tag = Tag(
          tag_name: aggregate.tag
        );
        aggregate.tag_id = await DbTagMng.instance.insert(new_tag);
      }
    }

    int id = await DbAggregateMng.instance.insert(aggregate);
    for(final e in elements){e.aggregate_id = id;}
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

  Future<Map<Aggregate, List<Element>>> getAllAggregates() async
  {

    final Map<Aggregate, List<Element>> result = {};

    List<Aggregate> aggregates = await DbAggregateMng.instance.readAll();

    for (var aggregate in aggregates) {
      result[aggregate] = await DbElementMng.instance.readByAggrId(aggregate.id!);
    }

    return result;
  }

  ///////////////////////////////////////////////////////////////////////
  //// DELETE

  void deleteAggregateById(int aggr_id) async {

    final aggregate = await DbAggregateMng.instance.read(aggr_id);

    if(aggregate.tag_id != null){
      // if the aggregate has a tag, shuld be verified if there is another aggregate associated
      // if this one is the only tag associated the tag will be eleminated
      if(await DbAggregateMng.instance.countByTagId(aggregate.tag_id!) <= 1){
        // if there is only this aggregate attached to the tag delete the tag
        DbTagMng.instance.delete(aggregate.tag_id!);
      }
    }

    final elements = await DbElementMng.instance.readByAggrId(aggr_id);
    for( var elem in elements){
      DbElementMng.instance.delete(elem.elem_id!);
    }

    DbAggregateMng.instance.delete(aggregate.id!);
  }

  void deleteAll(){
    DbAggregateMng.instance.deleteAll();
    DbElementMng.instance.deleteAll();
    DbTagMng.instance.deleteAll();
  }

}