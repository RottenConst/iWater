package ru.iwater.youwater.iwaterlogistic.screens.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.iwaterlogistic.databinding.ItemCompleteOrderBinding
import ru.iwater.youwater.iwaterlogistic.domain.CompleteOrder

class CompleteListOrdersAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<CompleteOrder, CompleteListOrdersAdapter.CompleteListOrdersHolder>(CompleteOrderDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompleteListOrdersHolder {
        return CompleteListOrdersHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CompleteListOrdersHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(item)
        }
        holder.bindCompleteOrders(item, position)
    }

    class CompleteListOrdersHolder(val binding: ItemCompleteOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindCompleteOrders(completeOrders: CompleteOrder, position: Int) {
            binding.completeOrder = completeOrders
            binding.executePendingBindings()
            binding.tvNumOrder.text = (position + 1).toString()

        }

        companion object {
            fun from(parent: ViewGroup): CompleteListOrdersHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCompleteOrderBinding.inflate(layoutInflater, parent, false)
                return CompleteListOrdersHolder(binding)
            }
        }
    }

    companion object CompleteOrderDiffCallback : DiffUtil.ItemCallback<CompleteOrder>() {
        override fun areItemsTheSame(oldItem: CompleteOrder, newItem: CompleteOrder): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CompleteOrder, newItem: CompleteOrder): Boolean {
            return oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (order: CompleteOrder) -> Unit) {
        fun onClick(order: CompleteOrder) = clickListener(order)
    }

}