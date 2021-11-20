package com.example.receiptApp

import android.content.res.Resources
import android.text.Editable
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import kotlin.math.round


fun String.toEditable(): Editable = Editable.Factory().newEditable(this)


private fun Double?.toFloatOrNull(): Float = this?.toFloat() ?: 0f

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

fun Float.round(decimals: Int): Float {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return (round(this * multiplier) / multiplier).toFloat()
}

fun <T> Array<T>.mapInPlace(transform: (T) -> T) {
    for (i in this.indices) {
        this[i] = transform(this[i])
    }
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()


val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()


const val DATABASE_NAME = "recipe-db"
const val THUMBNAIL_SIZE = 200
const val SHARED_PREF_DASHBOARD = "SharedPrefDashboard"

const val DATE_PICKER_TAG = "DatePickerTag"
