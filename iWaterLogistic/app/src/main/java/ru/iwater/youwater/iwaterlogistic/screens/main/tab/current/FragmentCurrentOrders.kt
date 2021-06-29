package ru.iwater.youwater.iwaterlogistic.screens.main.tab.current

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
//import ru.iwater.youwater.iwaterlogistic.domain.vm.OrderListViewModel
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.ListOrdersAdapter
import ru.iwater.youwater.iwaterlogistic.screens.map.MapsActivity
import timber.log.Timber
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

        btn_general_map.setOnClickListener {
            val intent = Intent(this.context, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onRefresh() {
        viewModel.getLoadCurrent()
        refresh_container.isRefreshing = false
    }

    private fun observeVW() {
        viewModel.getLoadCurrent()
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
            viewModel.getAboutOrder(context, it.id)
        }
    }

    private fun addCurrentOrders(orders: List<Order>) {
        adapter.orders.clear()
        adapter.orders.addAll(orders)
        adapter.notifyDataSetChanged()
    }

    private fun showSnack( message: String) {
        this.view?.let { Snackbar.make(it, message, Snackbar.LENGTH_LONG) }
    }

    companion object {
        fun newInstance(): FragmentCurrentOrders = FragmentCurrentOrders()
    }
}