package ru.iwater.youwater.iwaterlogistic.screens.main.tab.current

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.shipment_order_fragment.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.R.color.transperent
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.domain.vm.ShipmentsViewModel
import ru.iwater.youwater.iwaterlogistic.screens.main.MainActivity
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class ShipmentsFragment: BaseFragment(), LocationListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: ShipmentsViewModel by viewModels { factory }

    private val screenComponent = App().buildScreenComponent()

    private var myCoordinates = ""
    private var typeCash = ""
    private var documentsIsChecked: Boolean? = null
    private var typeClient: String? = null
    private var cash = 0.0F

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

        radio_cash_group.setOnCheckedChangeListener { _, checkedId ->
            radio_cash_group.setBackgroundColor(resources.getColor(R.color.transperent))
            when (checkedId) {
                R.id.radio_cash -> {
                    et_note_order_ship.text.clear()
                    et_note_order_ship.text.insert(0, "Оплата наличными, ")
                    typeCash = "Наличные"
                }
                R.id.radio_on_site -> {
                    et_note_order_ship.text.clear()
                    et_note_order_ship.text.insert(0, "Оплата на сайте, ")
                    typeCash = "На сайте"
                }
                R.id.radio_terminal -> {
                    et_note_order_ship.text.clear()
                    et_note_order_ship.text.insert(0, "Оплата через терминал, ")
                    typeCash = "Оплата через терминал"
                }
            }
        }

        et_tank_to_back.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                et_tank_to_back.setBackgroundColor(resources.getColor(transperent))
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btn_ship_order.setOnClickListener {
            val locationManager = screenComponent.appContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (ActivityCompat.checkSelfPermission(
                    screenComponent.appContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    screenComponent.appContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                showSnack("Включите GPS")
            }
            if (et_tank_to_back.text.toString().isEmpty() && typeCash.isEmpty() ) {
                showSnack("Укажиете количество бутылок к возврату и тип оплаты")
                radio_cash_group.setBackgroundColor(resources.getColor(R.color.shipmentBackground))
                et_tank_to_back.setBackgroundColor(resources.getColor(R.color.shipmentBackground))
            } else if (et_tank_to_back.text.toString().isEmpty()) {
                showSnack("Укажиете количество бутылок к возврату и тип оплаты")
                et_tank_to_back.setBackgroundColor(resources.getColor(R.color.shipmentBackground))
            } else if (typeCash.isEmpty()) {
                showSnack("Укажиете тип оплаты")
                radio_cash_group.setBackgroundColor(resources.getColor(R.color.shipmentBackground))
            } else if (documentsIsChecked == null && typeClient == "Юр. лицо"){
                showSnack("Укажиете подписаны ли документы")
                cb_doc_no.setBackgroundColor(resources.getColor(R.color.shipmentBackground))
                cb_doc_yes.setBackgroundColor(resources.getColor(R.color.shipmentBackground))
            } else {
                val currentDate = Calendar.getInstance()
                val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm")
                val timeComplete = formatter.format(currentDate.time)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10F, this)
                locationManager.getProviders(true)
                Timber.d(myCoordinates)
                viewModel.setCompleteOrder(
                    cash, typeCash,
                    et_tank_to_back.text.toString().toInt(),
                    timeComplete,
                    et_note_order_ship.text.toString(),
                    myCoordinates.split("-"),
                    myCoordinates
                )
                val intent = Intent(this.context, CompleteShipActivity::class.java)
                intent.putExtra("id", id)
                intent.putExtra("time", timeComplete)
                intent.putExtra("address", viewModel.order.value?.address)
                this.context?.let { it1 -> CompleteShipActivity.start(it1, intent) }
                activity?.finish()
            }
        }

        btn_no_ship_order.setOnClickListener {
            this.context?.let { it1 -> MainActivity.start(it1) }
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as CardOrderActivity?)
            ?.setActionBarTitle("Данные отгрузки")
    }

    override fun onLocationChanged(location: Location) {
        myCoordinates = "${location.latitude}-${location.longitude}"
    }

    override fun onProviderEnabled(provider: String) {
        super.onProviderEnabled(provider)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        if (status == LocationProvider.OUT_OF_SERVICE) Timber.d(
            "$provider OUT_OF_SERVICE"
        )
        if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) Timber.d(
            "$provider TEMPORARILY_UNAVAILABLE"
        )
    }

    override fun onProviderDisabled(provider: String) {
        super.onProviderDisabled(provider)
    }

    private fun observeVM() {
        viewModel.order.observe(viewLifecycleOwner, { order ->
            "№${order.id}, ${order.date}".also { tv_ship_num_date_order.text = it }
            tv_ship_address_order.text = order.address
            if (order.cash != 0.0F) {
                "Цена заказа: ${order.cash}".also { tv_ship_price.text = it }
                cash = order.cash
            } else {
                "Цена заказа: ${order.cash_b}".also { tv_ship_price.text = it }
                cash = order.cash_b
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
            "0" -> {
                ic_check_doc.visibility = View.GONE
                tv_check_doc.visibility = View.GONE
                cb_doc_no.visibility = View.GONE
                cb_doc_yes.visibility = View.GONE
                radio_cash_group.visibility = View.VISIBLE
                this.typeClient = "0"
            }
            "1" -> {
                ic_check_doc.visibility = View.VISIBLE
                tv_check_doc.visibility = View.VISIBLE
                cb_doc_no.visibility = View.VISIBLE
                cb_doc_yes.visibility = View.VISIBLE
                radio_cash_group.visibility = View.GONE
                this.typeClient = "1"
                typeCash = "Без наличные"
            }
            else -> {
                ic_check_doc.visibility = View.GONE
                tv_check_doc.visibility = View.GONE
                cb_doc_no.visibility = View.GONE
                cb_doc_yes.visibility = View.GONE
                radio_cash_group.visibility = View.GONE
                Toast.makeText(
                    this.context,
                    "Ошибка, возможно проблеиы с интернетом",
                    Toast.LENGTH_LONG
                ).show()
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
                cb_doc_no.setBackgroundColor(resources.getColor(R.color.transperent))
                cb_doc_yes.setBackgroundColor(resources.getColor(R.color.transperent))
                et_note_order_ship.text.insert(0, "Документы подписаны, ")
                documentsIsChecked = true
            } else {
                et_note_order_ship.text.clear()
                documentsIsChecked = null;
            }
        }
        cb_doc_no.setOnClickListener {
            if (cb_doc_no.isChecked) {
                cb_doc_yes.isChecked = false
                et_note_order_ship.text.clear()
                cb_doc_no.setBackgroundColor(resources.getColor(R.color.transperent))
                cb_doc_yes.setBackgroundColor(resources.getColor(R.color.transperent))
                et_note_order_ship.text.insert(0, "Документы не подписаны, ")
                documentsIsChecked = false
            } else {
                et_note_order_ship.text.clear()
                documentsIsChecked = null;
            }
        }
    }

    private fun showToast(value: String) {
        Toast.makeText(this.context, value, Toast.LENGTH_LONG).show()
    }

    private fun showSnack(message: String) {
        this.view?.let { Snackbar.make(it, message, Snackbar.LENGTH_LONG).show() }
    }

    companion object {
        fun newInstance(): ShipmentsFragment {
            return ShipmentsFragment()
        }
    }
}