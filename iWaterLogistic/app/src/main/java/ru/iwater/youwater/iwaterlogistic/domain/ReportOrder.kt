package ru.iwater.youwater.iwaterlogistic.domain

data class ReportOrder(
    val company_id: String,
    val date: String,
    val date_created: String,
    val driver_id: Int,
    val name: String,
    val number_containers: Int,
    val order_id: Int,
    val orders_delivered: Int,
    val payment: Float,
    val payment_type: String,
    val total_money: Float,
    val type_client: String
)