package ru.iwater.youwater.iwaterlogistic.screens.main.tab.current

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.complete_order.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity
import ru.iwater.youwater.iwaterlogistic.screens.main.MainActivity

class CompleteShipActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.complete_order)
        val intent = intent
        val id = intent.getIntExtra("id", 0)
        val address = intent.getStringExtra("address")
        val time = intent.getStringExtra("time")?.split(" ")
        tv_complete_order.text = "Заказ № $id, ${time?.get(0)}"
        tv_time_complete.text = "Время отгрузки: ${time?.get(1)}"
        tv_adress_order.text = "$address"
        btn_complete.setOnClickListener {
            MainActivity.start(this)
            finish()
        }
    }

    companion object {
        fun start(context: Context, intent: Intent) {
            ContextCompat.startActivity(context, intent, null)
        }
    }
}