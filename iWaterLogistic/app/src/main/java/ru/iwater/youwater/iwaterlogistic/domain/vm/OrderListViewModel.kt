package ru.iwater.youwater.iwaterlogistic.domain.vm

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.repository.OrderListRepository
import ru.iwater.youwater.iwaterlogistic.screens.cardOrder.CardOrderActivity
import javax.inject.Inject

/**
 * viewModel класс для действий с заказами
 */
@OnScreen
class OrderListViewModel @Inject constructor(
    private val orderListRepository: OrderListRepository,
    accountRepository: AccountRepository
        ) : ViewModel() {

    /**
     * при инициализации устанавливаем сессию
     */
    init {
        orderListRepository.driverWayBill.setProperty(accountRepository.getAccount().session)
    }

    /**
     * короутины дял асинхронных задач
     */
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val mListOrder: MutableLiveData<List<Order>> = MutableLiveData()

    val listOrder: LiveData<List<Order>>
        get() = mListOrder

    /**
     * загружаем инфу о заказах
     * и обновляем liveData
     */
    fun getLoadOrder() {
        uiScope.launch {
            orderListRepository.getLoadOrderList()
            mListOrder.value = orderListRepository.getOrders()
        }
    }

    fun getLoadOrderWithFactAddress() {
        uiScope.launch {
            orderListRepository.getLoadOrderList()
            mListOrder.value = orderListRepository.getOrders()
        }
    }

    /**
     * сохраняет заказ в бд и запускает экран с информацией о заказе с возможностью отгрузки
     **/
    fun getAboutOrder(context: Context, order: Order) {
        val intent = Intent(context, CardOrderActivity::class.java)
        intent.putExtra("id", order.id)
        uiScope.launch {
            orderListRepository.saveOrder(order)
        }
        CardOrderActivity.start(context, intent)
    }

    fun refreshOrder() {
        uiScope.launch {
            orderListRepository.getLoadOrderList()
            orderListRepository.updateOrder()
            mListOrder.value = orderListRepository.getOrders()
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