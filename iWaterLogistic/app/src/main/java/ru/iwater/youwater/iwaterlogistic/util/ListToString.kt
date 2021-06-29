package ru.iwater.youwater.iwaterlogistic.util

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.iwater.youwater.iwaterlogistic.domain.Product
import ru.iwater.youwater.iwaterlogistic.domain.mapdata.Location
import java.lang.reflect.Type
import java.util.*
import java.util.stream.Collectors


class TimeConverter {

    @SuppressLint("NewApi")
    @TypeConverter
    fun timeToString(list: List<String>): String = list.stream().collect(Collectors.joining("-"))

    @TypeConverter
    fun stringToTimeList(string: String): List<String> {
        return string.split("-");
    }
}

class ProductConverter {

    var gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<Product> {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object : TypeToken<List<Product>>() {}.type
        return gson.fromJson<List<Product>>(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(list: List<Product>): String {
        return gson.toJson(list)
    }
}

class CoordinateConverter {

    val gson = Gson()

    @TypeConverter
    fun coordinateToString(location: Location?): String {
        return gson.toJson(location)
    }

    @TypeConverter
    fun stringToCoordinate(string: String?): Location {
        if (string == null) {
            return Location(0.0, 0.0)
        }
        val location: Type = object : TypeToken<Location>() {}.type
        return gson.fromJson(string, location)
    }
}