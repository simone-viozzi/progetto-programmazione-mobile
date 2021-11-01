package com.example.receiptApp.pages.add

import android.graphics.Bitmap


/**
 * those are the data structures that will be displayed in the recyclerview
 */

sealed class AddDataModel {
    data class Header(
        var vId: Int = 0,
        var tag: String? = null,
        var str_date: String? = null,
        var thumbnail: Bitmap? = null
    ) : AddDataModel()

    data class SingleElement(
        var vId: Int = -1,
        var name: String? = null,
        var num: Int? = null,
        var elem_tag: String? = null,
        var cost: Double? = null,
    ) : AddDataModel()
}


open class AddDataModel2(){

}
