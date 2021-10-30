package com.example.cursoradapter

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

class App : Application()
{
    private val applicationScope = CoroutineScope(SupervisorJob())

    private val dataSource by lazy { ImgDataSource(contentResolver) }

    val dataSourcePagin by lazy { ImgsPaginator(dataSource) }


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