package ru.iwater.youwater.iwaterlogistic.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import ru.iwater.youwater.iwaterlogistic.util.TimeConverter

@Entity
@TypeConverters(TimeConverter::class)
data class Order(
    @PrimaryKey (autoGenerate = false)
    @SerializedName("order_id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("order")
    val product: String = "",
    @SerializedName("contact")
    val contact: String = "",
    @SerializedName("cash")
    val cash: Float = 0.0F,
    @SerializedName("cash_b")
    val cash_b: Float = 0.0F,
    @SerializedName("time")
    val timeStart: String = "",
    @SerializedName("notice")
    var notice: String = "",
    @SerializedName("period")
    val period: String = "",
    @SerializedName("address")
    var address: String = "",
//    @SerializedName("order_id")
//    val date: String = "",
    @SerializedName("status")
    var status: Int = 0,
//    var coordinates: List<String> = mutableListOf()
)
