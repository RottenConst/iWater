package ru.iwater.youwater.iwaterlogistic.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.iwater.youwater.iwaterlogistic.util.TimeConverter

@Entity
@TypeConverters(TimeConverter::class)
data class Order(
    @PrimaryKey (autoGenerate = false)
    val id: Int = 0,
    val name: String = "",
    val product: String = "",
    val cash: Float = 0.0F,
    val cash_b: Float = 0.0F,
    val timeStart: String = "",
    val timeEnd: String = "",
    val contact: String = "",
    var notice: String = "",
    val date: String = "",
    val period: String = "",
    var address: String = "",
    var status: Int = 0,
    var coordinates: List<String> = mutableListOf()
)
