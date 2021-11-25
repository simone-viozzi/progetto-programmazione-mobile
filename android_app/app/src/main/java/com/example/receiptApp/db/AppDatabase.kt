package com.example.receiptApp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.receiptApp.DATABASE_NAME
import com.example.receiptApp.db.aggregate.Aggregate
import com.example.receiptApp.db.aggregate.PublicAggregateDao
import com.example.receiptApp.db.element.Element
import com.example.receiptApp.db.element.PublicElementsDao
import com.example.receiptApp.db.tag.Tag
import com.example.receiptApp.db.tag.TagsDao


@Database(entities = [Aggregate::class, Element::class, Tag::class], version = 1, exportSchema = false)
// to auto-convert the values in and out of the database you need to specify the class responsible here
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase()
{
    // all the daos need to be declared here
    abstract fun aggregateDao(): PublicAggregateDao
    abstract fun elementsDao(): PublicElementsDao
    abstract fun tagsDao(): TagsDao

    companion object
    {
        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase
        {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase
        {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}