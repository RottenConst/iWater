package ru.iwater.youwater.iwaterlogistic.screens.main.tab.current

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.about_order_fragment.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.domain.vm.InfoOrderViewModel
import javax.inject.Inject

class AboutOrderFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: InfoOrderViewModel by viewModels { factory }

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
        return inflater.inflate(R.layout.about_order_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arg = arguments
        val id = arg?.getInt("id")

        viewModel.getOrderInfo(id)
        observeVM()

        //кнопка "позвонить клиенту"
        btn_call_client.setOnClickListener {
            callClient(viewModel.getPhoneNumberClient())
        }
        //кнопка "посмотреть на карте"
        btn_see_on_map.setOnClickListener {
//            val intent = Intent(this.context, MapsActivity::class.java)
//            startActivity(intent)
        }
        //кнопка в навигатор
        btn_navigator.setOnClickListener {
            val openApp = Intent(Intent.ACTION_VIEW)
            if (viewModel.order.value?.location?.lat == 0.0 && viewModel.order.value?.location?.lng == 0.0) {
                Toast.makeText(this.context, "Не удалось определить координаты", Toast.LENGTH_LONG).show()
            } else {
                openApp.data = Uri.parse(
                    "geo:" + "${viewModel.order.value?.location?.lng}, ${
                        viewModel.order.value?.location?.lat
                    }"
                )
                startActivity(openApp)
            }
        }
        //кнопка "копировать адрес"
        btn_copy_address.setOnClickListener {
            val address = viewModel.order.value?.address?.split(";")
            val clipBoard =
                activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("", address?.get(0))
            clipBoard.setPrimaryClip(clipData)
            getToast("адресс скопирован")
        }
        //кнопка "отгрузить заказ"
        btn_to_shipment_order.setOnClickListener {
//            viewModel.saveOrder()
            val fragment = ShipmentsFragment.newInstance()
            fragment.arguments = arg
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fl_card_order_container, fragment)
                ?.commit()
        }
    }

    private fun observeVM() {
        viewModel.order.observe(viewLifecycleOwner, { order ->
            "№ ${order.id}, ${order.time}".also { tv_name_date_order.text = it }
            tv_address_order.text = order.address
            if (order.products.size > 1) {
                tv_name_order.text = ""
                for (product in order.products) {
                    tv_name_order.append("${product.name} - ${product.count}шт.\n")
                }
            } else {
                "${order.products[0].name} - ${order.products[0].count}шт.".also { tv_name_order.text = it }
            }
            if (order.cash.isNotBlank()) {
                "Наличные: ${order.cash}".also { tv_price_order.text = it }
            } else {
                "Безналичные: ${order.cash_b}".also { tv_price_order.text = it }
            }
            "Точка #${order.num}".also { tv_number_point.text = it }
            "${order.name}; \n${order.address};".also { tv_about_client.text = it }
            tv_phone_number_client.text = order.contact
            tv_note_order.text = order.notice
        })
    }

    /**
     * диалошовое окно звонок клиенту
     **/
    private fun callClient(phones: Array<String>) {
        val intentCall = Intent(Intent.ACTION_CALL)
        if (ActivityCompat.checkSelfPermission(
                screenComponent.appContext(),
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.activity!!, arrayOf(Manifest.permission.CALL_PHONE),
                0
            )
        }
        AlertDialog.Builder(this.context!!)
            .setTitle(R.string.makeCall)
            .setPositiveButton("Ok") { _, _ ->
                if (intentCall.data != null) {
                    if (ActivityCompat.checkSelfPermission(
                            screenComponent.appContext(),
                            Manifest.permission.CALL_PHONE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        startActivity(intentCall)
                    }
                }
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }
            .setSingleChoiceItems(phones, -1) { _, item ->
                intentCall.data = Uri.parse("tel: ${phones[item]}")
            }.create().show()
    }

    private fun getToast(message: String) {
        Toast.makeText(this.context, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        fun newInstance(): AboutOrderFragment {
            return AboutOrderFragment()
        }
    }
}