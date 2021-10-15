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
import ru.iwater.youwater.iwaterlogistic.domain.ClientInfo
import ru.iwater.youwater.iwaterlogistic.domain.WaterOrder
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

    private val _order: MutableLiveData<WaterOrder> = MutableLiveData()
    val order: LiveData<WaterOrder>
        get() = _order
    private val _clientInfo: MutableLiveData<ClientInfo> = MutableLiveData()
    val clientInfo: LiveData<ClientInfo>
        get() = _clientInfo


    /**
     * возвращает заказ по id
     **/
    fun getOrderInfo(context: Context?, id: Int) {
        uiScope.launch {
            try {
                val orderDetail = orderListRepository.getDBOrderOnId(id)
                val orderInfoDetail = orderListRepository.getLoadOrderInfo(id)
                val clientInfo = orderListRepository.getAddressBottle(orderInfoDetail?.client_id, orderDetail.address)
                if (orderInfoDetail != null) {
                    if (!orderDetail.cash.isNullOrEmpty()) {
                        orderDetail.cash = orderInfoDetail.cash
                    } else {
                        orderDetail.cash_b = orderInfoDetail.cash_b
                    }
                    if (orderDetail.coords.isNullOrEmpty()) {
                        orderDetail.coords = getCoordinate(orderInfoDetail.address).toString()
                        orderListRepository.updateDBLocation(orderDetail, orderDetail.coords)
                    }
                    if (clientInfo?.fact_address?.isNotEmpty() == true) {
                        clientInfo.fact_address.also { orderDetail.address = it }
                    } else {
                        orderDetail.address = "${orderInfoDetail.address} - ${
                            orderInfoDetail.contact
                        }"
                    }
                    try {
                        if (orderDetail.contact.isEmpty()) orderDetail.contact = orderInfoDetail.contact.split(",").last()
                    } catch (e: Exception) {
                        UtilsMethods.showToast(context, "Ошибка оформления заказа, телефон не отображен")
                    }

                    _order.value = orderDetail
                    _clientInfo.value = clientInfo
                } else {
                    _order.value = orderDetail
                    _clientInfo.value = ClientInfo("", 0)
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