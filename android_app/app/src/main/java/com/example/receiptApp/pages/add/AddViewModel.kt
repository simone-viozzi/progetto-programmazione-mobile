package com.example.receiptApp.pages.add

import android.text.format.DateFormat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class AddViewModel : ViewModel()
{
    // the list observed by the recyclerview
    val rvList = MutableLiveData<List<AddDataModel>>()

    // the callback used by every element of the views in the textWatcher to update the corresponding element in the
    // view model, it pass an element with only the updated field and the id != null
    var textEditCallback: ((el: AddDataModel) -> (Unit)) = { el ->

        // need to separate the two types
        when (el)
        {
            is AddDataModel.Header ->
            {
                val oldEl = rvList.value?.get(0) as AddDataModel.Header

                // TODO (el... != "") in sill needed? need to test
                if (el.date != null && el.date != "") oldEl.date = el.date
                if (el.tag != null && el.tag != "") oldEl.tag = el.tag

                // TODO update the list only if there was a change!
                rvList.value = rvList.value?.toMutableList().also { it?.set(0, oldEl) }
            }

            is AddDataModel.SingleElement ->
            {
                val oldEl = rvList.value?.get(el.id) as AddDataModel.SingleElement

                // TODO (el... != "") in sill needed? need to test
                if (el.name != null && el.name != "") oldEl.name = el.name
                if (el.tag != null && el.tag != "") oldEl.tag = el.tag
                if (el.num != null) oldEl.num = el.num
                if (el.cost != null) oldEl.cost = el.cost

                val newList = rvList.value?.toMutableList().also { it?.set(el.id, oldEl) }

                if (el.id == getLastId(false) - 1)
                {
                    // TODO update the list only if there was a change!
                    rvList.value = newList?.plus(listOf(AddDataModel.SingleElement(id = getLastId())))
                } else
                {
                    // TODO update the list only if there was a change!
                    rvList.value = newList!!
                }
            }
        }
    }

    // this get called when the date picker return
    fun setDate(millis: Long)
    {
        val date = DateFormat.format("dd/MM/yyyy", millis).toString()
        rvList.value = rvList.value?.toMutableList().also { li ->
            val header = li?.get(0) as AddDataModel.Header
            li[0] = header.also { it.date = date }
        }
    }


    private var lastId = 1

    private fun getLastId(autoincrement: Boolean = true): Int
    {
        if (autoincrement)
        {
            return lastId++
        }
        return lastId
    }

    init
    {
        rvList.value = listOf(
            AddDataModel.Header(id = 0),
            AddDataModel.SingleElement(id = getLastId())
        )
    }
}