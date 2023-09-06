package ru.iwater.youwater.iwaterlogistic.domain

import androidx.annotation.Keep

@Keep
data class Product(
    val count: Int = 0,
    val name: String = "",

)