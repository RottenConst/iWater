package ru.iwater.youwater.iwaterlogistic.domain.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.domain.OrderInfo
import ru.iwater.youwater.iwaterlogistic.domain.mapdata.Location
import ru.iwater.youwater.iwaterlogistic.repository.OrderListRepository
import timber.log.Timber
import javax.inject.Inject

@OnScreen
class InfoOrderViewModel @Inject constructor(
    private val orderListRepository: OrderListRepository,
) : ViewModel() {

    /**
     * короутины дял асинхронных задач
     */
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var orderInfo: Order = Order()
    private val mOrder: MutableLiveData<Order> = MutableLiveData()

    val order: LiveData<Order>
        get() = mOrder

    /**
     * возвращает заказ по id
     **/
    fun getOrderInfo(id: Int?) {
        uiScope.launch {
            val orderInfoDetail = orderListRepository.getLoadOrderInfo(id)
//            Timber.d("info id = ${orderInfoDetail?.id}")
            orderInfo = orderListRepository.getDBOrderOnId(id)
            if (orderInfo.cash.isNotBlank() && orderInfo.cash != null) {
                orderInfo.cash = (orderInfoDetail?.cash ?: 0) as String
            } else {
                orderInfo.cash_b = (orderInfoDetail?.cash ?: 0) as String
            }
            val location = getCoordinate(orderInfo.address)
            orderInfo.location = location
            if (orderInfo.contact.isNotBlank()) {
                orderInfo.address = "${orderInfoDetail?.address} ${
                    orderInfoDetail?.contact?.split(",")
                        ?.get(0)
                }"
            } else {
                orderInfo.contact = orderInfoDetail?.contact?.split(",")?.last().toString()
                orderInfo.address = "${orderInfoDetail?.address} - ${
                    orderInfoDetail?.contact
                }"
            }
            updateOrder()
            mOrder.value = orderInfo
        }
    }

    suspend fun getCoordinate(address: String): Location {
        val mapData = orderListRepository.getCoordinates(address)
        return if (mapData != null) {
            mapData.results[0].geometry.location
        } else {
            Location(0.0, 0.0)
        }
    }

    /**
     * сохранить заказ
     **/
    fun updateOrder() {
        uiScope.launch {
            orderListRepository.updateOrder(orderInfo)
        }
    }

    /**
     * парсит в моссив телефоны клиента
     **/
    fun getPhoneNumberClient(): Array<String> {
        var contacts = mutableListOf<String>()
        if (orderInfo.contact.isNotEmpty()) {
            contacts = when {
                orderInfo.contact.contains(";") -> {
                    orderInfo.contact.split(";") as MutableList<String>
                }
                orderInfo.contact.contains(",") -> {
                    orderInfo.contact.split(",") as MutableList<String>
                }
                else -> {
                    arrayListOf(orderInfo.contact)
                }
            }
        }
        return contacts.toTypedArray()
    }

    /**
     * закрываем и отменяем корутины
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}