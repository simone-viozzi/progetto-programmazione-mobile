package com.example.receiptApp.repository

import android.content.Context
import com.example.receiptApp.App
import com.example.receiptApp.db.AppDatabase
import com.example.receiptApp.db.aggregate.AggregatesDao
import com.example.receiptApp.db.element.ElementsDao
import com.example.receiptApp.pages.graphs.GraphBuilder
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel

class GraphsRepository(
    private val applicationContext: Context,
    private val aggregateDao: AggregatesDao,
    private val elementDao: ElementsDao) {

    fun testGraph(): AAChartModel{
        return GraphBuilder.test(applicationContext)
    }

    
}