package com.example.receiptApp

import android.app.Application
import android.text.Editable
import com.example.receiptApp.db.AppDatabase
import timber.log.Timber

fun String.toEditable(): Editable = Editable.Factory().newEditable(this)

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