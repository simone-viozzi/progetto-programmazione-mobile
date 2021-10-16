package com.example.roomdbtest

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ColorViewModel(private val repository: ColorRepository): ViewModel()
{
    val allColors = repository.allColors

    fun addColor(color: MyColor) = viewModelScope.launch {
        repository.insert(color)
    }

}

class ColorViewModelFactory(private val repository: ColorRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(ColorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ColorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}