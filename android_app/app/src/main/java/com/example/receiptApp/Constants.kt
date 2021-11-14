package com.example.receiptApp

import android.content.res.Resources
import android.text.Editable
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout


fun String.toEditable(): Editable = Editable.Factory().newEditable(this)


private fun Double?.toFloatOrNull(): Float = this?.toFloat() ?: 0f


val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()


val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()


const val DATABASE_NAME = "recipe-db"
const val THUMBNAIL_SIZE = 200
const val SHARED_PREF_DASHBOARD = "SharedPrefDashboard"

const val DATE_PICKER_TAG = "DatePickerTag"
