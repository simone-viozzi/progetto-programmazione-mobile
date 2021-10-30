package com.example.receiptApp

import android.app.Application
import android.text.Editable
import com.example.receiptApp.db.AppDatabase
import com.example.receiptApp.sources.GalleryImages
import com.example.receiptApp.sources.GalleryImagesPaginated
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import timber.log.Timber


fun String.toEditable(): Editable = Editable.Factory().newEditable(this)


class App : Application()
{
    val database by lazy { AppDatabase.getInstance(this) }

    // TODO maybe maybe it would be better to pass galleryImagesPaginated into a repository
    private val galleryImages by lazy { GalleryImages(contentResolver) }

    val galleryImagesPaginated by lazy { GalleryImagesPaginated(galleryImages) }


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