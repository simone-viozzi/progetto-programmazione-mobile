package com.example.receiptApp.pages.archive

import android.graphics.Bitmap
import com.example.receiptApp.pages.add.AddDataModel

sealed class ArchiveDataModel {
    data class Aggregate(
        var id: Int = 0,
        var tag: String? = null,
        var str_date: String? = null,
        var thumbnail: Bitmap? = null,
        var tot_cost: Float? = null
    ): ArchiveDataModel()

    data class Element(
        var id: Int = -1,
        var name: String? = null,
        var num: Int? = null,
        var elem_tag: String? = null,
        var cost: Double? = null,
    ): ArchiveDataModel()
}