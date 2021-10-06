package ru.iwater.youwater.iwaterlogistic.screens.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.iwaterlogistic.databinding.ItemCurrentOrderBinding
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.util.HelpLoadingProgress
import ru.iwater.youwater.iwaterlogistic.util.HelpState

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

    companion object OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
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