package ru.iwater.youwater.iwaterlogistic.screens.report

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity
import java.text.SimpleDateFormat
import java.util.*

class ReportActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container_order)
        val currentDate = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        val timeComplete = formatter.format(currentDate.time)
        getFragment(timeComplete)
    }

    private fun getFragment(date: String) {
        val fragment = ReportFragment.newInstance()
        val bundle = Bundle()
        bundle.putString("date", date)
        fragment.arguments = bundle
        if (supportFragmentManager.fragments.isEmpty()) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fl_card_order_container, fragment)
                .commit()
        }
    }


    companion object {
        fun start(context: Context, intent: Intent) {
            ContextCompat.startActivity(context, intent, null)
        }
    }
}