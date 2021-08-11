package ru.iwater.youwater.iwaterlogistic.domain.vm

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.domain.Account
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.domain.mapdata.Location
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.repository.OrderListRepository
import ru.iwater.youwater.iwaterlogistic.screens.main.MainActivity
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.current.CardOrderActivity
import ru.iwater.youwater.iwaterlogistic.util.HelpLoadingProgress
import ru.iwater.youwater.iwaterlogistic.util.HelpState
import timber.log.Timber
import java.io.IOException
import java.util.*
import javax.inject.Inject


enum class OrderLoadStatus { LOADING, DONE, ERROR }

/**
 * viewModel класс для действий с заказами
 */
@OnScreen
class OrderListViewModel @Inject constructor(
    private val orderListRepository: OrderListRepository,
    accountRepository: AccountRepository
) : ViewModel() {

    //    val openDriverMonitor = MonitorDriverOpening()
    private var account: Account = accountRepository.getAccount()

    /**
     * при инициализации устанавливаем сессию
     */
    init {
        account = accountRepository.getAccount()
//        openDriverMonitor.setMonitorDriverOpening(accountRepository.getAccount().id)
    }

    /**
     * короутины дял асинхронных задач
     */
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private lateinit var orderFromDb: List<Order>

    private val _status: MutableLiveData<OrderLoadStatus> = MutableLiveData()
    val status: LiveData<OrderLoadStatus>
        get() = _status

    private val mCoordinate: MutableLiveData<String> = MutableLiveData()

    private val _listOrder: MutableLiveData<List<Order>> = MutableLiveData()
    val listOrder: LiveData<List<Order>>
        get() = _listOrder

    private val _dbListOrder: MutableLiveData<List<Order>> = MutableLiveData()
    val dbListOrder: LiveData<List<Order>>
        get() = _dbListOrder


    val coordinate: LiveData<String>
        get() = mCoordinate

    /**
     * загружаем инфу о заказах
     * и обновляем liveData
     */
//    fun getLoadOrder() {
//        uiScope.launch {
//            orderListRepository.getLoadOrderList()
//            orderListRepository.checkDbOrder()
//            val orders = orderListRepository.getOrders()
//            orderListRepository.saveOrders(orders)
//            mListOrder.value = orderListRepository.getDBOrders()
//        }
//    }

    fun openDriverDay(context: Context) {
        uiScope.launch {
//           val answer = openDriverMonitor.driverOpeningDay()
//           Timber.d(answer)
            val intent = Intent(context, MainActivity::class.java)
            HelpLoadingProgress.setLoginProgress(context, HelpState.IS_WORK_START, false)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(context, intent, null)
        }
    }

    fun getLoadCurrent2() {
        uiScope.launch {
            _status.value = OrderLoadStatus.LOADING
            val listOrderNet = orderListRepository.getLoadCurrentOrder2(account.session)
            if (listOrderNet.isNullOrEmpty()) {
                _status.value = OrderLoadStatus.ERROR
            } else {
                saveOrder(listOrderNet)
                _listOrder.value = listOrderNet
                _status.value = OrderLoadStatus.DONE
            }
        }
    }

    private suspend fun saveOrder(ordersNET: List<Order>) {
        val ordersDb = orderListRepository.getDBOrders()
        if (ordersDb.isNullOrEmpty()) {
            orderListRepository.saveOrders(ordersNET)
        } else {
            updateOrder(ordersNET, ordersDb)
        }
    }

    private suspend fun updateOrder(ordersNET: List<Order>, ordersDB: List<Order>) {
        if (ordersDB.size < ordersNET.size) {
            val iterator = ordersNET.iterator()
            while (iterator.hasNext()) {
                val order = iterator.next()
                var count = 0
                for (orderSaved in ordersDB) {
                    if (order.id == orderSaved.id) {
                        count += 1
                        Timber.d("1test!!!!! $count ${order.products.size} ${orderSaved.products.size}")
                        if (order.products.size != orderSaved.products.size) {
                            orderListRepository.updateOrder(order)
                        }
                    }
                    if (count == 0) {
                        orderListRepository.saveOrder(order)
                    }
                }
            }
        }
        for (order in ordersNET) {
            orderListRepository.getUpdateDBNum(order)
        }
    }

    fun getLoadCurrent() {
        Timber.d(account.session)
        uiScope.launch {
            Timber.d("${account.id}, weqweqweqweqeqweqweqwe")
            orderListRepository.getLoadCurrentOrders(account.session)
            val orders = orderListRepository.ordersList
            getLoadOrderCurrentOrderFromBd()
            val dbOrder = orderFromDb
            Timber.d("orders ${orderFromDb.size}")
            if (dbOrder.isNullOrEmpty()) {
                Timber.d("test2!!!!")
                orderListRepository.saveOrders(orders)
                _listOrder.value = orders
            } else {
                Timber.d("test!!!!!")
                if (dbOrder.size < orders.size) {
                    val iterator = orders.iterator()
                    while (iterator.hasNext()) {
                        val order = iterator.next()
                        var count = 0
                        for (orderSaved in dbOrder) {
                            if (order.id == orderSaved.id) {
                                count += 1
                                Timber.d("1test!!!!! $count ${order.products.size} ${orderSaved.products.size}")
                                if (order.products.size != orderSaved.products.size) {
                                    orderListRepository.updateOrder(order)
                                }
                            }
                            if (count == 0) {
                                orderListRepository.saveOrder(order)
                            }
                        }
                    }
                }
                for (order in orders) {
                    orderListRepository.getUpdateDBNum(order)
                }
                _listOrder.value = orders
            }
        }
    }

    suspend fun updateOrder(order: Order) {
        orderListRepository.updateOrder(order)
    }

    private suspend fun getCoordinate(address: String): Location {
        val mapData = orderListRepository.getCoordinates(address)
        return if (mapData != null && mapData.status != "ZERO_RESULTS") {
            mapData.results[0].geometry.location
        } else {
            Location(0.0, 0.0)
        }
    }

    fun getLocation(order: Order) {
        uiScope.launch {
            val location = getCoordinate(order.address)
            if (location.lat != 0.0 && location.lng != 0.0) {
                order.location = location
                updateOrder(order)
            }
        }
    }

    fun getLoadOrderFromDB() {
        uiScope.launch {
            _dbListOrder.value = orderListRepository.getDBOrders()
        }
    }

    /**
     * сохраняет заказ в бд и запускает экран с информацией о заказе с возможностью отгрузки
     **/
    fun getAboutOrder(context: Context?, id: Int) {
        val intent = Intent(context, CardOrderActivity::class.java)
        intent.putExtra("id", id)
        if (context != null) {
            CardOrderActivity.start(context, intent)
        }
    }

    /**
     * Получить координаты для заказа
     */
    @SuppressLint("TimberArgCount")
    fun getCoordinatesOnAddressOrder(order: Order, context: Context) {
        val locationAddress = order.address
        uiScope.launch {
            val geoCoder = Geocoder(context, Locale.getDefault())
            try {
                val addressList = geoCoder.getFromLocationName(locationAddress, 1)
                if (addressList != null && addressList.size > 0) {
                    val address = addressList[0]
                    val coordinate = "${address.latitude}-${address.longitude}"
//                    order.coordinates = coordinate.split("-")
//                    orderListRepository.saveOrder(order)
                    Timber.d("coordinate = $")
                }
            } catch (e: IOException) {
                Timber.e(e, "Unable to connect to Geocoder")
            }
        }
    }

    /**
     * Получить заказы за выбранную дату из бд
     */
    private suspend fun getLoadOrderCurrentOrderFromBd() {
        orderFromDb = orderListRepository.getDBOrders()
    }

    /**
     * закрываем и отменяем корутины
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}