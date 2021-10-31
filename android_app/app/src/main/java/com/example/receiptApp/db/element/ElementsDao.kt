package com.example.receiptApp.db.element

import androidx.room.*
import com.example.receiptApp.db.aggregate.Aggregate

/**
 * Elements dao
 *
 * @constructor Create empty Elements dao
 */

@Dao
interface ElementsDao
{
    /////////////////////////////////////////
    // Insert queries

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(element: Element)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(elements: List<Element>)

    /////////////////////////////////////////
    // Update queries

    @Update
    suspend fun update(element: Element)

    @Update
    suspend fun updateList(elements: List<Element>)

    /////////////////////////////////////////
    // Delete queries

    @Delete
    suspend fun delete(element: Element)

    @Delete
    suspend fun deleteList(elements: List<Element>)

    /**
     * Delete all
     *
     * Delete all elements inside the table.
     */
    @Query("DELETE FROM element")
    suspend fun deleteAll()

    /////////////////////////////////////////
    // Get count queries aggregates

    /**
     * Get the count of all elements inside the table
     *
     * @return the number of all the elements inside the table
     */
    @Query("SELECT COUNT(*) FROM element")
    suspend fun countAllElements(): Long

    /////////////////////////////////////////
    // Get queries

    /**
     * Get last element
     *
     * @return the element with the largest id
     */
    @Query("SELECT * FROM element ORDER BY elem_id DESC LIMIT 1")
    suspend fun getLastElement(): Element

    /**
     * Get all elements
     *
     * @return a list of all the elements inside the table
     */
    @Query("SELECT * FROM element")
    suspend fun getAllElements(): List<Element>



}