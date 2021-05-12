package ru.iwater.youwater.iwaterlogistic.screens.main.tab.current

//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import androidx.core.content.ContextCompat
//import kotlinx.android.synthetic.main.complete_order.*
//import ru.iwater.youwater.iwaterlogistic.R
//import ru.iwater.youwater.iwaterlogistic.base.BaseActivity
//import ru.iwater.youwater.iwaterlogistic.screens.main.MainActivity
//
//class CompleteShipActivity: BaseActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.complete_order)
//        val intent = intent
//        val id = intent.getIntExtra("id", 0)
//        val address = intent.getStringExtra("address")
//        val answer = intent.getStringExtra("answer")
//        val time = intent.getStringExtra("time")?.split(" ")
//        if (answer == "[0, Success.]") {
//            iv_complete.setImageResource(R.drawable.ic_green_check_circle_90)
//            tv_complete_order.text = "Заказ № $id, ${time?.get(0)}"
//            tv_time_complete.text = "Время отгрузки: ${time?.get(1)}"
//            tv_adress_order.text = "$address"
//        } else {
//            iv_complete.setImageResource(R.drawable.ic_order_cancel_90)
//            tv_complete_order.text = "Заказ № $id, ${time?.get(0)}"
//            tv_time_complete.text = "Отгузить не удалось"
//            tv_adress_order.text = "Что-то пошло не так, попробуйте перезапустить приложение и отрузить еще раз"
//        }
//        btn_complete.setOnClickListener {
//            MainActivity.start(this)
//            finish()
//        }
//    }
//
//    companion object {
//        fun start(context: Context?, intent: Intent) {
//            if (context != null) {
//                ContextCompat.startActivity(context, intent, null)
//            }
//        }
//    }
//}