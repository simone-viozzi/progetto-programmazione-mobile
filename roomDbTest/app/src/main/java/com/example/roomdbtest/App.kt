package com.example.roomdbtest

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

class App: Application()
{
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { ColorDb.getDatabase(this, applicationScope) }
    val repository by lazy { ColorRepository(database.colorDao()) }

    override fun onCreate()
    {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}