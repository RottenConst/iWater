package ru.iwater.youwater.iwaterlogistic.screens.main.tab.report

//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.EditText
//import android.widget.TextView
//import androidx.appcompat.app.AlertDialog
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.LinearLayoutManager
//import kotlinx.android.synthetic.main.fragment_report_day.*
//import ru.iwater.youwater.iwaterlogistic.R
//import ru.iwater.youwater.iwaterlogistic.base.App
//import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
//import ru.iwater.youwater.iwaterlogistic.domain.Expenses
//import ru.iwater.youwater.iwaterlogistic.domain.vm.ReportViewModel
//import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.ExpensesAdapter
//import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
//import javax.inject.Inject
//
//class ReportFragment: BaseFragment() {
//
////    @Inject
////    lateinit var factory: ViewModelProvider.Factory
////    private val viewModel: ReportViewModel by viewModels { factory }
//    private val adapter = ExpensesAdapter()
//
////    private val screenComponent = App().buildScreenComponent()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
////        screenComponent.inject(this)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_report_day, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val arg = arguments
//        val date = arg?.getString("date")
//        "Отчет за $date".also { tv_report_title.text = it }
//        initRV()
//        if (date == UtilsMethods.getTodayDateString()) {
////            observeReport()
////            observeTodayExpenses()
//        } else {
//            btn_set_cost.visibility = View.GONE
////            date?.let { observeReportDate(it) }
////            date?.let { observeExpenses(it) }
//        }
//
//        btn_set_cost.setOnClickListener {
//            val layoutInflater = LayoutInflater.from(context)
//            val viewDialog = layoutInflater.inflate(R.layout.layout_custom_alert_dialog, null)
//            val dialogBuilder = context?.let { it1 -> AlertDialog.Builder(it1) }
//            dialogBuilder?.setView(viewDialog)
//            val etNameParametr = viewDialog.findViewById<EditText>(R.id.et_name_parametr)
//            val etParametr = viewDialog.findViewById<EditText>(R.id.et_parametr)
//            val tvTitle = viewDialog.findViewById<TextView>(R.id.tv_title_dialog)
//            tvTitle.text = "Установить рассход"
//            dialogBuilder
//                ?.setCancelable(false)
//                ?.setPositiveButton("Ok") { _, _ ->
//                    when {
//                        etNameParametr.text.isEmpty() -> {
//                            UtilsMethods.showToast(this.context, "Вы не заполнели расход")
//                        }
//                        etParametr.text.isEmpty() -> {
//                            UtilsMethods.showToast(this.context, "Вы не ввели сумму расхода")
//                        }
//                        etNameParametr.text.isEmpty() && etParametr.text.isEmpty() -> {
//                            UtilsMethods.showToast(this.context, "Заполните расход и сумму расхода")
//                        }
//                        else -> {
////                            viewModel.addExpensesInBD(
////                                etNameParametr.text.toString(),
////                                etParametr.text.toString().toFloat()
////                            )
////                            viewModel.sendExpenses(
////                                etNameParametr.text.toString(),
////                                etParametr.text.toString().toFloat()
////                            )
//                            UtilsMethods.showToast(this.context, etNameParametr.text.toString())
////                            observeTodayExpenses()
////                            observeReport()
//                        }
//                    }
//                }
//                ?.setNegativeButton("Отмена") { dialog, _ ->
//                    dialog.cancel();
//                }
//            val alertDialog = dialogBuilder?.create()
//            alertDialog?.show()
//        }
//
//    }
//
////    private fun observeReport() {
////        viewModel.initThisReport()
////        viewModel.reportDay.observe(viewLifecycleOwner, {
////            tv_num_total_orders.text = "${it.orderComplete}"
////            tv_tank_report.text = "${it.tank}"
////            tv_total_money.text = "${it.totalMoney}руб."
////            tv_cash_num_total.text = "${it.cashOnSite + it.cashOnTerminal + it.cashMoney}руб."
////            tv_cash_num_on_site.text = "${it.cashOnSite}руб."
////            tv_cash_num_on_terminal.text = "${it.cashOnTerminal}руб."
////            tv_cash_num_money.text = "${it.cashMoney}руб."
////            tv_no_cash_num.text = "${it.noCashMoney}руб."
////            tv_cash_num_on_site.text = "${it.cashOnSite}руб."
////            tv_num_cash_many_report.text = "${it.moneyDelivery}руб."
////        })
////    }
//
////    private fun observeReportDate(date: String) {
////        viewModel.initDateReport(date)
////        viewModel.reportDay.observe(viewLifecycleOwner, {
////            tv_num_total_orders.text = "${it.orderComplete}"
////            tv_tank_report.text = "${it.tank}"
////            tv_total_money.text = "${it.totalMoney}руб."
////            tv_cash_num_total.text = "${it.cashOnSite + it.cashOnTerminal + it.cashMoney}руб."
////            tv_cash_num_on_site.text = "${it.cashOnSite}руб."
////            tv_cash_num_on_terminal.text = "${it.cashOnTerminal}руб."
////            tv_cash_num_money.text = "${it.cashMoney}руб."
////            tv_no_cash_num.text = "${it.noCashMoney}руб."
////            tv_cash_num_on_site.text = "${it.cashOnSite}руб."
////            tv_num_cash_many_report.text = "${it.moneyDelivery}руб."
////        })
////    }
//
////    private fun observeTodayExpenses() {
////        viewModel.getTodayExpenses()
////        viewModel.expenses.observe(viewLifecycleOwner, {
////            if (it.isNotEmpty()) {
////                tv_expenses_title.text = "Расходы"
////                addExpenses(it)
////            } else {
////                tv_expenses_title.text = "Расходов нет"
////            }
////        })
////    }
//
////    private fun observeExpenses(date: String) {
////        viewModel.getExpenses(date)
////        viewModel.expenses.observe(viewLifecycleOwner, {
////            if (it.isNotEmpty()) {
////                tv_expenses_title.text = "Расходы"
////                addExpenses(it)
////            } else {
////                tv_expenses_title.text = "Расходов нет"
////            }
////        })
////    }
//
//    private fun initRV() {
//        rv_expenses.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
//        adapter.notifyDataSetChanged()
//        rv_expenses.adapter = adapter
//    }
//
//    private fun addExpenses(expenses: List<Expenses>) {
//        adapter.expensesList.clear()
//        adapter.expensesList.addAll(expenses)
//        adapter.notifyDataSetChanged()
//    }
//
//    companion object {
//        fun newInstance(): ReportFragment {
//            return ReportFragment()
//        }
//    }
//}