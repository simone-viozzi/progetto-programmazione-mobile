package com.example.roomdbtest

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ColorDao
{
    @Insert
    suspend fun insert(vararg  myColor: MyColor)

    @Delete
    suspend fun delete(myColor: MyColor)

    @Query("SELECT * FROM MyColor")
    fun getAll(): LiveData<List<MyColor>>

    @Query("DELETE FROM MyColor")
    suspend fun deleteAll()

    // TODO add query to search

}