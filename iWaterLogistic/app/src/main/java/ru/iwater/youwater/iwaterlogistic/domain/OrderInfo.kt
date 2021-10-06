package ru.iwater.youwater.iwaterlogistic.domain

import androidx.annotation.Keep

@Keep
data class OrderInfo(
    val id : Int,
    val client_id : Int,
    val mobile : Int,
    val local_id : String?,
    val company_id : String,
    val name : String,
    val address : String,
    val contact : String,
    val date : Int,
    val no_date : Int,
    val time : String,
    val period : String,
    val notice : String,
    val cash : String,
    val cash_b : String,
    val driver : Int,
    val status : Int,
)