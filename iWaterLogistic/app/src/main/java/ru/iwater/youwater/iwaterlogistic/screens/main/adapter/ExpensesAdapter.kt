package ru.iwater.youwater.iwaterlogistic.screens.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.iwaterlogistic.databinding.ItemExpensesBinding
import ru.iwater.youwater.iwaterlogistic.domain.Expenses

class ExpensesAdapter(
    val expensesList: MutableList<Expenses> = mutableListOf()
) : RecyclerView.Adapter<ExpensesAdapter.ExpensesHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpensesHolder {
        return ExpensesHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ExpensesHolder, position: Int) {
        holder.bindExpenses(expensesList[position])
    }

    override fun getItemCount(): Int = expensesList.size

    class ExpensesHolder(val binding: ItemExpensesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindExpenses(expenses: Expenses) {
            binding.tvNameExpenses.text = expenses.expens
            binding.tvExpensesCost.text = "${expenses.money}"
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