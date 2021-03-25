package ru.iwater.youwater.iwaterlogistic.screens.main.tab

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_complete_order.*
import kotlinx.android.synthetic.main.layout_custom_alert_dialog.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.domain.*
import ru.iwater.youwater.iwaterlogistic.domain.vm.CompleteOrdersViewModel
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.CompleteListOrdersAdapter
import javax.inject.Inject

class FragmentCompleteOrders: BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: CompleteOrdersViewModel by viewModels {factory}
    private val screenComponent = App().buildScreenComponent()
    private val adapter = CompleteListOrdersAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_complete_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observeVW()
        viewModel.getCompleteListOrders()

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
                    showToast(etNameParametr.text.toString())
                }
                ?.setNegativeButton("Отмена") { dialog, _ ->
                            dialog.cancel();
                }
            val alertDialog = dialogBuilder?.create()
            alertDialog?.show()
        }

        btn_report.setOnClickListener {
            this.context?.let { it1 -> viewModel.getReportActivity(it1) }
        }
    }

    private fun observeVW() {
        viewModel.listCompleteOrder.observe(viewLifecycleOwner, {
            if (it.isNullOrEmpty()) {
                tv_no_complete.visibility = View.VISIBLE
                rv_complete_orders.visibility = View.GONE
            } else {
                addCompleteOrders(it)
                tv_no_complete.visibility = View.GONE
                rv_complete_orders.visibility = View.VISIBLE
            }
        })
    }

    private fun initRecyclerView() {
        rv_complete_orders.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        adapter.notifyDataSetChanged()
        rv_complete_orders.adapter = adapter
        adapter.onOrderClick = {
            context?.let { it1 -> viewModel.getAboutOrder(it1, it) }
        }
    }

    private fun addCompleteOrders(completeOrders: List<CompleteOrder>) {
        adapter.completeOrders.clear()
        adapter.completeOrders.addAll(completeOrders)
        adapter.notifyDataSetChanged()
    }

    private fun showToast(value: String) {
        Toast.makeText(this.context, value, Toast.LENGTH_LONG).show()
    }

    companion object {
        fun newInstance(): FragmentCompleteOrders = FragmentCompleteOrders()
    }
}