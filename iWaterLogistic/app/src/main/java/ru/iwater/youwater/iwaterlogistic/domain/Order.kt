package ru.iwater.youwater.iwaterlogistic.domain

import androidx.annotation.Keep
import androidx.room.*
import com.google.gson.annotations.SerializedName
import ru.iwater.youwater.iwaterlogistic.domain.mapdata.Location
import ru.iwater.youwater.iwaterlogistic.util.CoordinateConverter
import ru.iwater.youwater.iwaterlogistic.util.ProductConverter

@Entity
@TypeConverters(ProductConverter::class, CoordinateConverter::class)
@Keep
data class Order(
    @SerializedName("address")
    var address: String = "",
    @SerializedName("cash")
    var cash: String = "",
    @SerializedName("cash_b")
    var cash_b: String = "",
    @SerializedName("contact")
    var contact: String = "",
    @SerializedName("name")
    var name: String  = "",
    @SerializedName("notice")
    var notice: String = "",
    @SerializedName("order")
    var products: List<Product> = mutableListOf(),
    @SerializedName("order_id")
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0,
    @SerializedName("period")
    var period: String = "",
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("time")
    var time: String = "",
    @SerializedName("cord")
    var location: Location? = Location(0.0, 0.0),
    var num: Int = 0,
)