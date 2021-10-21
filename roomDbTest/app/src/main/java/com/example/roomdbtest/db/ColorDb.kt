package com.example.roomdbtest.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope

/**
 * Color db -> this get initialized in the App
 *
 * @constructor Create empty Color db
 */
@Database(entities = arrayOf(MyColor::class), version = 1)
abstract class ColorDb: RoomDatabase()
{
    abstract fun colorDao(): ColorDao

    companion object {
        @Volatile
        private var INSTANCE: ColorDb? = null

        fun getDatabase(context: Context,
                        scope: CoroutineScope): ColorDb
        {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ColorDb::class.java,
                    "word_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }


}