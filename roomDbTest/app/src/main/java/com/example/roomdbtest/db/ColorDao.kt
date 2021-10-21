package com.example.roomdbtest.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


/**
 * Color dao
 *
 * @constructor Create empty Color dao
 */
@Dao
interface ColorDao
{

    @Query("SELECT * FROM MyColor")
    fun getAll(): LiveData<List<MyColor>>

    @Insert
    suspend fun insert(vararg  myColor: MyColor)

    @Delete
    suspend fun delete(myColor: MyColor)

    @Query("DELETE FROM MyColor")
    suspend fun deleteAll()

    // TODO add query to search
}