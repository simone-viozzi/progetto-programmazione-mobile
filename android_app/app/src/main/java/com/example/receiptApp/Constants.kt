package com.example.receiptApp

import android.content.res.Resources
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout


val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()


val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()


fun View.changeVisibility(visibility: Int) {

    this.visibility = visibility

//    val motionLayout = parent as MotionLayout
//    motionLayout.constraintSetIds.forEach {
//        val constraintSet = motionLayout.getConstraintSet(it) ?: return@forEach
//        constraintSet.setVisibility(this.id, visibility)
//        constraintSet.applyTo(motionLayout)
//    }
}

const val DATABASE_NAME = "recipe-db"
const val THUMBNAIL_SIZE = 200
const val SHARED_PREF_DASHBOARD = "SharedPrefDashboard"
