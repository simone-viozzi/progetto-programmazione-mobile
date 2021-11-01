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

    @Update
    suspend fun _updateElement(element: Element): Int

    @Update
    suspend fun _updateElementsList(element: List<Element>): Int

    @Transaction
    suspend fun updateAggregate(
        aggregate: Aggregate,
        tag_id: Long? = null,
        date: Date? = null,
        location: Location? = null,
        attachment: Uri? = null): Int{

        if(tag_id != null){
            aggregate.tag_id = tag_id
            // each element must be updated
            val elementsList: List<Element> = getElementsByAggregate(aggregate)
            elementsList.forEach { it.parent_tag_id = tag_id }
            _updateElementsList(elementsList)
        }
        if(date != null)        aggregate.date = date
        if(location != null)    aggregate.location = location
        if(attachment != null)  aggregate.attachment = attachment

        return _update(aggregate)
    }

    @Transaction
    suspend fun updateAggregateById(
        id: Long,
        tag_id: Long? = null,
        date: Date? = null,
        location: Location? = null,
        attachment: Uri? = null): Int{

        val aggregate = getAggregateById(id)
        return updateAggregate(aggregate, tag_id, date, location, attachment)
    }

    @Transaction
    suspend fun updateElement(
        element: Element,
        name: String? = null,
        num: Long? = null,
        elem_tag_id: Long? = null,
        cost: Float? = null
    ): Int{
        var updateAggregateFlag = false
        val oldNum = element.num
        val oldCost = element.cost
        val newNum = num ?: element.num
        val newCost = cost ?: element.cost

        if(name != null) element.name = name
        if(num != null){
            element.num = num
            updateAggregateFlag = true
        }
        if(elem_tag_id != null) element.elem_tag_id = elem_tag_id
        if(cost != null){
            element.cost = cost
            updateAggregateFlag = true
        }
        if(updateAggregateFlag){
            val aggregate = getAggregateByElement(element)
            aggregate.total_cost += ((newNum * newCost) - (oldNum * oldCost))
            _update(aggregate)
        }
        return _updateElement(element)
    }

    @Transaction
    suspend fun updateElementById(
        id: Long,
        name: String? = null,
        num: Long? = null,
        elem_tag_id: Long? = null,
        cost: Float? = null
    ): Int{
        val element = getElementById(id)
        return updateElement(element, name, num, elem_tag_id, cost)
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
    suspend fun _deleteElement(element: Element): Int

    @Query("DELETE FROM element WHERE element.aggregate_id = :aggregate_id")
    suspend fun _deleteElementsByAggregateId(aggregate_id: Long): Int

    @Query("DELETE FROM element")
    suspend fun _deleteAllElements(): Int

    @Transaction
    suspend fun deleteElement(element: Element): Int{
        val aggregate = getAggregateByElement(element)
        aggregate.total_cost = aggregate.total_cost - (element.cost * element.num)
        _update(aggregate)
        return _deleteElement(element)
    }

    @Transaction
    suspend fun deleteElementById(elem_id: Long): Int{
        val element: Element = getElementById(elem_id)
        return deleteElement(element)
    }

    // delete aggregates with elements queries

    @Transaction
    suspend fun deleteAggregateWithElements(aggregate: Aggregate){
        _deleteElementsByAggregateId(aggregate.id)
        _delete(aggregate)
    }

    @Transaction
    suspend fun deleteAggregateWithElementsById(id: Long){
        _deleteAggregateById(id)
        _deleteElementsByAggregateId(id)
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

    @Transaction
    suspend fun getAggregateByElement(element: Element): Aggregate{
        return getAggregateById(element.aggregate_id)
    }

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