package ru.iwater.youwater.iwaterlogistic.screens.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.databinding.ItemCurrentOrderPrevBinding
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods

class ListCurrentOrdersPreview : ListAdapter<Order, ListCurrentOrdersPreview.ListCurrentOrderHolder>(OrderPreviewDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCurrentOrderHolder {
        return ListCurrentOrderHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ListCurrentOrderHolder, position: Int) {
        val item = getItem(position)
        holder.bindOrders(item)
    }

    class ListCurrentOrderHolder(val binding: ItemCurrentOrderPrevBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindOrders(order: Order) {
            binding.orderPreview = order
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ListCurrentOrderHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCurrentOrderPrevBinding.inflate(layoutInflater, parent, false)
                return ListCurrentOrderHolder(binding)
            }
        }

    }

    companion object OrderPreviewDiffCallback: DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}

@BindingAdapter("listOrderPreview")
fun bindRecycleViewPreview(recyclerView: RecyclerView, data: List<Order>?) {
    val adapter = recyclerView.adapter as ListCurrentOrdersPreview
    adapter.submitList(data)
}

@BindingAdapter("textOrderPreview")
fun TextView.setTextOrderPreview(item: Order) {
    "Заказ ${item.time},\n${item.address}".also { text = it }
    for (product in item.products) {
        append("\n${product.name} - ${product.count}шт.")
    }
}

@BindingAdapter("numOrderPreview")
fun TextView.setNumOrderPreview(item: Order) {
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