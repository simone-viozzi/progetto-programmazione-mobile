class Element{

  int? elem_id;
  int? aggregate_id;
  String name;
  final int num;
  int parent_tag_id;
  final double cost;
  String parent_tag;

  Element({
    this.elem_id = null,
    this.aggregate_id = null,
    required this.num,
    required this.cost,
    this.name = "",
    this.parent_tag_id = -1,
    this.parent_tag = "",
  });

  Map<String, dynamic> toMap() {
    return {
      'elem_id': elem_id,
      'aggregate_id': aggregate_id,
      'num': num,
      'cost': cost,
      'name': name
    };
  }

  static Element fromMap(Map<String, dynamic> map){
    return Element(
        elem_id: map['elem_id'],
        aggregate_id: map['aggregate_id'],
        num: map['num'],
        cost: map['cost'],
        name: map['name']
    );
  }

  @override
  String toString(){
    return 'Element{ elem_id: $elem_id, aggregate_id: $aggregate_id, num: $num, cost: $cost, name: $name, parent_tag_id: $parent_tag_id}';
  }
}
