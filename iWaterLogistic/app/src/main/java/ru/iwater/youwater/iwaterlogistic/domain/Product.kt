package ru.iwater.youwater.iwaterlogistic.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.SET_NULL
import androidx.room.PrimaryKey

data class Product(
    val count: Int = 0,
    val name: String = "",

)