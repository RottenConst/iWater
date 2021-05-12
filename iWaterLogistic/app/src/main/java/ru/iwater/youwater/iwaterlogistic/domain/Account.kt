package ru.iwater.youwater.iwaterlogistic.domain

import com.google.gson.annotations.SerializedName

/**
 * Класс аккаунта
 **/
data class Account(
    var session: String,
    var id: Int
)
