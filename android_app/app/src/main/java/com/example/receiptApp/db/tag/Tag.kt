package com.example.receiptApp.db.tag

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.receiptApp.db.aggregate.Aggregate
import com.example.receiptApp.db.element.Element

@Entity(tableName = "tag")
data class Tag(
    @PrimaryKey(autoGenerate = true)
    var tag_id: Long = -1,

    var tag_name: String? = null,

    var aggregate: Boolean? = null
)