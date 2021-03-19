package ru.iwater.youwater.iwaterlogistic.screens.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.login_activity.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity
import ru.iwater.youwater.iwaterlogistic.domain.vm.AccountViewModel
import ru.iwater.youwater.iwaterlogistic.screens.main.MainActivity
import ru.iwater.youwater.iwaterlogistic.util.HelpLoadingProgress.setLoginProgress
import ru.iwater.youwater.iwaterlogistic.util.HelpStateLogin.ACCOUNT_SAVED
import javax.inject.Inject

/**
 * экран входа в приложения
 */
class LoginActivity : BaseActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: AccountViewModel by viewModels {factory}
    private val screenComponent = App().buildScreenComponent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        screenComponent.inject(this)
        if (ActivityCompat.checkSelfPermission(
                this@LoginActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@LoginActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
        observeViewModel()
        authListener()
    }

    private fun authListener() {
        btn_enter.setOnClickListener {
            viewModel.auth(
                    company = et_company.text.toString(),
                    login = et_login.text.toString(),
                    password = et_password.text.toString(),
            )
        }
    }

    private fun observeViewModel() {
        viewModel.messageLD.observe(this, {
            if (it.isNotEmpty()) {
                showToast(it)
            } else {
                val intent = Intent(this, MainActivity::class.java)
                setLoginProgress(this, ACCOUNT_SAVED, false)
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