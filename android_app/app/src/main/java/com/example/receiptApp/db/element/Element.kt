package com.example.receiptApp.db.element

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.receiptApp.db.aggregate.Aggregate


@Entity(
    tableName = "element",
    foreignKeys = [ForeignKey(
        entity = Aggregate::class,
        parentColumns = ["id"],
        childColumns = ["aggregate_id"],
        onDelete = CASCADE //A "CASCADE" action propagates the delete or update operation on the parent key to each dependent child
    )],
    indices = [Index("id")]
)
data class Element(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    var aggregate_id: Long = 0L,

    var name: String,

    var num: Long = 0L,

    var parent_tag_id: Long = 0L,

    var tag_id: Long = 0L,

    var cost: Float = 0.0f

)
