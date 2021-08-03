package ru.iwater.youwater.iwaterlogistic.screens.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.databinding.ItemCurrentOrderBinding
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods

class ListOrdersAdapter(
    val orders: MutableList<Order> = mutableListOf()
) : RecyclerView.Adapter<ListOrdersAdapter.ListOrderHolder>() {

    lateinit var onOrderClick: ((Order) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListOrderHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ListOrderHolder(ItemCurrentOrderBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ListOrderHolder, position: Int) {
            holder.bindOrders(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    inner class ListOrderHolder(val binding: ItemCurrentOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
            init {
                binding.cardOrder.setOnClickListener { onOrderClick.invoke(orders[adapterPosition]) }
            }

        fun bindOrders(order: Order) {
            binding.numOrder.text = order.num.toString()
            "Заказ: ${order.time},\n${order.address}".also { binding.orderInfo.text = it }
            for (product in order.products) {
                binding.orderInfo.append("\n${product.name} - ${product.count}шт.")
            }
            val time = order.time.split("-")[1]
            if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) > 7200) {
                binding.numOrder.setBackgroundResource(R.drawable.circle_green)
            }

            if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) in 3601..7199) {
                binding.numOrder.setBackgroundResource(R.drawable.circle_yellow)
            }

            if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) < 3600) {
                binding.numOrder.setBackgroundResource(R.drawable.circle_red)
            }

            if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) < 0) {
                binding.numOrder.setBackgroundResource(R.drawable.circle_grey)
            }
        }

    }
}