package com.example.receiptApp.db.aggregate

import android.location.Location
import android.net.Uri
import androidx.room.Query
import androidx.room.Transaction
import com.example.receiptApp.db.element.BaseElementsDao
import com.example.receiptApp.db.element.Element
import java.util.*

interface PublicAggregatesDao : AggregatesDao, BaseAggregatesDao, BaseElementsDao {

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Insert queries

    /**
     * Insert aggregate with elements
     *
     * @param aggregate <Aggregate>
     * @param elements <List<Element>>
     * @return
     */

    @Transaction
    suspend fun insertAggregateWithElements(aggregate: Aggregate, elements: List<Element>): Long {

        val aggregateId = _insertAggregateWithTag(aggregate)
        val newAggregate = _getAggregateById(aggregateId)
        elements.forEach {
            it.aggregate_id = aggregateId
            it.parent_tag_id = newAggregate.tag_id
        }

        _insertElementsListWithTag(elements)

        return aggregateId
    }

    /**
     * Add element to aggregate by id
     *
     *  This method add an element to an existing aggregate.
     *  If the aggregate doesn't exist in the database it dose nothing.
     *  the element should contain the elem_tag_name if needed, the elem_tag_id it's not necessary.
     *  This method automatically update the total_cost inside the parent aggregate
     *
     *  ! NOTE: this method work on the copy of the aggregate inside the database
     *  any modification to the copy passed isn't considered, only the aggregate id is evaluated.
     *
     * @param element
     * @param aggregateId
     */

    @Transaction
    suspend fun addElementToAggregateById(element: Element, aggregateId: Long){

        val newNum = if(element.num >= 0) element.num else 0
        val newCost = if(element.cost >= 0) element.cost else 0.0f

        // the first step is check if the aggrgegate exist
        var aggregate = _getAggregateById(aggregateId)
        if(aggregate != null){
            // if the aggregate exist
            aggregate.total_cost += (((newNum as Float) * newCost))
            _updateAggregate(aggregate)

            element.parent_tag_id = aggregate.tag_id

            _insertElementWithTag(element)
        }
        // else do nothing
    }

