package com.example.receiptApp.repository

import com.example.receiptApp.R
import com.example.receiptApp.pages.dashboard.DashboardDataModel
import timber.log.Timber
import java.util.*

class DashboardRepository(
    private val sharedPrefRepository: SharedPrefRepository,
    private val dbRepository: DbRepository,
    private val graphsRepository: GraphsRepository
)
{
    private val currentStore: MutableList<DashboardDataModel> = mutableListOf()

    private object StoreId
    {
        var lastId: Int = 0
        fun getId() = ++lastId
    }



    suspend fun saveDashboard(dashboard: List<DashboardDataModel>)
    {
        val needToSave: MutableMap<Int, DashboardDataModel> = mutableMapOf()

        dashboard.forEachIndexed { i, element ->
            needToSave[i] = element
        }

        sharedPrefRepository.writeDashboard(needToSave)
    }

    suspend fun loadDashboard(): MutableList<DashboardDataModel>
    {
        val currentDashboard = sharedPrefRepository.readDashboard().toMutableMap()

        val list: MutableList<DashboardDataModel> = mutableListOf()

        currentDashboard.entries.forEach {
            loadSingleElement(it)?.let { it1 -> list.add(it1) }
        }

        return list
    }

    private suspend fun loadSingleElement(it: Map.Entry<Int, DashboardDataModel>): DashboardDataModel?
    {
        return when (val el = it.value)
        {
            is DashboardDataModel.Label ->
            {
                val contentParsing = el.content.split(":")

                when (contentParsing[0])
                {
                    "sumTag" ->
                    {
                        val tag = contentParsing[1]
                        val period = DbRepository.Period.valueOf(contentParsing[2])

                        el.name = tag
                        el.value = dbRepository.getAggregateExpensesByTagAndPeriod(tag, period) ?: return null
                        el.id = StoreId.getId()
                        el
                    }
                    "sum" ->
                    {
                        val period = DbRepository.Period.valueOf(contentParsing[1])

                        el.name = period.name.lowercase(Locale.getDefault())
                        el.value = dbRepository.getPeriodExpensesSum(period) ?: return null
                        el.id = StoreId.getId()
                        el
                    }
                    else -> null
                }
            }
            is DashboardDataModel.Pie ->
            {
                val contentParsing = el.content.split(":")

                when (contentParsing[0])
                {
                    "monthAggrCount" ->
                    {
                        el.name = graphsRepository.getStrings(R.string.pie_count_by_atag)
                        el.aaChartModel = graphsRepository.monthAggrCountByTagPie()
                        el
                    }
                    "monthElemCount" -> {
                        el.name = graphsRepository.getStrings(R.string.pie_count_by_etag)
                        el.aaChartModel = graphsRepository.monthElemCountByTagPie()
                        el
                    }
                    else -> null
                }
            }
            is DashboardDataModel.Histogram ->
            {
                val contentParsing = el.content.split(":")

                when (contentParsing[0])
                {
                    "monthExpenses" ->{
                        el.name = graphsRepository.getStrings(R.string.histo_month_expenses)
                        el.aaChartModel = graphsRepository.monthExpensesHistogram()
                        el
                    }
                    "yearExpenses" -> {
                        el.name = graphsRepository.getStrings(R.string.histo_year_expenses)
                        el.aaChartModel = graphsRepository.yearExpensesHistogram()
                        el
                    }
                    "monthAggrTagExpenses" -> {
                        el.name = graphsRepository.getStrings(R.string.histo_month_expenses_by_atag)
                        el.aaChartModel = graphsRepository.monthAggrTagExpensesHistogram()
                        el
                    }
                    else -> null
                }
            }
        }
    }

    suspend fun loadStore(dashboard: List<DashboardDataModel>?): MutableList<DashboardDataModel>
    {
        loadStoreTagExpense(dashboard)
        loadAllExpenses(dashboard)
        loadPies(dashboard)
        loadHistograms(dashboard)

        Timber.d("loading store -> $currentStore")

        return currentStore
    }

    private suspend fun loadStoreTagExpense(dashboard: List<DashboardDataModel>?)
    {
        val period = DbRepository.Period.MONTH
        val allTags = dbRepository.getAggregateTagsAndCountByPeriod(period)

        val dashboardTags = dashboard?.map { (it as? DashboardDataModel.Label)?.name } ?: listOf()
        val storeTags = currentStore.map { (it as? DashboardDataModel.Label)?.name }

        allTags.entries.forEach { (tag, _) ->
            if (tag.isNullOrEmpty()) return@forEach

            if (!dashboardTags.contains(tag) && !storeTags.contains(tag))
            {
                currentStore.add(
                    DashboardDataModel.Label(
                        id = StoreId.getId(),
                        name = tag,
                        value = dbRepository.getAggregateExpensesByTagAndPeriod(tag, period) ?: return@forEach,
                        content = "sumTag:$tag:${period.name}"
                    ).also { Timber.d("loadStoreTagExpense -> $it") }
                )
            }
        }
    }

    private suspend fun loadAllExpenses(dashboard: List<DashboardDataModel>?)
    {
        val dashboardPeriods = dashboard?.map { (it as? DashboardDataModel.Label)?.name } ?: listOf()
        val storePeriods = currentStore.map { (it as? DashboardDataModel.Label)?.name }

        DbRepository.Period.values().forEach { period ->

            val name = period.name.lowercase(Locale.getDefault())

            if (!dashboardPeriods.contains(name) && !storePeriods.contains(name))
            {
                currentStore.add(
                    DashboardDataModel.Label(
                        id = StoreId.getId(),
                        name = name,
                        value = dbRepository.getPeriodExpensesSum(period) ?: return@forEach,
                        content = "sum:$period"
                    ).also { Timber.d("loadAllExpenses -> $it") }
                )
            }
        }
    }

    private suspend fun loadPies(dashboard: List<DashboardDataModel>?)
    {
        val dashPies = dashboard?.map { (it as? DashboardDataModel.Pie)?.content } ?: listOf()
        val storePies = currentStore.map { (it as? DashboardDataModel.Pie)?.content }

        Timber.d(dashPies.toString())
        if (!dashPies.contains("monthAggrCount") && !storePies.contains("monthAggrCount"))
        {
            currentStore.add(
                DashboardDataModel.Pie(
                    id = StoreId.getId(),
                    name = graphsRepository.getStrings(R.string.pie_count_by_atag),
                    aaChartModel = graphsRepository.monthAggrCountByTagPie(),
                    content = "monthAggrCount"
                )
            )
        }

        if (!dashPies.contains("monthElemCount") && !storePies.contains("monthElemCount"))
        {
            currentStore.add(
                DashboardDataModel.Pie(
                    id = StoreId.getId(),
                    name = graphsRepository.getStrings(R.string.pie_count_by_etag),
                    aaChartModel = graphsRepository.monthElemCountByTagPie(),
                    content = "monthElemCount"
                )
            )
        }
    }


    private suspend fun loadHistograms(dashboard: List<DashboardDataModel>?)
    {
        val dashPies = dashboard?.map { (it as? DashboardDataModel.Pie)?.content } ?: listOf()
        val storePies = currentStore.map { (it as? DashboardDataModel.Pie)?.content }


        if (!dashPies.contains("monthExpenses") && !storePies.contains("monthExpenses"))
        {
            currentStore.add(
                DashboardDataModel.Histogram(
                    id = StoreId.getId(),
                    name = graphsRepository.getStrings(R.string.histo_month_expenses),
                    aaChartModel = graphsRepository.monthExpensesHistogram(),
                    content = "monthExpenses"
                )
            )
        }

        if (!dashPies.contains("yearExpenses") && !storePies.contains("yearExpenses"))
        {
            currentStore.add(
                DashboardDataModel.Histogram(
                    id = StoreId.getId(),
                    name = graphsRepository.getStrings(R.string.histo_year_expenses),
                    aaChartModel = graphsRepository.yearExpensesHistogram(),
                    content = "yearExpenses"
                )
            )
        }

        if (!dashPies.contains("monthAggrTagExpenses") && !storePies.contains("monthAggrTagExpenses"))
        {
            currentStore.add(
                DashboardDataModel.Histogram(
                    id = StoreId.getId(),
                    name = graphsRepository.getStrings(R.string.histo_month_expenses_by_atag),
                    aaChartModel = graphsRepository.monthAggrTagExpensesHistogram(),
                    content = "monthAggrTagExpenses"
                )
            )
        }
    }


    fun notifyAddToDash(element: DashboardDataModel)
    {
        currentStore.remove(element)
        Timber.d("removed -> $element")
        Timber.d("after remove -> $currentStore")
    }

    fun notifyRemoveFromDash(element: DashboardDataModel)
    {
        currentStore.add(element)
        Timber.d("add -> $element")
    }
}