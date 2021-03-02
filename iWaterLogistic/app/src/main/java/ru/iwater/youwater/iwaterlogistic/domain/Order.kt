package ru.iwater.youwater.iwaterlogistic.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import ru.iwater.youwater.iwaterlogistic.util.CoordinateConverter
import ru.iwater.youwater.iwaterlogistic.util.TimeConverter

@Entity
@TypeConverters(TimeConverter::class)
data class Order(
    @PrimaryKey (autoGenerate = false)
    val id: Int,
    val name: String,
    val product: String,
    val cash: Float,
    val cash_b: Float,
    val timeStart: String,
    val timeEnd: String,
    val contact: String,
    val notice: String,
    val date: String,
    val period: String,
    val address: String,
    var status: Int,
    val coordinates: List<String>
)
