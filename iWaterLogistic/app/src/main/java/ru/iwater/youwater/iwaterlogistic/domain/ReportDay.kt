package ru.iwater.youwater.iwaterlogistic.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ReportDay(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val totalMoney: Float,
    val cashMoney: Float,
    val cashOnSite: Float,
    val cashOnTerminal: Float,
    val noCashMoney: Float,
    val tank: Int,
    val orderComplete: Int,
)