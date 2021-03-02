package ru.iwater.youwater.iwaterlogistic.domain

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.bd.IWaterDB
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.iteractor.AccountStorage
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.repository.OrderListRepository
import ru.iwater.youwater.iwaterlogistic.response.DriverWayBill
import javax.inject.Inject

@OnScreen
class OrderListViewModel @Inject constructor(
    private val orderListRepository: OrderListRepository,
    private val accountRepository: AccountRepository
        ) : ViewModel() {

    private val mListOrder: MutableLiveData<List<Order>> = MutableLiveData()

    val listOrder: LiveData<List<Order>>
        get() = mListOrder

    fun getLoadOrder() {
        orderListRepository.driverWayBill.session = accountRepository.getAccount().session
        viewModelScope.launch {
            orderListRepository.saveOrders()
            mListOrder.value = orderListRepository.getOrders()
        }
    }
}