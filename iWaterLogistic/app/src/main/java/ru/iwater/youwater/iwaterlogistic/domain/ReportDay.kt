package ru.iwater.youwater.iwaterlogistic.domain

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
@Keep
data class ReportDay(
    val date: String,
    val totalMoney: Float,
    val cashMoney: Float,
    val cashOnSite: Float,
    val cashOnTerminal: Float,
    val noCashMoney: Float,
    val moneyDelivery: Float,
    val tank: Int,
    val orderComplete: Int,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}