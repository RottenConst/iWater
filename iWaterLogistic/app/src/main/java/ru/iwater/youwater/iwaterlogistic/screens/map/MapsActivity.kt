package ru.iwater.youwater.iwaterlogistic.screens.map

//import com.google.maps.android.ui.IconGenerator
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ui.IconGenerator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity
import ru.iwater.youwater.iwaterlogistic.databinding.ActivityMapsBinding
import ru.iwater.youwater.iwaterlogistic.domain.WaterOrder
import ru.iwater.youwater.iwaterlogistic.domain.vm.OrderListViewModel
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
import timber.log.Timber
import javax.inject.Inject

class MapsActivity : BaseActivity(), OnMapReadyCallback {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: OrderListViewModel by viewModels { factory }
    private val screenComponent = App().buildScreenComponent()

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var myPoint: LatLng
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps)
        screenComponent.inject(this)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.checkOrder.bottomSheet)
        bottomSheetBehavior.setPeekHeight(0, true)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        val mapController = binding.map.controller
        mapController.setZoom(11.5)
        val startPoint = GeoPoint(59.939098, 30.315868)
        mapController.setCenter(startPoint)
        // управление жестами
        val rotationGestureOverlay = RotationGestureOverlay(binding.map)
        rotationGestureOverlay.isEnabled
        binding.map.setMultiTouchControls(true)
        binding.map.overlays.add(rotationGestureOverlay)
        //определение геолокации телефон
        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this.baseContext), binding.map)
        locationOverlay.enableMyLocation()
        binding.map.overlays.add(locationOverlay)
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        observeDBOrder()

//         Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        binding.map.onResume() //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
//        binding.map.overlays.clear()
        binding.map.onPause() //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

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
        Timber.d("last location = $lastLocation")

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
            val info = marker.title?.split(";") ?: emptyList()
            Timber.i("INFO SIZE MARKER${info.size} info.size")
            "#${info[1]}".also { binding.checkOrder.tvNumMarker.text = it }
            "#${info[0]} ${info[2]}".also { binding.checkOrder.tvNameMarker.text = it }
            if (info.size == 5) {
                info[3].also { binding.checkOrder.tvTimeOrderMarker.text = it }
            } else {
                "${info[3]} ${info[4]}".also { binding.checkOrder.tvTimeOrderMarker.text = it }
            }
            binding.checkOrder.tvProductNameOrder.text = ""
            val productAll = info.last().split("+")
            for (product in productAll) {
                binding.checkOrder.tvProductNameOrder.append("$product \n")
            }
            binding.checkOrder.btToInfoOrder.setOnClickListener {
                viewModel.getAboutOrder(this, info[0].toInt())
            }
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            true
        }
    }

    private fun observeDBOrder() {
        viewModel.getLoadOrderFromDB()
        viewModel.dbListOrder.observe(this) {
            getMarker(it)
        }
    }


    private fun getMarker(orders: List<WaterOrder>) {
        for (order in orders) {
            if (!order.coords.isNullOrBlank()) {
                Timber.i("ORDER ${order.coords?.split(",")?.get(0)} ${order.coords?.split(",")?.get(1)}")
                val testMarker = Marker(binding.map)
                val lat = order.coords?.split(",")?.get(0)?.toDouble() ?: 0.0
                val lng = order.coords?.split(",")?.get(1)?.toDouble() ?: 0.0
                testMarker.position = GeoPoint(lat, lng)
                testMarker.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
//                val point = LatLng(lat, lng)
                val timeInitial = order.time.split("-").first()
                val timeEnd = order.time.split("-").last()
                val hour = order.time.split("-").last()
                Timber.i("Time - $hour")
                val colorInitial = timeInitial.split(":")[0].toInt()
                val colorEnd = timeEnd.split(":")[0].toInt()
//                val color = hour.split(":")[0].toInt()
                val orderProduct: String by lazy {
                    if (order.type == "1") {
                        order.notice
                    } else {
                        UtilsMethods.productToStringMap(order.order)
                    }
                }

                if ((colorInitial in 9..13) && (colorEnd in 13..22)) {
                    testMarker.icon = getCustomIcon(order.num, R.drawable.marker_green)?.toDrawable(this.resources)
                    testMarker.title = "${order.order_id};${order.num};${order.address};${order.time};${orderProduct}"
                    binding.map.overlays.add(testMarker)
                    binding.map.invalidate()
                } else {
                    if (colorInitial in 16..18) {
                        testMarker.icon = getCustomIcon(order.num, R.drawable.marker_violet)?.toDrawable(this.resources)
                        testMarker.title = "${order.order_id};${order.num};${order.address};${order.time};${orderProduct}"
                        binding.map.overlays.add(testMarker)
                        binding.map.invalidate()
                    } else {
                        if (colorInitial >= 19){
                            testMarker.icon = getCustomIcon(order.num, R.drawable.marker_blue)?.toDrawable(this.resources)
                            testMarker.title = "${order.order_id};${order.num};${order.address};${order.time};${orderProduct}"
                            binding.map.overlays.add(testMarker)
                            binding.map.invalidate()
                        } else {
                            testMarker.icon = getCustomIcon(order.num, R.drawable.marker_grey)?.toDrawable(this.resources)
                            testMarker.title = "${order.order_id};${order.num};${order.address};${order.time};${orderProduct}"
                            binding.map.overlays.add(testMarker)
                            binding.map.invalidate()
                        }
                    }
                }

//                when {
//                    color <= 19 -> {
//                        testMarker.icon = getCustomIcon(order.num, R.drawable.marker_blue)?.toDrawable(this.resources)
//                        testMarker.title = "${order.order_id};${order.num};${order.address};${order.time};${orderProduct}"
//                        binding.map.overlays.add(testMarker)
//                        binding.map.invalidate()
//                    val marker = MarkerOptions().position(point).icon(
//                        BitmapDescriptorFactory.fromBitmap(
//                            getCustomIcon(order.num, R.drawable.marker_red)!!
//                        )
//                    )
//                        .title("${order.order_id};${order.num};${order.address};${order.time};${orderProduct}")
//                    Timber.d("orderproduct $orderProduct")
//                    mMap.addMarker(marker)
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 10.0f))
//                    }

//                    color in 12..13 -> {
//                        testMarker.icon = getCustomIcon(order.num, R.drawable.marker_yellow)?.toDrawable(this.resources)
//                        testMarker.title = "${order.order_id};${order.num};${order.address};${order.time};${orderProduct}"
//                        binding.map.overlays.add(testMarker)
//                        binding.map.invalidate()
//                    val marker = MarkerOptions().position(point).icon(
//                        BitmapDescriptorFactory.fromBitmap(
//                            getCustomIcon(order.num, R.drawable.marker_yellow)!!
//                        )
//                    )
//                        .title("${order.order_id};${order.num};${order.address};${order.time};${orderProduct}")
//                    mMap.addMarker(marker)
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 10.0f))
//                    }

