package ru.iwater.youwater.iwaterlogistic.util

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.iwater.youwater.iwaterlogistic.domain.Product
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
    @TypeConverter
    fun coordinateToString(list: List<String>): String {
        return "${list[0]};${list[1]}"
    }
    @TypeConverter
    fun stringToCoordinateList(string: String): List<String> {
        return string.split(";")
    }
}