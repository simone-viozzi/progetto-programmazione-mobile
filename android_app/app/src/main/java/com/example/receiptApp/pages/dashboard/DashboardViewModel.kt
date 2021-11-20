package com.example.receiptApp.pages.dashboard

import androidx.lifecycle.*
import com.example.receiptApp.repository.DashboardRepository
import com.example.receiptApp.repository.DbRepository
import com.example.receiptApp.utils.StateStack
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class DashboardViewModel(
    private val dbRepository: DbRepository,
    private val dashboardRepository: DashboardRepository
    ) : ViewModel()
{
    private val _dashboard: MutableLiveData<List<DashboardDataModel>> = MutableLiveData<List<DashboardDataModel>>()
    val dashboard: LiveData<List<DashboardDataModel>> = _dashboard

    private val _store: MutableLiveData<List<DashboardDataModel>> = MutableLiveData<List<DashboardDataModel>>()
    val store: LiveData<List<DashboardDataModel>> = _store

    // the stare of the dashboard is represented as objects, this give the flexibility to pass a data class with things
    //  in it. right now this functionality is not needed and this is equivalent to an enum.
    sealed class DashboardState
    {
        object NoState: DashboardState()
        object EmptyDashMode : DashboardState()
        object NormalMode : DashboardState()
        object EditMode : DashboardState()
        object StoreMode : DashboardState()

        // pretty print of the state
        override fun toString(): String = this.javaClass.name.replaceBeforeLast("$", "")
    }

    // to know the history of the states the user transitioned i keep em in a stack
    private val homeStateStack = StateStack<DashboardState>()

    private val _dashboardState: MutableLiveData<DashboardState> = MutableLiveData<DashboardState>()
    val dashboardState: LiveData<DashboardState> = _dashboardState

    init
    {
        // the dashboard start from NoState and than change in loadDashboard()
        homeStateStack.push(DashboardState.NoState)
        _dashboardState.value = DashboardState.NoState

        loadDashboard()
        
        // TODO only for debug purposes print the db at the start of the app
        viewModelScope.launch {
            Timber.e("DATABASE: \n${
                dbRepository.getAggregates(null)?.map { el ->
                    el.also {
                        it.location = null
                        it.date = null
                    }
                }?.joinToString {
                    "${it}\n"
                }
            }")
        }
    }

    fun setEditMode()
    {
        if (homeStateStack.peek() == DashboardState.EditMode) return

        homeStateStack.push(DashboardState.EditMode)
        Timber.e("homeStateStack -> $homeStateStack")

        _dashboardState.value = DashboardState.EditMode
    }


    fun setStoreMode()
    {
        homeStateStack.push(DashboardState.StoreMode)
        Timber.e("homeStateStack -> $homeStateStack")

        _dashboardState.value = DashboardState.StoreMode

        loadStore()
    }

    fun saveDashboard() = viewModelScope.launch {

        _dashboard.value.let {
            if (!it.isNullOrEmpty())
            {
                dashboardRepository.saveDashboard(it)

                homeStateStack.clear()
                homeStateStack.push(DashboardState.NormalMode)
                Timber.e("homeStateStack -> $homeStateStack")

                _dashboardState.value = DashboardState.NormalMode
            }
            else
            {
                homeStateStack.push(DashboardState.EmptyDashMode)
                Timber.e("homeStateStack -> $homeStateStack")

                _dashboardState.value = DashboardState.EmptyDashMode
            }
        }
    }

    /**
     * helper to load the dashboard
     */
    private fun loadDashboard() = viewModelScope.launch {

        val list = dashboardRepository.loadDashboard()

        if (list.isNotEmpty())
        {
            // if the list is not empty i can load the data onto the dashboard and go to normal mode
            homeStateStack.push(DashboardState.NormalMode)
            Timber.e("homeStateStack -> $homeStateStack")

            _dashboard.value = list
            _dashboardState.value = DashboardState.NormalMode
        } else
        {
            // if the dashboard is empty i will go to EmptyDashMode and display the welcome page
            homeStateStack.push(DashboardState.EmptyDashMode)
            Timber.e("homeStateStack -> $homeStateStack")

            _dashboard.value = emptyList()
            _dashboardState.value = DashboardState.EmptyDashMode
        }
    }


    private fun loadStore() = viewModelScope.launch {
        // to load the store i need the current dashboard so i can avoid to load things that are not needed,
        //  like if they are already in the dashboard
        _store.value = dashboardRepository.loadStore(_dashboard.value)
    }


    fun addToDashboard(element: DashboardDataModel)
    {
        homeStateStack.clear()
        homeStateStack.push(DashboardState.EditMode)
        Timber.e("homeStateStack -> $homeStateStack")

        // need to notify the store that this particular element is on the dashboard now and should not appear in the
        //  store now
        dashboardRepository.notifyAddToDash(element)
        _dashboard.value = _dashboard.value?.toMutableList()?.also { it.add(0, element) }
        _dashboardState.value = DashboardState.EditMode
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
            _dashboardState.value = it
            Timber.e("homeStateStack -> $homeStateStack")
        }
    }

    fun getPreviousState(): DashboardState
    {
        val state = homeStateStack.peekPrevious()

        Timber.e("state -> $state")
        Timber.e("homeStateStack -> $homeStateStack")

        return state ?: DashboardState.NoState
    }

    fun clearDashboard()
    {
        homeStateStack.clear()
        homeStateStack.push(DashboardState.EmptyDashMode)
        Timber.e("homeStateStack -> $homeStateStack")

        dashboardRepository.clearDashboard()
        _dashboard.value = emptyList()
        _dashboardState.value = DashboardState.EmptyDashMode
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


class DashboardViewModelFactory(
    private val dbRepository: DbRepository,
    private val dashboardRepository: DashboardRepository
    ) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java))
        {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(
                dbRepository,
                dashboardRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

