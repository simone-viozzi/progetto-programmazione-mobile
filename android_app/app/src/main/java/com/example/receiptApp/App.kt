package com.example.receiptApp

import android.app.Application
import com.example.receiptApp.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

class App : Application()
{
    val database by lazy { AppDatabase.getInstance(this) }
    // TODO implement repositories
    // val repository by lazy { ColorRepository(database.colorDao()) }


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