    @Transaction
    suspend fun addElementToAggregate(element: Element, aggregate: Aggregate){
        addElementToAggregateById(element, aggregate.id)
        // else do nothing
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Update queries

    /**
     * Update aggregate
     *
     * Method for aggregate update, all the parameters are changed only if passed,
     * all the parameters can be cancelled from the aggregate passing a special
     * value not null except the date value that can't be cancelled.
     *
     * @param aggregate  aggregate on which apply changes
     * @param tag_name  name of the tag to apply to the aggregate, if passed as empty string (es. "")
     *                   in the database it will be changed with a null value, if passed as null or not passed
     *                   it won't change.
     * @param date  This field can't be deleted from the aggregate in database if passed it will be changed
     *               inside the database otherwise if null or not passed it won't change.
     * @param location  This parameter will be changed with a null value inside the db if passed as 0.0, 0.0
     *                   latitude longitude location, if passed as any other value it will be changed, and if passed as
     *                   null value or not passed it won't change
     * @param attachment  This parameter will be changed if an uri is passed with at least one character,
     *                     if an empty uri is passed it will be deletted from the database.
     * @return The id of the new tag attached to the aggregate
     */

    @Transaction
    suspend fun updateAggregate(
        aggregate: Aggregate,
        tag_name: String? = null,
        date: Date? = null,
        location: Location? = null,
        attachment: Uri? = null
    ): Int {

        if (tag_name != null) {

            // if an empty string is passed as new value delete the tag from the aggregate
            aggregate.tag = if(tag_name == "") null else tag_name

            // procedure of tag updating
            val tag_id = _updateAggregateTag(aggregate)

            aggregate.tag_id = tag_id
            // each element must be updated
            val elementsList: List<Element> = _getElementsByAggregate(aggregate)
            elementsList.forEach { it.parent_tag_id = tag_id }
            _updateElementsList(elementsList)
        }

        if (date != null) aggregate.date = date
        if (location != null){
            // if passed a value with a 0.0, 0.0 position the location will be cancelled
            aggregate.location = if(
                location.latitude == 0.0 &&
                location.longitude == 0.0) null else location
        }
        if (attachment != null){
            // if passed an Empty Uri it will be cancelled from the database
            aggregate.attachment = if(attachment == Uri.EMPTY) null else attachment
        }

        return _updateAggregate(aggregate)
    }

    @Transaction
    suspend fun updateAggregateById(
        id: Long,
        tag_name: String? = null,
        date: Date? = null,
        location: Location? = null,
        attachment: Uri? = null
    ): Int {

        val aggregate = _getAggregateById(id)
        return updateAggregate(aggregate, tag_name, date, location, attachment)
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Delete queries

    @Transaction
    suspend fun deleteAggregate(aggregate: Aggregate) {
        _deleteAggregateWithElements(aggregate)
    }

    @Transaction
    suspend fun deleteAggregateById(id: Long) {
        _deleteAggregateWithElementsById(id)
    }

    @Transaction
    suspend fun deleteAll() {
        _deleteAllAggregates()
        _deleteAllElements()
        _deleteAllTags()
    }

    // TODO: aggiungere il metodo pubblico per eliminare un elemento da un aggregato

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Get count queries aggregates

    @Query("SELECT COUNT(*) FROM aggregate")
    suspend fun countAllAggregates(): Long

    @Transaction
    suspend fun countAllAggregatesByTagId(tag_id: Long?): Long{
        return _countAllAggregatesByTagId(tag_id)
    }

    @Query("SELECT SUM(aggregate.total_cost) FROM aggregate")
    suspend fun countAllExpenses(): Float

    @Query("SELECT SUM(aggregate.total_cost) FROM aggregate WHERE aggregate.date <= :date")
    suspend fun countAllExpensesBeforeDate(date: Date): Float

    @Query("SELECT SUM(aggregate.total_cost) FROM aggregate WHERE aggregate.date >= :date")
    suspend fun countAllExpensesAfterDate(date: Date): Float

    @Query("SELECT SUM(aggregate.total_cost) FROM aggregate WHERE aggregate.date >= :start_date AND aggregate.date <= :end_date")
    suspend fun countAllExpensesBetweenDates(start_date: Date, end_date: Date): Float

    @Query("SELECT SUM(aggregate.total_cost) FROM aggregate WHERE aggregate.tag_id = :tag_id")
    suspend fun countAllExpensesByTag(tag_id: Long): Float

    // TODO: aggiungere funzioni per filtraggio in base a data e tag

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Get queries aggregates with elements

    @Transaction
    suspend fun getLastAggregateWithElements(): Map<Aggregate, List<Element>> {
        val resultWithoutTags = _getLastAggregateWithElements()
        return _addTagNameToAggregatesListWithElements(resultWithoutTags)
    }

    @Transaction
    suspend fun getAggregateWithElementsById(id: Long): Map<Aggregate, List<Element>> {
        val resultWithoutTags = _getAggregateWithElementsById(id)
        return _addTagNameToAggregatesListWithElements(resultWithoutTags)
    }

    @Transaction
    suspend fun getAggregateWithElementsByDate(date: Date): Map<Aggregate, List<Element>> {
        val resultWithoutTags = _getAggregateWithElementsByDate(date)
        return _addTagNameToAggregatesListWithElements(resultWithoutTags)
    }

    @Transaction
    suspend fun getAllAggregatesWithElements(): Map<Aggregate, List<Element>> {
        val resultWithoutTags = _getAllAggregatesWithElements()
        return _addTagNameToAggregatesListWithElements(resultWithoutTags)
    }

    @Transaction
    suspend fun getAggregateWithElementsUntilDate(date: Date): Map<Aggregate, List<Element>> {
        val resultWithoutTags = _getAggregateWithElementsUntilDate(date)
        return _addTagNameToAggregatesListWithElements(resultWithoutTags)
    }

    @Transaction
    suspend fun getAggregateWithElementsAfterDate(date: Date): Map<Aggregate, List<Element>> {
        val resultWithoutTags = _getAggregateWithElementsAfterDate(date)
        return _addTagNameToAggregatesListWithElements(resultWithoutTags)
    }

    @Transaction
    suspend fun getAggregateWithElementsBetweenDate(
        start_date: Date,
        end_date: Date
    ): Map<Aggregate, List<Element>> {
        val resultWithoutTags = _getAggregateWithElementsBetweenDate(start_date, end_date)
        return _addTagNameToAggregatesListWithElements(resultWithoutTags)
    }

    @Transaction
    suspend fun getAggregateWithElementsByTag(tag_id: Long): Map<Aggregate, List<Element>> {
        val resultWithoutTags = _getAggregateWithElementsByTag(tag_id)
        return _addTagNameToAggregatesListWithElements(resultWithoutTags)
    }

}
