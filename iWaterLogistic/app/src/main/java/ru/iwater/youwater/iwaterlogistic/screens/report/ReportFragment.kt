package ru.iwater.youwater.iwaterlogistic.screens.report

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_report_day.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.domain.Expenses
import ru.iwater.youwater.iwaterlogistic.domain.vm.CompleteOrdersViewModel
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.ExpensesAdapter
import ru.iwater.youwater.iwaterlogistic.screens.splash.SplashActivity
import ru.iwater.youwater.iwaterlogistic.service.TimeListenerService
import ru.iwater.youwater.iwaterlogistic.util.HelpLoadingProgress
import ru.iwater.youwater.iwaterlogistic.util.HelpStateLogin
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
import javax.inject.Inject

class ReportFragment: BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: CompleteOrdersViewModel by viewModels { factory }
    private val adapter = ExpensesAdapter()

    private val screenComponent = App().buildScreenComponent()
    private var isComplete: Boolean = false

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
        val date = UtilsMethods.getTodayDateString()
        "Отчет за $date".also { tv_report_title.text = it }
        initRV()
        observeReport()
        observeExpenses()


        btn_set_cost.setOnClickListener {
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
                    viewModel.addExpensesInBD(etNameParametr.text.toString(), etParametr.text.toString().toFloat())
                    UtilsMethods.showToast(this.context, etNameParametr.text.toString())
                    observeExpenses()
                }
                ?.setNegativeButton("Отмена") { dialog, _ ->
                    dialog.cancel();
                }
            val alertDialog = dialogBuilder?.create()
            alertDialog?.show()
        }

        btn_end_day.setOnClickListener {

            if (isComplete) {
                this.context?.let { it1 ->
                    AlertDialog.Builder(it1)
                        .setMessage(R.string.confirmEndDay)
                        .setPositiveButton(
                            R.string.yes
                        ) { _, _ ->
                            val intent = Intent(it1, SplashActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            HelpLoadingProgress.setLoginProgress(
                                it1,
                                HelpStateLogin.IS_WORK_START,
                                true
                            )
                            val service = Intent(
                                activity?.applicationContext,
                                TimeListenerService::class.java
                            )
                            activity?.stopService(service)
                            activity?.finish()
                            startActivity(intent)
                        }
                        .setNegativeButton(R.string.no) { dialog, _ ->
                            dialog.cancel()
                        }.create().show()
                }
            } else {
                this.context?.let { it1 ->
                    AlertDialog.Builder(it1)
                        .setMessage(R.string.confirmEndOrder)
                        .setPositiveButton(
                            R.string.ok
                        ) { dialog, _ ->
                            dialog.cancel()
                        }.create().show()
                }
            }
        }

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
        viewModel.isSendReportDay()
        viewModel.isCompleteOrder.observe(viewLifecycleOwner, {
            isComplete = it
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
        viewModel.manyToReport.observe(viewLifecycleOwner, {
            tv_num_cash_many_report.text = "${it}руб."
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