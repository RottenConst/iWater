package ru.iwater.youwater.iwaterlogistic.screens.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_report_day.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.domain.Expenses
import ru.iwater.youwater.iwaterlogistic.domain.vm.CompleteOrdersViewModel
import ru.iwater.youwater.iwaterlogistic.screens.completeCardOrder.FragmentCompleteOrderInfo
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.ExpensesAdapter
import javax.inject.Inject

class ReportFragment: BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: CompleteOrdersViewModel by viewModels { factory }
    private val adapter = ExpensesAdapter()

    private val screenComponent = App().buildScreenComponent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report_day, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arg = arguments
        val date = arg?.getString("date")
        "Отчет за $date".also { tv_report_title.text = it }
        initRV()
        observeReport()
        observeExpenses()

    }

    private fun observeReport() {
        viewModel.initReport()
        viewModel.reportDay.observe(viewLifecycleOwner, {
            tv_num_total_orders.text = "${it.orderComplete}"
            tv_tank_report.text = "${it.tank}"
            tv_total_money.text = "${it.totalMoney}руб."
            tv_cash_num_total.text = "${it.cashOnSite + it.cashOnTerminal + it.cashMoney}руб."
            tv_cash_num_on_site.text = "${it.cashOnSite}руб."
            tv_cash_num_on_terminal.text = "${it.cashOnTerminal}руб."
            tv_cash_num_money.text = "${it.cashMoney}руб."
            tv_no_cash_num.text = "${it.noCashMoney}руб."
            tv_cash_num_on_site.text = "${it.cashOnSite}руб."
        })
    }

    private fun observeExpenses() {
        viewModel.getTodayExpenses()
        viewModel.expenses.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                tv_expenses_title.text = "Расходы"
                addExpenses(it)
            } else {
                tv_expenses_title.text = "Расходов нет"
            }

        })
    }

    private fun initRV() {
        rv_expenses.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        adapter.notifyDataSetChanged()
        rv_expenses.adapter = adapter
    }

    private fun addExpenses(expenses: List<Expenses>) {
        adapter.expensesList.clear()
        adapter.expensesList.addAll(expenses)
        adapter.notifyDataSetChanged()
    }

    companion object {
        fun newInstance(): ReportFragment {
            return ReportFragment()
        }
    }
}