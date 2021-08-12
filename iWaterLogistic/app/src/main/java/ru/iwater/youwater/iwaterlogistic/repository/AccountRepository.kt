package ru.iwater.youwater.iwaterlogistic.repository

import retrofit2.HttpException
import ru.iwater.youwater.iwaterlogistic.domain.Account
import ru.iwater.youwater.iwaterlogistic.iteractor.StorageStateAccount
import ru.iwater.youwater.iwaterlogistic.response.ApiRequest
import ru.iwater.youwater.iwaterlogistic.response.RetrofitFactory
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.acos

/**
 * Класс для авторизации, получения сведений аккаунта водителя
 */
class AccountRepository @Inject constructor(
    private val accountStorage: StorageStateAccount
) {

    private val service: ApiRequest = RetrofitFactory.makeRetrofit()

    suspend fun authDriver(
        company: String,
        login: String,
        password: String,
        notification: String
    ): Account {
        var account = Account("", 0, "", "")
        try {
            account = service.authDriver("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", login, company, password, notification)!!
            if (account.session.isNotBlank()) {
                account.company = company
                account.status = "ok"
                return account
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        account.status = "Неверный логин или пароль, возможны проблемы с интернетом"
        return account
    }

    fun setAccount(account: Account?) {
        if (account != null) {
            accountStorage.save(account)
        }
    }

    fun deleteAccount() {
        accountStorage.remove()
    }

    fun getAccount(): Account = accountStorage.get()
}