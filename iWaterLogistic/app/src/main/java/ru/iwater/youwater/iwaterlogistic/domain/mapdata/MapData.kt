package ru.iwater.youwater.iwaterlogistic.domain.mapdata

data class MapData(
    val html_attributions: List<Any>,
    val results: List<Result>,
    val status: String
)