package com.example.receiptApp.db.aggregate

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.example.receiptApp.db.element.Element
import java.util.*


@Dao
interface AggregatesDao
{

    /////////////////////////////////////////
    // Insert queries

    @Insert(onConflict = REPLACE)
    suspend fun insert(aggregate: Aggregate)

    @Insert(onConflict = REPLACE)
    suspend fun insertList(aggregates: List<Aggregate>)

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
    // Get queries

    /**
     * Get last aggregate
     *
     * @return the aggregate with the largest id
     */
    @Query("SELECT * FROM aggregate ORDER BY id DESC LIMIT 1")
    suspend fun getLastAggregate(): Aggregate

    /**
     * Get all aggregates
     *
     * @return a list of all the aggregate inside the table
     */
    @Query("SELECT * FROM aggregate")
    suspend fun getAllAggregates(): List<Aggregate>

    /**
     * Get all aggregates with elements
     *
     * @return a Map with aggregates as keys and as values their elements as a list
     */
    @Query("SELECT * FROM aggregate JOIN element ON aggregate.id == element.aggregate_id")
    fun getAllAggregatesWithElements(): LiveData<Map<Aggregate, List<Element>>>

    /**
     * Get aggregate with elements by date
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM aggregate JOIN element ON aggregate.id == element.aggregate_id WHERE aggregate.date = :date")
    fun getAggregateWithElementsByDate(date: Date): LiveData<Map<Aggregate, List<Element>>>

    /**
     * Get aggregate with elements until date
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM aggregate JOIN element ON aggregate.id == element.aggregate_id WHERE aggregate.date = :date")
    fun getAggregateWithElementsUntilDate(date: Date): LiveData<Map<Aggregate, List<Element>>>

    /**
     * Get aggregate with elements after date
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM aggregate JOIN element ON aggregate.id == element.aggregate_id WHERE aggregate.date = :date")
    fun getAggregateWithElementsAfterDate(date: Date): LiveData<Map<Aggregate, List<Element>>>

    /**
     * Get aggregate with elements between date
     *
     * @param dstart
     * @param dend
     * @return
     */
    @Query("SELECT * FROM aggregate JOIN element ON aggregate.id == element.aggregate_id WHERE aggregate.date >= :dstart AND aggregate.date <= :dend")
    fun getAggregateWithElementsBetweenDate(dstart: Date, dend: Date): LiveData<Map<Aggregate, List<Element>>>

    /**
     * Get aggregate with elements by id
     *
     * @param id
     * @return
     */
    @Query("SELECT * FROM aggregate JOIN element ON aggregate.id == element.aggregate_id WHERE aggregate.id = :id LIMIT 1")
    fun getAggregateWithElementsById(id: Long): LiveData<Map<Aggregate, List<Element>>>

}