package com.example.receiptApp.db.element

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.example.receiptApp.db.aggregate.Aggregate
import com.example.receiptApp.db.tag.Tag
import com.google.gson.annotations.SerializedName

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
    foreignKeys = [
        ForeignKey(
        entity = Aggregate::class,
        parentColumns = ["id"],
        childColumns = ["aggregate_id"]
        ),
        ForeignKey(
            entity = Tag::class,
            parentColumns = ["tag_id"],
            childColumns = ["elem_tag_id"]
        ),
    ],
    indices = [Index("elem_id")]
)
data class Element @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true)
    var elem_id: Long? = null,

    var aggregate_id: Long? = null,

    var name: String? = null,

    var num: Long = 0L,

    var parent_tag_id: Long? = null,

    var elem_tag_id: Long? = null,

    var cost: Float = 0.0f,
){
    // fields not used in the table

    @Ignore
    var parent_tag: String? = null

    @Ignore
    var elem_tag: String? = null
}
