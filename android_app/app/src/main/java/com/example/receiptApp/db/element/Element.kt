package com.example.receiptApp.db.element

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.receiptApp.db.aggregate.Aggregate

/**
 * Element
 *
 * Clear explanation of relation between Entity and how to use it
 * link: https://www.tutorialguruji.com/android/how-to-insert-entities-with-a-one-to-many-relationship-in-room/
 *
 * NOTE:    if this relation is established no elements can be added with
 *          the field aggregate_id that not correspond to any aggregate
 *
 * @property elem_id
 * @property aggregate_id
 * @property name
 * @property num
 * @property parent_tag_id
 * @property elem_tag_id
 * @property cost
 * @property cost_n
 *
 * @constructor Create empty Element
 */

@Entity(
    tableName = "element",
    foreignKeys = [ForeignKey(
        entity = Aggregate::class,
        parentColumns = ["id"],
        childColumns = ["aggregate_id"],
        onDelete = CASCADE //A "CASCADE" action propagates the delete or update operation on the parent key to each dependent child
    )],
    indices = [Index("elem_id")]
)
data class Element(
    @PrimaryKey(autoGenerate = true)
    var elem_id: Long = 0L,

    var aggregate_id: Long = 0L,

    var name: String,

    var num: Long = 0L,

    var parent_tag_id: Long = 0L,

    var elem_tag_id: Long = 0L,

    var cost: Float = 0.0f,
)
