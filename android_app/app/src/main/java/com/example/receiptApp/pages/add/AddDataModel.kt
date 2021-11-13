package com.example.receiptApp.pages.add

import android.graphics.Bitmap


/**
 * those are the data structures that will be displayed in the recyclerview
 */

sealed class AddDataModel {
    data class Aggregate(
        var vId: Int = 0,
        var tag: String? = null,
        var str_date: String? = null,
        var thumbnail: Bitmap? = null
    ) : AddDataModel()

    data class Element(
        var vId: Int = -1,
        var name: String? = null,
        var num: Int? = null,
        var elem_tag: String? = null,
        var cost: Double? = null,
    ) : AddDataModel()
}


fun AddDataModel.update(new: AddDataModel): Boolean
{
    var update = false
    if (this is AddDataModel.Element && new is AddDataModel.Element)
    {
        if (this.name != new.name && new.name != null) {
            this.name = new.name
            update = true
        }
        if (this.elem_tag != new.elem_tag && new.elem_tag != null) {
            this.elem_tag = new.elem_tag
            update = true
        }
        if (this.num != new.num && new.num != null) {
            this.num = new.num
            update = true
        }
        if (this.cost != new.cost && new.cost != null){
            this.cost = new.cost
            update = true
        }
    }
    return update
}
