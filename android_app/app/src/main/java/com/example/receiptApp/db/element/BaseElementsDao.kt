package com.example.receiptApp.db.element

import androidx.room.*
import com.example.receiptApp.db.aggregate.Aggregate
import com.example.receiptApp.db.tag.TagsDao
import java.util.*

@Dao
interface BaseElementsDao : TagsDao {

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Insert queries

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insertElement(element: Element): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insertElementsList(elements: List<Element>)

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Update queries

    @Update
    suspend fun _updateElement(element: Element): Int

    @Update
    suspend fun _updateElementsList(element: List<Element>): Int

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // delete elements queries

    @Delete
    suspend fun _deleteElement(element: Element): Int

    @Delete
    suspend fun _deleteElementsList(elements: List<Element>): Int

    @Query("DELETE FROM element WHERE element.aggregate_id = :aggregate_id")
    suspend fun _deleteElementsByAggregateId(aggregate_id: Long?): Int

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Get count queries aggregates

    @Query("SELECT COUNT(*) FROM element")
    suspend fun _countAllElements(): Long

    @Query("SELECT COUNT(*) FROM element WHERE element.aggregate_id = :parent_id")
    suspend fun _countAllElementsByParentId(parent_id: Long?): Long

    @Query("SELECT SUM(element.num) FROM element")
    suspend fun _countAllSingleElements(): Long

    @Query("SELECT COUNT(*) FROM element WHERE element.elem_tag_id = :elem_tag_id")
    suspend fun _countAllElementsByTagId(elem_tag_id: Long?): Long

    @Query("SELECT COUNT(*) FROM element WHERE element.parent_tag_id = :parent_tag_id")
    suspend fun _countAllElementsByParentTagId(parent_tag_id: Long): Long

    @Query("SELECT SUM(element.cost * element.num) FROM element WHERE element.parent_tag_id = :parent_tag_id")
    suspend fun _countAllExpensesByParentTagId(parent_tag_id: Long): Float

    @Query("SELECT SUM(element.cost * element.num) FROM element WHERE element.elem_tag_id = :elem_tag_id")
    suspend fun _countAllExpensesByElementTagId(elem_tag_id: Long): Float





    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Get queries elemnts

    @Query("SELECT * FROM element ORDER BY elem_id DESC LIMIT 1")
    suspend fun _getLastElement(): Element

    @Query("SELECT * FROM element WHERE element.elem_id = :elem_id LIMIT 1")
    suspend fun _getElementById(elem_id: Long): Element

    @Query("SELECT * FROM element")
    suspend fun _getAllElements(): List<Element>

    @Query("SELECT * FROM element WHERE element.aggregate_id = :aggregateId")
    suspend fun _getElementsByAggregateId(aggregateId: Long?): List<Element>

    @Transaction
    suspend fun _getElementsByAggregate(aggregate: Aggregate): List<Element> {
        return _getElementsByAggregateId(aggregate.id)
    }

    @Query("SELECT * FROM element WHERE element.name = :name")
    suspend fun _getElementsByName(name: String): List<Element>

    @Query("SELECT * FROM element WHERE element.cost = :cost")
    suspend fun _getElementByCost(cost: Float): List<Element>

    @Query("SELECT * FROM element WHERE element.cost >= :start_cost")
    suspend fun _getElementOverCost(start_cost: Float): List<Element>

    @Query("SELECT * FROM element WHERE element.cost <= :end_cost")
    suspend fun _getElementUnderCost(end_cost: Float): List<Element>

    @Query("SELECT * FROM element WHERE element.cost >= :start_cost AND element.cost <= :end_cost")
    suspend fun _getElementBetweenCosts(start_cost: Float, end_cost: Float): List<Element>

    @Query("SELECT * from element WHERE element.parent_tag_id = :parent_tag_id")
    suspend fun _getElementByParentTag(parent_tag_id: Long): List<Element>

    @Query("SELECT * from element WHERE element.elem_tag_id = :elem_tag_id")
    suspend fun _getElementByTag(elem_tag_id: Long): List<Element>

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // query helpers

    @Transaction
    suspend fun _addTagNameToElement(element: Element): Element{
        val tag = getElementTagById(element.elem_tag_id)
        if (tag != null) {
            element.elem_tag = tag.tag_name
        }
        return element
    }

    @Transaction
    suspend fun _addTagNameToElementsList(elementsList: List<Element>, parentTag: String? = null): List<Element>{

        val tagList = getElementTags()?.groupBy{it.tag_id}

        elementsList.forEach {
            if(it.elem_tag_id != null) it.elem_tag = tagList?.get(it.elem_tag_id)?.get(0)?.tag_name
            if(parentTag != null) it.parent_tag = parentTag
        }

        return elementsList
    }

}