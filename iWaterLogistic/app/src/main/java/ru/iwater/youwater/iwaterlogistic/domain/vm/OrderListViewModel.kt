package ru.iwater.youwater.iwaterlogistic.domain.vm

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.repository.OrderListRepository
import ru.iwater.youwater.iwaterlogistic.screens.cardOrder.CardOrderActivity
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * viewModel класс для действий с заказами
 */
@OnScreen
class OrderListViewModel @Inject constructor(
    private val orderListRepository: OrderListRepository,
    accountRepository: AccountRepository
        ) : ViewModel() {

    /**
     * при инициализации устанавливаем сессию
     */
    init {
        orderListRepository.driverWayBill.setProperty(accountRepository.getAccount().session)
    }

    /**
     * короутины дял асинхронных задач
     */
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val mCoordinate: MutableLiveData<String> = MutableLiveData()

    private val mListOrder: MutableLiveData<List<Order>> = MutableLiveData()
    private val mDbListOrder: MutableLiveData<List<Order>> = MutableLiveData()

    val listOrder: LiveData<List<Order>>
        get() = mListOrder

    val dbListOrder: LiveData<List<Order>>
        get() = mDbListOrder

    val coordinate: LiveData<String>
        get() = mCoordinate

    /**
     * загружаем инфу о заказах
     * и обновляем liveData
     */
    fun getLoadOrder() {
        uiScope.launch {
            orderListRepository.getLoadOrderList()
            mListOrder.value = orderListRepository.getOrders()
            listOrder.value?.let { orderListRepository.saveOrders(it) }
        }
    }

    fun getLoadOrderWithFactAddress() {
        uiScope.launch {
            orderListRepository.getLoadOrderList()
            mListOrder.value = orderListRepository.getOrders()
        }
    }

    /**
     * сохраняет заказ в бд и запускает экран с информацией о заказе с возможностью отгрузки
     **/
    fun getAboutOrder(context: Context, order: Order) {
        val intent = Intent(context, CardOrderActivity::class.java)
        intent.putExtra("id", order.id)
        uiScope.launch {
            orderListRepository.saveOrder(order)
        }
        CardOrderActivity.start(context, intent)
    }

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
                    order.coordinates = coordinate.split("-")
                    orderListRepository.saveOrder(order)
                    Timber.d("coordinate = $")
                }
            } catch (e: IOException) {
                Timber.e(e, "Unable to connect to Geocoder")
            }
        }
    }

    fun getLoadOrderCurrentOrderFromBd() {
        val currentDate = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val timeComplete = formatter.format(currentDate.time)
        uiScope.launch {
            mDbListOrder.value = orderListRepository.getDBLoadCurrentOrder(timeComplete)
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