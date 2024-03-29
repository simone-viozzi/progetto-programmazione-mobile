package com.example.receiptApp.db.aggregate

import android.location.Location
import android.net.Uri
import androidx.room.*
import com.example.receiptApp.db.tag.Tag
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
    indices = [Index("id")]
)
data class Aggregate @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,

    var tag_id: Long? = null,

    var date: Date? = null, // ESSENTIAL

    var location: Location? = null,

    var attachment: Uri? = null,

    var total_cost: Float = 0.0f,
){
    // fields not used in the table

    @Ignore
    var tag: String? = null
}
