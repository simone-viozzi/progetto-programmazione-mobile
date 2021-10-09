package com.example.viewmodelapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    private val _total = MutableLiveData<Int>(0)
    val total: LiveData<Int>
        get() = _total

    fun increaseNum(){ //OK
        _total.value = _total.value!! + 1
    }



}