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

    // helper to store the last id
    private object StoreId
    {
        var lastId: Int = 0
        fun getId() = ++lastId
    }

    /**
     * Save dashboard
     *
     * @param dashboard
     */
    suspend fun saveDashboard(dashboard: List<DashboardDataModel>)
    {
        val needToSave: MutableMap<Int, DashboardDataModel> = mutableMapOf()

        // to save the position and the order of the dashboard i save the map between the index in
        // the array and it's value
        dashboard.forEachIndexed { i, element ->
            needToSave[i] = element
        }

        sharedPrefRepository.writeDashboard(needToSave)
    }

    /**
     * Load dashboard
     *  this will load only valid widgets. if a type of widget change or change in it's function it
     *  will be ignored in the loading phase.
     *
     * @return
     */
    suspend fun loadDashboard(): MutableList<DashboardDataModel>
    {
        val currentDashboard = sharedPrefRepository.readDashboard()

        val list: MutableList<DashboardDataModel> = mutableListOf()

        currentDashboard.entries.forEach {
            loadSingleElement(it.value)?.let { it1 -> list.add(it1) }
        }

        return list
    }

    /**
     * Load single element
     *  this function load the values of each type of widget and each subtype into the correct slots.
     *
     * @param el -> the widget that need to be filled
     * @return
     */
    private suspend fun loadSingleElement(el: DashboardDataModel): DashboardDataModel?
    {
        return when (el)
        {
            // need to fill every type of element individually
            is DashboardDataModel.Label ->
            {
                // and every subtype individually
                val contentParsing = el.content.split(":")

                when (contentParsing[0]) {
                    "sumTag" -> {
                        val tag = contentParsing[1]
                        val period = DbRepository.Period.valueOf(contentParsing[2])

                        el.name = tag
                        el.value = dbRepository.getAggregateExpensesByTagAndPeriod(tag, period)
                            ?: return null
                        el.id = StoreId.getId()
                        el
                    }
                    "sum" -> {
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

                when (contentParsing[0]) {
                    "monthAggrCount" -> {
                        el.name = graphsRepository.getStrings(R.string.pie_count_by_atag)
                        el.aaChartModel = graphsRepository.monthAggrCountByTagPie()
                        el.id = StoreId.getId()
                        el
                    }
                    "monthElemCount" -> {
                        el.name = graphsRepository.getStrings(R.string.pie_count_by_etag)
                        el.aaChartModel = graphsRepository.monthElemCountByTagPie()
                        el.id = StoreId.getId()
                        el
                    }
                    else -> null
                }
            }
            is DashboardDataModel.Histogram ->
            {
                val contentParsing = el.content.split(":")

                when (contentParsing[0]) {
                    "monthExpenses" -> {
                        el.name = graphsRepository.getStrings(R.string.histo_month_expenses)
                        el.aaChartModel = graphsRepository.monthExpensesHistogram()
                        el.id = StoreId.getId()
                        el
                    }
                    "yearExpenses" -> {
                        el.name = graphsRepository.getStrings(R.string.histo_year_expenses)
                        el.aaChartModel = graphsRepository.yearExpensesHistogram()
                        el.id = StoreId.getId()
                        el
                    }
                    "monthAggrTagExpenses" -> {
                        el.name = graphsRepository.getStrings(R.string.histo_month_expenses_by_atag)
                        el.aaChartModel = graphsRepository.monthAggrTagExpensesHistogram()
                        el.id = StoreId.getId()
                        el
                    }
                    else -> null
                }
            }
        }
    }

    /**
     * Load store
     *
     * @param dashboard needed because i don't want to load widgets that are already in the dashboard
     * @return
     */
    suspend fun loadStore(dashboard: List<DashboardDataModel>?): MutableList<DashboardDataModel>
    {
        loadTagExpense(dashboard)
        loadAllExpenses(dashboard)
        loadPies(dashboard)
        loadHistograms(dashboard)

        Timber.d("loading store -> $currentStore")

        return currentStore
    }

    suspend fun loadStoreFromScratch(dashboard: List<DashboardDataModel>?): MutableList<DashboardDataModel>
    {
        currentStore.clear()
        return loadStore(dashboard)
    }

    private suspend fun loadTagExpense(dashboard: List<DashboardDataModel>?)
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

    /**
     * Notify add to dash
     *  if a widget is added to the dashboard it needs to be removed from the store
     *
     * @param element
     */
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

    fun clearDashboard()
    {
        sharedPrefRepository.clearDashboard()
    }
}