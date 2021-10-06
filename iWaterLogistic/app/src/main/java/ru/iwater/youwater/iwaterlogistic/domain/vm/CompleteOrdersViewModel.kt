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
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.repository.CompleteOrdersRepository
import ru.iwater.youwater.iwaterlogistic.repository.OrderListRepository
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.complete.CardCompleteActivity
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@OnScreen
class CompleteOrdersViewModel @Inject constructor(
    private val completeOrdersRepository: CompleteOrdersRepository,
    private val orderListRepository: OrderListRepository,
    accountRepository: AccountRepository
) : ViewModel() {

    private var account: Account = accountRepository.getAccount()

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var timeComplete = ""

    private val _listOrders: MutableLiveData<List<CompleteOrder>> = MutableLiveData()
    val listCompleteOrder: LiveData<List<CompleteOrder>>
        get() = _listOrders

    private val _order: MutableLiveData<CompleteOrder> = MutableLiveData()
    val order: LiveData<CompleteOrder>
        get() = _order

    private val _report: MutableLiveData<ReportOrder> = MutableLiveData()
    val report: LiveData<ReportOrder>
        get() = _report

    init {
        val currentDate = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        timeComplete = formatter.format(currentDate.time)
    }
    /**
     * возвращает заказ по id
     **/
    fun getCompleteOrder(id: Int?) {
        uiScope.launch {
            _order.value = completeOrdersRepository.getCompleteOrder(id)
        }
    }

    fun getReport(id: Int?) {
        uiScope.launch {
            if (id != null) _report.value = completeOrdersRepository.getReportOrder(id)
            Timber.d(_report.value?.payment_type)
        }
    }

    /**
     * возвращает выполненные заказы за текущий день
     **/
    fun getCompleteListOrders() {
        uiScope.launch {
            _listOrders.value = completeOrdersRepository.getAllCompleteOrders()
        }
    }

    fun getLoadLostOrder() {
        uiScope.launch {
            val orders = completeOrdersRepository.getLoadCompleteOrder(account.session).sortedBy { it.id }
            val completeOrders = completeOrdersRepository.getAllCompleteOrders().sortedBy { it.id }
            val lostOrder = mutableListOf<Order>()
            lostOrder.addAll(orders)
            if (completeOrders.isNotEmpty()) completeOrders.forEach { completeOrder ->
                lostOrder.remove(orders.find { it.id == completeOrder.id })
            }
            val savedOrders = mutableListOf<CompleteOrder>()
            if (lostOrder.isNotEmpty()) {
                savedOrders.addAll(lostOrder.map { order ->
                    CompleteOrder(
                        order.id,
                        order.name,
                        order.products,
                        if (order.cash.isEmpty()) order.cash_b.toFloat() else order.cash.toFloat(),
                        "-",
                        0,
                        order.time,
                        0L,
                        order.contact,
                        order.notice,
                        "",
                        order.period,
                        order.address,
                        order.status
                    )
                })
                for (save in savedOrders) {
                    completeOrdersRepository.saveCompleteOrder(save)
                }
            }
        }
    }

    fun setCompleteOrder(orderId: Int?, reportOrder: ReportOrder) {
        uiScope.launch {
            val order = completeOrdersRepository.getCompleteOrder(orderId)
            val saveOrder = CompleteOrder(
                order.id,
                order.name,
                order.products,
                order.cash,
                reportOrder.payment_type,
                reportOrder.number_containers,
                order.time,
                reportOrder.date_created.toLong(),
                order.contact,
                order.notice,
                order.noticeDriver,
                order.period,
                order.address,
                order.status
            )
            completeOrdersRepository.saveCompleteOrder(saveOrder)
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
     * закрываем и отменяем корутины
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}