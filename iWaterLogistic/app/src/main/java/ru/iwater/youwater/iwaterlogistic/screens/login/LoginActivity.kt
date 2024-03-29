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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity
import ru.iwater.youwater.iwaterlogistic.databinding.LoginActivityBinding
import ru.iwater.youwater.iwaterlogistic.domain.vm.AccountViewModel
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.start.StartWorkActivity
import ru.iwater.youwater.iwaterlogistic.util.HelpLoadingProgress.setLoginProgress
import ru.iwater.youwater.iwaterlogistic.util.HelpState.ACCOUNT_SAVED
import timber.log.Timber
import javax.inject.Inject

/**
 * экран входа в приложения
 */
class LoginActivity : BaseActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: AccountViewModel by viewModels { factory }
    private val screenComponent = App().buildScreenComponent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<LoginActivityBinding>(this, R.layout.login_activity)
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
        binding.btnEnter.setOnClickListener {
            viewModel.authDriver(
                binding.etLogin.text.toString(),
                binding.etCompany.text.toString(),
                binding.etPassword.text.toString()
            )
        }
    }

    private fun observeViewModel() {
        viewModel.messageLD.observe(this, {
            Timber.d(it)
            if (it.isNotEmpty() && it != null) {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(this, StartWorkActivity::class.java)
                setLoginProgress(this, ACCOUNT_SAVED, false)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        })
    }

    companion object {
        fun start(context: Context) {
            startActivity(context, Intent(context, LoginActivity::class.java), null)
        }
    }
}