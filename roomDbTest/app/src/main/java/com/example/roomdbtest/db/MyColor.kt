package com.example.roomdbtest.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * this represents a single row of the database
 *
 * @property id
 * @property red
 * @property green
 * @property blue
 * @constructor Create empty My color
 */
@Entity
data class MyColor(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    var red: Int = 0,

    var green: Int = 0,

    var blue: Int = 0,
)
{
    companion object
    {
        /**
         * Create random color from a static context
         *
         * @return the color created
         */
        fun createRandomColor(): MyColor
        {
            val red = (0..255).random()
            val green = (0..255).random()
            val blue = (0..255).random()

            return MyColor(red = red, green = green, blue = blue)
        }
    }

    fun generateHex(): String
    {
        return String.format("#%02x%02x%02x", red, green, blue)
    }

}