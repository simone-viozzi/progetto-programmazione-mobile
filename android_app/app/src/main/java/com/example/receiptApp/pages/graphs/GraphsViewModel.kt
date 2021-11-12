package com.example.receiptApp.pages.graphs

import androidx.lifecycle.*
import com.example.receiptApp.repository.GraphsRepository
import kotlinx.coroutines.launch

class GraphsViewModel(private val graphsRepository: GraphsRepository) : ViewModel()
{
    // the list observed by the recyclerview
    private val _rvList = MutableLiveData<List<GraphsDataModel>>()
    val rvList: LiveData<List<GraphsDataModel>>
        get() = _rvList

    init{
        // In the initialization each graph should be created
        viewModelScope.launch {

            // loading graphs data


            // create all the graph objects
            _rvList.value = listOf(
                GraphsDataModel.Histogram(
                    id = 0,
                    name = "test histogram 1",
                    aaChartModel = graphsRepository.testMonthGraph()
                ),
                GraphsDataModel.Histogram(
                    id = 1,
                    name = "test histogram 2",
                    aaChartModel = graphsRepository.testYearGraph()
                ),
                GraphsDataModel.Cake(
                    id = 2,
                    name = "test pie 1",
                    aaChartModel = graphsRepository.testPieGraph()
                ),
            )
        }
    }
}

class GraphsViewModelFactory(private val repository: GraphsRepository) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(GraphsViewModel::class.java))
        {
            @Suppress("UNCHECKED_CAST")
            return GraphsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}