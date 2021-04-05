package ru.iwater.youwater.iwaterlogistic.screens.main.tab.complete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_complete_order.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.domain.*
import ru.iwater.youwater.iwaterlogistic.domain.vm.CompleteOrdersViewModel
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.CompleteListOrdersAdapter
import javax.inject.Inject

class FragmentCompleteOrders : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: CompleteOrdersViewModel by viewModels { factory }
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

//        btn_report.setOnClickListener {
//            this.context?.let { it1 -> viewModel.getReportActivity(it1) }
//        }
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
        rv_complete_orders.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
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

    companion object {
        fun newInstance(): FragmentCompleteOrders = FragmentCompleteOrders()
    }
}