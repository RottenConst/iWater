package ru.iwater.youwater.iwaterlogistic.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.ui.IconGenerator
import kotlinx.android.synthetic.main.info_order_map.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.domain.vm.OrderListViewModel
import ru.iwater.youwater.iwaterlogistic.util.ProductConverter
import timber.log.Timber
import javax.inject.Inject

class MapsActivity : BaseActivity(), OnMapReadyCallback {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: OrderListViewModel by viewModels {factory}
    private val screenComponent = App().buildScreenComponent()

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var myPoint: LatLng
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var coordinate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        screenComponent.inject(this)
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        bottomSheetBehavior.setPeekHeight(0, true)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


//         Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

//        observeCoordinate()
//        observeOrders(mMap)

        observeDBOrder()


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastLocation = location
                myPoint = LatLng(location.latitude, location.longitude)
            }
        }

        infoOrderMarker()

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //
            }
        })
    }

    @SuppressLint("PotentialBehaviorOverride")
    fun infoOrderMarker() {
        mMap.setOnMarkerClickListener { marker ->
            val info = marker.title.split(";")
            tv_num_marker.text = "#${info[1]}"
            tv_name_marker.text = "#${info[0]} ${info[2]}"
            tv_time_order_marker.text = info[3]
//            tv_product_name_order.text = info[4]
            bt_to_info_order.setOnClickListener {
                viewModel.getAboutOrder(this, info[0].toInt())
            }
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            true
        }
    }


    private fun observeCoordinate() {
        viewModel.coordinate.observe(this, {
            coordinate = it
        })
    }

    private fun observeOrders(mMap: GoogleMap) {
//        viewModel.getLoadOrder()
        viewModel.listOrder.observe(this, {
            for (order in it) {
                viewModel.getCoordinatesOnAddressOrder(order, applicationContext)
            }
            observeDBOrder()
        })
    }

    private fun observeDBOrder(){
        viewModel.getLoadOrderFromDB()
        viewModel.dbListOrder.observe(this, {
            getMarker(it)
        })
    }


    private fun getMarker(orders: List<Order>) {
        for (order in orders) {
            if (order.location?.lat == 0.0 && order.location?.lng == 0.0) {
                viewModel.getLocation(order)
                viewModel.getLoadOrderFromDB()
                Timber.d("find ${order.location?.lat}, ${order.location?.lng}")
            }
            val point = LatLng(order.location?.lat!!, order.location?.lng!!)
            val hour = order.time.split("-")[1]
            val color = hour.split(":")[0].toInt()
            when {
                color < 12 -> {
                    val marker = MarkerOptions().position(point).icon(
                        BitmapDescriptorFactory.fromBitmap(
                            getCustomIcon(order.num, R.drawable.marker_red)
                        )
                    ).title("${order.id};${order.num};${order.address};")
                    mMap.addMarker(marker)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 10.0f))
                }
                color in 12..13 -> {
                    val marker = MarkerOptions().position(point).icon(
                        BitmapDescriptorFactory.fromBitmap(
                            getCustomIcon(order.num, R.drawable.marker_yellow)
                        )
                    ).title("${order.id};${order.num};${order.address};${order.products}")
                    mMap.addMarker(marker)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 10.0f))
                }
                color in 14..16 -> {
                    val marker = MarkerOptions().position(point).icon(
                        BitmapDescriptorFactory.fromBitmap(
                            getCustomIcon(order. num, R.drawable.marker_green)
                        )
                    ).title("${order.id};${order.num};${order.address};")
                    mMap.addMarker(marker)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 10.0f))
                }
                color in 17..19 -> {
                    val marker = MarkerOptions().position(point).icon(
                        BitmapDescriptorFactory.fromBitmap(
                            getCustomIcon(order.num, R.drawable.marker_violet)
                        )
                    ).title("${order.id};${order.num};${order.address};${ProductConverter().someObjectListToString(order.products)}")
                    mMap.addMarker(marker)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 10.0f))
                }
                color in 20..21 -> {
                    val marker = MarkerOptions().position(point).icon(
                        BitmapDescriptorFactory.fromBitmap(
                            getCustomIcon(order.num, R.drawable.marker_blue)
                        )
                    ).title("${order.id};${order.num};${order.address};")
                    mMap.addMarker(marker)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 10.0f))
                }
                else -> {
                    val marker = MarkerOptions().position(point).icon(
                        BitmapDescriptorFactory.fromBitmap(
                            getCustomIcon(order.num, R.drawable.marker_grey)
                        )
                    ).title("${order.id};${order.num};${order.address};")
                    mMap.addMarker(marker)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 10.0f))
                }
            }
        }
    }

//    Кастомные иконки с номерами точек
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getCustomIcon(count: Int, icon: Int): Bitmap? {
        val generator = IconGenerator(this)
        generator.setTextAppearance(this, R.style.TextMarker)
        generator.setBackground(this.resources.getDrawable(icon))
        return generator.makeIcon(count.toString())
    }
}