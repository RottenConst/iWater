package ru.iwater.youwater.iwaterlogistic.screens.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_expenses.view.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.domain.CompleteOrder
import ru.iwater.youwater.iwaterlogistic.domain.Expenses

class ExpensesAdapter(
    val expensesList: MutableList<Expenses> = mutableListOf()
): RecyclerView.Adapter<ExpensesAdapter.ExpensesHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpensesHolder {
        return ExpensesHolder(LayoutInflater.from(parent.context), parent, R.layout.item_expenses)
    }

    override fun onBindViewHolder(holder: ExpensesHolder, position: Int) {
        holder.bindExpenses(expensesList[position])
    }

    override fun getItemCount(): Int = expensesList.size

    inner class ExpensesHolder(inflater: LayoutInflater, parent: ViewGroup, resource: Int) :
        RecyclerView.ViewHolder(inflater.inflate(resource, parent, false)) {

            fun bindExpenses(expenses: Expenses) {
                itemView.tv_name_expenses.text = expenses.expens
                itemView.tv_expenses_cost.text = "${expenses.money}"
            }

        }


}