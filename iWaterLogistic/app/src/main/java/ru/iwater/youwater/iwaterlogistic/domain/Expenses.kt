package ru.iwater.youwater.iwaterlogistic.domain

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
@Keep
data class Expenses(
    val driver_id: Int,
    val expens: String,
    val money: Float,
    @PrimaryKey(autoGenerate = false)
    val date_created: Long,
    val date: String,
    val company_id: String
) {
    constructor(driver_id: Int, date: String, name: String, cost: Float, company_id: String) : this(driver_id, name, cost,Calendar.getInstance().timeInMillis/1000, date,  company_id)
}
