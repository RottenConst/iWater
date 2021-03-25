package ru.iwater.youwater.iwaterlogistic.domain.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.domain.CompleteOrder
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.repository.CompleteOrdersRepository
import ru.iwater.youwater.iwaterlogistic.repository.OrderListRepository
import ru.iwater.youwater.iwaterlogistic.response.TypeClient
import javax.inject.Inject

@OnScreen
class ShipmentsViewModel @Inject constructor(
    private val orderListRepository: OrderListRepository,
    private val completeOrdersRepository: CompleteOrdersRepository
) : ViewModel() {

    /**
     * короутины дял асинхронных задач
     */
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val mOrder: MutableLiveData<Order> = MutableLiveData()
    private val mTypeClient: MutableLiveData<String> = MutableLiveData()
    private val mCompleteOrder: MutableLiveData<CompleteOrder> = MutableLiveData()

    val typeClient: LiveData<String>
        get() = mTypeClient

    val order: LiveData<Order>
        get() = mOrder

    /**
     * возвращает заказ по id
     **/
    fun getOrderInfo(id: Int) {
        uiScope.launch {
            mOrder.value = orderListRepository.getDBOrderOnId(id)
        }
    }

    fun setCompleteOrder(cash: Float, typeCash: String, tank: Int, timeComplete: String, noticeDriver: String, shipCoordinate: List<String>, shipCoord: String) {
        val orderId = order.value?.id
        if (orderId != null) {
            completeOrdersRepository.accept.setProperty(orderId, tank, noticeDriver, shipCoord)
        }
        uiScope.launch {
            orderId?.let {
                val order = orderListRepository.getDBOrderOnId(it)
                val completeOrder = CompleteOrder(
                    order.id,
                    order.name,
                    order.product,
                    cash, typeCash, tank,
                    order.timeStart,
                    order.timeEnd,
                    timeComplete,
                    order.contact,
                    order.notice,
                    noticeDriver,
                    order.date,
                    order.period,
                    order.address,
                    status = 1,
                    order.coordinates,
                    shipCoordinate )
                completeOrdersRepository.saveCompleteOrder(completeOrder)
                orderListRepository.deleteOrder(order)
                completeOrdersRepository.accept.acceptOrder()
            }
        }
    }

    fun getAcceptOrder(orderId: Int, tank: Int, comment: String, coordinate: String) {
        completeOrdersRepository.accept.setProperty(orderId, tank, comment, coordinate)
        uiScope.launch {
            completeOrdersRepository.accept.acceptOrder()
        }
    }

    /**
     * запрашивает и устанавливает тип клиента
     **/
    fun getTypeClient(id: Int) {
        val typeClient = TypeClient()
        typeClient.setProperty(id)
        uiScope.launch {
            when (typeClient.getTypeClient()) {
                "0" -> {
                    mTypeClient.value = "Физ. лицо"
                }
                "1" -> {
                    mTypeClient.value = "Юр. лицо"
                }
                else -> {
                    mTypeClient.value = "0"
                }
            }
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