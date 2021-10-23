package com.example.receiptApp.db.aggregate

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.receiptApp.db.Converters
import java.util.*

@Entity
data class Aggregate(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    var tag_id: Long = 0L,

    @TypeConverters(Converters::class)
    var date: Calendar = Calendar.getInstance()
)
