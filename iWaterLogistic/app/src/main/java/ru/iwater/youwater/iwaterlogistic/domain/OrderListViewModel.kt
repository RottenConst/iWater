package ru.iwater.youwater.iwaterlogistic.domain

import android.app.PendingIntent
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.bd.IWaterDB
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.iteractor.AccountStorage
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.repository.OrderListRepository
import ru.iwater.youwater.iwaterlogistic.response.DriverWayBill
import javax.inject.Inject

/**
 * viewModel класс для действий с заказами
 */
@OnScreen
class OrderListViewModel @Inject constructor(
    private val orderListRepository: OrderListRepository,
    private val accountRepository: AccountRepository
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