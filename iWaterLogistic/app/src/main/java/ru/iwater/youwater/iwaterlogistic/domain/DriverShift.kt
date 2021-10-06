package ru.iwater.youwater.iwaterlogistic.domain

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class OpenDriverShift(
    @SerializedName("driver_id")
    val driverId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("company_id")
    val companyId: String,
    @SerializedName("open_date")
    val openDate: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("session")
    val session: String
)

@Keep
data class CloseDriverShift(
    @SerializedName("driver_id")
    val driverId: Int,
    @SerializedName("date_close")
    val openDate: String,
    @SerializedName("date")
    val date: String,
)