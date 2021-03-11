package ru.iwater.youwater.iwaterlogistic.domain

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.repository.OrderListRepository
import ru.iwater.youwater.iwaterlogistic.response.TypeClient
import timber.log.Timber
import javax.inject.Inject

@OnScreen
class ShipmentsViewModel @Inject constructor(
    private val orderListRepository: OrderListRepository
) : ViewModel() {

    /**
     * короутины дял асинхронных задач
     */
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val mOrder: MutableLiveData<Order> = MutableLiveData()
    private val mTypeClient: MutableLiveData<String> = MutableLiveData()

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