package com.example.receiptApp

import android.content.Context
import android.content.res.Resources
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import kotlin.math.round

/*
 * in this file there are the extension functions used around the app and some constants
 */
fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.hideKeyboard() {
    view?.let {
        activity?.hideKeyboard(it)
    }
}

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
