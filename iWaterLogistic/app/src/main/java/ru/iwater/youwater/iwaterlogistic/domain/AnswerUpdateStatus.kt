package ru.iwater.youwater.iwaterlogistic.domain

import androidx.annotation.Keep

@Keep
data class AnswerUpdateStatus(
    val status: Int,
    val error: Int,
    val oper: String
)
