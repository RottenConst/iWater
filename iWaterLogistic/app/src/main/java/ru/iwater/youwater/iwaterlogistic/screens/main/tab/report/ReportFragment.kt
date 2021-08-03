package ru.iwater.youwater.iwaterlogistic.screens.main.tab.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.databinding.FragmentReportDayBinding
import ru.iwater.youwater.iwaterlogistic.domain.Expenses
import ru.iwater.youwater.iwaterlogistic.domain.vm.ReportViewModel
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.ExpensesAdapter
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
import javax.inject.Inject

class ReportFragment: BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: ReportViewModel by viewModels { factory }
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
    ): View {
        val binding = DataBindingUtil.inflate<FragmentReportDayBinding>(inflater, R.layout.fragment_report_day, container, false)

        val arg = arguments
        val date = arg?.getString("date")
        "Отчет за $date".also { binding.tvReportTitle.text = it }

        binding.rvExpenses.adapter = adapter
        adapter.notifyDataSetChanged()

        if (date == UtilsMethods.getTodayDateString()) {
            observeReport(binding)
            observeTodayExpenses(binding)
        } else {
            binding.btnSetCost.visibility = View.GONE
            date?.let { observeReportDate(it, binding) }
            date?.let { observeExpenses(it, binding) }
        }

        binding.btnSetCost.setOnClickListener {
            val layoutInflater = LayoutInflater.from(context)
            val viewDialog = layoutInflater.inflate(R.layout.layout_custom_alert_dialog, null)
            val dialogBuilder = context?.let { it1 -> AlertDialog.Builder(it1) }
            dialogBuilder?.setView(viewDialog)
            val etNameParametr = viewDialog.findViewById<EditText>(R.id.et_name_parametr)
            val etParametr = viewDialog.findViewById<EditText>(R.id.et_parametr)
            val tvTitle = viewDialog.findViewById<TextView>(R.id.tv_title_dialog)
            tvTitle.text = "Установить рассход"
            dialogBuilder
                ?.setCancelable(false)
                ?.setPositiveButton("Ok") { _, _ ->
                    when {
                        etNameParametr.text.isEmpty() -> {
                            UtilsMethods.showToast(this.context, "Вы не заполнели расход")
                        }
                        etParametr.text.isEmpty() -> {
                            UtilsMethods.showToast(this.context, "Вы не ввели сумму расхода")
                        }
                        etNameParametr.text.isEmpty() && etParametr.text.isEmpty() -> {
                            UtilsMethods.showToast(this.context, "Заполните расход и сумму расхода")
                        }
                        else -> {
                            viewModel.addExpensesInBD(
                                etNameParametr.text.toString(),
                                etParametr.text.toString().toFloat()
                            )
                            viewModel.sendExpenses(
                                etNameParametr.text.toString(),
                                etParametr.text.toString().toFloat()
                            )
                            UtilsMethods.showToast(this.context, etNameParametr.text.toString())
                            observeTodayExpenses(binding)
                            observeReport(binding)
                        }
                    }
                }
                ?.setNegativeButton("Отмена") { dialog, _ ->
                    dialog.cancel();
                }
            val alertDialog = dialogBuilder?.create()
            alertDialog?.show()
        }

        return binding.root
    }

    private fun observeReport(binding: FragmentReportDayBinding) {
        viewModel.initThisReport()
        viewModel.reportDay.observe(viewLifecycleOwner, {
            binding.tvNumTotalOrders.text = "${it.orderComplete}"
            binding.tvTankReport.text = "${it.tank}"
            "${it.totalMoney}руб.".also { binding.tvTotalMoney.text = it }
            "${it.cashOnSite + it.cashOnTerminal + it.cashMoney}руб.".also { binding.tvCashNumTotal.text = it }
            "${it.cashOnSite}руб.".also { binding.tvCashNumOnSite.text = it }
            "${it.cashOnTerminal}руб.".also { binding.tvCashNumOnTerminal.text = it }
            "${it.cashMoney}руб.".also { binding.tvCashNumMoney.text = it }
            "${it.noCashMoney}руб.".also { binding.tvNoCashNum.text = it }
            "${it.cashOnSite}руб.".also { binding.tvCashNumOnSite.text = it }
            "${it.moneyDelivery}руб.".also { binding.tvNumCashManyReport.text = it }
        })
    }

    private fun observeReportDate(date: String, binding: FragmentReportDayBinding) {
        viewModel.initDateReport(date)
        viewModel.reportDay.observe(viewLifecycleOwner, {
            binding.tvNumTotalOrders.text = "${it.orderComplete}"
            binding.tvTankReport.text = "${it.tank}"
            "${it.totalMoney}руб.".also { binding.tvTotalMoney.text = it }
            "${it.cashOnSite + it.cashOnTerminal + it.cashMoney}руб.".also { binding.tvCashNumTotal.text = it }
            "${it.cashOnSite}руб.".also { binding.tvCashNumOnSite.text = it }
            "${it.cashOnTerminal}руб.".also { binding.tvCashNumOnTerminal.text = it }
            "${it.cashMoney}руб.".also { binding.tvCashNumMoney.text = it }
            "${it.noCashMoney}руб.".also { binding.tvNoCashNum.text = it }
            "${it.cashOnSite}руб.".also { binding.tvCashNumOnSite.text = it }
            "${it.moneyDelivery}руб.".also { binding.tvNumCashManyReport.text = it }
        })
    }

    private fun observeTodayExpenses(binding: FragmentReportDayBinding) {
        viewModel.getTodayExpenses()
        viewModel.expenses.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                binding.tvExpensesTitle.text = "Расходы"
                addExpenses(it)
            } else {
                binding.tvExpensesTitle.text = "Расходов нет"
            }
        })
    }

    private fun observeExpenses(date: String, binding: FragmentReportDayBinding) {
        viewModel.getExpenses(date)
        viewModel.expenses.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                binding.tvExpensesTitle.text = "Расходы"
                addExpenses(it)
            } else {
                binding.tvExpensesTitle.text = "Расходов нет"
            }
        })
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