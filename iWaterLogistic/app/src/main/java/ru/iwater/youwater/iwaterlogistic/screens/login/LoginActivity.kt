package ru.iwater.youwater.iwaterlogistic.screens.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.login_activity.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity
import ru.iwater.youwater.iwaterlogistic.domain.LoginViewModel
import ru.iwater.youwater.iwaterlogistic.screens.main.MainActivity

/**
 * экран входа в приложения
 */
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
        btn_enter.setOnClickListener {
            loginViewModel.auth(
                    company = et_company.text.toString(),
                    login = et_login.text.toString(),
                    password = et_password.text.toString(),
            )
        }
    }

    private fun observeViewModel() {
        loginViewModel.answer.observe(this, Observer {
            if (it.isNotEmpty()) {
                showToast(it)
            } else {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        })
    }

    private fun showToast(value: String) {
        Toast.makeText(this, value, Toast.LENGTH_LONG).show()
    }

    companion object {
        fun start(context: Context) {
            startActivity(context, Intent(context, LoginActivity::class.java), null)
        }
    }
}