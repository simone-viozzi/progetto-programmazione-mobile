package com.example.receiptApp.pages.dashboard

import androidx.lifecycle.*
import com.example.receiptApp.repository.DbRepository
import com.example.receiptApp.utils.StateStack
import com.example.receiptApp.repository.SharedPrefRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class HomeViewModel(private val sharedPrefRepository: SharedPrefRepository, private val dbRepository: DbRepository) : ViewModel()
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

    private object StoreId
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

    fun setEditMode()
    {
        if (homeStateStack.peek() == HomeState.EditMode) return

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

    fun saveDashboard() = viewModelScope.launch {
        val needToSave: MutableMap<Int, DashboardDataModel> = mutableMapOf()

        _dashboard.value?.let {
            it.forEachIndexed { i, element ->
                needToSave[i] = element
            }
        }

        sharedPrefRepository.writeDashboard(needToSave)

        homeStateStack.clear()
        homeStateStack.push(HomeState.NormalMode)
        Timber.e("homeStateStack -> $homeStateStack")

        _homeState.value = HomeState.NormalMode
    }

    private fun loadDashboard() = viewModelScope.launch {

        val dashboard: Map<Int, DashboardDataModel> = sharedPrefRepository.readDashboard()

        val list: MutableList<DashboardDataModel> = mutableListOf()

        dashboard.entries.forEach {

            when (val el = it.value)
            {
                is DashboardDataModel.Test -> {

                    val contentParsing = el.content.split(":")

                    when(contentParsing[0])
                    {
                        "sumTag" -> {
                            val tag = contentParsing[1]
                            val period = DbRepository.Period.valueOf(contentParsing[3])

                            el.name = tag
                            //el.value = dbRepository
                        }
                    }

                    list.add(el)
                }
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

    private fun loadStore() = viewModelScope.launch {

        val storeList: MutableList<DashboardDataModel> = mutableListOf()

        val period = DbRepository.Period.MONTH
        dbRepository.getAggregateTagsAndExpensesByPeriod(period).entries.forEach {
            it.key?.let { name ->
                storeList.add(
                    DashboardDataModel.Test(
                        id = StoreId.getId(),
                        name = name,
                        value = it.value,
                        content = "sumTag:$name:${period.name}"
                    )
                )
            }
        }

        _store.value = storeList
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

        sharedPrefRepository.clearDashboard()
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


class HomeViewModelFactory(
    private val sharedPrefRepository: SharedPrefRepository,
    private val dbRepository: DbRepository
    ) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java))
        {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(sharedPrefRepository, dbRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

