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
import ru.iwater.youwater.iwaterlogistic.repository.OrderListRepository
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

    private lateinit var orderInfo: Order
    private val mOrder: MutableLiveData<Order> = MutableLiveData()

    val order: LiveData<Order>
        get() = mOrder

    /**
     * возвращает заказ по id
     **/
    fun getOrderInfo(id: Int) {
        orderListRepository.orderCurrent.setProperty(id)
        uiScope.launch {
            orderInfo = orderListRepository.getDBOrderOnId(id)
            orderInfo.address = orderListRepository.getFactAddress()
            mOrder.value = orderInfo
        }
    }

    /**
     * сохранить заказ
     **/
    fun saveOrder() {
        uiScope.launch {
            orderListRepository.saveOrder(orderInfo)
        }
    }

    /**
     * парсит в моссив телефоны клиента
     **/
    fun getPhoneNumberClient(): Array<String> {
        if (orderInfo.contact.isNotEmpty()) {
            return when {
                orderInfo.contact.contains(",") -> {
                    orderInfo.contact.split(",").toTypedArray()
                }
                orderInfo.contact.contains(";") -> {
                    orderInfo.contact.split(";").toTypedArray()
                }
                else -> {
                    arrayOf(orderInfo.contact)
                }
            }
        }
        return arrayOf(orderInfo.contact)
    }

    /**
     * закрываем и отменяем корутины
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}