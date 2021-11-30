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
    int id = await DbAggregateMng.instance.insert(aggregate);

  }

  ///////////////////////////////////////////////////////////////////////
  //// GET

  Future<Map<Aggregate, List<Element>>> getAggregateById(int id) async
  {

    Aggregate aggregate = await DbAggregateMng.instance.read(id);
    List<Element> elements = await DbElementMng.instance.readByAggrId(id);

    return {
      aggregate: elements
    };
  }

  Future<Map<Aggregate, List<Element>>> getAllAggregates() async
  {

    final Map<Aggregate, List<Element>> result = {};

    List<Aggregate> aggregates = await DbAggregateMng.instance.readAll();

    aggregates.forEach((aggregate) async => {
      result[aggregate] = await DbElementMng.instance.readByAggrId(aggregate.id);
    });

    return result;
  }

  ///////////////////////////////////////////////////////////////////////
  //// DELETE

  void deleteAggregateById(Aggregate aggregate){

  }

  void deleteAll(){

  }

}