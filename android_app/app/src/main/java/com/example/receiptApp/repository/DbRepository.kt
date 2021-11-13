package com.example.receiptApp.repository

import android.net.Uri
import androidx.room.Transaction
import com.example.receiptApp.db.aggregate.Aggregate
import com.example.receiptApp.db.aggregate.PublicAggregatesDao
import com.example.receiptApp.db.element.Element
import com.example.receiptApp.db.element.PublicElementsDao
import com.example.receiptApp.db.tag.TagsDao
import com.example.receiptApp.pages.add.AddDataModel
import java.text.SimpleDateFormat
import java.util.*

class DbRepository(
    private val aggregateDao: PublicAggregatesDao,
    private val elementDao: PublicElementsDao,
    private val tagDao: TagsDao
){

    enum class Period{
        DAY,
        WEEK,
        MONTH,
        YEAR
    }

    // ##########################################################################
    // DATE HELPERS METHODS

    /**
     * Get period start date
     *
     * @param period enum that specify the time interval of interest
     * @return return the date of start of the specified period, for example if
     *         today is Tuesday and WEEK is passed, the date returned will be
     *         the date of the last monady at hour 00:00, similar for MONTH and YEAR.
     */
    fun getPeriodStartDate(period: Period): Date{
        //TODO: debug this function

        // create calendar instance of now
        val cal = Calendar.getInstance()

        // set first day on monday maybe it is sunday check it!
        cal.firstDayOfWeek = Calendar.MONDAY;

        // reset all the date variables
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.getTime()

        when(period){
            //Period.DAY -> do nothing, cal already point to the start of the day
            Period.WEEK -> cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek); // set as calendar day the start of this week
            Period.MONTH -> cal.set(Calendar.DAY_OF_MONTH, 1); // set as calendar day the start of this week
            Period.YEAR -> cal.set(Calendar.DAY_OF_YEAR, 1); // set as calendar day the start of this week
            //else -> throw IllegalArgumentException("Wrong period passed to getPeriodExpensesSum()")
        }
        cal.getTime()

        return cal.time
    }

    /**
     * Get sub periods dates
     *
     * @param period the period that will be subdivided into shorter intervals
     * @return a list of array of dates, in each array there are 2 Date, [start, end]
     *         the start Date at index 0 is the start of the period chunk, and at index 1
     *         there is the end of that period chunk.
     *         The returned list of periods cover the entire period passed
     *         The returned list has length dependant to the period type, if the period is
     *         YEAR the length will be 12, if MONTH is passed the length will vary with
     *         the actual month number of days.
     */
    fun getSubPeriodsDates(period: Period): List<Array<Date>>{

        var intervals = mutableListOf<Array<Date>>()

        val cal = Calendar.getInstance()
        // reset all the date variables to the start of the month
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.getTime()

        var start = Date()
        var end = Date()

        if(period == Period.MONTH){
            // set the day to the first of the month
            cal.set(Calendar.DAY_OF_MONTH, 1)
            start = cal.getTime()

            val numOfDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            // iter over each day of month
            for(i in 2..(numOfDays+1)){
                if(i < (numOfDays+1)) {
                    cal.set(Calendar.DAY_OF_MONTH, i)
                    end = cal.getTime()
                    intervals.add(arrayOf(start, end))
                    start = cal.getTime()
                }else{
                    // as the final end date pass 00:00:00 of the first day of the next month
                    cal.add(Calendar.MONTH, 1)
                    cal.set(Calendar.DAY_OF_MONTH, 1)
                    end = cal.getTime()
                    intervals.add(arrayOf(start, end))
                }
            }
        }else if(period == Period.YEAR){
            // set the day to the first of the year
            cal.set(Calendar.DAY_OF_YEAR, 1)
            start = cal.getTime()

            // iter over each month
            for(i in 2..13){
                if(i < 13){
                    cal.set(Calendar.MONTH, i)
                    cal.set(Calendar.DAY_OF_MONTH, 1)
                    end = cal.getTime()
                    intervals.add(arrayOf(start, end))
                    start = cal.getTime()
                }else{
                    // as the final end date pass 00:00:00 of the first day
                    // of the first mont of the next year.
                    cal.add(Calendar.YEAR, 1)
                    cal.set(Calendar.MONTH, 1)
                    cal.set(Calendar.DAY_OF_MONTH, 1)
                    end = cal.getTime()
                    intervals.add(arrayOf(start, end))
                }
            }
        }

        return intervals
    }

    // ##########################################################################
    // EXPENSES SUM METHODS

    /**
     * Get tot expenses
     *
     * @param start start date of interval considered for the sum
     * @param end end date of interval considered for the sum
     * @return sum off all expenses as float, related to the specified date interval
     */
    suspend fun getExpenses(
        start: Date = Date(0),
        end: Date = Date()
    ):Float{
        return aggregateDao.countAllExpensesBetweenDates(start, end)
    }

    /**
     * Get week expenses sum
     *
     * @param period enum that specify the time interval of interest
     * @return sum off all expenses of the specified period as float
     */
    suspend fun getPeriodExpensesSum(period: Period): Float{

        // return all the expenses from the start of the week to now
        return getExpenses(
            start = getPeriodStartDate(period)
        )
    }

    /**
     * Get period expenses
     *
     * @param period
     * @return
     */
    suspend fun getPeriodExpenses(period:Period): Array<Float>{

        val periodMaxSize = when(period){
            Period.MONTH -> Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
            Period.YEAR -> 12
            else -> throw IllegalArgumentException("Wrong period passed to getPeriodExpenses()")
        }
        
        val expenses = Array(periodMaxSize){0.0f}
        val subPeriods = getSubPeriodsDates(period)

        for(i in 1..periodMaxSize){
            if(i < subPeriods.size){
                expenses[i] = getExpenses(subPeriods[i][0], subPeriods[i][1])
            }
        }

        return expenses
    }

    // ##########################################################################
    // TAGS METHODS

    /**
     * Get aggregate tags
     *
     * @return an Array<String?> of each aggregate tags, only tag names are returned
     */
    suspend fun getAggregateTagNames(): Array<String?>{
        val tags = tagDao.getAggregateTags()
        if(tags != null) {
            return Array<String?>(tags.size) { tags[it].tag_name }
        }else{
            // if tags are not present return an empty array
            return Array<String?>(0){""}
        }
    }

    /**
     * Get element tags
     *
     * @return an Array<String?> of each element tags, only tag names are returned
     */
    suspend fun getElementTagNames(): Array<String?>{
        val tags = tagDao.getElementTags()
        if(tags != null) {
            return Array<String?>(tags.size) { tags[it].tag_name }
        }else{
            // if tags are not present return an empty array
            return Array<String?>(0){""}
        }
    }

    /**
     * Get aggregate tags and count
     * @param start the start date interval for the query. by default take 1-1-1970 as start date as filter
     * @param end the end date interval for the query. by default take the call moment as end date as filter
     * @return a map of each tag belong to an aggregate and the count of all aggregates that
     *         have this tag between start and end dates
     */
    suspend fun getAggregateTagsAndCount(
        start: Date = Date(0),
        end: Date = Date()
    ):Map<String?, Long>{
        // NOTA: poteva essere reso piu efficiente integrando le query ma non c'è tempo
        val tagsList = tagDao.getAggregateTags()
        var mapTagCount = mutableMapOf<String?, Long>()

        tagsList?.forEach {
            mapTagCount[it.tag_name] = aggregateDao.countAllAggregatesBetweenDatesByTag(start, end, it.tag_id)
        }

        return mapTagCount
    }

    /**
     * Get aggregate tags and count by period
     *
     * @param period
     * @return
     */
    suspend fun getAggregateTagsAndCountByPeriod(
        period: Period
    ):Map<String?, Long>{
        return getAggregateTagsAndCount(
            start = getPeriodStartDate(period)
        )
    }

    /**
     * Get aggregate tags and expenses
     * @param start the start date interval for the query. by default take 1-1-1970 as start date as filter
     * @param end the end date interval for the query. by default take the call moment as end date as filter
     * @return a map of each tag belong to an aggregate and the sum of all expenses
     *         for each specific tag between start and end dates
     */
    @Transaction
    suspend fun getAggregateTagsAndExpenses(
        start: Date = Date(0), // by default take 1-1-1970 as start date as filter
        end: Date = Date() // by default take the call moment as end date as filter
    ):Map<String?, Float>{
        val tagsList = tagDao.getAggregateTags()
        var mapTagExpenses = mutableMapOf<String?, Float>()

        tagsList?.forEach {
            mapTagExpenses[it.tag_name] = aggregateDao.countAllExpensesBetweenDatesByTag(start, end, it.tag_id)
        }
        return mapTagExpenses
    }

    /**
     * Get aggregate tags and expenses by period
     *
     * @param period
     * @return
     */
    suspend fun getAggregateTagsAndExpensesByPeriod(
        period: Period
    ):Map<String?, Float>{
        return getAggregateTagsAndExpenses(
            start = getPeriodStartDate(period)
        )
    }

    /**
     * Get element tags and count
     * @param start the start date interval for the query. by default take 1-1-1970 as start date as filter
     * @param end the end date interval for the query. by default take the call moment as end date as filter
     * @return a map of each tag belong to an element and the count of all elements that
     *         have this tag between start and end dates
     */
    suspend fun getElementTagsAndCount(
        start: Date = Date(0),
        end: Date = Date()
    ):Map<String?, Long>{
        // NOTA: poteva essere reso piu efficiente integrando le query ma non c'è tempo
        val tagsList = tagDao.getElementTags()
        var mapTagCount = mutableMapOf<String?, Long>()

        tagsList?.forEach {
            mapTagCount[it.tag_name] = elementDao.countAllSingleElementsBetweenDatesByElementTagId(start, end, it.tag_id)
        }

        return mapTagCount
    }

    /**
     * Get element tags and count by period
     *
     * @param period
     * @return
     */
    suspend fun getElementTagsAndCountByPeriod(
        period: Period
    ):Map<String?, Long>{
        return getElementTagsAndCount(
            start = getPeriodStartDate(period)
        )
    }

    /**
     * Get element tags and expenses
     * @param start the start date interval for the query. by default take 1-1-1970 as start date as filter
     * @param end the end date interval for the query. by default take the call moment as end date as filter
     * @return a map of each tag belong to an element and the sum of all expenses
     *         for each specific tag between start and end dates
     */
    @Transaction
    suspend fun getElementTagsAndExpenses(
        start: Date = Date(0), // by default take 1-1-1970 as start date as filter
        end: Date = Date() // by default take the call moment as end date as filter
    ):Map<String?, Float>{
        val tagsList = tagDao.getElementTags()
        var mapTagExpenses = mutableMapOf<String?, Float>()

        tagsList?.forEach {
            mapTagExpenses[it.tag_name] = elementDao.countAllExpensesBetweenDatesByElementTagId(start, end, it.tag_id)
        }
        return mapTagExpenses
    }

    /**
     * Get element tags and expenses by period
     *
     * @param period
     * @return
     */
    suspend fun getElementTagsAndExpensesByPeriod(
        period: Period
    ):Map<String?, Float>{
        return getElementTagsAndExpenses(
            start = getPeriodStartDate(period)
        )
    }


    suspend fun insertAggregateWithElements(
        aggregate: AddDataModel.Aggregate,
        elements: List<AddDataModel.Element>,
        attachmentUri: Uri?)
    {
        var date: Date? = null
        aggregate.str_date?.let { strDate ->
            date = SimpleDateFormat("dd/MM/yyyy").parse(strDate)
        }

        val dbAggregate = Aggregate(date = date, attachment = attachmentUri).also { it.tag = aggregate.tag }
        val dbElements = elements.map {
            if (it.cost == null || it.cost == null) throw IllegalArgumentException("cost or num cannot be null")

            Element(
                cost = it.cost?.toFloat() ?: 0f,
                name = it.name,
                num = it.num?.toLong() ?: 0L
            ).also { el ->
                el.elem_tag = it.elem_tag
            }
        }

        aggregateDao.insertAggregateWithElements(dbAggregate, dbElements)
    }

}