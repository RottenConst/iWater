package ru.iwater.youwater.iwaterlogistic.domain.vm

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.domain.Account
import ru.iwater.youwater.iwaterlogistic.domain.OpenDriverShift
import ru.iwater.youwater.iwaterlogistic.domain.WaterOrder
import ru.iwater.youwater.iwaterlogistic.domain.mapdata.Location
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.repository.OrderListRepository
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.current.CardOrderActivity
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.start.LoadDriveFragment
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
import timber.log.Timber
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

    private var account: Account = accountRepository.getAccount()

    /**
     * при инициализации устанавливаем сессию
     */
    init {
        account = accountRepository.getAccount()
    }

    /**
     * короутины дял асинхронных задач
     */
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _status: MutableLiveData<OrderLoadStatus> = MutableLiveData()
    val status: LiveData<OrderLoadStatus>
        get() = _status

    private val _listWaterOrder: MutableLiveData<List<WaterOrder>> = MutableLiveData()
    val listOrder: LiveData<List<WaterOrder>>
        get() = _listWaterOrder

    private val _dbListWaterOrder: MutableLiveData<List<WaterOrder>> = MutableLiveData()
    val dbListOrder: LiveData<List<WaterOrder>>
        get() = _dbListWaterOrder


    fun getWorkShift(context: Context?, activity: FragmentActivity?) {
        val unix = System.currentTimeMillis() / 1000L
        uiScope.launch {
            val driverShift = OpenDriverShift(
                account.id,
                account.login,
                account.company,
                unix.toString(),
                UtilsMethods.getTodayDateString(),
                account.session
            )
            if (orderListRepository.getOpenDriverShift(driverShift)) {
                val fragment = LoadDriveFragment.newInstance(true)
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.container, fragment)
                    ?.commit()
            } else {
                UtilsMethods.showToast(context, "За сегодня у вас уже закрыта смена ")
            }
        }
    }

    fun getLoadCurrent() {
        uiScope.launch {
            _status.value = OrderLoadStatus.LOADING
            val listOrderNet = orderListRepository.getLoadOrder(account.session)
            val notCurrentId = orderListRepository.getLoadNotCurrentOrder(account.session)
            if (listOrderNet.isNullOrEmpty()) {
                _status.value = OrderLoadStatus.ERROR
            } else {
                saveOrder(listOrderNet, notCurrentId)
                _listWaterOrder.value = listOrderNet
                _status.value = OrderLoadStatus.DONE
            }
        }
    }

    private suspend fun saveOrder(ordersNET: List<WaterOrder>, notCurrentId: List<Int>) {
        val ordersDb = orderListRepository.getDBOrders()
        if (ordersDb.isEmpty() && ordersNET.isNotEmpty()) {
            orderListRepository.saveOrders(ordersNET)
        } else {
            val iterator = ordersDb.iterator()
            while (iterator.hasNext()) {
                val order = iterator.next()
                for (id in notCurrentId) {
                    if (order.order_id == id) orderListRepository.deleteOrder(order)
                }
            }
            updateOrder(ordersNET, ordersDb)
        }
    }

    private suspend fun updateOrder(ordersNET: List<WaterOrder>, ordersDB: List<WaterOrder>) {
        if (ordersDB.size < ordersNET.size) {
            val iterator = ordersNET.iterator()
            while (iterator.hasNext()) {
                val order = iterator.next()
                var count = 0
                for (orderSaved in ordersDB) {
                    if (order.order_id == orderSaved.order_id) {
                        count += 1
                        Timber.d("1test!!!!! $count ${order.order.size} ${orderSaved.order.size}")
                        if (orderSaved.coords.isNullOrEmpty()) {
                            orderListRepository.updateOrder(order)
                        } else {
                            orderListRepository.updateOrder(order)
                            orderListRepository.updateDBLocation(order, orderSaved.coords)
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

    private suspend fun updateOrder(waterOrder: WaterOrder) {
        orderListRepository.updateOrder(waterOrder)
    }

    private suspend fun getCoordinate(address: String): Location {
        val mapData = orderListRepository.getCoordinates(address)
        return if (mapData != null && mapData.status != "ZERO_RESULTS") {
            mapData.results[0].geometry.location
        } else {
            Location(0.0, 0.0)
        }
    }

    fun loadCoordinate(orders: List<WaterOrder>) {
        uiScope.launch {
            orders.forEach { order ->
//                if (order.coords.isNullOrEmpty()) {
//                    val location = getCoordinate(order.address)
                    Timber.d("location = ${order.coords}")
//                    order.coords = "${location.lat},${location.lng}"
//                    updateOrder(order)
//                }
            }
            _status.value = OrderLoadStatus.DONE
        }
    }

    fun getLoadOrderFromDB() {
        uiScope.launch {
            _dbListWaterOrder.value = orderListRepository.getDBOrders()
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
     * закрываем и отменяем корутины
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}