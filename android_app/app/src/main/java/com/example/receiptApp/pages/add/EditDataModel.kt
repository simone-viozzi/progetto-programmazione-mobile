package com.example.receiptApp.pages.add

import android.graphics.Bitmap


/**
 * those are the data structures that will be displayed in the recyclerview
 */

sealed class EditDataModel {
    data class Aggregate(
        var vId: Int = 0,
        var tag: String? = null,
        var str_date: String? = null,
        var thumbnail: Bitmap? = null,
        var dbId: Long? = null
    ) : EditDataModel()

    data class Element(
        var vId: Int = -1,
        var name: String? = null,
        var num: Int? = null,
        var elem_tag: String? = null,
        var cost: Double? = null,
        var dbId: Long? = null
    ) : EditDataModel()
}

/**
 * to wrap some code and functionality the update is done here
 */
fun EditDataModel.update(new: EditDataModel): Boolean
{
    // right now update is useless, maybe will used in the future
    var update = false
    if (this is EditDataModel.Element && new is EditDataModel.Element)
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
