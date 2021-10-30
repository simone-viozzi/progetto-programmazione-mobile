package com.example.receiptApp.db.element

import androidx.room.*
import com.example.receiptApp.db.aggregate.Aggregate


@Dao
interface ElementsDao
{
    /////////////////////////////////////////
    // Insert queries

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(aggregate: Element)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(aggregates: List<Element>)

    /////////////////////////////////////////
    // Update queries

    @Update
    suspend fun update(aggregate: Element)

    @Update
    suspend fun updateList(aggregates: List<Element>)

    /////////////////////////////////////////
    // Delete queries

    @Delete
    suspend fun delete(aggregate: Element)

    @Delete
    suspend fun deleteList(aggregates: List<Element>)

    /**
     * Delete all
     *
     * Delete all elements inside the table.
     */
    @Query("DELETE FROM element")
    suspend fun deleteAll()

    /////////////////////////////////////////
    // Get queries

    /**
     * Get last element
     *
     * @return the element with the largest id
     */
    @Query("SELECT * FROM element ORDER BY id DESC LIMIT 1")
    suspend fun getLastElement(): Element

    /**
     * Get all elements
     *
     * @return a list of all the elements inside the table
     */
    @Query("SELECT * FROM element")
    suspend fun getAllElements(): List<Element>



}