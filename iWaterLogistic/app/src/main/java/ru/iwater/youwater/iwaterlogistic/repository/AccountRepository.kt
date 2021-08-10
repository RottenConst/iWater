package ru.iwater.youwater.iwaterlogistic.repository

import retrofit2.HttpException
import ru.iwater.youwater.iwaterlogistic.domain.Account
import ru.iwater.youwater.iwaterlogistic.iteractor.StorageStateAccount
import ru.iwater.youwater.iwaterlogistic.response.ApiRequest
import ru.iwater.youwater.iwaterlogistic.response.RetrofitFactory
import timber.log.Timber
import javax.inject.Inject

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
    ): Pair<String, Account?> {
        var message = ""
        var account: Account? = Account("", 0, company)
        val response = service.authDriver(
            "3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX",
            login,
            company,
            password,
            notification
        )
        try {
            if (response.isSuccessful) {
                if (response.body()?.session != null) {
                    val idDriver = response.body()?.id
                    val session = response.body()?.session
                    account = Account(session!!, idDriver!!, company)
                } else {
                    message = "Неверный логин или пароль"
                }
            } else {
                message = response.message()
            }
        } catch (e: HttpException) {
            Timber.e(e.message())
        }
        return Pair(message, account)
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