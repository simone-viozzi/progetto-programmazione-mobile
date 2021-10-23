package com.example.receiptApp.db

import android.location.Location
import android.net.Uri
import androidx.room.TypeConverter
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson
import java.util.*


data class LocationStripped(var latitude: Double, var longitude: Double)

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
    fun locationStrippedToLocation(locationStrip: LocationStripped): Location
    {
        val location = Location("") //provider name is unnecessary

        location.latitude = locationStrip.latitude
        location.longitude = locationStrip.longitude
        return location
    }

    @TypeConverter
    fun locationToLocationStripped(location: Location): LocationStripped
    {
        return LocationStripped(location.latitude, location.longitude)
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