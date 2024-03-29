package com.example.receiptApp.db.element

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.receiptApp.db.aggregate.Aggregate
import java.util.*

@Dao
interface PublicElementsDao : ElementsDao{

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Insert queries

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Update queries

    /**
     * Update element
     *
     * Method for element update, all the parameters are changed only if passed,
     * all the parameters can be cancelled from the element passing a special
     * value not null, except the cost value that can't be cancelled.
     *
     * @param element
     * @param name
     * @param num
     * @param elem_tag
     * @param cost
     * @return
     */

    @Transaction
    suspend fun updateElement(
        element: Element,
        name: String? = null,
        num: Long? = null,
        elem_tag: String? = null,
        cost: Float? = null
    ): Int {

        var updateAggregateFlag = false
        val oldNum = element.num
        val oldCost = element.cost
        val newNum = num ?: element.num
        val newCost = cost ?: element.cost

        // get the parent aggregate
        val aggregate = _getAggregateByElement(element)

        // this check is needed for secure fields integrity
        // if element is passed with wrong parent_tag_id this field
        // is overwritten with the correct one
        element.parent_tag_id = aggregate.tag_id

        if (name != null) element.name = name

        if (num != null && num >= 1) {
            element.num = num
            updateAggregateFlag = true
        }

        if (elem_tag != null) {
            // if an empty string is passed as new value delete the tag from the aggregate
            element.elem_tag = if(elem_tag == "") null else elem_tag

            // procedure of tag updating
            _updateElementTag(element)
        }

        if (cost != null && cost >= 0) {
            element.cost = cost
            updateAggregateFlag = true
        }

        if (updateAggregateFlag) {
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
        elem_tag: String? = null,
        cost: Float? = null
    ): Int {
        val element = _getElementById(id)
        return updateElement(element, name, num, elem_tag, cost)
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Delete elements queries

    @Transaction
    suspend fun deleteElement(element: Element): Int {
        val aggregate = _getAggregateByElement(element)
        val elementsCount = _countAllElementsByParentId(aggregate.id)
        // if there is only one element attached to the aggregate do nothing
        if(elementsCount <= 1) return 0
        // if there is more than one element attached to this aggregate update it and delete the element
        aggregate.total_cost = aggregate.total_cost - (element.cost * element.num)
        _updateAggregate(aggregate)
        return _deleteElementWithTag(element)
    }

    @Transaction
    suspend fun deleteElementById(elem_id: Long): Int {
        val element: Element = _getElementById(elem_id)
        return deleteElement(element)
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Get count queries aggregates

    @Transaction
    suspend fun countAllElements(): Long{
        return _countAllElements()
    }

    @Transaction
    suspend fun countAllElementsByParentId(parent_id: Long): Long{
        return _countAllElementsByParentId(parent_id)
    }

    @Transaction
    suspend fun countAllSingleElements(): Long{
        return _countAllSingleElements()
    }

    @Transaction
    suspend fun countAllElementsByTagId(elem_tag_id: Long): Long{
        return _countAllElementsByTagId(elem_tag_id)
    }

    @Transaction
    suspend fun countAllElementsByParentTagId(parent_tag_id: Long): Long{
        return _countAllElementsByParentTagId(parent_tag_id)
    }

    @Transaction
    suspend fun countAllExpensesByParentTagId(parent_tag_id: Long): Float{
        return _countAllExpensesByParentTagId(parent_tag_id)
    }

    @Transaction
    suspend fun countAllExpensesByElementTagId(elem_tag_id: Long): Float?{
        return _countAllExpensesByElementTagId(elem_tag_id)
    }

    @Query("SELECT SUM(cost * num) FROM element INNER JOIN aggregate ON " +
            "element.aggregate_id=aggregate.id WHERE elem_tag_id = :elem_tag_id " +
            "AND aggregate.date >= :start_date AND aggregate.date < :end_date")
    suspend fun countAllExpensesBetweenDatesByElementTagId(start_date: Date, end_date: Date, elem_tag_id: Long?): Float?

    @Query("SELECT SUM(num) FROM element INNER JOIN aggregate ON" +
            " element.aggregate_id=aggregate.id WHERE elem_tag_id = :elem_tag_id" +
            " AND aggregate.date >= :start_date AND aggregate.date < :end_date")
    suspend fun countAllSingleElementsBetweenDatesByElementTagId(start_date: Date, end_date: Date, elem_tag_id: Long?): Long?


    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Get queries elemnts

    @Transaction
    suspend fun getLastElement(): Element{
        val element = _getLastElement()
        return _addTagNameToElement(element)
    }

    @Transaction
    suspend fun getElementById(elem_id: Long): Element{
        val element = _getElementById(elem_id)
        return _addTagNameToElement(element)
    }

    @Transaction
    suspend fun getAllElements(): List<Element>{
        val elements = _getAllElements()
        return _addTagNameToElementsList(elements)
    }

    @Transaction
    suspend fun getElementsByAggregateId(aggregateId: Long?): List<Element>{
        var elements = _getElementsByAggregateId(aggregateId)
        return _addTagNameToElementsList(elements)
    }

    @Transaction
    suspend fun getElementsByAggregate(aggregate: Aggregate): List<Element> {
        var elements = _getElementsByAggregate(aggregate)
        return _addTagNameToElementsList(elements)
    }

    @Transaction
    suspend fun getElementsByName(name: String): List<Element>{
        var elements = _getElementsByName(name)
        return _addTagNameToElementsList(elements)
    }

    @Transaction
    suspend fun getElementByCost(cost: Float): List<Element>{
        var elements = _getElementByCost(cost)
        return _addTagNameToElementsList(elements)
    }

    @Transaction
    suspend fun getElementOverCost(start_cost: Float): List<Element>{
        var elements = _getElementOverCost(start_cost)
        return _addTagNameToElementsList(elements)
    }

    @Transaction
    suspend fun getElementUnderCost(end_cost: Float): List<Element>{
        var elements = _getElementUnderCost(end_cost)
        return _addTagNameToElementsList(elements)
    }

    @Transaction
    suspend fun getElementBetweenCosts(start_cost: Float, end_cost: Float): List<Element>{
        var elements = _getElementBetweenCosts(start_cost, end_cost)
        return _addTagNameToElementsList(elements)
    }

    @Transaction
    suspend fun getElementByParentTag(parent_tag_id: Long): List<Element>{
        var elements = _getElementByParentTag(parent_tag_id)
        return _addTagNameToElementsList(elements)
    }

    @Transaction
    suspend fun getElementByTag(elem_tag_id: Long): List<Element>{
        var elements = _getElementByTag(elem_tag_id)
        return _addTagNameToElementsList(elements)
    }


}