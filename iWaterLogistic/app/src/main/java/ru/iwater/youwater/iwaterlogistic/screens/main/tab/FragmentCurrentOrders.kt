package ru.iwater.youwater.iwaterlogistic.screens.main.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_orders_list.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.domain.vm.OrderListViewModel
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.ListOrdersAdapter
import javax.inject.Inject

class FragmentCurrentOrders : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: OrderListViewModel by viewModels {factory}
    private val adapter = ListOrdersAdapter()
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
        return layoutInflater.inflate(R.layout.fragment_orders_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        refresh_container.setOnRefreshListener(this)
        initRecyclerView()
        observeVW()
        viewModel.getLoadOrder()
    }

    override fun onRefresh() {
        viewModel.getLoadOrder()
        refresh_container.isRefreshing = false
    }

    private fun observeVW() {
        viewModel.listOrder.observe(viewLifecycleOwner, {
            if (it.isNullOrEmpty()) {
                tv_not_current_orders.visibility = View.VISIBLE
                list_current_order.visibility = View.GONE
            } else {
                addCurrentOrders(it)
                tv_not_current_orders.visibility = View.GONE
                list_current_order.visibility = View.VISIBLE
            }
        })
    }

    private fun initRecyclerView() {
        list_current_order.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        adapter.notifyDataSetChanged()
        list_current_order.adapter = adapter
        adapter.onOrderClick = {
            context?.let { it1 -> viewModel.getAboutOrder(it1, it) }
        }
    }

    private fun addCurrentOrders(orders: List<Order>) {
        adapter.orders.clear()
        adapter.orders.addAll(orders)
        adapter.notifyDataSetChanged()
    }

    private fun showToast(value: String) {
        Toast.makeText(this.context, value, Toast.LENGTH_LONG).show()
    }

    private fun showSnack( message: String) {
        this.view?.let { Snackbar.make(it, message, Snackbar.LENGTH_LONG) }
    }

    companion object {
        fun newInstance(): FragmentCurrentOrders = FragmentCurrentOrders()
    }
}