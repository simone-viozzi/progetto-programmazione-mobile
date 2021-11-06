package com.example.receiptApp.pages.home

import androidx.lifecycle.*
import com.example.receiptApp.repository.SharedPrefRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: SharedPrefRepository) : ViewModel()
{
    private val _list: MutableLiveData<List<DashboardDataModel>> = MutableLiveData<List<DashboardDataModel>>()
    val list: LiveData<List<DashboardDataModel>> = _list

    private val _store: MutableLiveData<List<DashboardDataModel>> = MutableLiveData<List<DashboardDataModel>>()
    val store: LiveData<List<DashboardDataModel>> = _store

    sealed class HomeState
    {
        object NullState : HomeState()
        data class NormalMode(val list: List<DashboardDataModel>): HomeState()
        data class EditMode(val onItemMove: (List<DashboardDataModel>) -> Unit): HomeState()
        data class StoreMode(val store: List<DashboardDataModel>): HomeState()
    }

    private val _homeState: MutableLiveData<HomeState> = MutableLiveData<HomeState>()
    val homeState: LiveData<HomeState> = _homeState

    init
    {
        loadDashboard()
    }

    val onItemMove: (List<DashboardDataModel>) -> Unit = {
        _list.value = it
    }

    val setEditMode: () -> Unit = {
        _homeState.value = HomeState.EditMode(onItemMove)

        if (store.value == null)
        {
            loadStore()
        }
    }

    val setStoreMode: () -> Unit = {
        _homeState.value = HomeState.StoreMode(store.value!!)
    }

    private fun loadStore() = viewModelScope.launch {

        val list = (0..20).map {
            arrayOf (DashboardDataModel.Test(id=it), DashboardDataModel.TestBig(id=it)).random()
        }.toMutableList()

        _store.value = list
    }


    fun saveDashboard() = viewModelScope.launch {
        val needToSave: MutableMap<Int, DashboardElement> = mutableMapOf()

        _list.value?.let {

            it.forEachIndexed { i, element ->
                needToSave[i] = element
            }
        }

        repository.writeDashboard(needToSave)

        _homeState.value = HomeState.NormalMode(_list.value!!)
    }

    private fun loadDashboard() = viewModelScope.launch {

        val dashboard: Map<Int, DashboardElement> = repository.readDashboard()

        var list: MutableList<DashboardDataModel> = mutableListOf()

        dashboard.entries.forEach {
            when (it.value)
            {
                is DashboardDataModel.Test -> list.add(it.value as DashboardDataModel.Test)
                is DashboardDataModel.TestBig -> list.add(it.value as DashboardDataModel.TestBig)
            }
        }

        if (list.isEmpty()) {
            list = (0..20).map {
                arrayOf (DashboardDataModel.Test(id=it), DashboardDataModel.TestBig(id=it)).random()
            }.toMutableList()
        }

        _list.value = list
        _homeState.value = HomeState.NormalMode(list)
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

