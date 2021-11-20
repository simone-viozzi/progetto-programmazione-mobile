package com.example.receiptApp.pages.dashboard

import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel

/**
 * this is what of every element of the dashboard get saved into the shared preferences
 */
interface DashboardElement
{
    var id: Int
    val type: TYPE
    var content: String
}

/**
 * every type have it's own object
 */
enum class TYPE()
{
    LABEL,
    PIE,
    HISTOGRAM;

    // with this i can convert the type into the actual object
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

// those are the objects that will be displayed in the recyclerview
//  every type have it's own field but all of them implement DashboardElement
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