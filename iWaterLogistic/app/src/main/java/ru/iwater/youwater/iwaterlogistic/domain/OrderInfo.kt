package ru.iwater.youwater.iwaterlogistic.domain

data class OrderInfo(
    val id : Int,
    val client_id : Int,
    val mobile : Int,
    val local_id : String,
    val company_id : Int,
    val name : String,
    val address : String,
    val contact : String,
    val date : Int,
    val no_date : Int,
    val time : String,
    val period : String,
    val notice : String,
    val cash : Double,
    val driver : Int,
    val status : Int,
)