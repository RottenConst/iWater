package ru.iwater.youwater.iwaterlogistic.screens.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.iwaterlogistic.databinding.ItemCurrentOrderBinding
import ru.iwater.youwater.iwaterlogistic.domain.OrderNewItem
import ru.iwater.youwater.iwaterlogistic.util.HelpLoadingProgress
import ru.iwater.youwater.iwaterlogistic.util.HelpState

class ListOrdersAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<OrderNewItem, ListOrdersAdapter.ListOrderHolder>(OrderDiffCallback) {

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


        fun bindOrders(order: OrderNewItem) {
            binding.order = order
            binding.executePendingBindings()
            if (HelpLoadingProgress.getStartDayShow(
                    binding.root.context,
                    HelpState.IS_WORK_START
                )
            ) {
                binding.arrow.visibility = View.GONE
            }
        }

        companion object {
            fun from(parent: ViewGroup): ListOrderHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCurrentOrderBinding.inflate(layoutInflater, parent, false)
                return ListOrderHolder(binding)
            }
        }

    }

    companion object OrderDiffCallback : DiffUtil.ItemCallback<OrderNewItem>() {
        override fun areItemsTheSame(oldItem: OrderNewItem, newItem: OrderNewItem): Boolean {
            return oldItem.order_id == newItem.order_id
        }

        override fun areContentsTheSame(oldItem: OrderNewItem, newItem: OrderNewItem): Boolean {
            return oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (order: OrderNewItem) -> Unit) {
        fun onClick(order: OrderNewItem) = clickListener(order)
    }
}