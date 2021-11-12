package com.example.receiptApp.repository

import android.content.Context
import com.example.receiptApp.db.AppDatabase
import com.example.receiptApp.db.aggregate.AggregatesDao
import com.example.receiptApp.db.element.ElementsDao
import com.example.receiptApp.pages.graphs.GraphBuilder
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel

class GraphsRepository(
    private val applicationContext: Context,
    private val dbRepository: DbRepository
) {

    private val graphBuilder: GraphBuilder = GraphBuilder(applicationContext)

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
            values = month_values
        )
    }

    //ISTOGRAMMI
}