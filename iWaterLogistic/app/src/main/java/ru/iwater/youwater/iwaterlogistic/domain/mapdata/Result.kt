package ru.iwater.youwater.iwaterlogistic.domain.mapdata

import androidx.annotation.Keep

@Keep
data class Result(
    val formatted_address: String,
    val geometry: Geometry,
    val icon: String,
    val name: String,
    val place_id: String,
    val plus_code: PlusCode,
    val reference: String,
    val types: List<String>
)