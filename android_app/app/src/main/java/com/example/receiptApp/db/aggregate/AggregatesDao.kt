package com.example.receiptApp.db.aggregate

import android.location.Location
import android.net.Uri
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.example.receiptApp.db.element.Element
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
interface AggregatesDao
{

    /////////////////////////////////////////
    // Insert queries

    @Insert(onConflict = REPLACE)
    suspend fun _insert(aggregate: Aggregate): Long

    @Insert(onConflict = REPLACE)
    suspend fun _insertList(aggregates: List<Aggregate>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insertElementList(elements: List<Element>)

    @Transaction
    suspend fun insertWithElements(aggregate: Aggregate, elements: List<Element>): Long {

        val aggregateId = _insert(aggregate)

        elements.forEach { it.aggregate_id = aggregateId }

        _insertElementList(elements)

        return aggregateId
    }

    /////////////////////////////////////////
    // Update queries

    @Update
    suspend fun _update(aggregate: Aggregate): Int

    @Update
    suspend fun _updateList(aggregates: List<Aggregate>): Int

    @Query("UPDATE aggregate SET tag_id = :new_tag_id WHERE id = :id")
    suspend fun _updateAgregateTag(id: Long, new_tag_id: Long): Int

    @Query("UPDATE aggregate SET date = :date WHERE id = :id")
    suspend fun _updateAgregateDate(id: Long, date: Date): Int

    @Query("UPDATE aggregate SET location = :location WHERE id = :id")
    suspend fun _updateAgregateLocation(id: Long, location: Location): Int

    @Query("UPDATE aggregate SET attachment = :attachment WHERE id = :id")
    suspend fun _updateAgregateAttachment(id: Long, attachment: Uri): Int

    @Transaction
    suspend fun updateAggregate(
        id: Long,
        tag_id: Long? = null,
        date: Date? = null,
        location: Location? = null,
        attachment: Uri? = null): Int{

        var succesUpdates: Int = 0
        if(tag_id != null)      succesUpdates += _updateAgregateTag(id,tag_id)
        if(date != null)        succesUpdates += _updateAgregateDate(id, date)
        if(location != null)    succesUpdates += _updateAgregateLocation(id, location)
        if(attachment != null)  succesUpdates += _updateAgregateAttachment(id, attachment)

        return succesUpdates
    }

    /////////////////////////////////////////
    // Delete queries

    // delete aggregates queries
    @Delete
    suspend fun _delete(aggregate: Aggregate)

    @Delete
    suspend fun _deleteList(aggregates: List<Aggregate>)

    @Query("DELETE FROM aggregate WHERE aggregate.id = :id")
    suspend fun _deleteAggregateById(id: Long)

    @Query("DELETE FROM aggregate")
    suspend fun _deleteAll()

    // delete elements queries

    @Delete
    suspend fun _deleteElement(element: Element)

    @Query("DELETE FROM element WHERE element.aggregate_id = :aggregate_id")
    suspend fun _deleteElementByAggregateId(aggregate_id: Long)

    @Query("DELETE FROM element")
    suspend fun _deleteAllElements()

    // delete aggregates with elements queries

    @Transaction
    suspend fun deleteAggregateWithElements(aggregate: Aggregate){
        _deleteElementByAggregateId(aggregate.id)
        _delete(aggregate)
    }

    @Transaction
    suspend fun deleteAggregateWithElementsById(id: Long){
        _deleteAggregateById(id)
        _deleteElementByAggregateId(id)
    }

    @Transaction
    suspend fun deleteAllWithElements(){
        _deleteAll()
        _deleteAllElements()
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Get count queries aggregates

    @Query("SELECT COUNT(*) FROM aggregate")
    suspend fun countAllAggregates(): Long

    @Query("SELECT COUNT(*) FROM element")
    suspend fun countAllElements(): Long

    @Query("SELECT SUM(element.num) FROM element")
    suspend fun countAllSingleElements(): Long

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

    @Query("SELECT SUM(element.cost * element.num) FROM element WHERE element.elem_tag_id = :elem_tag_id")
    suspend fun countAllExpensesByElementTag(elem_tag_id: Long): Float

    // TODO: aggiungere funzioni per filtraggio in base a data e tag

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Get queries aggregates

    @Query("SELECT * FROM aggregate ORDER BY id DESC LIMIT 1")
    suspend fun getLastAggregate(): Aggregate

    @Query("SELECT * FROM aggregate WHERE aggregate.id = :id LIMIT 1")
    suspend fun getAggregateById(id: Long): Aggregate

    @Query("SELECT * FROM aggregate")
    suspend fun getAllAggregates(): List<Aggregate>

    @Query("SELECT * FROM aggregate WHERE aggregate.date = :date")
    suspend fun getAggregatesByDate(date: Date): List<Aggregate>

    @Query("SELECT * FROM aggregate WHERE aggregate.date <= :date")
    suspend fun getAggregatesUntilDate(date: Date): List<Aggregate>

    @Query("SELECT * FROM aggregate WHERE aggregate.date >= :date")
    suspend fun getAggregatesAfterDate(date: Date): List<Aggregate>

    @Query("SELECT * FROM aggregate WHERE aggregate.date >= :dstart AND aggregate.date <= :dend")
    suspend fun getAggregatesBetweenDate(dstart: Date, dend: Date): List<Aggregate>

    @Query("SELECT * from aggregate WHERE aggregate.tag_id = :tag")
    suspend fun getAggregatesByTag(tag: Long): List<Aggregate>

    //TODO: aggiungere funzioni per fitraggio in base a data e tag

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Get queries elemnts

    @Query("SELECT * FROM element ORDER BY elem_id DESC LIMIT 1")
    suspend fun getLastElement(): Element

    @Query("SELECT * FROM element WHERE element.elem_id = :elem_id LIMIT 1")
    suspend fun getElementById(elem_id: Long): Element

    @Query("SELECT * FROM element")
    suspend fun getAllElements(): List<Element>

    @Query("SELECT * FROM element WHERE element.aggregate_id = :aggregateId")
    suspend fun getElementsByAggregateId(aggregateId: Long): List<Element>

    @Transaction
    suspend fun getElementsByAggregate(aggregate: Aggregate): List<Element>{
        return getElementsByAggregateId(aggregate.id)
    }

    @Query("SELECT * FROM element WHERE element.name = :name")
    suspend fun getElementsByName(name: String): List<Element>

    @Query("SELECT * FROM element WHERE element.cost = :cost")
    suspend fun getElementByCost(cost: Float): List<Element>

    @Query("SELECT * FROM element WHERE element.cost >= :start_cost")
    suspend fun getElementOverCost(start_cost: Float): List<Element>

    @Query("SELECT * FROM element WHERE element.cost <= :end_cost")
    suspend fun getElementUnderCost(end_cost: Float): List<Element>

    @Query("SELECT * FROM element WHERE element.cost >= :start_cost AND element.cost <= :end_cost")
    suspend fun getElementBetweenCosts(start_cost: Float, end_cost: Float): List<Element>

    @Query("SELECT * from element WHERE element.parent_tag_id = :parent_tag_id")
    suspend fun getElementByParentTag(parent_tag_id: Long): List<Element>

    @Query("SELECT * from element WHERE element.elem_tag_id = :elem_tag_id")
    suspend fun getElementByTag(elem_tag_id: Long): List<Element>

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
    suspend fun getAggregateWithElementsById(id: Long): Map<Aggregate, List<Element>>{
        val aggregate: Aggregate = getAggregateById(id)
        val elementsList: List<Element> = getElementsByAggregateId(aggregate.id)
        return mapOf(aggregate to elementsList)
    }

    @Transaction
    suspend fun getAggregateWithElementsByDate(date: Date): Map<Aggregate, List<Element>>{
        val aggregatesList: List<Aggregate> = getAggregatesByDate(date)
        val resultMap = mutableMapOf<Aggregate, List<Element>>()
        for(aggregate in aggregatesList){
            val listOfElements = getElementsByAggregateId(aggregate.id)
            resultMap[aggregate] = listOfElements
        }
        return resultMap
    }

    @Transaction
    suspend fun getAllAggregatesWithElements(): Map<Aggregate, List<Element>>{
        val aggregatesList: List<Aggregate> = getAllAggregates()
        val resultMap = mutableMapOf<Aggregate, List<Element>>()
        for(aggregate in aggregatesList){
            val listOfElements = getElementsByAggregateId(aggregate.id)
            resultMap[aggregate] = listOfElements
        }
        return resultMap
    }

    @Transaction
    suspend fun getAggregateWithElementsUntilDate(date: Date): Map<Aggregate, List<Element>>{
        val aggregatesList: List<Aggregate> = getAggregatesByDate(date)
        val resultMap = mutableMapOf<Aggregate, List<Element>>()
        for(aggregate in aggregatesList){
            val listOfElements = getElementsByAggregateId(aggregate.id)
            resultMap[aggregate] = listOfElements
        }
        return resultMap
    }

    @Transaction
    suspend fun getAggregateWithElementsAfterDate(date: Date): Map<Aggregate, List<Element>>{
        val aggregatesList: List<Aggregate> = getAggregatesAfterDate(date)
        val resultMap = mutableMapOf<Aggregate, List<Element>>()
        for(aggregate in aggregatesList){
            val listOfElements = getElementsByAggregateId(aggregate.id)
            resultMap[aggregate] = listOfElements
        }
        return resultMap
    }

    @Transaction
    suspend fun getAggregateWithElementsBetweenDate(start_date: Date, end_date: Date): Map<Aggregate, List<Element>>{
        val aggregatesList: List<Aggregate> = getAggregatesBetweenDate(start_date, end_date)
        val resultMap = mutableMapOf<Aggregate, List<Element>>()
        for(aggregate in aggregatesList){
            val listOfElements = getElementsByAggregateId(aggregate.id)
            resultMap[aggregate] = listOfElements
        }
        return resultMap
    }

    @Transaction
    suspend fun getAggregateWithElementsByTag(tag_id: Long): Map<Aggregate, List<Element>>{
        val aggregatesList: List<Aggregate> = getAggregatesByTag(tag_id)
        val resultMap = mutableMapOf<Aggregate, List<Element>>()
        for(aggregate in aggregatesList){
            val listOfElements = getElementsByAggregateId(aggregate.id)
            resultMap[aggregate] = listOfElements
        }
        return resultMap
    }
}