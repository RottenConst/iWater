package ru.iwater.youwater.iwaterlogistic.domain

import androidx.room.*
import com.google.gson.annotations.SerializedName
import ru.iwater.youwater.iwaterlogistic.domain.mapdata.Location
import ru.iwater.youwater.iwaterlogistic.util.CoordinateConverter
import ru.iwater.youwater.iwaterlogistic.util.ProductConverter

@Entity

data class Order(
    var address: String = "",
    var cash: String = "",
    var cash_b: String = "",
    var contact: String = "",
    var name: String  = "",
    var notice: String = "",
    @SerializedName("order")
    @TypeConverters(ProductConverter::class)
    var products: List<Product> = mutableListOf(),
    @SerializedName("order_id")
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0,
    var period: String = "",
    var status: Int = 0,
    var time: String = "",
    var location: Location? = Location(0.0, 0.0),
    var num: Int = 0,
)