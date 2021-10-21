package com.example.viewmodelbindinggraph.hello

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class HelloViewModel : ViewModel()
{
    // the double declaration is needed to espone to the outside the redOnly live data,
    // and use the MutableLiveData internally
    private val _helloName = MutableLiveData<String>("world")
    val helloName: LiveData<String> = _helloName

    fun setName(name: String) {
        _helloName.value = "Hello $name!"
    }
}