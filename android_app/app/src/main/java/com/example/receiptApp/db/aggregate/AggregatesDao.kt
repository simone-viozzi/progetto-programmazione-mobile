package com.example.receiptApp.db.aggregate

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.example.receiptApp.db.element.Element
import com.example.receiptApp.db.element.ElementsDao
import java.util.*

/**
 * Aggregates dao
 *
 * Clear explanation of relation between Entity and how to use it
 * link: https://www.tutorialguruji.com/android/how-to-insert-entities-with-a-one-to-many-relationship-in-room/
 *
 * @constructor Create empty Aggregates dao
 */

@Dao
interface AggregatesDao
{

    /////////////////////////////////////////
    // Insert queries

    @Insert(onConflict = REPLACE)
    suspend fun insert(aggregate: Aggregate): Long

    @Insert(onConflict = REPLACE)
    suspend fun insertList(aggregates: List<Aggregate>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElementList(elements: List<Element>)

    @Transaction
    suspend fun insertWithElements(aggregate: Aggregate, elements: List<Element>): Long {

        val aggregateId = insert(aggregate)

        elements.forEach { it.aggregate_id = aggregateId }

        insertElementList(elements)

        return aggregateId
    }

    /////////////////////////////////////////
    // Update queries

    @Update
    suspend fun update(aggregate: Aggregate)

    @Update
    suspend fun updateList(aggregates: List<Aggregate>)

    /////////////////////////////////////////
    // Delete queries

    @Delete
    suspend fun delete(aggregate: Aggregate)

    @Delete
    suspend fun deleteList(aggregates: List<Aggregate>)

    /**
     * Delete all
     *
     * Delete all elements inside the table.
     */
    @Query("DELETE FROM aggregate")
    suspend fun deleteAll()

    /////////////////////////////////////////
    // Get count queries aggregates

    /**
     * Get the count of all aggregates inside the table
     *
     * @return the count of all aggregates inside the table
     */
    @Query("SELECT COUNT(*) FROM aggregate")
    suspend fun countAllAggregates(): Long

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Get queries aggregates

    /**
     * Get last aggregate
     *
     * @return the aggregate with the largest id
     */
    @Query("SELECT * FROM aggregate ORDER BY id DESC LIMIT 1")
    suspend fun getLastAggregate(): Aggregate

    /**
     * Get an aggregate by id
     *
     * @param id the id of the selected aggregate
     * @return
     */
    @Query("SELECT * FROM aggregate WHERE aggregate.id = :id LIMIT 1")
    suspend fun getAggregateById(id: Long): Aggregate

    /**
     * Get all aggregates
     *
     * @return a list of all the aggregates inside the table
     */
    @Query("SELECT * FROM aggregate")
    fun getAllAggregates(): LiveData<List<Aggregate>>

    /**
     * Get a list of aggregates by date
     *
     * @param date in which you want aggregates
     * @return a list of aggregates with the given date
     */
    @Query("SELECT * FROM aggregate WHERE aggregate.date = :date")
    fun getAggregateByDate(date: Date): LiveData<List<Aggregate>>

    /**
     * Get a list of aggregates until date
     *
     * @param date
     * @return a list of aggregates with the date field until the given date
     */
    @Query("SELECT * FROM aggregate WHERE aggregate.date <= :date")
    fun getAggregateUntilDate(date: Date): LiveData<List<Aggregate>>

    /**
     * Get a list of aggregates after date
     *
     * @param date
     * @return a list of aggregates with the date field after the given date
     */
    @Query("SELECT * FROM aggregate WHERE aggregate.date >= :date")
    fun getAggregateAfterDate(date: Date): LiveData<List<Aggregate>>

    /**
     * Get a list of aggregates between two dates
     *
     * @param dstart
     * @param dend
     * @return a list of aggregates with the date field between the given dates
     */
    @Query("SELECT * FROM aggregate WHERE aggregate.date >= :dstart AND aggregate.date <= :dend")
    fun getAggregateBetweenDate(dstart: Date, dend: Date): LiveData<List<Aggregate>>

    /**
     * Get a list of aggregates by tag
     *
     * @param tag
     * @return
     */
    @Query("SELECT * from aggregate WHERE aggregate.tag_id = :tag")
    fun getAggregateByTag(tag: Long): LiveData<List<Aggregate>>

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Get queries aggregates with elements

    /**
     * Get a map of aggregates with a list of elements foreach aggregate
     * in the return map will be only one aggregate with its elements
     *
     * @return a map of aggregates with a list of elements foreach aggregate with the largest id
     */
    @Query("SELECT * FROM aggregate JOIN element ON aggregate.id == element.aggregate_id WHERE aggregate.id = (SELECT max(id) FROM aggregate)")
    suspend fun getLastAggregateWithElements(): Map<Aggregate, List<Element>>

    /**
     * Get a map of aggregates with a list of elements foreach aggregate by id
     * in the return map will be only one aggregate with its elements
     *
     * @param id the id of the selected aggregate
     * @return
     */
    @Query("SELECT * FROM aggregate JOIN element ON aggregate.id == element.aggregate_id WHERE aggregate.id = :id LIMIT 1")
    fun getAggregateWithElementsById(id: Long): LiveData<Map<Aggregate, List<Element>>>

    /**
     * Get a map of aggregates with a list of elements foreach aggregate by date
     *
     * @param date date of the selected aggregates
     * @return a map of aggregates with a list of elements foreach aggregate by date
     */
    @Query("SELECT * FROM aggregate JOIN element ON aggregate.id == element.aggregate_id WHERE aggregate.date = :date")
    fun getAggregateWithElementsByDate(date: Date): LiveData<Map<Aggregate, List<Element>>>

    /**
     * Get a map of aggregates with a list of elements foreach aggregate
     *
     * @return a Map with aggregates as keys and as values their elements as a list
     */
    @Query("SELECT * FROM aggregate JOIN element ON aggregate.id == element.aggregate_id")
    suspend fun getAllAggregatesWithElements(): Map<Aggregate, List<Element>>

    /**
     * Get a map of aggregates with a list of elements until date foreach aggregate
     *
     * @param date
     * @return a map of
     */
    @Query("SELECT * FROM aggregate JOIN element ON aggregate.id == element.aggregate_id WHERE aggregate.date = :date")
    fun getAggregateWithElementsUntilDate(date: Date): LiveData<Map<Aggregate, List<Element>>>

    /**
     * Get a map of aggregates with a list of elements after date foreach aggregate
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM aggregate JOIN element ON aggregate.id == element.aggregate_id WHERE aggregate.date = :date")
    fun getAggregateWithElementsAfterDate(date: Date): LiveData<Map<Aggregate, List<Element>>>

    /**
     * Get a map of aggregates with a list of elements between dates foreach aggregate
     *
     * @param dstart
     * @param dend
     * @return
     */
    @Query("SELECT * FROM aggregate JOIN element ON aggregate.id == element.aggregate_id WHERE aggregate.date >= :dstart AND aggregate.date <= :dend")
    fun getAggregateWithElementsBetweenDate(dstart: Date, dend: Date): LiveData<Map<Aggregate, List<Element>>>

    /**
     * Get a map of aggregates with a list of elements by tag foreach aggregate
     *
     * @param tag
     * @return
     */
    @Query("SELECT * from aggregate JOIN element ON aggregate.id == element.aggregate_id WHERE aggregate.tag_id = :tag")
    fun getAggregateWithElementsByTag(tag: Long): LiveData<Map<Aggregate, List<Element>>>
}