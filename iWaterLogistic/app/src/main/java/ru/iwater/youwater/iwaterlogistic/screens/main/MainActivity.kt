package ru.iwater.youwater.iwaterlogistic.screens.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.bd.IWaterDB
import ru.iwater.youwater.iwaterlogistic.databinding.MainContainerActivityBinding
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.complete.FragmentCompleteOrders
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.current.FragmentCurrentOrders
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.report.FragmentListReport
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.start.LoadDriveFragment
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.start.StartFragment
import ru.iwater.youwater.iwaterlogistic.screens.splash.SplashActivity
import ru.iwater.youwater.iwaterlogistic.service.TimeListenerService
import ru.iwater.youwater.iwaterlogistic.util.HelpLoadingProgress.setLoginProgress
import ru.iwater.youwater.iwaterlogistic.util.HelpState.ACCOUNT_SAVED
import timber.log.Timber
import javax.inject.Inject


class MainActivity : BaseActivity() {

    @Inject
    lateinit var accountRepository: AccountRepository
    private val screenComponent = App().buildScreenComponent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<MainContainerActivityBinding>(
            this,
            R.layout.main_container_activity
        )
        accountRepository = AccountRepository(screenComponent.accountStorage())

        val service = Intent(this.applicationContext, TimeListenerService::class.java)
        this.startService(service)
        Timber.d("account = ${accountRepository.getAccount().id}")

        binding.bottomBarNavigation.menu[1].isChecked = true
        loadFragment(FragmentCurrentOrders.newInstance())
        binding.bottomBarNavigation.setOnNavigationItemSelectedListener(bottomNavFragment)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
//        initThirdCounter(menu)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.log_out_menu -> {
                AlertDialog.Builder(this)
                    .setMessage(R.string.confirmLogout)
                    .setPositiveButton(
                        R.string.yes
                    ) { _, _ ->
                        val intent = Intent(applicationContext, SplashActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        setLoginProgress(this, ACCOUNT_SAVED, true)
                        CoroutineScope(Dispatchers.Default).launch {
                            IWaterDB.getIWaterDB(applicationContext)?.clearAllTables()
                        }
                        accountRepository.deleteAccount()
                        startActivity(intent)
                    }
                    .setNegativeButton(R.string.no) { dialog, _ ->
                        dialog.cancel()
                    }.create().show()
            }
            R.id.go_to_load_menu -> {
                val fragment = LoadDriveFragment.newInstance(false)
                supportFragmentManager.beginTransaction().replace(R.id.fl_container, fragment)
                    .commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val bottomNavFragment = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.complete_order -> {
                loadFragment(FragmentCompleteOrders.newInstance())
                return@OnNavigationItemSelectedListener true
            }
            R.id.orders -> {
                loadFragment(FragmentCurrentOrders.newInstance())
                return@OnNavigationItemSelectedListener true
            }
            R.id.report -> {
                loadFragment(FragmentListReport.newInstance())
                return@OnNavigationItemSelectedListener true
            }
        }
        true
    }

    private fun loadFragment(fragment: BaseFragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {

//        private var mCounterValue3 = 0

        fun start(context: Context?) {
            if (context != null) {
                ContextCompat.startActivity(
                    context,
                    Intent(context, MainActivity::class.java),
                    null
                )
            }
        }
    }
}