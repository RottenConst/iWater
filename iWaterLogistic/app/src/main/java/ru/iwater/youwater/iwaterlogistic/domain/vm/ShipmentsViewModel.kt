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
import ru.iwater.youwater.iwaterlogistic.domain.vm.TypeClient.*
import ru.iwater.youwater.iwaterlogistic.repository.CompleteOrdersRepository
import ru.iwater.youwater.iwaterlogistic.repository.OrderListRepository
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.current.CompleteShipActivity
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
import timber.log.Timber
import java.util.*
import javax.inject.Inject

enum class TypeClient { PHYSICS, JURISTIC, ERROR }

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

    private val _order: MutableLiveData<OrderInfo> = MutableLiveData()
    val order: LiveData<OrderInfo>
        get() = _order

    private val _typeClient: MutableLiveData<TypeClient> = MutableLiveData()
    val typeClient: LiveData<TypeClient>
        get() = _typeClient

    private val _typeCash: MutableLiveData<String> = MutableLiveData()
    val typeCash: LiveData<String>
        get() = _typeCash

    private var myCoordinate = ""

    /**
     * возвращает заказ по id
     **/
    fun getOrderInfo(context: Context?, id: Int?) {
        uiScope.launch {
            try {
                _order.value = orderListRepository.getLoadOrderInfo(id)
            }catch (e: Exception) {
                Timber.e(e)
                UtilsMethods.showToast(context, "Проблемы с интернетом")
            }
        }
    }

    fun setCompleteOrder(context: Context?, id: Int, tank: Int, noticeDriver: String) {
        uiScope.launch {

            val answer = completeOrdersRepository.updateStatusOrder(id)
            val cash = _order.value?.cash?.toFloat()
            val typeCash = _typeCash.value
            val timeComplete = Calendar.getInstance().timeInMillis/1000
            if (answer.error == 0 && answer.oper == "Запись изменена") {
                if (cash != null && typeCash != null) {
                    val order = orderListRepository.getDBOrderOnId(id)
                    val reportOrder =
                        DecontrolReport(id, timeComplete, myCoordinate, tank, noticeDriver)
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
                    completeOrdersRepository.saveCompleteOrder(completeOrder)
                    orderListRepository.deleteOrder(order)
                    completeOrdersRepository.addReport(
                        reportOrder
                    )
                }
            }

            val intent = Intent(context, CompleteShipActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("typeCash", _typeCash.value)
            intent.putExtra("timeComplete", timeComplete)
            intent.putExtra("address", _order.value?.address)
            intent.putExtra("error", answer.error)
            CompleteShipActivity.start(context, intent)

        }
    }

    fun setTypeOfCash(string: String) {
        _typeCash.value = string
        Timber.i(typeCash.value)
    }

    fun setMyCoordinate(string: String) {
        myCoordinate = string
    }

    /**
     * запрашивает и устанавливает тип клиента
     **/
    fun getTypeClient(context: Context?, id: Int?) {
        uiScope.launch {
            try {
                when (orderListRepository.getTypeClient(id)) {
                    0 -> _typeClient.value = PHYSICS
                    1 -> {
                        _typeClient.value = JURISTIC
                        _typeCash.value = "Без наличные"
                    }
                    else -> _typeClient.value = ERROR
                }
            } catch (e: Exception) {
                Timber.e(e)
                UtilsMethods.showToast(context, "Error!")
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