package ru.iwater.youwater.iwaterlogistic.screens.main.tab.current

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.shipment_order_fragment.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.R.color.transperent
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.domain.vm.ShipmentsViewModel
import ru.iwater.youwater.iwaterlogistic.screens.main.MainActivity
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class ShipmentsFragment: BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: ShipmentsViewModel by viewModels { factory }
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private val screenComponent = App().buildScreenComponent()

    private var myCoordinates = ""
    private var typeCash = ""
    private var documentsIsChecked: Boolean? = null
    private var typeClient: String? = null
    private var cash = 0.0F
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.shipment_order_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getLastLocation()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arg = arguments
        val id = arg?.getInt("id")
        viewModel.getOrderInfo(id)
        observeVM()
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
                val timeComplete = Calendar.getInstance().timeInMillis/1000
//                Timber.d("this my coordinate $myCoordinates !!!!!!!!".toUpperCase())
                if (id != null) viewModel.setCompleteOrder(
                    this.context,
                    id,
                    typeCash,
                    cash,
                    et_tank_to_back.text.toString().toInt(),
                    timeComplete,
                    et_note_order_ship.text.toString(),
                    myCoordinates,
                )
            }
        }

        btn_no_ship_order.setOnClickListener {
            MainActivity.start(this.context)
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as CardOrderActivity?)
            ?.setActionBarTitle("Данные отгрузки")
    }


    private fun observeVM() {
        viewModel.order.observe(viewLifecycleOwner, { order ->
            viewModel.getTypeClient(order.id)
            "№${order.id}, ${order.time}".also { tv_ship_num_date_order.text = it }
            tv_ship_address_order.text = order.address
            "Цена заказа: ${order.cash}".also { tv_ship_price.text = it }
            cash = order.cash.toFloat()
        })
        viewModel.typeClient.observe(viewLifecycleOwner, {
            methodsOfCash(it.type)
        })
    }

    /**
     * в зависимости от типа клиента показываются методы оплаты
     **/
    private fun methodsOfCash(typeClient: Int?) {
        Timber.d("$typeClient!!!!!!!!!!!")
        Timber.d("${Calendar.getInstance().timeInMillis/1000}")
        when (typeClient) {
            0 -> {
                ic_check_doc.visibility = View.GONE
                tv_check_doc.visibility = View.GONE
                cb_doc_no.visibility = View.GONE
                cb_doc_yes.visibility = View.GONE
                radio_cash_group.visibility = View.VISIBLE
//                this.typeClient = "0"
            }
            1 -> {
                ic_check_doc.visibility = View.VISIBLE
                tv_check_doc.visibility = View.VISIBLE
                cb_doc_no.visibility = View.VISIBLE
                cb_doc_yes.visibility = View.VISIBLE
                radio_cash_group.visibility = View.GONE
//                this.typeClient = "1"
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

    private fun checkPermissions() =
        ActivityCompat.checkSelfPermission(screenComponent.appContext(), ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED

    private fun startLocationPermissionRequest() {
        this.activity?.parent?.let {
            ActivityCompat.requestPermissions(
                it, arrayOf(ACCESS_COARSE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnCompleteListener { taskLocation ->
            if (taskLocation.isSuccessful && taskLocation.result !== null) {
                val location = taskLocation.result
                myCoordinates = "${location.latitude}-${location.longitude}"
            } else {
                Timber.w(taskLocation.exception, "getLastLocation:exception")
            }
        }
    }

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!.parent, ACCESS_COARSE_LOCATION)) {
            // Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn't check the "Don't ask again" checkbox.
            Timber.i("Displaying permission rationale to provide additional context.")
            showSnackbar(R.string.permission_rationale, android.R.string.ok, View.OnClickListener {
                // Request permission
                startLocationPermissionRequest()
            })

        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            Timber.i("Requesting permission")
            startLocationPermissionRequest()
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

    private fun showSnackbar(
        snackStrId: Int,
        actionStrId: Int = 0,
        listener: View.OnClickListener? = null
    ) {
        val snackbar = Snackbar.make(
            this.context!!, this.view?.findViewById(android.R.id.content)!!, getString(snackStrId),
            LENGTH_INDEFINITE)
        if (actionStrId != 0 && listener != null) {
            snackbar.setAction(getString(actionStrId), listener)
        }
        snackbar.show()
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