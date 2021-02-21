package ru.iwater.youwater.iwaterlogistic.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.response.Authorisation
import java.text.SimpleDateFormat
import java.util.*

class LoginViewModel : ViewModel() {

    private val _message = MutableLiveData<String>()
    private val _account = MutableLiveData<Account>()

    val answer: LiveData<String>
        get() = _message

    fun auth(company: String, login: String, password: String) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.DAY_OF_YEAR, 0)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val notification = sdf.format(calendar.time)

        val authorisation = Authorisation(company, login, password, notification)
        viewModelScope.launch {
            val auth = AccountRepository().getAuth(authorisation, login, company)
            if (auth.first.isEmpty()) {
                _account.postValue(auth.second)
                _message.postValue("")
            } else {
                _message.postValue(auth.first)
            }
        }


    }
}