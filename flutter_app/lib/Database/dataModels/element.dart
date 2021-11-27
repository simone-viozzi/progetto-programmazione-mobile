class Element{

  final int elem_id;
  final int aggregate_id;
  String name;
  final int num;
  int parent_tag_id;
  final int elem_tag_id;
  final double cost;
  String parent_tag;
  String elem_tag;

  Element({
    required this.elem_id,
    required this.aggregate_id,
    required this.num,
    required this.elem_tag_id,
    required this.cost,
    this.name = "",
    this.parent_tag_id = -1,
    this.parent_tag = "",
    this.elem_tag = ""
  });

  Map<String, dynamic> toMap() {
    return {
      'elem_id': elem_id,
      'aggregate_id': aggregate_id,
      'num': num,
      'elem_tag_id': elem_tag_id,
      'cost': cost,
      'name': name,
      'parent_tag_id': parent_tag_id
    };
  }

  @override
  String toString(){
    return 'Element{ elem_id: $elem_id, aggregate_id: $aggregate_id, num: $num, elem_tag_id: $elem_tag_id, cost: $cost, name: $name, parent_tag_id: $parent_tag_id}';
  }
}
