package ru.iwater.youwater.iwaterlogistic.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.iwater.youwater.iwaterlogistic.util.TimeConverter

@Entity
@TypeConverters(TimeConverter::class)
data class CompleteOrder(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
    val product: String,
    val cash: Float,
    val typeOfCash: String,
    val tank: Int,
    val timeStart: String,
    val timeEnd: String,
    val timeComplete: String,
    val contact: String,
    var notice: String,
    val noticeDriver: String,
    val date: String,
    val period: String,
    val address: String,
    var status: Int,
    val coordinates: List<String>,
    val coordinates_ship: List<String>
)
