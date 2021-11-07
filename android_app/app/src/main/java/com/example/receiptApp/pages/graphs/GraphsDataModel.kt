package com.example.receiptApp.pages.graphs


enum class GraphType()
{
    ISTOGRAM,
    CAKE;

    fun getObj(): GraphsDataModel
    {
        return when (this)
        {
            ISTOGRAM -> GraphsDataModel.Istogram()
            CAKE -> GraphsDataModel.Cake()
        }
    }
}

interface GraphElement
{
    var id: Int
    val type: GraphType
}

sealed class GraphsDataModel : GraphElement {

    data class Istogram(
        override var id: Int = 0,
        override val type: GraphType = GraphType.ISTOGRAM,
        var name: String = "",

        ) : GraphsDataModel()

    data class Cake(
        override var id: Int = 0,
        override val type: GraphType = GraphType.CAKE,

        ) : GraphsDataModel()
}