package com.example.receiptApp.repository

import android.net.Uri
import com.example.receiptApp.Utils.DatabaseTestHelper
import com.example.receiptApp.db.aggregate.Aggregate
import com.example.receiptApp.db.aggregate.PublicAggregatesDao
import com.example.receiptApp.db.element.Element
import com.example.receiptApp.db.element.PublicElementsDao
import com.example.receiptApp.db.tag.Tag
import com.example.receiptApp.db.tag.TagsDao
import com.example.receiptApp.pages.edit.EditDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        cal.time

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
            for(i in 1..12){
                if(i < 12){
                    cal.set(Calendar.MONTH, i)
                    cal.set(Calendar.DAY_OF_MONTH, 1)
                    end = cal.getTime()
                    intervals.add(arrayOf(start, end))
                    start = cal.getTime()
                }else{
                    // as the final end date pass 00:00:00 of the first day
                    // of the first mont of the next year.
                    cal.add(Calendar.YEAR, 1)
                    cal.set(Calendar.MONTH, 0)
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
     * Category: label
     * @param start start date of interval considered for the sum
     * @param end end date of interval considered for the sum
     * @return sum off all expenses as float, related to the specified date interval
     */
    suspend fun getExpenses(
        start: Date = Date(0),
        end: Date = Date()
    ):Float?{
        return aggregateDao.countAllExpensesBetweenDates(start, end)
    }

    /**
     * Get week expenses sum
     * Category: label
     * @param period enum that specify the time interval of interest
     * @return sum off all expenses of the specified period as float
     */
    suspend fun getPeriodExpensesSum(period: Period): Float?{

        // return all the expenses from the start of the week to now
        return getExpenses(
            start = getPeriodStartDate(period)
        )
    }

    /**
     * Get period expenses
     * Category: Histogram
     * @param period
     * @return
     */
    suspend fun getPeriodExpenses(period:Period): Array<Float>{

        val periodSize = when(period){
            Period.MONTH -> Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
            Period.YEAR -> 12
            else -> throw IllegalArgumentException("Wrong period passed to getPeriodExpenses()")
        }

        val expenses = Array(periodSize){0.0f}
        val subPeriods = getSubPeriodsDates(period)

        if(periodSize != subPeriods.size) throw Exception("Unexpected periodSize != subPeriods.size in DbRepository::getPeriodExpenses()")

        for(i in 0 until periodSize){
            getExpenses(subPeriods[i][0], subPeriods[i][1])?.let{
                expenses[i] = it
            }
        }

        return expenses
    }



    // ##########################################################################
    // TAGS METHODS

    /**
     * Get aggregate tags
     * Category: helper, histogram, pie
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
     * Category: helper, histogram, pie
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
     * Category: histogram, pie
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
            val result = aggregateDao.countAllAggregatesBetweenDatesByTag(start, end, it.tag_id)

            if(result != null){
                mapTagCount[it.tag_name] = result
            }else{
                mapTagCount[it.tag_name] = 0L
            }
        }

        return mapTagCount.toList().sortedByDescending { pair -> pair.second }.toMap()
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
     * Get aggregate count by tag
     *
     * @param tag_name
     * @param start
     * @param end
     * @return
     */
    suspend fun getAggregateCountByTag(
        tag_name: String,
        start: Date = Date(0),
        end: Date = Date(),
    ): Long?{
        val tag = tagDao.getAggregateTagByName(tag_name)
        return if(tag != null)
            aggregateDao.countAllAggregatesBetweenDatesByTag(start, end, tag.tag_id)
        else
            null
    }

    /**
     * Get aggregate count by tag and period
     *
     * @param tag_name
     * @param period
     * @return
     */
    suspend fun getAggregateCountByTagAndPeriod(
        tag_name: String,
        period: Period
    ): Long?{
        return getAggregateCountByTag(
            tag_name = tag_name,
            start = getPeriodStartDate(period)
        )
    }

    /**
     * Get aggregate tags and expenses
     * Category:
     * @param start the start date interval for the query. by default take 1-1-1970 as start date as filter
     * @param end the end date interval for the query. by default take the call moment as end date as filter
     * @return a map of each tag belong to an aggregate and the sum of all expenses
     *         for each specific tag between start and end dates
     */
    suspend fun getAggregateTagsAndExpenses(
        start: Date = Date(0), // by default take 1-1-1970 as start date as filter
        end: Date = Date() // by default take the call moment as end date as filter
    ):Map<String?, Float>{
        val tagsList = tagDao.getAggregateTags()
        var mapTagExpenses = mutableMapOf<String?, Float>()

        tagsList?.forEach {
            val result = aggregateDao.countAllExpensesBetweenDatesByTag(start, end, it.tag_id)
            if(result != null){
                mapTagExpenses[it.tag_name] = result
            }else{
                mapTagExpenses[it.tag_name] = 0.0f
            }
        }

        return mapTagExpenses.toList().sortedByDescending { pair -> pair.second }.toMap()
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
     * Get aggregate expenses by tag
     *
     * @param tag_name
     * @param start
     * @param end
     * @return
     */
    suspend fun getAggregateExpensesByTag(
        tag_name: String,
        start: Date = Date(0), // by default take 1-1-1970 as start date as filter
        end: Date = Date() // by default take the call moment as end date as filter
    ): Float?{
        val tag = tagDao.getAggregateTagByName(tag_name)
        return if(tag != null)
                aggregateDao.countAllExpensesBetweenDatesByTag(start_date = start, end_date = end, tag.tag_id)
            else
                null
    }

    /**
     * Get aggregate expenses by tag and period
     *
     * @param tag_name
     * @param period
     * @return
     */
    suspend fun getAggregateExpensesByTagAndPeriod(
        tag_name: String,
        period: Period
    ): Float?{
        return getAggregateExpensesByTag(
            tag_name = tag_name,
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
            mapTagCount[it.tag_name] = elementDao.countAllSingleElementsBetweenDatesByElementTagId(start, end, it.tag_id) ?: 0
        }

        return mapTagCount.toList().sortedByDescending { pair -> pair.second }.toMap()
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
     * Get element count by tag
     *
     * @param tag_name
     * @param start
     * @param end
     * @return
     */
    suspend fun getElementCountByTag(
        tag_name: String,
        start: Date = Date(0),
        end: Date = Date(),
    ): Long?{
        val tag = tagDao.getElementTagByName(tag_name)
        return if(tag != null)
            elementDao.countAllSingleElementsBetweenDatesByElementTagId(start, end, tag.tag_id)
        else
            null
    }

    /**
     * Get element count by tag and period
     *
     * @param tag_name
     * @param period
     * @return
     */
    suspend fun getElementCountByTagAndPeriod(
        tag_name: String,
        period: Period
    ): Long?{
        return getElementCountByTag(
            tag_name = tag_name,
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
    suspend fun getElementTagsAndExpenses(
        start: Date = Date(0), // by default take 1-1-1970 as start date as filter
        end: Date = Date() // by default take the call moment as end date as filter
    ):Map<String?, Float>{
        val tagsList = tagDao.getElementTags()
        var mapTagExpenses = mutableMapOf<String?, Float>()

        tagsList?.forEach {
            val expense = elementDao.countAllExpensesBetweenDatesByElementTagId(start, end, it.tag_id)
            mapTagExpenses[it.tag_name] = expense ?: 0.0f
        }
        return mapTagExpenses.toList().sortedByDescending { pair -> pair.second }.toMap()
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

    /**
     * Get aggregate expenses by tag
     *
     * @param tag_name
     * @param start
     * @param end
     * @return
     */
    suspend fun getElementExpensesByTag(
        tag_name: String,
        start: Date = Date(0), // by default take 1-1-1970 as start date as filter
        end: Date = Date() // by default take the call moment as end date as filter
    ): Float? {
        val tag = tagDao.getElementTagByName(tag_name)
        return tag?.let {
            elementDao.countAllExpensesBetweenDatesByElementTagId(
                start_date = start,
                end_date = end,
                it.tag_id
            )
        }
    }

    /**
     * Get aggregate expenses by tag and period
     *
     * @param tag_name
     * @param period
     * @return
     */
    suspend fun getElementExpensesByTagAndPeriod(
        tag_name: String,
        period: Period
    ): Float?{
        return getElementExpensesByTag(
            tag_name = tag_name,
            start = getPeriodStartDate(period)
        )
    }

    // ##########################################################################
    // INSERT METHODS

    suspend fun insertAggregateWithElements(
        aggregate: EditDataModel.Aggregate,
        elements: List<EditDataModel.Element>,
        attachmentUri: Uri?
    ) = withContext(Dispatchers.IO) {

        val (dbAggregate, dbElements) = convertAggregateElementsToDbFormat(
            aggregate,
            attachmentUri,
            elements
        )

        aggregateDao.insertAggregateWithElements(dbAggregate, dbElements)
    }

    // ##########################################################################
    // GET METHODS

    suspend fun getAggregates(
        tag_name: String? = null,
        start: Date? = null, // by default take 1-1-1970 as start date as filter
        end: Date? = null // by default take the call moment as end date as filter
    ): List<Aggregate>?{

        val tag: Tag? = tag_name?.let{ tagDao.getAggregateTagByName(tag_name) }
        return aggregateDao.getAggregates(
            start_date = start ?: Date(0),
            end_date = end ?: Date(0),
            tag?.tag_id
        )
    }

    suspend fun getAggregateWithElementsById(id: Long): Map<Aggregate, List<Element>>{
        return aggregateDao.getAggregateWithElementsById(id)
    }

    suspend fun getFisrtAggregateId(): Long? {
        return aggregateDao.getFisrtAggregateId()
    }

    // ##########################################################################
    // UPDATE METHODS

    suspend fun updateAggregateWithElements(
        aggregate: EditDataModel.Aggregate,
        elements: List<EditDataModel.Element>,
        attachmentUri: Uri?
    ) {
        val (dbAggregate, dbElements) = convertAggregateElementsToDbFormat(
            aggregate,
            attachmentUri,
            elements
        )

        aggregateDao.updateAggregateById(
            dbAggregate.id
                ?: throw java.lang.IllegalArgumentException("cannot update something if the id is not present"),
            tag_name = dbAggregate.tag,
            date = dbAggregate.date,
            location = null,
            attachment = dbAggregate.attachment
        )

        dbElements.forEach { el ->
            el.elem_id?.let { id ->
                elementDao.updateElementById(
                    id = id,
                    name = el.name,
                    num = el.num,
                    elem_tag = el.elem_tag,
                    cost = el.cost
                )
            } ?: aggregateDao.addElementToAggregateById(el, dbAggregate.id)
        }
    }


    // ##########################################################################
    // DELETE METHODS

    suspend fun clearDb() {
        aggregateDao.deleteAll()
    }

    suspend fun deleteAggregateWithElements(id: Long){
        aggregateDao.deleteAggregateById(id)
    }

    // ##########################################################################
    // HELPER METHODS

    suspend fun dbIsEmpty(): Boolean {
        val aggrNum = aggregateDao.countAllAggregates()
        return aggrNum == null || aggrNum == 0L
    }

    private fun convertAggregateElementsToDbFormat(
        aggregate: EditDataModel.Aggregate,
        attachmentUri: Uri?,
        elements: List<EditDataModel.Element>
    ): Pair<Aggregate, List<Element>> {

        var date: Date? = null
        aggregate.str_date?.let { strDate ->
            date = SimpleDateFormat("dd/MM/yyyy").parse(strDate)
        }

        val dbAggregate = Aggregate(
            id = aggregate.dbId,
            date = date,
            attachment = attachmentUri
        ).also { it.tag = aggregate.tag }
        val dbElements = elements.map {
            if (it.cost == null || it.cost == null) throw IllegalArgumentException("cost or num cannot be null")

            Element(
                elem_id = it.dbId,
                cost = it.cost?.toFloat() ?: 0f,
                name = it.name,
                num = it.num?.toLong() ?: 0L
            ).also { el ->
                el.elem_tag = it.elem_tag
            }
        }
        return Pair(dbAggregate, dbElements)
    }

    // ##########################################################################
    // DEBUG METHODS

    suspend fun RandomFillDatabase() {
        // TODO solo per il debug, rimuovere

        val aggregatesList = mutableListOf<Aggregate>()
        val listOfElementsLists = mutableListOf<List<Element>>()
        val aggregateIdsList = mutableListOf<Long>()

        val aggregateTagsList =
            listOf<String>(
                "alimentari",
                "banca",
                "macchina",
                "bollette",
                "viaggi"
            )

        val elementTagsList =
            listOf<String>(
            "colazione",
            "tagliando auto",
            "acqua",
            "luce",
            "biglietto aereo",
            "cornetto",
            "mouse",
            "bullone",
            "taralli"
        )

        // loading graphs data
        DatabaseTestHelper.generateAgregatesAndElements(
            aggregatesList = aggregatesList,
            listOfElementsLists = listOfElementsLists,
            aggregateIdsList = aggregateIdsList,
            aggregateTagsList = aggregateTagsList,
            elementTagsList = elementTagsList,
            aggregatesDao = aggregateDao,
            aggr_num = 150,
            elem_num = 5,
            elem_num_casual = true
        )
    }
}