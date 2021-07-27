package ru.iwater.youwater.iwaterlogistic.domain

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * Класс аккаунта
 **/
@Keep
data class Account(
    var session: String,
    var id: Int,
    var company: String
)
