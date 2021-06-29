package ru.iwater.youwater.iwaterlogistic.domain.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
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

    fun authDriver(login: String, company: String, password: String) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, 0)
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val notification = sdf.format(calendar.time)
            val answer = accountRepository.authDriver(company, login, password, notification)
            val account = answer.second
            if (account?.session.isNullOrEmpty()) {
                mMessageLD.value = answer.first
            } else {
                mMessageLD.value = answer.first
                accountRepository.setAccount(account)
            }
        }


    }
}