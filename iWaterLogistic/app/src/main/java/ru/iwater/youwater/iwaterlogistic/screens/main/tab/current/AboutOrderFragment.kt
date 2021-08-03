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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.databinding.AboutOrderFragmentBinding
import ru.iwater.youwater.iwaterlogistic.domain.vm.InfoOrderViewModel
import ru.iwater.youwater.iwaterlogistic.screens.map.MapsActivity
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
    ): View {
        val binding = DataBindingUtil.inflate<AboutOrderFragmentBinding>(
            inflater,
            R.layout.about_order_fragment,
            container,
            false
        )
        //достаём id заказа
        val arg = arguments
        val id = arg?.getInt("id")
        if (id != null) viewModel.getOrderInfo(id)

        observeVM(binding)

        //кнопка "позвонить клиенту"
        binding.btnCallClient.setOnClickListener {
            callClient(viewModel.getPhoneNumberClient())
        }

        //кнопка "посмотреть на карте"
        binding.btnSeeOnMap.setOnClickListener {
            val intent = Intent(this.context, MapsActivity::class.java)
            startActivity(intent)
        }

        //кнопка в навигатор
        binding.btnNavigator.setOnClickListener {
            val openApp = Intent(Intent.ACTION_VIEW)
            if (viewModel.order.value?.location?.lat == 0.0 && viewModel.order.value?.location?.lng == 0.0) {
                Toast.makeText(this.context, "Не удалось определить координаты", Toast.LENGTH_LONG)
                    .show()
            } else {
                openApp.data = Uri.parse(
                    "geo:" + "${viewModel.order.value?.location?.lat}, ${
                        viewModel.order.value?.location?.lng
                    }"
                )
                startActivity(openApp)
            }
        }

        //кнопка "копировать адрес"
        binding.btnCopyAddress.setOnClickListener {
            val address = viewModel.order.value?.address?.split(";")
            val clipBoard =
                activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("", address?.get(0))
            clipBoard.setPrimaryClip(clipData)
            getToast("адресс скопирован")
        }

        //кнопка "отгрузить заказ"
        binding.btnToShipmentOrder.setOnClickListener {
//            viewModel.saveOrder()
            val fragment = ShipmentsFragment.newInstance()
            fragment.arguments = arg
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fl_card_order_container, fragment)
                ?.commit()
        }

        return binding.root
    }

    private fun observeVM(binding: AboutOrderFragmentBinding) {
        viewModel.order.observe(viewLifecycleOwner, { order ->
            "№ ${order.id}, ${order.time}".also { binding.tvNameDateOrder.text = it }
            binding.tvAddressOrder.text = order.address
            if (order.products.size > 1) {
                binding.tvNameOrder.text = ""
                for (product in order.products) {
                    binding.tvNameOrder.append("${product.name} - ${product.count}шт.\n")
                }
            } else {
                "${order.products[0].name} - ${order.products[0].count}шт.".also {
                    binding.tvNameOrder.text = it
                }
            }
            if (order.cash.isNotBlank()) {
                "Наличные: ${order.cash}".also { binding.tvPriceOrder.text = it }
            } else {
                "Безналичные: ${order.cash_b}".also { binding.tvPriceOrder.text = it }
            }
            "Точка #${order.num}".also { binding.tvNumberPoint.text = it }
            "${order.name}; \n${order.address};".also { binding.tvAboutClient.text = it }
            binding.tvPhoneNumberClient.text = order.contact
            binding.tvNoteOrder.text = order.notice
        })
    }

    /**
     * диалошовое окно звонок клиенту
     **/
    private fun callClient(phones: Array<String>) {
        val intentCall = Intent(Intent.ACTION_CALL)
        val activity = this.activity
        val context = this.context
        if (ActivityCompat.checkSelfPermission(
                screenComponent.appContext(),
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (activity != null) ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CALL_PHONE),
                0
            )
        }
        if (context != null) {
            AlertDialog.Builder(context)
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