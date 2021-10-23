package com.example.receiptApp.db.element

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
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
