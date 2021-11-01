package com.example.receiptApp.db.element

import androidx.room.*
import com.example.receiptApp.db.aggregate.Aggregate
import com.example.receiptApp.db.aggregate.AggregatesDao
import com.example.receiptApp.db.aggregate.BaseAggregateDao
import com.example.receiptApp.db.tag.TagsDao

/**
 * Elements dao
 *
 * @constructor Create empty Elements dao
 */

@Dao
interface ElementsDao : BaseAggregateDao, TagsDao {
    /////////////////////////////////////////
    // Insert queries

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insertElement(element: Element)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insertElementsList(elements: List<Element>)

    /////////////////////////////////////////
    // Update queries

    @Update
    suspend fun _updateElement(element: Element): Int

    @Update
    suspend fun _updateElementsList(element: List<Element>): Int

    @Transaction
    suspend fun updateElement(
        element: Element,
        name: String? = null,
        num: Long? = null,
        elem_tag_id: Long? = null,
        cost: Float? = null
    ): Int {
        var updateAggregateFlag = false
        val oldNum = element.num
        val oldCost = element.cost
        val newNum = num ?: element.num
        val newCost = cost ?: element.cost

        if (name != null) element.name = name
        if (num != null) {
            element.num = num
            updateAggregateFlag = true
        }
        if (elem_tag_id != null) element.elem_tag_id = elem_tag_id
        if (cost != null) {
            element.cost = cost
            updateAggregateFlag = true
        }
        if (updateAggregateFlag) {
            val aggregate = getAggregateByElement(element)
            aggregate.total_cost += ((newNum * newCost) - (oldNum * oldCost))
            _updateAggregate(aggregate)
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
    ): Int {
        val element = getElementById(id)
        return updateElement(element, name, num, elem_tag_id, cost)
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // delete elements queries

    @Delete
    suspend fun _deleteElement(element: Element): Int

    @Query("DELETE FROM element WHERE element.aggregate_id = :aggregate_id")
    suspend fun _deleteElementsByAggregateId(aggregate_id: Long): Int

    @Query("DELETE FROM element")
    suspend fun _deleteAllElements(): Int

    @Transaction
    suspend fun deleteElement(element: Element): Int {
        val aggregate = getAggregateByElement(element) //TODO ricreare il metodo accessibile dal elements dao
        aggregate.total_cost = aggregate.total_cost - (element.cost * element.num)
        _updateAggregate(aggregate)
        return _deleteElement(element)
    }

    @Transaction
    suspend fun deleteElementById(elem_id: Long): Int {
        val element: Element = getElementById(elem_id)
        return deleteElement(element)
    }

    /////////////////////////////////////////
    // Get count queries aggregates

    @Query("SELECT COUNT(*) FROM element")
    suspend fun countAllElements(): Long

    @Query("SELECT SUM(element.num) FROM element")
    suspend fun countAllSingleElements(): Long

    @Query("SELECT SUM(element.cost * element.num) FROM element WHERE element.elem_tag_id = :elem_tag_id")
    suspend fun countAllExpensesByElementTag(elem_tag_id: Long): Float

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
    suspend fun getElementsByAggregate(aggregate: Aggregate): List<Element> {
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



}