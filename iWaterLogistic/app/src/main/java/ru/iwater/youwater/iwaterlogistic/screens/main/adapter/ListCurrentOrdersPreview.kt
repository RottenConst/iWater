package ru.iwater.youwater.iwaterlogistic.screens.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_current_order.view.*
import kotlinx.android.synthetic.main.item_current_order_prev.view.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods

class ListCurrentOrdersPreview (
    val orders: MutableList<Order> = mutableListOf()
) : RecyclerView.Adapter<ListCurrentOrdersPreview.ListCurrentOrderHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCurrentOrderHolder {
        return ListCurrentOrderHolder(LayoutInflater.from(parent.context), parent, R.layout.item_current_order_prev)
    }

    override fun onBindViewHolder(holder: ListCurrentOrderHolder, position: Int) {
        holder.bindOrders(orders[position], position)
    }

    override fun getItemCount(): Int = orders.size

    inner class ListCurrentOrderHolder(inflater: LayoutInflater, parent: ViewGroup, resource: Int) :
        RecyclerView.ViewHolder(inflater.inflate(resource, parent, false)) {

        fun bindOrders(order: Order, position: Int) {
            itemView.num_order_prev.text = (position + 1).toString()
            "Заказ: ${order.timeStart} - ${order.timeEnd},\n${order.address}\n${order.product}".also { itemView.tv_order_text.text = it }
            if (UtilsMethods.timeDifference(order.timeEnd, UtilsMethods.getFormatedDate()) > 7200) {
                itemView.num_order_prev.setBackgroundResource(R.drawable.circle_green)
            }

            if (UtilsMethods.timeDifference(order.timeEnd, UtilsMethods.getFormatedDate()) in 3601..7199) {
                itemView.num_order_prev.setBackgroundResource(R.drawable.circle_yellow)
            }

            if (UtilsMethods.timeDifference(order.timeEnd, UtilsMethods.getFormatedDate()) < 3600) {
                itemView.num_order_prev.setBackgroundResource(R.drawable.circle_red)
            }

            if (UtilsMethods.timeDifference(order.timeEnd, UtilsMethods.getFormatedDate()) < 0) {
                itemView.num_order_prev.setBackgroundResource(R.drawable.circle_grey)
            }
        }

    }
}