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
import ru.iwater.youwater.iwaterlogistic.domain.*
import ru.iwater.youwater.iwaterlogistic.domain.vm.TypeClient.*
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
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
    private val completeOrdersRepository: CompleteOrdersRepository,
    accountRepository: AccountRepository
) : ViewModel() {

    /**
     * короутины дял асинхронных задач
     */
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _order: MutableLiveData<OrderInfoNew> = MutableLiveData()
    val order: LiveData<OrderInfoNew>
        get() = _order

    private val _typeClient: MutableLiveData<TypeClient> = MutableLiveData()
    val typeClient: LiveData<TypeClient>
        get() = _typeClient

    private val _typeCash: MutableLiveData<String> = MutableLiveData()
    val typeCash: LiveData<String>
        get() = _typeCash

    private var myCoordinate = ""
    private val account: Account = accountRepository.getAccount()

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

    private suspend fun getCountCompleteOrder(): Int {
        return completeOrdersRepository.getAllCompleteOrders().size
    }

    private suspend fun getTotalMoney(): Float {
        val orders = completeOrdersRepository.getAllCompleteOrders()
        var money = 0.0F
        orders.forEach { money += it.cash }
        return money
    }

    private fun setCompleteOrder(context: Context?, id: Int, cash: Float, tank: Int, noticeDriver: String, type: String) {
        uiScope.launch {
            val timeComplete = Calendar.getInstance().timeInMillis / 1000
            val typeCash = _typeCash.value
            val decontrolReport = DecontrolReport(id, timeComplete, myCoordinate, tank, noticeDriver)
            if (completeOrdersRepository.addDecontrol(decontrolReport)) {
                if (typeCash != null) {
                    if (isSendReportOrder(
                            id = id,
                            typeCash = typeCash,
                            cash = cash,
                            tank = tank,
                            timeComplete = timeComplete,
                            type = type,
                            coord = myCoordinate,
                            noticeDriver
                        )
                    ) {
                        val answer = completeOrdersRepository.updateStatusOrder(id)
                        if (answer.error == 0 && answer.oper == "Запись изменена") {
                                saveCompleteOrder(
                                    id,
                                    cash,
                                    typeCash,
                                    tank,
                                    timeComplete,
                                    noticeDriver
                                )
                                setShipmentOrder(context, id, timeComplete, answer.error, "")

                        } else {
                            setShipmentOrder(context, id, timeComplete, answer.error, "не удалось изменить статус")
                        }
                    } else {
                        setShipmentOrder(context, id, timeComplete, 1, "Ошибка отправки данных для отчета")
                    }
                }
            } else {
                setShipmentOrder(context, id, timeComplete, 1, "ошибка с таблицой контроля")
            }
        }
    }

    fun setEmptyBottle(context: Context?, id: Int, clientId: Int, cash: String?, tank: Int, address: String, noticeDriver: String, type: String) {
        uiScope.launch {
            val cashOrder = if (cash.isNullOrEmpty()) 0F else cash.toFloat()
            if (orderListRepository.setEmptyBottle(clientId, tank, id, address) || clientId == 0) {
                setCompleteOrder(context, id, cashOrder, tank, noticeDriver, type)
            } else {
                UtilsMethods.showToast(context, "ОШИБКА")
            }
        }

    }

    private fun setShipmentOrder(context: Context?, id: Int, timeComplete: Long, error: Int, notice: String) {
        val intent = Intent(context, CompleteShipActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("typeCash", _typeCash.value)
        intent.putExtra("timeComplete", timeComplete)
        intent.putExtra("address", _order.value?.address)
        intent.putExtra("error", error)
        intent.putExtra("notice", notice)
        CompleteShipActivity.start(context, intent)
    }

    private suspend fun saveCompleteOrder(id: Int, cash: Float, typeCash: String, tank: Int, timeComplete: Long, noticeDriver: String) {
        val order = orderListRepository.getDBOrderOnId(id)
        val completeOrder = CompleteOrder(
            order.order_id,
            order.name,
            order.order,
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
    }

    private suspend fun isSendReportOrder(id: Int, typeCash: String, cash: Float, tank: Int, timeComplete: Long, type: String, coord: String, notice: String): Boolean {
        val typeClient = if (_typeClient.value == PHYSICS) "0" else "1"
        val reportOrder = ReportOrder(
            company_id = account.company,
            driver_id = account.id,
            name = account.login,
            date = UtilsMethods.getTodayDateString(),
            date_created = timeComplete.toString(),
            order_id = id,
            type_client = typeClient,
            payment_type = typeCash,
            payment = cash,
            number_containers = tank,
            orders_delivered = getCountCompleteOrder(),
            total_money = getTotalMoney() + cash,
            type = type.toInt(),
            coord = coord,
            notice = notice
        )
        return completeOrdersRepository.addReport(reportOrder)
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