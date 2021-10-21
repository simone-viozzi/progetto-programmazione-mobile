package com.example.roomdbtest

import android.app.Application
import com.example.roomdbtest.db.ColorDb
import com.example.roomdbtest.db.ColorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

class App: Application()
{
    // here is inizialized the repository, which need the database, which need the CoroutineScope
    // the repository and the database are inizialized with lazy, so they will be initialized only when they are needed
    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { ColorDb.getDatabase(this, applicationScope) }
    val repository by lazy { ColorRepository(database.colorDao()) }


    override fun onCreate()
    {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}