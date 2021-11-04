package com.example.receiptApp.pages.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.receiptApp.repository.SharedPrefRepository

class HomeViewModel(private val repository: SharedPrefRepository) : ViewModel()
{
    private val _list: MutableLiveData<List<DashboardDataModel>> = MutableLiveData<List<DashboardDataModel>>()
    val list: LiveData<List<DashboardDataModel>> = _list

    init
    {
        val list: MutableList<DashboardDataModel> = (1..20).map { DashboardDataModel.Test(it) }.toMutableList()

        list += (1..5).map { DashboardDataModel.TestBig(it) }

        _list.value = list.also { it.shuffle() }
    }

    val onItemMove: ((List<DashboardDataModel>) -> Unit) = {
        _list.value = it
    }


    fun save()
    {
        val needToSave: MutableMap<Int, DashboardElement> = mutableMapOf()

        _list.value?.let {

            it.forEachIndexed { i, element ->
                needToSave[i] = element
            }
        }

        repository.writeDashboard(needToSave)

    }

    fun load()
    {
        val dashboard: Map<Int, DashboardElement> = repository.readDashboard()

        val list: MutableList<DashboardDataModel> = mutableListOf()

        dashboard.entries.forEach {
            when (it.value)
            {
                is DashboardDataModel.Test -> list.add(it.value as DashboardDataModel.Test)
                is DashboardDataModel.TestBig -> list.add(it.value as DashboardDataModel.TestBig)
            }
        }
        _list.value = list
    }
}


class HomeViewModelFactory(private val repository: SharedPrefRepository) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java))
        {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

