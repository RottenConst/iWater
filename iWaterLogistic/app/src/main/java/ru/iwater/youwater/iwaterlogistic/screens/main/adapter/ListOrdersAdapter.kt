package ru.iwater.youwater.iwaterlogistic.screens.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_current_order.view.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods

class ListOrdersAdapter(
    val orders: MutableList<Order> = mutableListOf()
) : RecyclerView.Adapter<ListOrdersAdapter.ListOrderHolder>() {

    lateinit var onOrderClick: ((Order) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListOrderHolder {
          return ListOrderHolder(LayoutInflater.from(parent.context), parent, R.layout.item_current_order)
    }

    override fun onBindViewHolder(holder: ListOrderHolder, position: Int) {
            holder.bindOrders(orders[position], position)
    }

    override fun getItemCount(): Int = orders.size

    inner class ListOrderHolder(inflater: LayoutInflater, parent: ViewGroup, resource: Int) :
        RecyclerView.ViewHolder(inflater.inflate(resource, parent, false)) {
            init {
                itemView.card_order.setOnClickListener { onOrderClick.invoke(orders[adapterPosition]) }
            }

        fun bindOrders(order: Order, position: Int) {
            itemView.num_order.text = order.num.toString()
            "Заказ: ${order.time},\n${order.address}".also { itemView.order_info.text = it }
            for (product in order.products) {
                itemView.order_info.append("\n${product.name} - ${product.count}шт.")
            }
            val time = order.time.split("-")[1]
            if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) > 7200) {
                itemView.num_order.setBackgroundResource(R.drawable.circle_green)
            }

            if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) in 3601..7199) {
                itemView.num_order.setBackgroundResource(R.drawable.circle_yellow)
            }

            if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) < 3600) {
                itemView.num_order.setBackgroundResource(R.drawable.circle_red)
            }

            if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) < 0) {
                itemView.num_order.setBackgroundResource(R.drawable.circle_grey)
            }
        }

    }
}