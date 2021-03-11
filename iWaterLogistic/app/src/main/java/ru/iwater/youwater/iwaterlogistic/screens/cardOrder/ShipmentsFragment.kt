package ru.iwater.youwater.iwaterlogistic.screens.cardOrder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.shipment_order_fragment.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.domain.InfoOrderViewModel
import ru.iwater.youwater.iwaterlogistic.domain.ShipmentsViewModel
import javax.inject.Inject

class ShipmentsFragment: BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: ShipmentsViewModel by viewModels { factory }

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
        return inflater.inflate(R.layout.shipment_order_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arg = arguments
        val id = arg?.getInt("id")
        observeVM()
        id?.let { viewModel.getTypeClient(it) }
        id?.let { viewModel.getOrderInfo(it) }
        initDocuments()
    }

    override fun onResume() {
        super.onResume()
        (activity as CardOrderActivity?)
            ?.setActionBarTitle("Данные отгрузки")
    }

    private fun observeVM() {
        viewModel.order.observe(viewLifecycleOwner, { order ->
            "№${order.id}, ${order.date}".also { tv_ship_num_date_order.text = it }
            tv_ship_address_order.text = order.address
            if (order.cash != 0.0F) {
                "Цена заказа: ${order.cash}".also { tv_ship_price.text = it }
            } else {
                "Цена заказа: ${order.cash_b}".also { tv_ship_price.text = it }
                radio_cash_group.visibility = View.GONE
            }
        })
        viewModel.typeClient.observe(viewLifecycleOwner, {
            methodsOfCash(it)
        })
    }

    /**
     * в зависимости от типа клиента показываются методы оплаты
     **/
    private fun methodsOfCash(typeClient: String) {
        when (typeClient) {
            "Физ. лицо" -> {
                ic_check_doc.visibility = View.GONE
                tv_check_doc.visibility = View.GONE
                cb_doc_no.visibility = View.GONE
                cb_doc_yes.visibility = View.GONE
                radio_cash_group.visibility = View.VISIBLE
            }
            "Юр. лицо" -> {
                ic_check_doc.visibility = View.VISIBLE
                tv_check_doc.visibility = View.VISIBLE
                cb_doc_no.visibility = View.VISIBLE
                cb_doc_yes.visibility = View.VISIBLE
                radio_cash_group.visibility = View.GONE
            }
            else -> {
                ic_check_doc.visibility = View.GONE
                tv_check_doc.visibility = View.GONE
                cb_doc_no.visibility = View.GONE
                cb_doc_yes.visibility = View.GONE
                radio_cash_group.visibility = View.GONE
                Toast.makeText(this.context, "Ошибка, возможно проблеиы с интернетом", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * для юр лиц устанавливается были подписаны документы или нет
     **/
    private fun initDocuments() {
        cb_doc_yes.setOnClickListener {
            if (cb_doc_yes.isChecked) {
                cb_doc_no.isChecked = false
                et_note_order_ship.text.clear()
                et_note_order_ship.text.insert(0, "Документы подписаны")
            } else et_note_order_ship.text.clear()
        }
        cb_doc_no.setOnClickListener {
            if (cb_doc_no.isChecked) {
                cb_doc_yes.isChecked = false
                et_note_order_ship.text.clear()
                et_note_order_ship.text.insert(0, "Документы не подписаны")
            } else et_note_order_ship.text.clear()
        }
    }

    companion object {
        fun newInstance(): ShipmentsFragment {
            return ShipmentsFragment()
        }
    }
}