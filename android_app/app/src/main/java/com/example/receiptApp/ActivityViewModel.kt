package com.example.receiptApp

import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivityViewModel: ViewModel()
{
    val fabOnClickListener: MutableLiveData<View.OnClickListener> = MutableLiveData<View.OnClickListener>()
    val bABOnMenuItemClickListener: MutableLiveData<Toolbar.OnMenuItemClickListener> = MutableLiveData<Toolbar.OnMenuItemClickListener>()

    fun setFabOnClickListener(clickListener: View.OnClickListener)
    {
        fabOnClickListener.value = clickListener
    }

    fun setBABOnMenuItemClickListener(clickListener: Toolbar.OnMenuItemClickListener)
    {
        bABOnMenuItemClickListener.value = clickListener
    }
}