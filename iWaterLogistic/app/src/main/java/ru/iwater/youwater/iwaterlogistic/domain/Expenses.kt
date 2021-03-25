package ru.iwater.youwater.iwaterlogistic.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Expenses(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: String,
    val name: String,
    val cost: Float
) {
    constructor(date: String, name: String, cost: Float) : this(0, date, name, cost)
}
