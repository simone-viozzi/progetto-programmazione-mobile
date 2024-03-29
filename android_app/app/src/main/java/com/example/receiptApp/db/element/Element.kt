package com.example.receiptApp.db.element

import androidx.room.*
import com.example.receiptApp.db.aggregate.Aggregate
import com.example.receiptApp.db.tag.Tag

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
        )
    ],
    indices = [Index("elem_id")]
)
data class Element @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true)
    var elem_id: Long? = null,

    var aggregate_id: Long? = null,

    var name: String? = null,

    var num: Long = 0L, // ESSENTIAL

    var parent_tag_id: Long? = null,

    var elem_tag_id: Long? = null,

    var cost: Float = 0.0f, // ESSENTIAL
){
    // fields not used in the table

    @Ignore
    var parent_tag: String? = null

    @Ignore
    var elem_tag: String? = null
}
