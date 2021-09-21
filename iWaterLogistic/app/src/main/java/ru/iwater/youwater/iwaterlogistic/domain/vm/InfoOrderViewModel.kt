package ru.iwater.youwater.iwaterlogistic.domain.vm

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.domain.mapdata.Location
import ru.iwater.youwater.iwaterlogistic.repository.OrderListRepository
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
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

    private val _order: MutableLiveData<Order> = MutableLiveData()
    val order: LiveData<Order>
        get() = _order


    /**
     * возвращает заказ по id
     **/
    fun getOrderInfo2(context: Context?, id: Int) {
        uiScope.launch {
            try {
                val orderDetail = orderListRepository.getDBOrderOnId(id)
                val orderInfoDetail = orderListRepository.getLoadOrderInfo(id)
                if (orderInfoDetail != null) {
                    if (orderDetail.cash.isNotEmpty()) {
                        orderDetail.cash = orderInfoDetail.cash
                    } else {
                        orderDetail.cash_b = orderInfoDetail.cash
                    }
                    orderDetail.location = getCoordinate(orderDetail.address)
                    if (orderDetail.contact.isNotBlank()) {
                        val room = orderInfoDetail.contact.split(",").size
                        orderDetail.address = "${orderInfoDetail.address} ${
                            orderInfoDetail.contact.split(",").get(room - 2)
                        }"
                    } else {
                        orderDetail.contact = orderInfoDetail.contact.split(",").last().toString()
                        orderDetail.address = "${orderInfoDetail.address} - ${
                            orderInfoDetail.contact
                        }"
                    }
                    _order.value = orderDetail
                    updateOrder(orderDetail)
                } else {
                    _order.value = orderDetail
                    UtilsMethods.showToast(
                        context,
                        "Проблемы с интернетом, данные могут быть не коктными"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e)
                UtilsMethods.showToast(context, "Ошибка!")
            }
        }
    }

    private suspend fun getCoordinate(address: String): Location {
        val mapData = orderListRepository.getCoordinates(address)
        return if (mapData != null && mapData.status != "ZERO_RESULTS") {
            mapData.results[0].geometry.location
        } else {
            Location(0.0, 0.0)
        }
    }

    /**
     * сохранить заказ
     **/
    private fun updateOrder(order: Order) {
        uiScope.launch {
            orderListRepository.updateOrder(order)
        }
    }

    /**
     * парсит в моссив телефоны клиента
     **/
    fun getPhoneNumberClient(): Array<String> {
        var contacts = mutableListOf<String>()
        val contact = _order.value?.contact
        if (!contact.isNullOrEmpty()) {
            contacts = when {
                contact.contains(";") -> {
                    contact.split(";") as MutableList<String>
                }
                contact.contains(",") -> {
                    contact.split(",") as MutableList<String>
                }
                else -> {
                    arrayListOf(contact)
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