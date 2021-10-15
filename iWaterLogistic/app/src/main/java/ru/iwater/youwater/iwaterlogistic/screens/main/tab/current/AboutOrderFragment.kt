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
import ru.iwater.youwater.iwaterlogistic.screens.splash.LoadMapActivity
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
        binding.lifecycleOwner = this
        binding.viewModelDetail = viewModel

        //достаём id заказа
        val arg = arguments
        val id = arg?.getInt("id")
        if (id != null) viewModel.getOrderInfo(context, id)

        //кнопка "позвонить клиенту"
        binding.btnCallClient.setOnClickListener {
            callClient(viewModel.getPhoneNumberClient())
        }

        //кнопка "посмотреть на карте"
        binding.btnSeeOnMap.setOnClickListener {
            val intent = Intent(this.context, LoadMapActivity::class.java)
            startActivity(intent)
        }

        //кнопка в навигатор
        binding.btnNavigator.setOnClickListener {
            val openApp = Intent(Intent.ACTION_VIEW)
            if (viewModel.order.value?.coords.isNullOrBlank()) {
                Toast.makeText(this.context, "Не удалось определить координаты", Toast.LENGTH_LONG)
                    .show()
            } else {
                openApp.data = Uri.parse(
                    "geo:" + "${viewModel.order.value?.coords?.split(",")?.get(0)?.toDouble()}, ${
                        viewModel.order.value?.coords?.split(",")?.get(1)?.toDouble()
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
            val fragment = ShipmentsFragment.newInstance()
            fragment.arguments = arg
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fl_card_order_container, fragment)
                ?.commit()
        }

        return binding.root
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