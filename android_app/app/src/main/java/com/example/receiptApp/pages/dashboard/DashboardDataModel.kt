package com.example.receiptApp.pages.dashboard

import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel

interface DashboardElement
{
    var id: Int
    val type: TYPE
    var content: String
}

enum class TYPE()
{
    LABEL,
    PIE,
    HISTOGRAM;


    fun getObj(): DashboardDataModel
    {
        return when (this)
        {
            LABEL -> DashboardDataModel.Label()
            PIE -> DashboardDataModel.Pie()
            HISTOGRAM -> DashboardDataModel.Histogram()
        }
    }
}


sealed class DashboardDataModel : DashboardElement
{
    data class Label(
        override var id: Int = 0,
        override val type: TYPE = TYPE.LABEL,
        var name: String = "",
        override var content: String = "",
        var value: Float = 0f,
    ) : DashboardDataModel()

    data class Pie(
        override var id: Int = 0,
        override val type: TYPE = TYPE.PIE,
        override var content: String = "",
        var name: String = "",
        var aaChartModel: AAChartModel = AAChartModel()
    ) : DashboardDataModel()

    data class Histogram(
        override var id: Int = 0,
        override val type: TYPE = TYPE.HISTOGRAM,
        override var content: String = "",
        var name: String = "",
        var aaChartModel: AAChartModel = AAChartModel()
    ): DashboardDataModel()
}