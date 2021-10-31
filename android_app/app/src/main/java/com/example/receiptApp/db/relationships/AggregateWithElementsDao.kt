package com.example.receiptApp.db.relationships

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.example.receiptApp.db.aggregate.Aggregate
import com.example.receiptApp.db.element.Element

@Dao
interface AggregateWithElementsDao
{
    @Transaction
    @Query("SELECT * FROM Aggregate")
    fun getAggregateWithElements(): List<AggregateWithElements>

    // If the @Insert method receives only 1 parameter, it can return a long,
    // which is the new rowId for the inserted item.
    // https://developer.android.com/training/data-storage/room/accessing-data
    /*
    @Transaction
    @Insert(onConflict = REPLACE)
    suspend fun insert(aggregate: AggregateWithElements?): Long
    */
}

/*
@Transaction
fun insert(aggregate: Aggregate?, elements: List<Element?>)
{
    // Save rowId of inserted aggregate as aggregateId
    val aggregateId: Long = insert(aggregate)

    // Set aggregateId for all related elements
    for (el in elements)
    {
        el?.let {
            it.aggregate_id = aggregateId
            insert(it)
        }
    }
}*/