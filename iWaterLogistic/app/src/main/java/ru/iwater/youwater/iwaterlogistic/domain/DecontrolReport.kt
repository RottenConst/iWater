package ru.iwater.youwater.iwaterlogistic.domain

import androidx.annotation.Keep

@Keep
data class DecontrolReport(
    val order_id: Int?,
    val time: Long,
    val coord: String,
    val tank: Int,
    val notice: String
)