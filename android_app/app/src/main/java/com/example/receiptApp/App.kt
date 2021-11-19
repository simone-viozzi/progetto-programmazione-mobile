package com.example.receiptApp

import android.app.Application
import android.text.Editable
import com.example.receiptApp.db.AppDatabase
import com.example.receiptApp.db.aggregate.AggregatesDao
import com.example.receiptApp.db.element.ElementsDao
import com.example.receiptApp.repository.*
import timber.log.Timber



class App : Application()
{
    val database by lazy { AppDatabase.getInstance(this) }

    val dbRepository by lazy {
        DbRepository(
            database.aggregateDao(),
            database.elementsDao(),
            database.tagsDao()
        )
    }

    val attachmentRepository by lazy { AttachmentRepository(this) }

    val sharedPrefRepository by lazy { SharedPrefRepository(this) }

    val graphsRepository by lazy { GraphsRepository(this, dbRepository)}

    val archiveRepository by lazy { ArchiveRepository(this, dbRepository, attachmentRepository) }

    override fun onCreate()
    {
        super.onCreate()

        Timber.plant(object : Timber.DebugTree()
        {
            override fun createStackElementTag(element: StackTraceElement): String
            {
                return (super.createStackElementTag(element)
                        + "->" + element.methodName
                        + ":" + element.lineNumber)
            }
        })
    }
}