package com.example.receiptApp.pages.add


/**
 * those are the data structures that will be displayed in the recyclerview
 */
sealed class AddDataModel
{
    data class Header(
        var id: Int = 0,
        var tag: String? = null,
        var date: String? = null,
    ) : AddDataModel()

    data class SingleElement(
        var id: Int = -1,
        var name: String? = null,
        var num: Int? = null,
        var tag: String? = null,
        var cost: Double? = null,
    ) : AddDataModel()
}