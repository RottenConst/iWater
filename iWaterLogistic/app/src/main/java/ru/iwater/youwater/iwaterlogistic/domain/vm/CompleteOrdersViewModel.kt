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
import ru.iwater.youwater.iwaterlogistic.repository.CompleteOrdersRepository
import ru.iwater.youwater.iwaterlogistic.screens.completeCardOrder.CardCompleteActivity
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@OnScreen
class CompleteOrdersViewModel @Inject constructor(
    private val completeOrdersRepository: CompleteOrdersRepository
) : ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val mListOrders: MutableLiveData<List<CompleteOrder>> = MutableLiveData()
    private val mOrder: MutableLiveData<CompleteOrder> = MutableLiveData()

    val listCompleteOrder: LiveData<List<CompleteOrder>>
        get() = mListOrders

    val order: LiveData<CompleteOrder>
        get() = mOrder

    /**
     * возвращает заказ по id
     **/
    fun getCompleteOrder(id: Int) {
        uiScope.launch {
            mOrder.value = completeOrdersRepository.getCompleteOrder(id)
        }
    }

    fun getCompleteListOrders() {
        val currentDate = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val timeComplete = formatter.format(currentDate.time)
        uiScope.launch {
            mListOrders.value = completeOrdersRepository.getCompleteListOrders(timeComplete)
        }
    }

    /**
     * сохраняет заказ в бд и запускает экран с информацией о заказе с возможностью отгрузки
     **/
    fun getAboutOrder(context: Context, completeOrder: CompleteOrder) {
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