package ru.iwater.youwater.iwaterlogistic.domain.mapdata

import androidx.annotation.Keep

@Keep
data class MapData(
    val html_attributions: List<Any>,
    val results: List<Result>,
    val status: String
)