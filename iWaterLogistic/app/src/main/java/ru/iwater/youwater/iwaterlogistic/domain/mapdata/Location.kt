package ru.iwater.youwater.iwaterlogistic.domain.mapdata

import androidx.annotation.Keep

@Keep
data class Location(
    val lat: Double,
    val lng: Double
)