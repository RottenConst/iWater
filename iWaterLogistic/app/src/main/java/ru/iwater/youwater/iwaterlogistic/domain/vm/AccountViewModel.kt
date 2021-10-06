package ru.iwater.youwater.iwaterlogistic.domain.vm

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@OnScreen
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _messageLD: MutableLiveData<String> = MutableLiveData()
    val messageLD: LiveData<String>
        get() = _messageLD

    @SuppressLint("SimpleDateFormat")
    fun authDriver(login: String, company: String, password: String) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, 0)
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val notification = sdf.format(calendar.time)
            val account = accountRepository.authDriver(company, login, password, notification)
            if (account.status == "ok") {
                _messageLD.value = ""
                accountRepository.setAccount(account)
            } else {
                _messageLD.value = account.status
            }
        }
    }
}