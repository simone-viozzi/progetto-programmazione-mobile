package com.example.roomdbtest

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.roomdbtest.db.ColorRepository
import com.example.roomdbtest.db.MyColor
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

/**
 * Color view model get initialized from App, for this simple app it is basically a wrapper for the
 * repository. here we launch the coroutines.
 *
 * @property repository
 * @constructor Create empty Color view model
 */
class ColorViewModel(private val repository: ColorRepository): ViewModel()
{
    val allColors: LiveData<List<MyColor>> = repository.allColors

    fun addColor(color: MyColor) = viewModelScope.launch {
        repository.insert(color)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun delete(color: MyColor) = viewModelScope.launch {
        repository.delete(color)
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