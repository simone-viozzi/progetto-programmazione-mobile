package com.example.receiptApp.repository

import android.content.Context
import com.example.receiptApp.R
import com.example.receiptApp.Utils.TypesHelper
import com.example.receiptApp.pages.graphs.GraphBuilder
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel

class GraphsRepository(
    private val applicationContext: Context,
    private val dbRepository: DbRepository
) {

    private val graphBuilder: GraphBuilder = GraphBuilder(applicationContext)

    // ##########################################################################
    // RESOURCES METHODS

    /**
     * Get strings
     *
     * easy access to graph names from graph view model
     *
     * @param resourceId string id needed
     * @return
     */
    fun getStrings(resourceId: Int): String{
        return applicationContext.getString(resourceId)
    }


    // ##########################################################################
    // HISTOGRAM METHODS

    suspend fun monthExpensesHistogram(): AAChartModel{

        val values = TypesHelper.float2DoubleArray(dbRepository.getPeriodExpenses(DbRepository.Period.MONTH))

        return graphBuilder.category_graph(
            categories = graphBuilder.generate_month_labels(),
            values_name = getStrings(R.string.expense),
            values = values
        )
    }

    suspend fun yearExpensesHistogram(): AAChartModel{

        val values = TypesHelper.float2DoubleArray(dbRepository.getPeriodExpenses(DbRepository.Period.YEAR))

        return graphBuilder.category_graph(
            categories = graphBuilder.year_labels,
            values_name = getStrings(R.string.expense),
            values = values
        )
    }

    suspend fun monthAggrTagExpensesHistogram(): AAChartModel{

        val mapResult = dbRepository.getAggregateTagsAndExpensesByPeriod(DbRepository.Period.MONTH)

        // default values if something goes wrong
        var labels = arrayOf("")
        var values = arrayOf(0.0)

        //TODO check behaviour whene there are no tags inside db
        if(mapResult != null) {
            // NOTE: null tags shouldnt return from the call
            labels = mapResult.keys.toTypedArray() as Array<String>
            values = TypesHelper.float2DoubleArray(mapResult.values.toTypedArray())
        }

        return graphBuilder.category_graph(
            categories = labels,
            values_name = getStrings(R.string.expense),
            values = values
        )
    }

    suspend fun yearAggrTagExpensesHistogram(): AAChartModel{

        val mapResult = dbRepository.getAggregateTagsAndExpensesByPeriod(DbRepository.Period.YEAR)

        // default values if something goes wrong
        var labels = arrayOf("")
        var values = arrayOf(0.0)

        //TODO check behaviour whene there are no tags inside db
        if(mapResult != null) {
            // NOTE: null tags shouldnt return from the call
            labels = mapResult.keys.toTypedArray() as Array<String>
            values = TypesHelper.float2DoubleArray(mapResult.values.toTypedArray())
        }

        return graphBuilder.category_graph(
            categories = labels,
            values_name = getStrings(R.string.expense),
            values = values
        )
    }


    suspend fun monthElemTagExpensesHistogram(): AAChartModel{

        val mapResult = dbRepository.getElementTagsAndExpensesByPeriod(DbRepository.Period.MONTH)

        // default values if something goes wrong
        var labels = arrayOf("")
        var values = arrayOf(0.0)

        //TODO check behaviour whene there are no tags inside db
        if(mapResult != null) {
            // NOTE: null tags shouldnt return from the call
            labels = mapResult.keys.toTypedArray() as Array<String>
            values = TypesHelper.float2DoubleArray(mapResult.values.toTypedArray())
        }

        return graphBuilder.category_graph(
            categories = labels,
            values_name = getStrings(R.string.expense),
            values = values
        )
    }

    suspend fun yearElemTagExpensesHistogram(): AAChartModel{

        val mapResult = dbRepository.getElementTagsAndExpensesByPeriod(DbRepository.Period.YEAR)

        // default values if something goes wrong
        var labels = arrayOf("")
        var values = arrayOf(0.0)

        //TODO check behaviour whene there are no tags inside db
        if(mapResult != null) {
            // NOTE: null tags shouldnt return from the call
            labels = mapResult.keys.toTypedArray() as Array<String>
            values = TypesHelper.float2DoubleArray(mapResult.values.toTypedArray())
        }

        return graphBuilder.category_graph(
            categories = labels,
            values_name = getStrings(R.string.expense),
            values = values
        )
    }

    // ##########################################################################
    // PIE METHODS

    suspend fun monthAggrCountByTagPie(): AAChartModel{
        val mapResult = dbRepository.getAggregateTagsAndCountByPeriod(DbRepository.Period.MONTH)

        // default values if something goes wrong
        var labels = arrayOf("")
        var values = arrayOf<Any>(0L)

        //TODO check behaviour whene there are no tags inside db
        if(mapResult != null) {
            // NOTE: null tags shouldnt return from the call
            labels = mapResult.keys.toTypedArray() as Array<String>
            values = TypesHelper.long2AnyArray(mapResult.values.toTypedArray())
        }

        return graphBuilder.pie(
            categories = labels,
            values_name = getStrings(R.string.count),
            values = values
        )
    }

    suspend fun monthElemCountByTagPie(): AAChartModel{
        val mapResult = dbRepository.getElementTagsAndCountByPeriod(DbRepository.Period.MONTH)

        // default values if something goes wrong
        var labels = arrayOf("")
        var values = arrayOf<Any>(0L)

        //TODO check behaviour whene there are no tags inside db
        if(mapResult != null) {
            // NOTE: null tags shouldnt return from the call
            labels = mapResult.keys.toTypedArray() as Array<String>
            values = TypesHelper.long2AnyArray(mapResult.values.toTypedArray())
        }

        return graphBuilder.pie(
            categories = labels,
            values_name = getStrings(R.string.count),
            values = values
        )
    }



    // ##########################################################################
    // DEBUG METHODS

    fun testMonthGraph(): AAChartModel{

        val day_labels = graphBuilder.generate_month_labels()
        val day_values = Array(day_labels.size){it.toDouble()}

        return graphBuilder.category_graph(
            categories = day_labels,
            values = day_values
        )
    }

    fun testYearGraph(): AAChartModel{

        val month_values = Array(graphBuilder.year_labels.size){it.toDouble()}

        return graphBuilder.category_graph(
            categories = graphBuilder.year_labels,
            values = month_values
        )
    }

    fun testPieGraph(): AAChartModel{


        val month_values = Array(graphBuilder.year_labels.size){it.toDouble()}

        return graphBuilder.pie(
            categories = graphBuilder.year_labels,
            values = month_values as Array<Any>
        )
    }

    suspend fun RandomFillDatabase(){
        dbRepository.RandomFillDatabase()
    }
}