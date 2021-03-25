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
import ru.iwater.youwater.iwaterlogistic.domain.Expenses
import ru.iwater.youwater.iwaterlogistic.domain.ReportDay
import ru.iwater.youwater.iwaterlogistic.repository.CompleteOrdersRepository
import ru.iwater.youwater.iwaterlogistic.screens.completeCardOrder.CardCompleteActivity
import ru.iwater.youwater.iwaterlogistic.screens.report.ReportActivity
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@OnScreen
class CompleteOrdersViewModel @Inject constructor(
    private val completeOrdersRepository: CompleteOrdersRepository
) : ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var timeComplete = ""

    private val mListOrders: MutableLiveData<List<CompleteOrder>> = MutableLiveData()
    private val mOrder: MutableLiveData<CompleteOrder> = MutableLiveData()

    //отчет
    private val mReportDay: MutableLiveData<ReportDay> = MutableLiveData()

    //расходы
    private val mExpenses: MutableLiveData<List<Expenses>> = MutableLiveData()


    val reportDay: LiveData<ReportDay>
        get() = mReportDay

    val expenses: LiveData<List<Expenses>>
        get() = mExpenses

    val listCompleteOrder: LiveData<List<CompleteOrder>>
        get() = mListOrders

    val order: LiveData<CompleteOrder>
        get() = mOrder


    init {
        val currentDate = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        timeComplete = formatter.format(currentDate.time)
    }
    /**
     * возвращает заказ по id
     **/
    fun getCompleteOrder(id: Int) {
        uiScope.launch {
            mOrder.value = completeOrdersRepository.getCompleteOrder(id)
        }
    }

    /**
     * возвращает выполненнык заказы за текущий день
     **/
    fun getCompleteListOrders() {
        uiScope.launch {
            mListOrders.value = completeOrdersRepository.getCompleteListOrders(timeComplete)
        }
    }


    fun initReport() {
        uiScope.launch {
            mReportDay.value = ReportDay(1,
                timeComplete,
                completeOrdersRepository.getSumCashFullCompleteOrder(timeComplete),
                completeOrdersRepository.getSumCashCompleteOrder("Наличные", timeComplete),
                completeOrdersRepository.getSumCashCompleteOrder("На сайте", timeComplete),
                completeOrdersRepository.getSumCashCompleteOrder("Оплата через терминал", timeComplete),
                completeOrdersRepository.getSumCashCompleteOrder("Без наличные", timeComplete),
                completeOrdersRepository.getTankCompleteOrder(timeComplete),
                completeOrdersRepository.getCountCompleteOrder(timeComplete) )
        }
    }

    fun addExpensesInBD(name: String, cost: Float) {
        uiScope.launch {
            completeOrdersRepository.saveExpenses(Expenses(timeComplete, name, cost))
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

    fun getTodayExpenses() {
        uiScope.launch {
            mExpenses.value = completeOrdersRepository.loadExpenses(timeComplete)
        }
    }


    /**
     * запустить экран с отчетами
     **/
    fun getReportActivity(context: Context) {
        val intent = Intent(context, ReportActivity::class.java)
        ReportActivity.start(context, intent)
    }

    /**
     * закрываем и отменяем корутины
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}