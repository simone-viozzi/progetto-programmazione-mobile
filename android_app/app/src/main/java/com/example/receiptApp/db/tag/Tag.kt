package com.example.receiptApp.db.tag

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tag(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
)