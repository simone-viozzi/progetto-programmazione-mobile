package com.example.receiptApp.pages.graphs

import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel


enum class GraphType()
{
    HISTOGRAM,
    CAKE,
    LABEL;

    fun getObj(): GraphsDataModel
    {
        return when (this)
        {
            HISTOGRAM -> GraphsDataModel.Histogram()
            CAKE -> GraphsDataModel.Cake()
            LABEL -> GraphsDataModel.Label()
        }
    }
}

interface GraphElement
{
    var id: Int
    val type: GraphType
}

sealed class GraphsDataModel : GraphElement {

    data class Histogram(
        override var id: Int = 0,
        override val type: GraphType = GraphType.HISTOGRAM,
        var name: String = "",
        var aaChartModel: AAChartModel = AAChartModel()

        ) : GraphsDataModel()

    data class Cake(
        override var id: Int = 0,
        override val type: GraphType = GraphType.CAKE,
        var name: String = "",
        var aaChartModel: AAChartModel = AAChartModel()

        ) : GraphsDataModel()

    data class Label(
        override var id: Int = 0,
        override val type: GraphType = GraphType.LABEL,
        var name: String = "",
        var value: Float = 0.0f

    ) : GraphsDataModel()
}