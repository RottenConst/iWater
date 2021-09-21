package ru.iwater.youwater.iwaterlogistic.screens.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity
import ru.iwater.youwater.iwaterlogistic.screens.login.LoginActivity
import ru.iwater.youwater.iwaterlogistic.screens.main.MainActivity
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.start.StartWorkActivity
import ru.iwater.youwater.iwaterlogistic.util.HelpLoadingProgress.getLoginShow
import ru.iwater.youwater.iwaterlogistic.util.HelpLoadingProgress.getStartDayShow
import ru.iwater.youwater.iwaterlogistic.util.HelpState.*

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
            getLoginShow(this, ACCOUNT_SAVED) -> LoginActivity.start(this)
            getStartDayShow(this, IS_WORK_START) -> StartWorkActivity.start(this)
            else -> MainActivity.start(this)
        }
        finish()
    }

    companion object {
        fun start(context: Context) {
            startActivity(context, Intent(context, SplashActivity::class.java), null)
        }
    }
}