package ru.iwater.youwater.iwaterlogistic.screens.main.tab.complete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.databinding.AboutCompleteFragmentBinding
import ru.iwater.youwater.iwaterlogistic.domain.vm.CompleteOrdersViewModel
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.current.ShipmentsFragment
import javax.inject.Inject

class FragmentCompleteOrderInfo: BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: CompleteOrdersViewModel by viewModels { factory }

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
        val binding = AboutCompleteFragmentBinding.inflate(inflater, container, false)
        val arg = arguments
        val id = arg?.getInt("id")
        observeVM(binding)
        viewModel.getCompleteOrder(id)
        binding.btnRestore.setOnClickListener {
//            val fragment = RestoreOrderFragment.newInstance()
//            fragment.arguments = arg
            activity?.supportFragmentManager?.beginTransaction()
//                ?.replace(R.id.fl_card_order_container, fragment)
                ?.commit()
        }
        return binding.root
    }

    private fun observeVM(binding: AboutCompleteFragmentBinding) {
        viewModel.order.observe(viewLifecycleOwner, { order ->
            "№ ${order.id}, ${order.time}".also { binding.tvIdAndDate.text = it }
            binding.tvAddress.text = order.address
            binding.tvNameCompleteOrder.text = ""
            for (product in order.products) {
                binding.tvNameCompleteOrder.append("${product.name} - ${product.count}шт.\n")
            }
            binding.tvNameCompleteOrder.append("\nзабрано тары: ${order.tank}шт")
            "${order.typeOfCash}: ${order.cash}".also { binding.tvPriceCompleteOrder.text = it }
            if (order.typeOfCash == "-") {
                viewModel.getReport(order.id)
                viewModel.report.observe(viewLifecycleOwner, { report ->
                    if (report == null) {
                        binding.btnRestore.visibility = View.VISIBLE
                    } else {
                        viewModel.setCompleteOrder(order.id, report)
                    }
                })

            }
            "${order.name}; \n${order.address};".also { binding.tvInfoCompleteClient.text = it }
            binding.tvPhoneNumber.text = order.contact
            binding.tvCommentOperator.text = order.notice
            binding.tvCommentDriver.text = order.noticeDriver
        })
    }

    companion object {
        fun newInstance(): FragmentCompleteOrderInfo {
            return FragmentCompleteOrderInfo()
        }
    }
}