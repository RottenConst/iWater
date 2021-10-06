package ru.iwater.youwater.iwaterlogistic.domain

data class NotifyOrder(
    val id: Int,
    val timeEnd: String,
    val address: String,
    var notification: Boolean = false,
    var fail: Boolean = false
)
