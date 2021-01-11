package ru.iwater.youwater.iwaterlogistic.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity
import ru.iwater.youwater.iwaterlogistic.util.HelpLoadingProgress
import ru.iwater.youwater.iwaterlogistic.util.HelpLoadingProgress.getLoginShow
import ru.iwater.youwater.iwaterlogistic.util.HelpProgressLoad
import ru.iwater.youwater.iwaterlogistic.util.HelpProgressLoad.LOGIN

class SplashActivity : BaseActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_layout);
        CoroutineScope(Dispatchers.Main).launch {
            runNextActivity()
        }
    }

    private suspend fun runNextActivity() {
        delay(500)
        when {
            getLoginShow(this, LOGIN) -> LoginActivity.start(this)
        }
        finish()
    }

    companion object {
        fun start(context: Context) {
            startActivity(context, Intent(context, SplashActivity::class.java), null)
        }
    }
}