package com.example.roomdbtest

import android.content.Context
import android.telecom.Call
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = arrayOf(MyColor::class), version = 1)
abstract class ColorDb: RoomDatabase()
{
    abstract fun colorDao(): ColorDao

    companion object {
        @Volatile
        private var INSTANCE: ColorDb? = null

        fun getDatabase(context: Context,
                        scope: CoroutineScope): ColorDb {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ColorDb::class.java,
                    "word_database"
                ).fallbackToDestructiveMigration()
                    .addCallback(ColorDbCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

    private class ColorDbCallback(
        private val scope: CoroutineScope,
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase)
        {
            super.onCreate(db)
            INSTANCE.let { database ->

                scope.launch{
                    val colorDao = database?.colorDao()

                    // Delete all content here.
                    colorDao?.deleteAll()
                }
            }
        }
    }
}