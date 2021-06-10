package ru.iwater.youwater.iwaterlogistic.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.iwater.youwater.iwaterlogistic.util.ProductConverter
import ru.iwater.youwater.iwaterlogistic.util.TimeConverter

@Entity
@TypeConverters(TimeConverter::class)
data class CompleteOrder(
    @PrimaryKey(autoGenerate = false)
    val id: Int?,
    val name: String,
    @TypeConverters(ProductConverter::class)
    var products: List<Product> = mutableListOf(),
    val cash: Float,
    val typeOfCash: String,
    val tank: Int,
    val time: String,
    val timeComplete: Long,
    val contact: String,
    var notice: String,
    val noticeDriver: String,
    val period: String,
    val address: String,
    var status: Int,
)
