package ru.iwater.youwater.iwaterlogistic.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
    }


    companion object {
        fun start(context: Context) {
            startActivity(context, Intent(context, LoginActivity::class.java), null)
        }
    }
}