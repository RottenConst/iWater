package ru.iwater.youwater.iwaterlogistic.domain

/**
 * Класс аккаунта
 **/
data class Account(
    var id: Int,
    var login: String,
    var session: String,
    var company: String
)
