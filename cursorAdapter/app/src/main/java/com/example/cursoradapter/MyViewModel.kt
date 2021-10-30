package com.example.cursoradapter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyViewModel(private val dataSourcePagin: ImgsPaginator) : ViewModel()
{

    val flow = Pager(PagingConfig(
        pageSize = 48
    )) { dataSourcePagin }.flow.cachedIn(viewModelScope)

}

class MyViewModelFactory(private val dataSourcePagin: ImgsPaginator) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(MyViewModel::class.java))
        {
            @Suppress("UNCHECKED_CAST")
            return MyViewModel(dataSourcePagin) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}