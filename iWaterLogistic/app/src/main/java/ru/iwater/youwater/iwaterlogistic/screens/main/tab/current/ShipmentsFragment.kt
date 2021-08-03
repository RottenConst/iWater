package ru.iwater.youwater.iwaterlogistic.screens.main.tab.current

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.R.color.transperent
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.databinding.ShipmentOrderFragmentBinding
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(screenComponent.appContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<ShipmentOrderFragmentBinding>(inflater, R.layout.shipment_order_fragment, container, false)

        val arg = arguments
        val id = arg?.getInt("id")
        viewModel.getOrderInfo(id)
        observeVM(binding)
        initDocuments(binding)

        binding.radioCashGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.radioCashGroup.setBackgroundColor(resources.getColor(transperent))
            when (checkedId) {
                R.id.radio_cash -> {
                    binding.etNoteOrderShip.text.clear()
                    binding.etNoteOrderShip.text.insert(0, "Оплата наличными, ")
                    typeCash = "Наличные"
                }
                R.id.radio_on_site -> {
                    binding.etNoteOrderShip.text.clear()
                    binding.etNoteOrderShip.text.insert(0, "Оплата на сайте, ")
                    typeCash = "На сайте"
                }
                R.id.radio_terminal -> {
                    binding.etNoteOrderShip.text.clear()
                    binding.etNoteOrderShip.text.insert(0, "Оплата через терминал, ")
                    typeCash = "Оплата через терминал"
                }
            }
        }

        binding.etTankToBack.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etTankToBack.setBackgroundColor(resources.getColor(transperent))
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnShipOrder.setOnClickListener {
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
            if (binding.etTankToBack.text.toString().isEmpty() && typeCash.isEmpty() ) {
                showSnack("Укажиете количество бутылок к возврату и тип оплаты")
                binding.radioCashGroup.setBackgroundColor(resources.getColor(R.color.shipmentBackground))
                binding.etTankToBack.setBackgroundColor(resources.getColor(R.color.shipmentBackground))
            } else if (binding.etTankToBack.text.toString().isEmpty()) {
                showSnack("Укажиете количество бутылок к возврату и тип оплаты")
                binding.etTankToBack.setBackgroundColor(resources.getColor(R.color.shipmentBackground))
            } else if (typeCash.isEmpty()) {
                showSnack("Укажиете тип оплаты")
                binding.radioCashGroup.setBackgroundColor(resources.getColor(R.color.shipmentBackground))
            } else if (documentsIsChecked == null && typeClient == "Юр. лицо"){
                showSnack("Укажиете подписаны ли документы")
                binding.cbDocNo.setBackgroundColor(resources.getColor(R.color.shipmentBackground))
                binding.cbDocYes.setBackgroundColor(resources.getColor(R.color.shipmentBackground))
            } else {
                val timeComplete = Calendar.getInstance().timeInMillis/1000
//                Timber.d("this my coordinate $myCoordinates !!!!!!!!".toUpperCase())
                if (id != null) viewModel.setCompleteOrder(
                    this.context,
                    id,
                    typeCash,
                    cash,
                    binding.etTankToBack.text.toString().toInt(),
                    timeComplete,
                    binding.etNoteOrderShip.text.toString(),
                    myCoordinates,
                )
            }
        }

        binding.btnNoShipOrder.setOnClickListener {
            MainActivity.start(this.context)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getLastLocation()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as CardOrderActivity?)
            ?.setActionBarTitle("Данные отгрузки")
    }


    private fun observeVM(binding: ShipmentOrderFragmentBinding) {
        viewModel.order.observe(viewLifecycleOwner, { order ->
            viewModel.getTypeClient(order.id)
            "№${order.id}, ${order.time}".also { binding.tvShipNumDateOrder.text = it }
            binding.tvShipAddressOrder.text = order.address
            "Цена заказа: ${order.cash}".also { binding.tvShipPrice.text = it }
            cash = order.cash.toFloat()
        })
        viewModel.typeClient.observe(viewLifecycleOwner, {
            methodsOfCash(it.type, binding)
        })
    }

    /**
     * в зависимости от типа клиента показываются методы оплаты
     **/
    private fun methodsOfCash(typeClient: Int?, binding: ShipmentOrderFragmentBinding) {
        Timber.d("$typeClient!!!!!!!!!!!")
        Timber.d("${Calendar.getInstance().timeInMillis/1000}")
        when (typeClient) {
            0 -> {
                binding.icCheckDoc.visibility = View.GONE
                binding.tvCheckDoc.visibility = View.GONE
                binding.cbDocNo.visibility = View.GONE
                binding.cbDocYes.visibility = View.GONE
                binding.radioCashGroup.visibility = View.VISIBLE
//                this.typeClient = "0"
            }
            1 -> {
                binding.icCheckDoc.visibility = View.VISIBLE
                binding.tvCheckDoc.visibility = View.VISIBLE
                binding.cbDocNo.visibility = View.VISIBLE
                binding.cbDocYes.visibility = View.VISIBLE
                binding.radioCashGroup.visibility = View.GONE
//                this.typeClient = "1"
                typeCash = "Без наличные"
            }
            else -> {
                binding.icCheckDoc.visibility = View.GONE
                binding.tvCheckDoc.visibility = View.GONE
                binding.cbDocNo.visibility = View.GONE
                binding.cbDocYes.visibility = View.GONE
                binding.radioCashGroup.visibility = View.GONE
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

    @SuppressLint("UseRequireInsteadOfGet")
    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!.parent, ACCESS_COARSE_LOCATION)) {
            // Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn't check the "Don't ask again" checkbox.
            Timber.i("Displaying permission rationale to provide additional context.")
            showSnackBar(R.string.permission_rationale, android.R.string.ok) {
                // Request permission
                startLocationPermissionRequest()
            }

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
    private fun initDocuments(binding: ShipmentOrderFragmentBinding) {
        binding.cbDocYes.setOnClickListener {
            if (binding.cbDocYes.isChecked) {
                binding.cbDocNo.isChecked = false
                binding.etNoteOrderShip.text.clear()
                binding.cbDocNo.setBackgroundColor(resources.getColor(R.color.transperent))
                binding.cbDocYes.setBackgroundColor(resources.getColor(R.color.transperent))
                binding.etNoteOrderShip.text.insert(0, "Документы подписаны, ")
                documentsIsChecked = true
            } else {
                binding.etNoteOrderShip.text.clear()
                documentsIsChecked = null;
            }
        }
        binding.cbDocNo.setOnClickListener {
            if (binding.cbDocNo.isChecked) {
                binding.cbDocYes.isChecked = false
                binding.etNoteOrderShip.text.clear()
                binding.cbDocNo.setBackgroundColor(resources.getColor(R.color.transperent))
                binding.cbDocYes.setBackgroundColor(resources.getColor(R.color.transperent))
                binding.etNoteOrderShip.text.insert(0, "Документы не подписаны, ")
                documentsIsChecked = false
            } else {
                binding.etNoteOrderShip.text.clear()
                documentsIsChecked = null;
            }
        }
    }

    private fun showToast(value: String) {
        Toast.makeText(this.context, value, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun showSnackBar(
        snackStrId: Int,
        actionStrId: Int = 0,
        listener: View.OnClickListener? = null
    ) {
        val snackBar = Snackbar.make(
            this.context!!, view!!.findViewById(android.R.id.content), getString(snackStrId),
            LENGTH_INDEFINITE)
        if (actionStrId != 0 && listener != null) {
            snackBar.setAction(getString(actionStrId), listener)
        }
        snackBar.show()
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