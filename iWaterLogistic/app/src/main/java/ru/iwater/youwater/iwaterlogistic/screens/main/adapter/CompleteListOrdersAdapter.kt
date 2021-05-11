package ru.iwater.youwater.iwaterlogistic.screens.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_complete_order.view.*
import kotlinx.android.synthetic.main.item_current_order.view.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.domain.CompleteOrder
import ru.iwater.youwater.iwaterlogistic.domain.Order

class CompleteListOrdersAdapter(
    val completeOrders: MutableList<CompleteOrder> = mutableListOf()
): RecyclerView.Adapter<CompleteListOrdersAdapter.CompleteListOrdersHolder>() {

    lateinit var onOrderClick: ((CompleteOrder) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompleteListOrdersHolder {
        return  CompleteListOrdersHolder(LayoutInflater.from(parent.context), parent, R.layout.item_complete_order)
    }

    override fun onBindViewHolder(holder: CompleteListOrdersHolder, position: Int) {
        holder.bindCompleteOrders(completeOrders[position], position)
    }

    override fun getItemCount(): Int = completeOrders.size

    inner class CompleteListOrdersHolder(inflater: LayoutInflater, parent: ViewGroup, resource: Int) :
        RecyclerView.ViewHolder(inflater.inflate(resource, parent, false)) {
            init {
                itemView.card_complete_order.setOnClickListener {
                    onOrderClick.invoke(completeOrders[adapterPosition])
                }
            }

        fun bindCompleteOrders(completeOrders: CompleteOrder, position: Int) {
            val time = completeOrders.timeComplete.split(" ")
            itemView.tv_num_order.text = (position + 1).toString()
            "№${completeOrders.id} ${completeOrders.date} Завершен в ${time[1]}, ${completeOrders.address}".also { itemView.tv_info_order.text = it }
        }
    }

}