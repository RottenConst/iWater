package ru.iwater.youwater.iwaterlogistic.domain.vm

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.response.Authorisation
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@OnScreen
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val mMessageLD: MutableLiveData<String> = MutableLiveData()

    val messageLD: LiveData<String>
        get() = mMessageLD

    /**
     * авторизация волителя
     */
    fun auth(company: String, login: String, password: String) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.DAY_OF_YEAR, 0)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val notification = sdf.format(calendar.time)
        val authorisation = Authorisation(company, login, password, notification)
        viewModelScope.launch {
            val auth = accountRepository.getAuth(authorisation, login, company)
            if (auth.first.isEmpty()) {
                mMessageLD.value = ""
                accountRepository.setAccount(auth.second)
            } else {
                mMessageLD.value = auth.first
            }
        }
    }
}