package ru.iwater.youwater.iwaterlogistic.screens.main.tab.current

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.databinding.FragmentOrdersListBinding
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.domain.vm.OrderListViewModel
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.ListOrdersAdapter
import ru.iwater.youwater.iwaterlogistic.screens.map.MapsActivity
import javax.inject.Inject

class FragmentCurrentOrders : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: OrderListViewModel by viewModels {factory}
    private val adapter = ListOrdersAdapter()
    private val screenComponent = App().buildScreenComponent()

    private lateinit var binding: FragmentOrdersListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders_list, container, false)
        binding.refreshContainer.setOnRefreshListener(this)
        initRecyclerView(binding)
        observeVW(binding)

        binding.btnGeneralMap.setOnClickListener {
            val intent = Intent(this.context, MapsActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    override fun onRefresh() {
        viewModel.getLoadCurrent()
        binding.refreshContainer.isRefreshing = true
    }

    private fun observeVW(binding: FragmentOrdersListBinding) {
        viewModel.getLoadCurrent()
        viewModel.listOrder.observe(viewLifecycleOwner, {
            if (it.isNullOrEmpty()) {
                binding.tvNotCurrentOrders.visibility = View.VISIBLE
                binding.listCurrentOrder.visibility = View.GONE
            } else {
                addCurrentOrders(it)
                binding.tvNotCurrentOrders.visibility = View.GONE
                binding.listCurrentOrder.visibility = View.VISIBLE
            }
        })
    }

    private fun initRecyclerView(binding: FragmentOrdersListBinding) {
        binding.listCurrentOrder.adapter = adapter
        adapter.notifyDataSetChanged()
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