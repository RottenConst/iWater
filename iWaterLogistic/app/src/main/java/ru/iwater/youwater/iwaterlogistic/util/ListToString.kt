package ru.iwater.youwater.iwaterlogistic.util

import android.annotation.SuppressLint
import androidx.room.TypeConverter
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