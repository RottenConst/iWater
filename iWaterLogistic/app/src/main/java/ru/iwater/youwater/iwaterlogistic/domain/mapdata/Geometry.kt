package ru.iwater.youwater.iwaterlogistic.domain.mapdata

import androidx.annotation.Keep

@Keep
data class Geometry(
    val location: Location,
    val viewport: Viewport
)