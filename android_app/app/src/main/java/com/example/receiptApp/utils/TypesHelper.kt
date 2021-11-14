package com.example.receiptApp.Utils

object TypesHelper {

    fun float2DoubleArray(array: Array<Float>): Array<Double>{
        return Array<Double>(size = array.size){ array[it].toDouble() }
    }

    fun long2AnyArray(array: Array<Long>): Array<Any>{
        return Array<Any>(size = array.size){ array[it] }
    }
}