package com.example.receiptApp.db.aggregate

import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.receiptApp.db.Converters
import com.example.receiptApp.pages.add.AddDataModel
import com.example.receiptApp.pages.add.AddDataModel2
import java.util.*

/**
 * Aggregate
 *
 *
 *
 * @property id
 * @property tag_id
 * @property date
 * @property location
 * @property attachment
 * @property total_cost
 * @constructor Create empty Aggregate
 */

@Entity(tableName = "aggregate")
data class Aggregate(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    var tag_id: Long = 0L,

    var date: Date? = null,

    var location: Location? = null,

    var attachment: Uri? = null,

    var total_cost: Float = 0.0f,

    // fields not used in the table
    @Ignore
    var thumbnail: Bitmap? = null,

    @Ignore
    var tag: String? = null,

    @Ignore
    var vId: Int? = null,

    @Ignore
    var str_date: String? = null,

    ): AddDataModel2()
