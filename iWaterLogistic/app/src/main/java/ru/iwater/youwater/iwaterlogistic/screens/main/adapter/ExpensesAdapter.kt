package ru.iwater.youwater.iwaterlogistic.screens.main.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.iwaterlogistic.databinding.ItemExpensesBinding
import ru.iwater.youwater.iwaterlogistic.domain.Expenses

class ExpensesAdapter : ListAdapter<Expenses, ExpensesAdapter.ExpensesHolder>(ExpensesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpensesHolder {
        return ExpensesHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ExpensesHolder, position: Int) {
        val item = getItem(position)
        holder.bindExpenses(item)
    }

    class ExpensesHolder(val binding: ItemExpensesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindExpenses(expenses: Expenses) {
            binding.expenses = expenses
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ExpensesHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemExpensesBinding.inflate(inflater, parent, false)
                return ExpensesHolder(binding)
            }

        }

    }
}

class ExpensesDiffCallback : DiffUtil.ItemCallback<Expenses>() {
    override fun areItemsTheSame(oldItem: Expenses, newItem: Expenses): Boolean {
        return oldItem.date_created == newItem.date_created
    }

    override fun areContentsTheSame(oldItem: Expenses, newItem: Expenses): Boolean {
        return oldItem == newItem
    }
}

@BindingAdapter("expensesImage")
fun ImageView.setExpensesImage(item: Expenses) {
    val file = BitmapFactory.decodeFile(item.fileName)
    setImageBitmap(file)
}

@BindingAdapter("nameText")
fun TextView.setNameText(item: Expenses) {
    text = item.expens
}

@BindingAdapter("costText")
fun TextView.setCostText(item: Expenses) {
    text = "${item.money}"
}