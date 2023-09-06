package ru.iwater.youwater.iwaterlogistic.domain

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import ru.iwater.youwater.iwaterlogistic.util.ProductConverter


@Entity
data class WaterOrder(
    @SerializedName("order_id")
    @PrimaryKey(autoGenerate = false)
    val order_id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("order")
    val order: List<Product>,
    @SerializedName("contact")
    var contact: String,
    @SerializedName("cash")
    var cash: String?,
    @SerializedName("cash_b")
    var cash_b: String?,
    @SerializedName("time")
    val time: String,
    @SerializedName("notice")
    val notice: String,
    @SerializedName("period")
    val period: String,
    @SerializedName("address")
    var address: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("date")
    val date: Int,
    @SerializedName("coords")
    var coords: String?,
    var num: String,
)