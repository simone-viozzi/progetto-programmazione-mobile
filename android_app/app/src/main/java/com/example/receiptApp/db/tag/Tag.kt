package com.example.receiptApp.db.tag

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag")
data class Tag(
    @PrimaryKey(autoGenerate = true)
    var tag_id: Long? = null,

    var tag_name: String? = null,

    var aggregate: Boolean? = null
)