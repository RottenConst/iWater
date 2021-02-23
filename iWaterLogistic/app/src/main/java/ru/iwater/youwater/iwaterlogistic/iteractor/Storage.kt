package ru.iwater.youwater.iwaterlogistic.iteractor

import ru.iwater.youwater.iwaterlogistic.domain.Account

interface Storage<Type> {

    fun save(data: Type)

    fun get (): Type

    fun remove()
}

interface StorageStateAccount: Storage<Account>