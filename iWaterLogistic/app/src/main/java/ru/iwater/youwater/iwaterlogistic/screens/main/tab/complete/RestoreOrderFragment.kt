package ru.iwater.youwater.iwaterlogistic.screens.main.tab.complete

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
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
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.databinding.RestoreOrderFragmentBinding
import ru.iwater.youwater.iwaterlogistic.domain.vm.RestoreViewModel
import ru.iwater.youwater.iwaterlogistic.domain.vm.TypeClient
import ru.iwater.youwater.iwaterlogistic.screens.main.MainActivity
import timber.log.Timber
import javax.inject.Inject

private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34

class RestoreOrderFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: RestoreViewModel by viewModels { factory }

    private val screenComponent = App().buildScreenComponent()

    private var documentsIsChecked: Boolean? = null
//    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(screenComponent.appContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<RestoreOrderFragmentBinding>(inflater, R.layout.restore_order_fragment, container, false)
        binding.lifecycleOwner = this
        binding.restoreVM = viewModel

        val arg = arguments
        val id = arg?.getInt("id")
        viewModel.getOrderInfo(context, id)
        viewModel.getTypeClient(context, id)
//        initDocuments(binding)

        binding.radioCashGroupRestore.setOnCheckedChangeListener { _, checkedId ->
            binding.radioCashGroupRestore.setBackgroundColor(
                ContextCompat.getColor(requireContext(),
                    R.color.transperent
                ))
            when (checkedId) {
                R.id.radio_cash_restore -> {
                    binding.apply {
                        etNoteOrderRestore.text.clear()
                        etNoteOrderRestore.text.insert(0, "Оплата наличными, ")
                    }
                    viewModel.setTypeOfCash("Наличные")
                }
                R.id.radio_on_site_restore -> {
                    binding.apply {
                        etNoteOrderRestore.text.clear()
                        etNoteOrderRestore.text.insert(0, "Оплата на сайте, ")
                    }
                    viewModel.setTypeOfCash("На сайте")
                }
                R.id.radio_terminal_restore -> {
                    binding.apply {
                        etNoteOrderRestore.text.clear()
                        etNoteOrderRestore.text.insert(0, "Оплата через терминал, ")
                    }
                    viewModel.setTypeOfCash("Оплата через терминал")
                }
            }
        }

        binding.etTankRestore.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etTankRestore.setBackgroundColor(
                    ContextCompat.getColor(requireContext(),
                        R.color.transperent
                    ))
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnRestoreOrder.setOnClickListener {
            screenComponent.appContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (ActivityCompat.checkSelfPermission(
                    screenComponent.appContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    screenComponent.appContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
//                showSnack("Включите GPS")
            }

//            if (binding.etTankRestore.text.toString().isEmpty() && viewModel.typeCash.value == null) {
//                showSnack("Укажиете количество бутылок к возврату и тип оплаты")
//                binding.radioCashGroupRestore.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.shipmentBackground))
//                binding.etTankRestore.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.shipmentBackground))
//            } else if (binding.etTankRestore.text.toString().isEmpty()) {
//                showSnack("Укажиете количество бутылок к возврату и тип оплаты")
//                binding.etTankRestore.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.shipmentBackground))
//            } else if (viewModel.typeCash.value == null) {
//                showSnack("Укажиете тип оплаты")
//                binding.radioCashGroupRestore.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.shipmentBackground))
//            } else if (documentsIsChecked == null && viewModel.typeClient.value == TypeClient.JURISTIC){
//                showSnack("Укажиете подписаны ли документы")
//                binding.cbDocNoRestore.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.shipmentBackground))
//                binding.cbDocYesRestore.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.shipmentBackground))
//            } else {
//                if (id != null) viewModel.order.observe(this.viewLifecycleOwner, {
//                    Timber.d("SHIP ORDER = ${it.id}, ${it.client_id}, cash_b = ${it.cash_b} ; cash = ${it.cash}, ${binding.etTankRestore.text.toString()}, ${it.address} ${binding.etNoteOrderRestore.text.toString()}")
//                    if (it.cash.isEmpty() || it.cash.isBlank()) {
//                        viewModel.setEmptyBottle(this.context, it.id, it.client_id, it.cash_b,
//                            binding.etTankRestore.text.toString().toInt(),
//                            it.address,
//                            binding.etNoteOrderRestore.text.toString(), it.type)
//                    } else {
//                        viewModel.setEmptyBottle(this.context, it.id, it.client_id, it.cash,
//                            binding.etTankRestore.text.toString().toInt(),
//                            it.address,
//                            binding.etNoteOrderRestore.text.toString(), it.type)
//                    }
//                })
//            }
        }

        binding.btnNoRestoreOrder.setOnClickListener {
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
        (activity as CardCompleteActivity)
            ?.setActionBarTitle("Восстановление заказа")
    }

    private fun checkPermissions() =
        ActivityCompat.checkSelfPermission(screenComponent.appContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun startLocationPermissionRequest() {
        this.activity?.parent?.let {
            ActivityCompat.requestPermissions(
                it, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
//        fusedLocationClient.lastLocation.addOnCompleteListener { taskLocation ->
//            if (taskLocation.isSuccessful && taskLocation.result !== null) {
//                val location = taskLocation.result
//                viewModel.setMyCoordinate("${location.latitude}-${location.longitude}")
//            } else {
//                Timber.w(taskLocation.exception, "getLastLocation:exception")
//            }
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun requestPermissions() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!.parent,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            )) {
            // Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn't check the "Don't ask again" checkbox.
//            Timber.i("Displaying permission rationale to provide additional context.")
//            showSnackBar(R.string.permission_rationale, android.R.string.ok) {
                // Request permission
//                startLocationPermissionRequest()
            }

//        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
//            Timber.i("Requesting permission")
//            startLocationPermissionRequest()
//        }
//    }
    /**
     * для юр лиц устанавливается были подписаны документы или нет
     **/
//    private fun initDocuments(binding: RestoreOrderFragmentBinding) {
//        binding.cbDocYesRestore.setOnClickListener {
//            if (binding.cbDocYesRestore.isChecked) {
//                binding.apply {
//                    cbDocNoRestore.isChecked = false
//                    etNoteOrderRestore.text.clear()
//                    cbDocNoRestore.setBackgroundColor(
//                        ContextCompat.getColor(requireContext(),
//                            R.color.transperent
//                        ))
//                    cbDocYesRestore.setBackgroundColor(
//                        ContextCompat.getColor(requireContext(),
//                            R.color.transperent
//                        ))
//                    etNoteOrderRestore.text.insert(0, "Документы подписаны, ")
//                }
//                documentsIsChecked = true
//            } else {
//                binding.etNoteOrderRestore.text.clear()
//                documentsIsChecked = null
//            }
//        }
//        binding.cbDocNoRestore.setOnClickListener {
//            if (binding.cbDocNoRestore.isChecked) {
//                binding.apply {
//                    cbDocYesRestore.isChecked = false
//                    etNoteOrderRestore.text.clear()
//                    cbDocNoRestore.setBackgroundColor(
//                        ContextCompat.getColor(requireContext(),
//                            R.color.transperent
//                        ))
//                    cbDocYesRestore.setBackgroundColor(
//                        ContextCompat.getColor(requireContext(),
//                            R.color.transperent
//                        ))
//                    etNoteOrderRestore.text.insert(0, "Документы не подписаны, ")
//                }
//                documentsIsChecked = false
//            } else {
//                binding.etNoteOrderRestore.text.clear()
////                documentsIsChecked = null
////            }
//        }
//    }

//    @SuppressLint("UseRequireInsteadOfGet")
//    private fun showSnackBar(
//        snackStrId: Int,
//        actionStrId: Int = 0,
//        listener: View.OnClickListener? = null
//    ) {
//        val snackBar = Snackbar.make(
//            this.context!!, view!!.findViewById(android.R.id.content), getString(snackStrId),
//            BaseTransientBottomBar.LENGTH_INDEFINITE
//        )
//        if (actionStrId != 0 && listener != null) {
//            snackBar.setAction(getString(actionStrId), listener)
//        }
//        snackBar.show()
//    }
//
//    private fun showSnack(message: String) {
//        this.view?.let { Snackbar.make(it, message, Snackbar.LENGTH_LONG).show() }
//    }
//
//    companion object {
//        fun newInstance(): RestoreOrderFragment {
//            return RestoreOrderFragment()
//        }
//    }
//}