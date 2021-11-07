package com.example.receiptApp.pages.home

interface DashboardElement
{
    var id: Int
    val type: TYPE
}

enum class TYPE()
{
    TEST,
    TEST_BIG;

    fun getObj(): DashboardDataModel
    {
        return when (this)
        {
            TEST -> DashboardDataModel.Test()
            TEST_BIG -> DashboardDataModel.TestBig()
        }
    }
}

sealed class DashboardDataModel : DashboardElement
{
    data class Test(
        override var id: Int = 0,
        override val type: TYPE = TYPE.TEST,
        var name: String = "",

        ) : DashboardDataModel(), DashboardElement

    data class TestBig(
        override var id: Int = 0,
        override val type: TYPE = TYPE.TEST_BIG,

        ) : DashboardDataModel(), DashboardElement
}




