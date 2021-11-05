package com.example.receiptApp.db.aggregate

import androidx.room.*
import com.example.receiptApp.db.element.Element
import com.example.receiptApp.db.tag.TagsDao
import java.util.*

interface BaseAggregatesDao : TagsDao{

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Insert queries

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insertAggregate(aggregate: Aggregate): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insertAggregateList(aggregates: List<Aggregate>): List<Long>

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Update queries

    @Update
    suspend fun _updateAggregate(aggregate: Aggregate): Int

    @Update
    suspend fun _updateAggregatesList(aggregates: List<Aggregate>): Int

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
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
    suspend fun _getLastAggregate(): Aggregate

    @Query("SELECT * FROM aggregate WHERE aggregate.id = :id LIMIT 1")
    suspend fun _getAggregateById(id: Long?): Aggregate

    @Transaction
    suspend fun _getAggregateByElement(element: Element): Aggregate {
        return _getAggregateById(element.aggregate_id)
    }

    @Query("SELECT * FROM aggregate")
    suspend fun _getAllAggregates(): List<Aggregate>

    @Query("SELECT * FROM aggregate WHERE aggregate.date = :date")
    suspend fun _getAggregatesByDate(date: Date): List<Aggregate>

    @Query("SELECT * FROM aggregate WHERE aggregate.date <= :date")
    suspend fun _getAggregatesUntilDate(date: Date): List<Aggregate>

    @Query("SELECT * FROM aggregate WHERE aggregate.date >= :date")
    suspend fun _getAggregatesAfterDate(date: Date): List<Aggregate>

    @Query("SELECT * FROM aggregate WHERE aggregate.date >= :dstart AND aggregate.date <= :dend")
    suspend fun _getAggregatesBetweenDate(dstart: Date, dend: Date): List<Aggregate>

    @Query("SELECT * from aggregate WHERE aggregate.tag_id = :tag")
    suspend fun _getAggregatesByTag(tag: Long): List<Aggregate>

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // query helpers

    @Transaction
    suspend fun _addTagNameToAggregate(aggregate: Aggregate): Aggregate{
        val tag = getAggregateTagById(aggregate.tag_id)
        if (tag != null) {
            aggregate.tag = tag.tag_name
        }
        return aggregate
    }

    @Transaction
    suspend fun _addTagNameToAggregatesList(aggregateList: List<Aggregate>): List<Aggregate>{

        val tagList = getAggregateTags()?.groupBy{it.tag_id}

        aggregateList.forEach {
            if(it.tag_id != null) it.tag = tagList?.get(it.tag_id)?.get(0)?.tag_name
        }

        return aggregateList
    }

}