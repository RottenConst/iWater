package ru.iwater.youwater.iwaterlogistic.domain.vm

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.domain.CompleteOrder
import ru.iwater.youwater.iwaterlogistic.domain.DecontrolReport
import ru.iwater.youwater.iwaterlogistic.domain.OrderInfo
import ru.iwater.youwater.iwaterlogistic.domain.TypeClient
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.repository.CompleteOrdersRepository
import ru.iwater.youwater.iwaterlogistic.repository.OrderListRepository
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.current.CompleteShipActivity
import javax.inject.Inject

@OnScreen
class ShipmentsViewModel @Inject constructor(
    private val orderListRepository: OrderListRepository,
    private val completeOrdersRepository: CompleteOrdersRepository,
    accountRepository: AccountRepository
) : ViewModel() {

    /**
     * короутины дял асинхронных задач
     */
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val mOrder: MutableLiveData<OrderInfo> = MutableLiveData()
    private val mTypeClient: MutableLiveData<TypeClient> = MutableLiveData()
//    private var nameDriver: String = accountRepository.getAccount().login
//    private var company: String = accountRepository.getAccount().company

    val typeClient: LiveData<TypeClient>
        get() = mTypeClient

    val order: LiveData<OrderInfo>
        get() = mOrder

    /**
     * возвращает заказ по id
     **/
    fun getOrderInfo(id: Int?) {
        uiScope.launch {
            mOrder.value = orderListRepository.getLoadOrderInfo(id)
        }
    }

    fun setCompleteOrder(context: Context?, id: Int?, typeCash: String, cash: Float, tank: Int, timeComplete: Long, noticeDriver: String, shipCoord: String) {
        uiScope.launch {

            val answer = completeOrdersRepository.updateStatusOrder(id)
            val order = orderListRepository.getDBOrderOnId(id)

            if (answer?.error == 0 && answer.oper == "Запись изменена") {
                val order = orderListRepository.getDBOrderOnId(id)
                val reportOrder = DecontrolReport(id, timeComplete, "57.8046651;28.3571715", tank, noticeDriver)
                val completeOrder = CompleteOrder(
                    order.id,
                    order.name,
                    order.products,
                    cash,
                    typeCash,
                    tank,
                    order.time,
                    timeComplete,
                    order.contact,
                    order.notice,
                    noticeDriver,
                    order.period,
                    order.address,
                    2,
                )
                order.status = 2
                completeOrdersRepository.saveCompleteOrder(completeOrder)
                orderListRepository.updateStatus(order)
                completeOrdersRepository.addReport(
                    reportOrder
                )
            }

            val intent = Intent(context, CompleteShipActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("typeCash", typeCash)
            intent.putExtra("timeCompete", timeComplete)
            intent.putExtra("address", mOrder.value?.address)
            intent.putExtra("error", answer?.error)
            CompleteShipActivity.start(context, intent)

        }
    }

    /**
     * запрашивает и устанавливает тип клиента
     **/
    fun getTypeClient(id: Int?) {
        uiScope.launch {
            mTypeClient.value = TypeClient(orderListRepository.getTypeClient(id)?.toInt())
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