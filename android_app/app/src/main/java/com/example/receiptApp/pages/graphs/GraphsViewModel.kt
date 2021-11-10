package com.example.receiptApp.pages.graphs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.receiptApp.pages.add.AddDataModel
import com.example.receiptApp.pages.add.AddViewModel
import com.example.receiptApp.repository.GraphsRepository

class GraphsViewModel(private val graphsRepository: GraphsRepository) : ViewModel()
{
    // the list observed by the recyclerview
    private val _rvList = MutableLiveData<List<GraphsDataModel>>()
    val rvList: LiveData<List<GraphsDataModel>>
        get() = _rvList

    init{
        // In the initialization each graph should be created
        _rvList.value = listOf(
            GraphsDataModel.Histogram(
                id = 0,
                name = "test",
                aaChartModel = graphsRepository.testGraph()
            ),
            GraphsDataModel.Histogram(
                id = 1,
                name = "test",
                aaChartModel = graphsRepository.testGraph()
            ),
            GraphsDataModel.Histogram(
                id = 2,
                name = "test",
                aaChartModel = graphsRepository.testGraph()
            ),
        )
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