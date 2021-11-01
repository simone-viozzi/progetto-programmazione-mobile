package com.example.receiptApp.db.aggregate

import androidx.room.*
import com.example.receiptApp.db.element.Element
import java.util.*

interface BaseAggregateDao {

    /////////////////////////////////////////
    // Insert queries

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insertAggregate(aggregate: Aggregate): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insertAggregateList(aggregates: List<Aggregate>): List<Long>

    /////////////////////////////////////////
    // Update queries

    @Update
    suspend fun _updateAggregate(aggregate: Aggregate): Int

    @Update
    suspend fun _updateAggregatesList(aggregates: List<Aggregate>): Int

    /////////////////////////////////////////
    // Delete queries

    // delete aggregates queries
    @Delete
    suspend fun _deleteAggregate(aggregate: Aggregate)

    @Delete
    suspend fun _deleteAggregateList(aggregates: List<Aggregate>)

    @Query("DELETE FROM aggregate WHERE aggregate.id = :id")
    suspend fun _deleteAggregateById(id: Long)

    @Query("DELETE FROM aggregate")
    suspend fun _deleteAllAggregates()

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Get queries aggregates

    @Query("SELECT * FROM aggregate ORDER BY id DESC LIMIT 1")
    suspend fun getLastAggregate(): Aggregate

    @Query("SELECT * FROM aggregate WHERE aggregate.id = :id LIMIT 1")
    suspend fun getAggregateById(id: Long): Aggregate

    @Transaction
    suspend fun getAggregateByElement(element: Element): Aggregate {
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

}