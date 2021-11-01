package com.example.receiptApp.db.aggregate

import android.location.Location
import android.net.Uri
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.example.receiptApp.db.element.Element
import com.example.receiptApp.db.element.ElementsDao
import com.example.receiptApp.db.tag.TagsDao
import java.util.*

/**
 * Aggregates dao
 *
 * Clear explanation of relation between Entity and how to use it
 * link: https://www.tutorialguruji.com/android/how-to-insert-entities-with-a-one-to-many-relationship-in-room/
 *
 *  Utilization policy:
 *  - The aggregates must be ever inserted with their list of elements
 *  - The deletion of one aggregate should be ever done with the managed method
 *
 *  NOTE: private methods seems can't be created so methods with this
 *        form "_methodName()" shouldn't be touched, otherway it isn't
 *        granted the correct behaviour of the db.
 *
 * @constructor Create empty Aggregates dao
 */

@Dao
interface AggregatesDao : BaseAggregateDao, ElementsDao, TagsDao {

    /////////////////////////////////////////
    // Insert queries

    @Transaction
    suspend fun insertAggregateWithElements(aggregate: Aggregate, elements: List<Element>): Long {

        val aggregateId = _insertAggregate(aggregate)

        elements.forEach { it.aggregate_id = aggregateId }

        _insertElementsList(elements)

        return aggregateId
    }

    /////////////////////////////////////////
    // Update queries

    @Transaction
    suspend fun updateAggregate(
        aggregate: Aggregate,
        tag_id: Long? = null,
        date: Date? = null,
        location: Location? = null,
        attachment: Uri? = null
    ): Int {

        if (tag_id != null) {
            aggregate.tag_id = tag_id
            // each element must be updated
            val elementsList: List<Element> = getElementsByAggregate(aggregate)
            elementsList.forEach { it.parent_tag_id = tag_id }
            _updateElementsList(elementsList)
        }

        if (date != null) aggregate.date = date
        if (location != null) aggregate.location = location
        if (attachment != null) aggregate.attachment = attachment

        return _updateAggregate(aggregate)
    }

    @Transaction
    suspend fun updateAggregateById(
        id: Long,
        tag_id: Long? = null,
        date: Date? = null,
        location: Location? = null,
        attachment: Uri? = null
    ): Int {

        val aggregate = getAggregateById(id)
        return updateAggregate(aggregate, tag_id, date, location, attachment)
    }

    /////////////////////////////////////////
    // Delete queries

    // delete aggregates with elements queries

    @Transaction
    suspend fun deleteAggregateWithElements(aggregate: Aggregate) {
        _deleteElementsByAggregateId(aggregate.id)
        _deleteAggregate(aggregate)
    }

    @Transaction
    suspend fun deleteAggregateWithElementsById(id: Long) {
        _deleteAggregateById(id)
        _deleteElementsByAggregateId(id)
    }

    @Transaction
    suspend fun deleteAllWithElements() {
        _deleteAllAggregates()
        _deleteAllElements()
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Get count queries aggregates

    @Query("SELECT COUNT(*) FROM aggregate")
    suspend fun countAllAggregates(): Long

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
        val lastAggregate: Aggregate = getLastAggregate()
        val elementsList: List<Element> = getElementsByAggregateId(lastAggregate.id)
        return mapOf(lastAggregate to elementsList)
    }

    @Transaction
    suspend fun getAggregateWithElementsById(id: Long): Map<Aggregate, List<Element>> {
        val aggregate: Aggregate = getAggregateById(id)
        val elementsList: List<Element> = getElementsByAggregateId(aggregate.id)
        return mapOf(aggregate to elementsList)
    }

    @Transaction
    suspend fun getAggregateWithElementsByDate(date: Date): Map<Aggregate, List<Element>> {
        val aggregatesList: List<Aggregate> = getAggregatesByDate(date)
        val resultMap = mutableMapOf<Aggregate, List<Element>>()
        for (aggregate in aggregatesList) {
            val listOfElements = getElementsByAggregateId(aggregate.id)
            resultMap[aggregate] = listOfElements
        }
        return resultMap
    }

    @Transaction
    suspend fun getAllAggregatesWithElements(): Map<Aggregate, List<Element>> {
        val aggregatesList: List<Aggregate> = getAllAggregates()
        val resultMap = mutableMapOf<Aggregate, List<Element>>()
        for (aggregate in aggregatesList) {
            val listOfElements = getElementsByAggregateId(aggregate.id)
            resultMap[aggregate] = listOfElements
        }
        return resultMap
    }

    @Transaction
    suspend fun getAggregateWithElementsUntilDate(date: Date): Map<Aggregate, List<Element>> {
        val aggregatesList: List<Aggregate> = getAggregatesByDate(date)
        val resultMap = mutableMapOf<Aggregate, List<Element>>()
        for (aggregate in aggregatesList) {
            val listOfElements = getElementsByAggregateId(aggregate.id)
            resultMap[aggregate] = listOfElements
        }
        return resultMap
    }

    @Transaction
    suspend fun getAggregateWithElementsAfterDate(date: Date): Map<Aggregate, List<Element>> {
        val aggregatesList: List<Aggregate> = getAggregatesAfterDate(date)
        val resultMap = mutableMapOf<Aggregate, List<Element>>()
        for (aggregate in aggregatesList) {
            val listOfElements = getElementsByAggregateId(aggregate.id)
            resultMap[aggregate] = listOfElements
        }
        return resultMap
    }

    @Transaction
    suspend fun getAggregateWithElementsBetweenDate(
        start_date: Date,
        end_date: Date
    ): Map<Aggregate, List<Element>> {
        val aggregatesList: List<Aggregate> = getAggregatesBetweenDate(start_date, end_date)
        val resultMap = mutableMapOf<Aggregate, List<Element>>()
        for (aggregate in aggregatesList) {
            val listOfElements = getElementsByAggregateId(aggregate.id)
            resultMap[aggregate] = listOfElements
        }
        return resultMap
    }

    @Transaction
    suspend fun getAggregateWithElementsByTag(tag_id: Long): Map<Aggregate, List<Element>> {
        val aggregatesList: List<Aggregate> = getAggregatesByTag(tag_id)
        val resultMap = mutableMapOf<Aggregate, List<Element>>()
        for (aggregate in aggregatesList) {
            val listOfElements = getElementsByAggregateId(aggregate.id)
            resultMap[aggregate] = listOfElements
        }
        return resultMap
    }
}