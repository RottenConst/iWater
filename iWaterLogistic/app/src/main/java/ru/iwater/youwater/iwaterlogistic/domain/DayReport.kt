package ru.iwater.youwater.iwaterlogistic.domain

import androidx.annotation.Keep
import java.util.*
@Keep
data class DayReport(
    val driver_id: Int,
    val taken_bottles: Int,
    val orders_completed: Int,
    val money_site: Float,
    val money_terminal: Float,
    val money_cash: Float,
    val money_cash_b: Float,
    val cash_delivery: Float,
    val cash_sum: Float,
    val total_money: Float?,
    val date_created: String,
    val date: String?,
    val company_id: String
) {
    constructor(
        driver_id: Int,
        taken_bottles: Int,
        orders_completed: Int,
        money_site: Float,
        money_terminal: Float,
        money_cash: Float,
        money_cash_b: Float,
        cash_delivery: Float,
        total_money: Float?,
        date_created: String,
        date: String?,
        company_id: String
        ) : this (
        driver_id,
        taken_bottles,
        orders_completed,
        money_site,
        money_terminal,
        money_cash,
        money_cash_b,
        cash_delivery,
        money_site + money_terminal + money_cash,
        total_money,
        date_created,
        date,
        company_id
    )
}