package com.example.receiptApp.db.tag

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag")
data class Tag(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    var tag_name: String,

    var aggregate: Boolean
)