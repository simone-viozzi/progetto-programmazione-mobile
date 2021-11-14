package com.example.receiptApp.pages.graphs

import androidx.lifecycle.*
import com.example.receiptApp.R
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

            // if database is empty fill it with random data
            // TODO remove in production, only for debug purposes
            // graphsRepository.RandomFillDatabase()

            // create all the graph objects
            _rvList.value = listOf(
                // ###########################################################################
                // HIST GRAPHS

                GraphsDataModel.Histogram(
                    id = 0,
                    name = graphsRepository.getStrings(R.string.histo_month_expenses),
                    aaChartModel = graphsRepository.monthExpensesHistogram()
                ),
                GraphsDataModel.Histogram(
                    id = 1,
                    name = graphsRepository.getStrings(R.string.histo_year_expenses),
                    aaChartModel = graphsRepository.yearExpensesHistogram()
                ),
                GraphsDataModel.Histogram(
                    id = 2,
                    name = graphsRepository.getStrings(R.string.histo_month_expenses_by_atag),
                    aaChartModel = graphsRepository.monthAggrTagExpensesHistogram()
                ),
                GraphsDataModel.Histogram(
                    id = 3,
                    name = graphsRepository.getStrings(R.string.histo_month_expenses_by_etag),
                    aaChartModel = graphsRepository.monthElemTagExpensesHistogram()
                ),
                GraphsDataModel.Histogram(
                    id = 4,
                    name = graphsRepository.getStrings(R.string.histo_year_expenses_by_atag),
                    aaChartModel = graphsRepository.yearAggrTagExpensesHistogram()
                ),
                GraphsDataModel.Histogram(
                    id = 5,
                    name = graphsRepository.getStrings(R.string.histo_year_expenses_by_etag),
                    aaChartModel = graphsRepository.yearElemTagExpensesHistogram()
                ),

                // ###########################################################################
                // CAKE GRAPHS

                GraphsDataModel.Cake(
                    id = 6,
                    name = graphsRepository.getStrings(R.string.pie_count_by_atag),
                    aaChartModel = graphsRepository.monthAggrCountByTagPie()
                ),
                GraphsDataModel.Cake(
                    id = 7,
                    name = graphsRepository.getStrings(R.string.pie_count_by_etag),
                    aaChartModel = graphsRepository.monthElemCountByTagPie()
                )
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