package ru.iwater.youwater.iwaterlogistic.screens.main.tab.current

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.databinding.FragmentOrdersListBinding
import ru.iwater.youwater.iwaterlogistic.domain.vm.OrderListViewModel
import ru.iwater.youwater.iwaterlogistic.domain.vm.OrderLoadStatus
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.ListOrdersAdapter
import ru.iwater.youwater.iwaterlogistic.screens.map.MapsActivity
import javax.inject.Inject

class FragmentCurrentOrders : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: OrderListViewModel by viewModels { factory }
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
        binding = FragmentOrdersListBinding.inflate(inflater)
        binding.refreshContainer.setOnRefreshListener(this)
        binding.lifecycleOwner = this
        binding.viewModelCurrentOrder = viewModel
        binding.listCurrentOrder.adapter = ListOrdersAdapter(ListOrdersAdapter.OnClickListener {
            viewModel.getAboutOrder(context, it.id)
        })
        viewModel.getLoadCurrent2()
        binding.btnGeneralMap.setOnClickListener {
            val intent = Intent(this.context, MapsActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    override fun onRefresh() {
        viewModel.getLoadCurrent2()
        if (viewModel.status.value == OrderLoadStatus.LOADING) {
            binding.refreshContainer.isRefreshing = true
        } else if (viewModel.status.value == OrderLoadStatus.DONE || viewModel.status.value == OrderLoadStatus.ERROR) {
            binding.refreshContainer.isRefreshing = false
        }

    }

    companion object {
        fun newInstance(): FragmentCurrentOrders = FragmentCurrentOrders()
    }
}