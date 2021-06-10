package ru.iwater.youwater.iwaterlogistic.screens.main.tab.current

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.complete_order.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity
import ru.iwater.youwater.iwaterlogistic.screens.main.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class CompleteShipActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.complete_order)
        val intent = intent
        val id = intent.getIntExtra("id", 0)
        val timeComplete = intent.getLongExtra("timeComplete", 0)
        val address = intent.getStringExtra("address")
        val error = intent.getIntExtra("error", 1)

        val sdf = SimpleDateFormat("HH:mm")
        val time = Date(timeComplete * 1000)


        if (error == 0) {
            iv_complete.setImageResource(R.drawable.ic_green_check_circle_90)
            tv_complete_order.text = "Заказ № $id"
            tv_time_complete.text = "Время отгрузки: ${sdf.format(time)}"
            tv_adress_order.text = "$address"
        } else {
            iv_complete.setImageResource(R.drawable.ic_order_cancel_90)
            tv_complete_order.text = "Заказ № $id"
            tv_time_complete.text = "Отгузить не удалось"
            tv_adress_order.text = "Что-то пошло не так, попробуйте перезапустить приложение и отрузить еще раз"
        }
        btn_complete.setOnClickListener {
            MainActivity.start(this)
            finish()
        }
    }

    companion object {
        fun start(context: Context?, intent: Intent) {
            if (context != null) {
                ContextCompat.startActivity(context, intent, null)
            }
        }
    }
}