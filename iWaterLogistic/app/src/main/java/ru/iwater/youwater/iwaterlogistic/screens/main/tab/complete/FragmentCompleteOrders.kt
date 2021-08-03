package ru.iwater.youwater.iwaterlogistic.screens.main.tab.complete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.databinding.FragmentCompleteOrderBinding
import ru.iwater.youwater.iwaterlogistic.domain.CompleteOrder
import ru.iwater.youwater.iwaterlogistic.domain.vm.CompleteOrdersViewModel
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.CompleteListOrdersAdapter
import javax.inject.Inject

class FragmentCompleteOrders : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: CompleteOrdersViewModel by viewModels { factory }
    private val screenComponent = App().buildScreenComponent()
    private val adapter = CompleteListOrdersAdapter()

    lateinit var binding: FragmentCompleteOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_complete_order, container, false)
        binding.refreshContainerComplete.setOnRefreshListener(this)
        initRecyclerView()
        observeVW(binding)
        return binding.root
    }

    override fun onRefresh() {
        viewModel.getCompleteListOrders()
        binding.refreshContainerComplete.isRefreshing = false
    }

    private fun observeVW(binding: FragmentCompleteOrderBinding) {
        viewModel.getCompleteListOrders()
        viewModel.listCompleteOrder.observe(viewLifecycleOwner, {
            if (it.isNullOrEmpty()) {
                binding.tvNoComplete.visibility = View.VISIBLE
                binding.rvCompleteOrders.visibility = View.GONE
            } else {
                addCompleteOrders(it)
                binding.tvNoComplete.visibility = View.GONE
                binding.rvCompleteOrders.visibility = View.VISIBLE
            }
        })
    }

    private fun initRecyclerView() {
        binding.rvCompleteOrders.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        adapter.notifyDataSetChanged()
        binding.rvCompleteOrders.adapter = adapter
        adapter.onOrderClick = {
            viewModel.getAboutOrder(this.context, it)
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