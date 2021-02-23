package ru.iwater.youwater.iwaterlogistic.repository

import ru.iwater.youwater.iwaterlogistic.domain.Account
import ru.iwater.youwater.iwaterlogistic.iteractor.StorageStateAccount
import ru.iwater.youwater.iwaterlogistic.response.Authorisation
import timber.log.Timber
import javax.inject.Inject

/**
 * Класс для авторизации, получения сведений аккаунта водителя
 */
class AccountRepository @Inject constructor(
    private val accountStorage: StorageStateAccount
) {

    suspend fun getAuth(
        authorisation: Authorisation,
        login: String,
        company: String
    ): Pair<String, Account> {
        val answer = authorisation.auth()
        var message = ""
        var account = Account(0, "", "", "")
        if (answer.first == 0) {
            val arg = answer.second.split("</session>")
            val session = arg[0].replace("\\s+|<session>".toRegex(), "")
            val id = arg[1].replace("\\s+|<id>|</id>".toRegex(), "")
            account = Account(id.toInt(), login, session, company)
        } else {
            message = "Ошибка авторизации"
        }
        Timber.d("$message, ${account.id}")
        return Pair(message, account)
    }

    fun setAccount(account: Account) {
        accountStorage.save(account)
    }

    fun deleteAccount() {
        accountStorage.remove()
    }

    fun getAccount(): Account = accountStorage.get()
}