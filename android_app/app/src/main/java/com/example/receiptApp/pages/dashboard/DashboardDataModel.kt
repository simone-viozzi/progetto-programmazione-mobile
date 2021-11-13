package com.example.receiptApp.pages.dashboard

interface DashboardElement
{
    var id: Int
    val type: TYPE
    var content: String
}

enum class TYPE()
{
    TEST,
    TEST_BIG,
    SQUARE;

    fun getObj(): DashboardDataModel
    {
        return when (this)
        {
            TEST -> DashboardDataModel.Test()
            TEST_BIG -> DashboardDataModel.TestBig()
            SQUARE -> DashboardDataModel.Square()
        }
    }
}

sealed class DashboardDataModel : DashboardElement
{
    data class Test(
        override var id: Int = 0,
        override val type: TYPE = TYPE.TEST,
        var name: String = "",
        override var content: String = "",
        var value: Float = 0f,
    ) : DashboardDataModel()

    data class TestBig(
        override var id: Int = 0,
        override val type: TYPE = TYPE.TEST_BIG,
        override var content: String = "",
    ) : DashboardDataModel()

    data class Square(
        override var id: Int = 0,
        override val type: TYPE = TYPE.SQUARE,
        override var content: String = ""
    ) : DashboardDataModel()
}