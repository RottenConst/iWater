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
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.repository.CompleteOrdersRepository
import ru.iwater.youwater.iwaterlogistic.repository.OrderListRepository
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.complete.CardCompleteActivity
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@OnScreen
class CompleteOrdersViewModel @Inject constructor(
    private val completeOrdersRepository: CompleteOrdersRepository,
    private val orderListRepository: OrderListRepository,
    accountRepository: AccountRepository
) : ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var timeComplete = ""

    private val mListOrders: MutableLiveData<List<CompleteOrder>> = MutableLiveData()
    private val mOrder: MutableLiveData<CompleteOrder> = MutableLiveData()

    val listCompleteOrder: LiveData<List<CompleteOrder>>
        get() = mListOrders

    val order: LiveData<CompleteOrder>
        get() = mOrder


    init {
        val currentDate = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        timeComplete = formatter.format(currentDate.time)
        orderListRepository.driverWayBill.setProperty(accountRepository.getAccount().session)
    }
    /**
     * возвращает заказ по id
     **/
    fun getCompleteOrder(id: Int?) {
        uiScope.launch {
            mOrder.value = completeOrdersRepository.getCompleteOrder(id)
        }
    }

    /**
     * возвращает выполненные заказы за текущий день
     **/
    fun getCompleteListOrders() {
        uiScope.launch {
            mListOrders.value = completeOrdersRepository.getCompleteListOrders(timeComplete)
        }
    }


    /**
     * сохраняет заказ в бд и запускает экран с информацией о заказе с возможностью отгрузки
     **/
    fun getAboutOrder(context: Context?, completeOrder: CompleteOrder) {
        val intent = Intent(context, CardCompleteActivity::class.java)
        intent.putExtra("id", completeOrder.id)
        CardCompleteActivity.start(context, intent)
    }

    /**
     * сохраняет в базу завершенные заказы если их нет базе
     */
    fun getCompleteOrderCRM() {
        uiScope.launch {
            orderListRepository.getLoadOrderList()
            val ordersCRM = orderListRepository.getCompleteOrder()
            val completeOrders = completeOrdersRepository.getCompleteListOrders(timeComplete)
            for (orderCRM  in ordersCRM) {
                val completeCRM = CompleteOrder(
                    orderCRM.id,
                    orderCRM.name,
                    orderCRM.product,
                    if (orderCRM.cash == 0.00F)orderCRM.cash_b else orderCRM.cash,
                    "неизвестно",
                    0,
                    orderCRM.timeStart,
                    orderCRM.timeEnd,
                    "$timeComplete ${orderCRM.timeEnd}",
                    orderCRM.contact,
                    orderCRM.notice,
                    "нет данных",
                    orderCRM.date,
                    orderCRM.period,
                    orderCRM.address,
                    orderCRM.status,
                    orderCRM.coordinates,
                    mutableListOf())
                if (completeOrders.isEmpty()) {
                    completeOrdersRepository.saveCompleteOrder(completeCRM)
                } else {
                    for (completeOrder in completeOrders) {
                        if (completeOrder.id != orderCRM.id) {
                            completeOrdersRepository.saveCompleteOrder(completeCRM)
                        }
                    }
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