//                    color in 9..16 -> {
//                        testMarker.icon = getCustomIcon(order.num, R.drawable.marker_green)?.toDrawable(this.resources)
//                        testMarker.title = "${order.order_id};${order.num};${order.address};${order.time};${orderProduct}"
//                        binding.map.overlays.add(testMarker)
//                        binding.map.invalidate()
//                    val marker = MarkerOptions().position(point).icon(
//                        BitmapDescriptorFactory.fromBitmap(
//                            getCustomIcon(order.num, R.drawable.marker_green)!!
//                        )
//                    )
//                        .title("${order.order_id};${order.num};${order.address};${order.time};${orderProduct}")
//                    mMap.addMarker(marker)
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 10.0f))
//                    }

//                    color in 17..22 -> {
//                        testMarker.icon = getCustomIcon(order.num, R.drawable.marker_violet)?.toDrawable(this.resources)
//                        testMarker.title = "${order.order_id};${order.num};${order.address};${order.time};${orderProduct}"
//                        binding.map.overlays.add(testMarker)
//                        binding.map.invalidate()
//                    val marker = MarkerOptions().position(point).icon(
//                        BitmapDescriptorFactory.fromBitmap(
//                            getCustomIcon(order.num, R.drawable.marker_violet)!!
//                        )
//                    )
//                        .title("${order.order_id};${order.num};${order.address};${order.time};${orderProduct}")
//                    mMap.addMarker(marker)
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 10.0f))
//                    }

//                    color in 20..21 -> {
//                        testMarker.icon = getCustomIcon(order.num, R.drawable.marker_blue)?.toDrawable(this.resources)
//                        testMarker.title = "${order.order_id};${order.num};${order.address};${order.time};${orderProduct}"
//                        binding.map.overlays.add(testMarker)
//                        binding.map.invalidate()
//                    val marker = MarkerOptions().position(point).icon(
//                        BitmapDescriptorFactory.fromBitmap(
//                            getCustomIcon(order.num, R.drawable.marker_blue)!!
//                        )
//                    )
//                        .title("${order.order_id};${order.num};${order.address};${order.time};${orderProduct}")
//                    mMap.addMarker(marker)
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 10.0f))
//                    }

//                    else -> {
//                        testMarker.icon = getCustomIcon(order.num, R.drawable.marker_blue)?.toDrawable(this.resources)
//                        testMarker.title = "${order.order_id};${order.num};${order.address};${order.time};${orderProduct}"
//                        binding.map.overlays.add(testMarker)
//                        binding.map.invalidate()
////                    val marker = MarkerOptions().position(point).icon(
////                        BitmapDescriptorFactory.fromBitmap(
//////                            getCustomIcon(order.num, R.drawable.marker_grey)!!
////                        )
////                    )
////                        .title("${order.order_id};${order.num};${order.address};${order.time};${orderProduct}")
////                    mMap.addMarker(marker)
////                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 10.0f))
//                    }
//                }
            }
        }
    }

    //    Кастомные иконки с номерами точек
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getCustomIcon(count: String, icon: Int): Bitmap? {
        val generator = IconGenerator(this)
        generator.setTextAppearance(this, R.style.TextMarker)
        generator.setBackground(ContextCompat.getDrawable(this.baseContext, icon))

        return generator.makeIcon(count)
    }
}