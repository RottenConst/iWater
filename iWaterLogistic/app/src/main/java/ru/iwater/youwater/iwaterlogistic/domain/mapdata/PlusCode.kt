package ru.iwater.youwater.iwaterlogistic.domain.mapdata

import androidx.annotation.Keep

@Keep
data class PlusCode(
    val compound_code: String,
    val global_code: String
)