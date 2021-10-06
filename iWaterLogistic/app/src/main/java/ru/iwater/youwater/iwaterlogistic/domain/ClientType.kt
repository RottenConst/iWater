package ru.iwater.youwater.iwaterlogistic.domain

import androidx.annotation.Keep

@Keep
data class ClientType(
    val type: Int?
    )

@Keep
data class Data(
    val `data`: List<ClientInfo>,
    val message: String
)

@Keep
data class ClientInfo(
    val fact_address: String,
    val return_tare: Int
)