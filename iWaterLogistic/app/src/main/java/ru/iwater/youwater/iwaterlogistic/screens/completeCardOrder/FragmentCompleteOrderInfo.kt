package ru.iwater.youwater.iwaterlogistic.screens.completeCardOrder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.about_complete_fragment.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.domain.vm.CompleteOrdersViewModel
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
    ): View? {
        return inflater.inflate(R.layout.about_complete_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arg = arguments
        val id = arg?.getInt("id")
        observeVM()
        id?.let { viewModel.getCompleteOrder(it) }
    }

    private fun observeVM() {
        viewModel.order.observe(viewLifecycleOwner, { order ->
            "№ ${order.id}, ${order.date}".also { tv_id_and_date.text = it }
            tv_address.text = order.address
            tv_name_complete_order.text = "${order.product}, забрано тары: ${order.tank}шт"
            "${order.typeOfCash}: ${order.cash}".also { tv_price_complete_order.text = it }
            "${order.name}; \n${order.address};".also { tv_info_complete_client.text = it }
            tv_phone_number.text = order.contact
            tv_comment_operator.text = order.notice
            tv_comment_driver.text = order.noticeDriver
        })
    }

    companion object {
        fun newInstance(): FragmentCompleteOrderInfo {
            return FragmentCompleteOrderInfo()
        }
    }
}