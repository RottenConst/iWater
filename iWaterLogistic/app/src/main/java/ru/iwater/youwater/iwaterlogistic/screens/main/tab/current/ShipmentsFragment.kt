package ru.iwater.youwater.iwaterlogistic.screens.main.tab.current

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import ru.iwater.youwater.iwaterlogistic.domain.vm.TypeClient
import ru.iwater.youwater.iwaterlogistic.screens.main.MainActivity
import timber.log.Timber
import javax.inject.Inject

private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34

class ShipmentsFragment: BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: ShipmentsViewModel by viewModels { factory }

    private val screenComponent = App().buildScreenComponent()

    private var documentsIsChecked: Boolean? = null
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
        binding.lifecycleOwner = this
        binding.viewModelShip = viewModel

        val arg = arguments
        val id = arg?.getInt("id")
        viewModel.getOrderInfo(context, id)
        viewModel.getTypeClient(context, id)
        initDocuments(binding)

        binding.radioCashGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.radioCashGroup.setBackgroundColor(ContextCompat.getColor(requireContext(), transperent))
            when (checkedId) {
                R.id.radio_cash -> {
                    binding.apply {
                        etNoteOrderShip.text.clear()
                        etNoteOrderShip.text.insert(0, "Оплата наличными, ")
                    }
                    viewModel.setTypeOfCash("Наличные")
                }
                R.id.radio_on_site -> {
                    binding.apply {
                        etNoteOrderShip.text.clear()
                        etNoteOrderShip.text.insert(0, "Оплата на сайте, ")
                    }
                    viewModel.setTypeOfCash("На сайте")
                }
                R.id.radio_terminal -> {
                    binding.apply {
                        etNoteOrderShip.text.clear()
                        etNoteOrderShip.text.insert(0, "Оплата через терминал, ")
                    }
                    viewModel.setTypeOfCash("Оплата через терминал")
                }
            }
        }

        binding.etTankToBack.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etTankToBack.setBackgroundColor(ContextCompat.getColor(requireContext(), transperent))
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnShipOrder.setOnClickListener {
            screenComponent.appContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (ActivityCompat.checkSelfPermission(
                    screenComponent.appContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    screenComponent.appContext(),
                    ACCESS_COARSE_LOCATION
                ) != PERMISSION_GRANTED
            ) {
                showSnack("Включите GPS")
            }

            if (binding.etTankToBack.text.toString().isEmpty() && viewModel.typeCash.value == null) {
                showSnack("Укажиете количество бутылок к возврату и тип оплаты")
                binding.radioCashGroup.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.shipmentBackground))
                binding.etTankToBack.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.shipmentBackground))
            } else if (binding.etTankToBack.text.toString().isEmpty()) {
                showSnack("Укажиете количество бутылок к возврату и тип оплаты")
                binding.etTankToBack.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.shipmentBackground))
            } else if (viewModel.typeCash.value == null) {
                showSnack("Укажиете тип оплаты")
                binding.radioCashGroup.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.shipmentBackground))
            } else if (documentsIsChecked == null && viewModel.typeClient.value == TypeClient.JURISTIC){
                showSnack("Укажиете подписаны ли документы")
                binding.cbDocNo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.shipmentBackground))
                binding.cbDocYes.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.shipmentBackground))
            } else {
                if (id != null) viewModel.setCompleteOrder2(
                    this.context,
                    id,
                    binding.etTankToBack.text.toString().toInt(),
                    binding.etNoteOrderShip.text.toString(),
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
                viewModel.setMyCoordinate("${location.latitude}-${location.longitude}")
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
                binding.apply {
                    cbDocNo.isChecked = false
                    etNoteOrderShip.text.clear()
                    cbDocNo.setBackgroundColor(ContextCompat.getColor(requireContext(), transperent))
                    cbDocYes.setBackgroundColor(ContextCompat.getColor(requireContext(), transperent))
                    etNoteOrderShip.text.insert(0, "Документы подписаны, ")
                }
                documentsIsChecked = true
            } else {
                binding.etNoteOrderShip.text.clear()
                documentsIsChecked = null
            }
        }
        binding.cbDocNo.setOnClickListener {
            if (binding.cbDocNo.isChecked) {
                binding.apply {
                    cbDocYes.isChecked = false
                    etNoteOrderShip.text.clear()
                    cbDocNo.setBackgroundColor(ContextCompat.getColor(requireContext(), transperent))
                    cbDocYes.setBackgroundColor(ContextCompat.getColor(requireContext(), transperent))
                    etNoteOrderShip.text.insert(0, "Документы не подписаны, ")
                }
                documentsIsChecked = false
            } else {
                binding.etNoteOrderShip.text.clear()
                documentsIsChecked = null
            }
        }
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