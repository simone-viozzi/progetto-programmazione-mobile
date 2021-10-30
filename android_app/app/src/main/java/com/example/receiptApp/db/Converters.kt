package com.example.receiptApp.db

import android.location.Location
import android.net.Uri
import androidx.room.TypeConverter
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson
import java.util.*


data class LocationStripped(var latitude: Double, var longitude: Double)

/**
 * this class hold all the type converter to save in the  database complex data
 */
class Converters
{
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let {value -> Date(value) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    /*
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
    */

    @TypeConverter
    fun locationStringToLocation(stringLocation: String?): Location?
    {
        if (stringLocation != null && (stringLocation.contains(","))) {
            var result = Location("")
            val locationStrings = stringLocation.split(",")
            if (locationStrings.size == 2) {
                result.latitude = locationStrings[0].toDouble()
                result.longitude = locationStrings[1].toDouble()
                return result
            } else {
                return null
            }
        } else return null
    }

    @TypeConverter
    fun locationToLocationString(location: Location?): String?
    {
        if (location != null) {
            return Location.convert(location.latitude, Location.FORMAT_DEGREES) +
                    "," +
                    Location.convert(location.longitude, Location.FORMAT_DEGREES)
        }else{
            return null
        }
    }

    @TypeConverter
    fun uriToString(uri: Uri?): String?
    {
        return uri?.let{it -> it.toString()}
    }

    @TypeConverter
    fun stringToUri(string: String?): Uri?
    {
        return string?.let{it -> Uri.parse(it)}
    }
}