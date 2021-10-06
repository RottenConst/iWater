package ru.iwater.youwater.iwaterlogistic.screens.main.tab.current

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity
import ru.iwater.youwater.iwaterlogistic.databinding.CompleteOrderBinding
import ru.iwater.youwater.iwaterlogistic.screens.main.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class CompleteShipActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<CompleteOrderBinding>(this, R.layout.complete_order)
        val intent = intent
        val id = intent.getIntExtra("id", 0)
        val timeComplete = intent.getLongExtra("timeComplete", 0)
        val address = intent.getStringExtra("address")
        val error = intent.getIntExtra("error", 1)

        val sdf = SimpleDateFormat("HH:mm")
        val time = Date(timeComplete * 1000)


        if (error == 0) {
            binding.ivComplete.setImageResource(R.drawable.ic_green_check_circle_90)
            "Заказ № $id".also { binding.tvCompleteOrder.text = it }
            "Время отгрузки: ${sdf.format(time)}".also { binding.tvTimeComplete.text = it }
            binding.tvAddressOrder.text = "$address"
        } else {
            binding.ivComplete.setImageResource(R.drawable.ic_order_cancel_90)
            "Заказ № $id".also { binding.tvCompleteOrder.text = it }
            binding.tvTimeComplete.text = "Отгузить не удалось"
            binding.tvAddressOrder.text =
                "Что-то пошло не так, попробуйте отрузить еще раз"
        }
        binding.btnComplete.setOnClickListener {
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