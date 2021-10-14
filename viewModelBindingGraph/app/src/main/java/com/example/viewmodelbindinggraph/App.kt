package com.example.viewmodelbindinggraph

import android.app.Application
import androidx.viewbinding.BuildConfig
import timber.log.Timber;



class App: Application()
{
    override fun onCreate()
    {
        super.onCreate()

        if (BuildConfig.DEBUG || Constants.DEBUG)
        {
            Timber.plant(Timber.DebugTree())
        }


    }
}