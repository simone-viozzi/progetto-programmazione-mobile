package com.example.receiptApp.db

import android.net.Uri
import androidx.room.TypeConverter
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson
import java.util.*


class Converters
{
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }


    // TODO how to save location
    @TypeConverter
    fun stringToGeoPoint(data: String?): GeoPoint
    {
        return Gson().fromJson(data, GeoPoint::class.java)
    }

    @TypeConverter
    fun geoPointToString(geoPoint: GeoPoint?): String
    {
        return Gson().toJson(geoPoint)
    }

    @TypeConverter
    fun uriToString(uri: Uri): String
    {
        return uri.toString();
    }

    @TypeConverter
    fun stringToUri(string: String): Uri
    {
        return Uri.parse(string)
    }

}