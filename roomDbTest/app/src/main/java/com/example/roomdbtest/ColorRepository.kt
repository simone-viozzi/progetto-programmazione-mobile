package com.example.roomdbtest

import androidx.lifecycle.LiveData

class ColorRepository(private val colorDao: ColorDao)
{
    val allColors: LiveData<List<MyColor>> = colorDao.getAll()


    suspend fun insert(myColor: MyColor) {
        colorDao.insert(myColor)
    }


    suspend fun delete(myColor: MyColor) {
        colorDao.delete(myColor)
    }
}