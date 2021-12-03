



class EditDataModel {}

class HasIndex {
  int index = 0;
}

class AggregateDataModel extends EditDataModel implements HasIndex {
  @override
  int index;
  DateTime date;
  String tag;
  double totalCost;

  AggregateDataModel({
    this.index = 0,
    required this.date,
    required this.tag,
    this.totalCost = 0,
  });

  @override
  String toString() {
    return "AggregateDataModel -> {index: $index; date: $date; tag: $tag;}";
  }
}

class ElementDataModel extends EditDataModel implements HasIndex {
  @override
  int index;
  String name;
  double cost;
  int num;

  ElementDataModel(
      {this.index = 0,
        required this.name,
        required this.cost,
        required this.num});

  @override
  String toString() {
    return "ElementDataModel -> {index: $index; name: $name; cost: $cost; num: $num;}";
  }
}