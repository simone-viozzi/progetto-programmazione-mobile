package com.example.receiptApp

import android.content.res.Resources


val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()


val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

const val DATABASE_NAME = "recipe-db"
const val THUMBNAIL_SIZE = 200
const val SHARED_PREF_DASHBOARD = "SharedPrefDashboard"
