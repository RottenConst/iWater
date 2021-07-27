package ru.iwater.youwater.iwaterlogistic.domain.mapdata

import androidx.annotation.Keep

@Keep
data class Viewport(
    val northeast: Northeast,
    val southwest: Southwest
)