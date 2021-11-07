package com.example.receiptApp.pages.home

import androidx.lifecycle.*
import com.example.receiptApp.utils.StateStack
import com.example.receiptApp.repository.SharedPrefRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class HomeViewModel(private val repository: SharedPrefRepository) : ViewModel()
{
    private val _dashboard: MutableLiveData<List<DashboardDataModel>> = MutableLiveData<List<DashboardDataModel>>()
    val dashboard: LiveData<List<DashboardDataModel>> = _dashboard

    private val _store: MutableLiveData<List<DashboardDataModel>> = MutableLiveData<List<DashboardDataModel>>()
    val store: LiveData<List<DashboardDataModel>> = _store

    sealed class HomeState
    {
        object NoState: HomeState()
        object EmptyDashMode : HomeState()
        object NormalMode : HomeState()
        object EditMode : HomeState()
        object StoreMode : HomeState()

        override fun toString(): String = this.javaClass.name.replaceBeforeLast("$", "")
    }

    private val homeStateStack = StateStack<HomeState>()
    private val _homeState: MutableLiveData<HomeState> = MutableLiveData<HomeState>()
    val homeState: LiveData<HomeState> = _homeState

    private object Id
    {
        var lastId: Int = 0
        fun getId() = ++lastId
    }

    init
    {
        homeStateStack.push(HomeState.NoState)

        _homeState.value = HomeState.NoState
        loadDashboard()
    }

    val onItemMove: (List<DashboardDataModel>) -> Unit = {
        _dashboard.value = it
    }


    fun setEditMode()
    {
        homeStateStack.push(HomeState.EditMode)
        Timber.e("homeStateStack -> $homeStateStack")

        _homeState.value = HomeState.EditMode

        if (store.value == null)
        {
            loadStore()
        }
    }


    fun setStoreMode()
    {
        homeStateStack.push(HomeState.StoreMode)
        Timber.e("homeStateStack -> $homeStateStack")

        _homeState.value = HomeState.StoreMode

        if (store.value == null)
        {
            loadStore()
        }
    }


    private fun loadStore() = viewModelScope.launch {

        val list = (0..20).map {
            arrayOf(
                DashboardDataModel.Test(id = it),
                DashboardDataModel.TestBig(id = it),
                DashboardDataModel.Square(id = it)
            ).random()
        }

        _store.value = list
    }


    fun saveDashboard() = viewModelScope.launch {
        val needToSave: MutableMap<Int, DashboardDataModel> = mutableMapOf()

        _dashboard.value?.let {
            it.forEachIndexed { i, element ->
                needToSave[i] = element
            }
        }

        repository.writeDashboard(needToSave)

        homeStateStack.clear()
        homeStateStack.push(HomeState.NormalMode)
        Timber.e("homeStateStack -> $homeStateStack")

        _homeState.value = HomeState.NormalMode
    }

    private fun loadDashboard() = viewModelScope.launch {

        val dashboard: Map<Int, DashboardDataModel> = repository.readDashboard()

        val list: MutableList<DashboardDataModel> = mutableListOf()

        dashboard.entries.forEach {
            when (it.value)
            {
                is DashboardDataModel.Test -> list.add(it.value)
                is DashboardDataModel.TestBig -> list.add(it.value)
                is DashboardDataModel.Square -> list.add(it.value)
            }
        }

        if (list.isNotEmpty())
        {
            homeStateStack.push(HomeState.NormalMode)
            Timber.e("homeStateStack -> $homeStateStack")

            Id.lastId = list.size
            _dashboard.value = list
            _homeState.value = HomeState.NormalMode
        } else
        {
            homeStateStack.push(HomeState.EmptyDashMode)
            Timber.e("homeStateStack -> $homeStateStack")

            _dashboard.value = emptyList()
            _homeState.value = HomeState.EmptyDashMode
        }
    }

    fun addToDashboard(element: DashboardDataModel)
    {
        homeStateStack.clear()
        homeStateStack.push(HomeState.EditMode)
        Timber.e("homeStateStack -> $homeStateStack")

        element.id = Id.getId()
        _dashboard.value = _dashboard.value?.toMutableList()?.also { it.add(0, element) }
        _homeState.value = HomeState.EditMode
    }

    fun swapItems(from: Int, to: Int)
    {
        _dashboard.value?.let {
            val list = it.toMutableList()
            Collections.swap(list, from, to)
            _dashboard.value = list
        }
    }

    fun clearDashboard()
    {
        homeStateStack.clear()
        homeStateStack.push(HomeState.EmptyDashMode)
        Timber.e("homeStateStack -> $homeStateStack")

        repository.clearDashboard()
        _dashboard.value = emptyList()
        Id.lastId = 0
        _homeState.value = HomeState.EmptyDashMode
    }

    fun goBackToPreviousState()
    {
        homeStateStack.peekPrevious()?.let {
            homeStateStack.push(it)
            _homeState.value = it
            Timber.e("homeStateStack -> $homeStateStack")
        }
    }

    fun getPreviousState(): HomeState
    {
        val state = homeStateStack.peekPrevious()

        Timber.e("state -> $state")
        Timber.e("homeStateStack -> $homeStateStack")

        return state ?: HomeState.NoState
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

