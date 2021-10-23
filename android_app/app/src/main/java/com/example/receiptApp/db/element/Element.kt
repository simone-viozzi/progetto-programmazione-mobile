package com.example.receiptApp.db.element

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Element(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
)
