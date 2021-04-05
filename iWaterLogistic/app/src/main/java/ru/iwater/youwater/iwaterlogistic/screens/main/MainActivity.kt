package ru.iwater.youwater.iwaterlogistic.screens.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.badge_with_counter_icon.*
import kotlinx.android.synthetic.main.main_container_activity.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.complete.FragmentCompleteOrders
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.current.FragmentCurrentOrders
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.report.FragmentListReport
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
        setContentView(R.layout.main_container_activity)
        accountRepository = AccountRepository(screenComponent.accountStorage())

        val service = Intent(this.applicationContext, TimeListenerService::class.java)
        startService(service)
        Timber.d("account = ${accountRepository.getAccount().login}")

        bottom_bar_navigation.menu[1].isChecked = true
        loadFragment(FragmentCurrentOrders.newInstance())
        bottom_bar_navigation.setOnNavigationItemSelectedListener(bottomNavFragment)
    }

//    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
//        initThirdCounter(menu)
//        return super.onPrepareOptionsMenu(menu)
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
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
                        accountRepository.deleteAccount()
                        startActivity(intent)
                    }
                    .setNegativeButton(R.string.no) { dialog, _ ->
                        dialog.cancel()
                    }.create().show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val bottomNavFragment = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId) {
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


//    private fun initThirdCounter(menu: Menu?) {
//        val counterItem = menu?.findItem(R.id.action_counter_3)
//        val counter: View? = counterItem?.actionView
//        counter?.setOnClickListener { _ -> onThirdCounterClick() }
//        updateThirdCounter(mCounterValue3)
//    }
//
//    private fun onThirdCounterClick() {
//        updateThirdCounter(++mCounterValue3)
//    }
//
//    private fun updateThirdCounter(newCounterValue: Int) {
//        if (icon_badge == null || counter == null) {
//            return
//        }
//        if (newCounterValue == 0) {
//            icon_badge.setImageResource(R.drawable.ic_grey_notification)
//            counter.visibility = View.GONE
//        } else {
//            icon_badge.setImageResource(R.drawable.ic_grey_notification)
//            counter.visibility = View.VISIBLE
//            counter.text = newCounterValue.toString()
//        }
//    }

    companion object {

//        private var mCounterValue3 = 0

        fun start(context: Context) {
            ContextCompat.startActivity(context, Intent(context, MainActivity::class.java), null)
        }
    }
}