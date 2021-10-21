package com.example.roomdbtest.db

import androidx.lifecycle.LiveData

/**
 * the repository get initialized in the App
 *
 * @property colorDao -> we get this from the database
 * @constructor Create empty Color repository
 */
class ColorRepository(private val colorDao: ColorDao)
{
    /*
    allColors -> is live data so it's asynchronous by default

    the other function are suspended and run in the corutine scope
     */
    val allColors: LiveData<List<MyColor>> = colorDao.getAll()

    suspend fun insert(myColor: MyColor) {
        colorDao.insert(myColor)
    }

    suspend fun delete(myColor: MyColor) {
        colorDao.delete(myColor)
    }

    suspend fun deleteAll() {
        colorDao.deleteAll()
    }
}