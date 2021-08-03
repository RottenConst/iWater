package ru.iwater.youwater.iwaterlogistic.screens.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.databinding.ItemCurrentOrderPrevBinding
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods

class ListCurrentOrdersPreview(
    val orders: MutableList<Order> = mutableListOf()
) : RecyclerView.Adapter<ListCurrentOrdersPreview.ListCurrentOrderHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCurrentOrderHolder {
        return ListCurrentOrderHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ListCurrentOrderHolder, position: Int) {
        holder.bindOrders(orders[position], position)
    }

    override fun getItemCount(): Int = orders.size

    class ListCurrentOrderHolder(val binding: ItemCurrentOrderPrevBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindOrders(order: Order, position: Int) {
            binding.numOrderPrev.text = (position + 1).toString()
            "Заказ: ${order.time},\n${order.address}".also { binding.tvOrderText.text = it }
            for (product in order.products) {
                binding.tvOrderText.append("\n${product.name} - ${product.count}шт.")
            }
            val time = order.time.split("-")[1]
            if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) > 7200) {
                binding.numOrderPrev.setBackgroundResource(R.drawable.circle_green)
            }

            if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) in 3601..7199) {
                binding.numOrderPrev.setBackgroundResource(R.drawable.circle_yellow)
            }

            if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) < 3600) {
                binding.numOrderPrev.setBackgroundResource(R.drawable.circle_red)
            }

            if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) < 0) {
                binding.numOrderPrev.setBackgroundResource(R.drawable.circle_grey)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ListCurrentOrderHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCurrentOrderPrevBinding.inflate(layoutInflater, parent, false)
                return ListCurrentOrderHolder(binding)
            }
        }

    }
}