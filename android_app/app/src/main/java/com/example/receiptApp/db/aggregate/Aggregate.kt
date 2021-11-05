package com.example.receiptApp.db.aggregate

import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import androidx.room.*
import com.example.receiptApp.db.Converters
import com.example.receiptApp.db.tag.Tag
import com.google.gson.annotations.SerializedName
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

@Entity(
    tableName = "aggregate",
    foreignKeys = [
        ForeignKey(
            entity = Tag::class,
            parentColumns = ["tag_id"],
            childColumns = ["tag_id"]
        ),
    ],
    indices = [Index("id")]
)
data class Aggregate @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Long = -1,

    var tag_id: Long? = null,

    var date: Date? = null,

    var location: Location? = null,

    var attachment: Uri? = null,

    var total_cost: Float = 0.0f,
){
    // fields not used in the table

    @Ignore
    var tag: String? = null
}
