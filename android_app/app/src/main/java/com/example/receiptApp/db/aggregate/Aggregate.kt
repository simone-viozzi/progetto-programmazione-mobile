package com.example.receiptApp.db.aggregate

import android.location.Location
import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.receiptApp.db.Converters
import java.util.*

@Entity(tableName = "aggregate")
data class Aggregate(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    var tag_id: Long = 0L,

    var date: Date,

    var location: Location? = null,

    var attachment: Uri? = null,

    var total_cost: Long = 0L,
)
