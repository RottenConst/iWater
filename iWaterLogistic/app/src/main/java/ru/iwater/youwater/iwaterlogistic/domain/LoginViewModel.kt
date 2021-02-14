package ru.iwater.youwater.iwaterlogistic.domain

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.iwaterlogistic.response.Authorisation

class LoginViewModel : ViewModel() {

    private val _message = MutableLiveData<String>()
    var message = ""

    val answer: LiveData<String>
        get() = _message

    fun auth(company: String, login: String, password: String, notification: String) {
        Log.d("auth", "login $notification")
        val authorisation = Authorisation(company, login, password, notification)
        viewModelScope.launch {
          _message.postValue(authorisation.auth().second)
            Log.d("auth", "message $message")
        }
        Log.d("auth", "message $message")

    }
}