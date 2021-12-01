class Tag{

  int? tag_id = null;
  final String tag_name;

  Tag({
    required this.tag_name,
    this.tag_id
  });

  Map<String, dynamic> toMap() {
    return {
      'tag_id': tag_id,
      'tag_name': tag_name
    };
  }

  static Tag fromMap(Map<String, dynamic> map){
    return Tag(
      tag_id: map['tag_id'],
      tag_name: map['tag_name']
    );
  }

  @override
  String toString(){
    return 'Tag{tag_id: $tag_id, tag_name: $tag_name}';
  }
}