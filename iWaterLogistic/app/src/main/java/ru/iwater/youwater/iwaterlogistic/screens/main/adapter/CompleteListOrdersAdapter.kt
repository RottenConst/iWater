package ru.iwater.youwater.iwaterlogistic.screens.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.iwaterlogistic.databinding.ItemCompleteOrderBinding
import ru.iwater.youwater.iwaterlogistic.domain.CompleteOrder
import java.text.SimpleDateFormat
import java.util.*

class CompleteListOrdersAdapter(
    val completeOrders: MutableList<CompleteOrder> = mutableListOf()
) : RecyclerView.Adapter<CompleteListOrdersAdapter.CompleteListOrdersHolder>() {

    lateinit var onOrderClick: ((CompleteOrder) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompleteListOrdersHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CompleteListOrdersHolder(ItemCompleteOrderBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: CompleteListOrdersHolder, position: Int) {
        holder.bindCompleteOrders(completeOrders[position], position)
    }

    override fun getItemCount(): Int = completeOrders.size

    inner class CompleteListOrdersHolder(val binding: ItemCompleteOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.cardCompleteOrder.setOnClickListener {
                onOrderClick.invoke(completeOrders[adapterPosition])
            }
        }

        fun bindCompleteOrders(completeOrders: CompleteOrder, position: Int) {
            val sdf = SimpleDateFormat("HH:mm:ss")
            val time = Date(completeOrders.timeComplete * 1000)
            val date = sdf.format(time)
            binding.tvNumOrder.text = (position + 1).toString()
            "№${completeOrders.id} ${completeOrders.time} Завершен в ${date}, ${completeOrders.address}".also {
                binding.tvInfoOrder.text = it
            }
        }
    }

}