package com.example.receiptApp.pages.add

import android.text.format.DateFormat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber


class AddViewModel : ViewModel()
{
    val rvList = MutableLiveData<List<AddDataModel>>()

    var textEditCallback: ((el: AddDataModel) -> (Unit)) = { el ->

        if (el is AddDataModel.SingleElement)
        {
            val oldEl = rvList.value?.get(el.id) as AddDataModel.SingleElement

            if (el.name != null && el.name != "") oldEl.name = el.name
            if (el.tag != null && el.tag != "") oldEl.tag = el.tag
            if (el.num != null) oldEl.num = el.num
            if (el.cost != null) oldEl.cost = el.cost

            val newList = rvList.value?.toMutableList().also { it?.set(el.id, oldEl) }

            if (el.id == getLastId(false) - 1)
            {
                rvList.value = newList?.plus(listOf(initSingleElement()))
            }
            else
            {
                rvList.value = newList!!
            }
        }
        else if (el is AddDataModel.Header)
        {
            val oldEl = rvList.value?.get(0) as AddDataModel.Header

            if (el.date != null && el.date != "") oldEl.date = el.date
            if (el.tag != null && el.tag != "") oldEl.tag = el.tag

            rvList.value = rvList.value?.toMutableList().also { it?.set(0, oldEl) }
        }
    }

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
            val id = lastId++
            Timber.d("the generated id is -> $id")
            return id
        }
        return lastId
    }

    init
    {
        rvList.value = initRvList()
    }

    private fun initRvList(): List<AddDataModel>
    {
        val singleElement = AddDataModel.SingleElement()
        singleElement.id = getLastId()

        return listOf(AddDataModel.Header(), singleElement)
    }

    private fun initSingleElement(): AddDataModel.SingleElement
    {
        val singleElement = AddDataModel.SingleElement()
        singleElement.id = getLastId()
        return singleElement
    }
}