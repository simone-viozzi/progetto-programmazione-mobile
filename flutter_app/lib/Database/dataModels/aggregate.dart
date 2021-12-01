
class Aggregate{

  int? id;
  int? tag_id;
  final int date;
  String attachment;
  final double total_cost;
  String tag;

  Aggregate({
    this.id = null,
    required this.date,
    required this.total_cost,
    this.tag_id,
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

  static Aggregate fromMap(Map<String, dynamic> map){
    return Aggregate(
      id: map['id'],
      date: map['date'],
      total_cost: map['total_cost'],
      tag_id: map['tag_id'],
      attachment: map['attachment']
    );
  }

  @override
  String toString(){
    return 'Aggregate{id: $id, tag_id: $tag_id, date: $date, attachment: $attachment, total_cost: $total_cost }';
  }
}