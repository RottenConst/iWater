package ru.iwater.youwater.iwaterlogistic.screens.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.databinding.ItemCurrentOrderBinding
import ru.iwater.youwater.iwaterlogistic.databinding.ItemCurrentOrderPrevBinding
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.domain.vm.OrderLoadStatus
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods

class ListOrdersAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Order, ListOrdersAdapter.ListOrderHolder>(OrderDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListOrderHolder {
        return ListOrderHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ListOrderHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(item)
        }
        holder.bindOrders(item)
    }

    class ListOrderHolder(val binding: ItemCurrentOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bindOrders(order: Order) {
            binding.order = order
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ListOrderHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCurrentOrderBinding.inflate(layoutInflater, parent, false)
                return ListOrderHolder(binding)
            }
        }

    }

    companion object OrderDiffCallback: DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (order: Order) -> Unit) {
        fun onClick(order: Order) = clickListener(order)
    }
}

@BindingAdapter("listOrder")
fun bindRecycleView(recyclerView: RecyclerView, data: List<Order>?) {
    val adapter = recyclerView.adapter as ListOrdersAdapter
    adapter.submitList(data)
}

@BindingAdapter("orderStatus")
fun bindStatus(statusTextView: TextView, status: OrderLoadStatus?) {
    when (status) {
        OrderLoadStatus.DONE -> {
            statusTextView.visibility = View.GONE
        }
        OrderLoadStatus.ERROR -> {
            statusTextView.visibility = View.VISIBLE
        }
    }
}


@BindingAdapter("descriptionOrder")
fun TextView.setDescriptionOrder(item: Order) {
    "Заказ ${item.time},\n${item.address}".also { text = it }
    for (product in item.products) {
        append("\n${product.name} - ${product.count}шт.")
    }
}

@BindingAdapter("numOrder")
fun TextView.setNumOrder(item: Order) {
    text = "${item.num}"
    val time = item.time.split("-")[1]
    if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) > 7200) {
        setBackgroundResource(R.drawable.circle_green)
    }

    if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) in 3601..7199) {
        setBackgroundResource(R.drawable.circle_yellow)
    }

    if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) < 3600) {
        setBackgroundResource(R.drawable.circle_red)
    }

    if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) < 0) {
        setBackgroundResource(R.drawable.circle_grey)
    }
}