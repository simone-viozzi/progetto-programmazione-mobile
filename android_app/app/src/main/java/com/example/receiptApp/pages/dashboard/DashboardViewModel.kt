package com.example.receiptApp.pages.dashboard

import androidx.lifecycle.*
import com.example.receiptApp.repository.DashboardRepository
import com.example.receiptApp.repository.DbRepository
import com.example.receiptApp.utils.StateStack
import com.example.receiptApp.repository.SharedPrefRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class HomeViewModel(
    private val sharedPrefRepository: SharedPrefRepository,
    private val dbRepository: DbRepository,
    private val dashboardRepository: DashboardRepository

    ) : ViewModel()
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


    init
    {
        homeStateStack.push(HomeState.NoState)

        _homeState.value = HomeState.NoState
        loadDashboard()
    }

    fun setEditMode()
    {
        if (homeStateStack.peek() == HomeState.EditMode) return

        homeStateStack.push(HomeState.EditMode)
        Timber.e("homeStateStack -> $homeStateStack")

        _homeState.value = HomeState.EditMode
    }


    fun setStoreMode()
    {
        homeStateStack.push(HomeState.StoreMode)
        Timber.e("homeStateStack -> $homeStateStack")

        _homeState.value = HomeState.StoreMode

        loadStore()
    }

    fun saveDashboard() = viewModelScope.launch {

        _dashboard.value.let {
            if (!it.isNullOrEmpty())
            {
                dashboardRepository.saveDashboard(it)

                homeStateStack.clear()
                homeStateStack.push(HomeState.NormalMode)
                Timber.e("homeStateStack -> $homeStateStack")

                _homeState.value = HomeState.NormalMode
            }
            else
            {
                homeStateStack.push(HomeState.EmptyDashMode)
                Timber.e("homeStateStack -> $homeStateStack")

                _homeState.value = HomeState.EmptyDashMode
            }
        }
    }

    private fun loadDashboard() = viewModelScope.launch {

        val list = dashboardRepository.loadDashboard()

        if (list.isNotEmpty())
        {
            homeStateStack.push(HomeState.NormalMode)
            Timber.e("homeStateStack -> $homeStateStack")

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


    private fun loadStore() = viewModelScope.launch {

        _store.value = dashboardRepository.loadStore(_dashboard.value)

    }


    fun addToDashboard(element: DashboardDataModel)
    {
        homeStateStack.clear()
        homeStateStack.push(HomeState.EditMode)
        Timber.e("homeStateStack -> $homeStateStack")

        dashboardRepository.notifyAddToDash(element)
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

    fun clearDashboard()
    {
        homeStateStack.clear()
        homeStateStack.push(HomeState.EmptyDashMode)
        Timber.e("homeStateStack -> $homeStateStack")

        sharedPrefRepository.clearDashboard()
        _dashboard.value = emptyList()
        _homeState.value = HomeState.EmptyDashMode
    }

    fun clearDb() = viewModelScope.launch {
        dbRepository.clearDb()
    }

    fun removeItemFromDashBoard(id: Int)
    {
        _dashboard.value = _dashboard.value?.toMutableList()?.also {
            dashboardRepository.notifyRemoveFromDash(it[id])
            it.removeAt(id)
        }
    }

}


class HomeViewModelFactory(
    private val sharedPrefRepository: SharedPrefRepository,
    private val dbRepository: DbRepository,
    private val dashboardRepository: DashboardRepository
    ) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java))
        {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(
                sharedPrefRepository,
                dbRepository,
                dashboardRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

