package ru.iwater.youwater.iwaterlogistic.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.login_activity.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity
import ru.iwater.youwater.iwaterlogistic.domain.LoginViewModel


class LoginActivity : BaseActivity() {

    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        observeViewModel()
        authListener()
    }

    private fun authListener() {
        var notification = ""
        FirebaseInstanceId.getInstance().instanceId.
                addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        notification = task.result?.token.toString()
                    } else {
                        Log.w("firebase", "getInstanceId failed", task.exception)
                    }
                }
        btn_enter.setOnClickListener {
            loginViewModel.auth(
                    company = et_company.text.toString(),
                    login = et_login.text.toString(),
                    password = et_password.text.toString(),
                    notification = notification
            )
        }
    }

    private fun observeViewModel() {
        loginViewModel.answer.observe(this, Observer {
            showToast(it)
        })
    }

    fun showToast(value: String) {
        Toast.makeText(this, value, Toast.LENGTH_LONG).show()
    }

    companion object {
        fun start(context: Context) {
            startActivity(context, Intent(context, LoginActivity::class.java), null)
        }
    }
}