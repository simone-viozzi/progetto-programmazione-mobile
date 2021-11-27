
class Aggregate{

  final int id;
  int tag_id;
  final int date;
  String attachment;
  final double total_cost;
  String tag;

  Aggregate({
    required this.id,
    required this.date,
    required this.total_cost,
    this.tag_id = -1,
    this.attachment = "",
    this.tag = ""
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'tag_id': tag_id,
      'date': date,
      'attachment': attachment,
      'total_cost': total_cost
    };
  }

  @override
  String toString(){
    return 'Aggregate{id: $id, tag_id: $tag_id, date: $date, attachment: $attachment, total_cost: $total_cost }';
  }
}