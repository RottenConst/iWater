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
import ru.iwater.youwater.iwaterlogistic.domain.Expenses
import ru.iwater.youwater.iwaterlogistic.domain.ReportDay
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.repository.CompleteOrdersRepository
import ru.iwater.youwater.iwaterlogistic.repository.ReportRepository
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.report.ReportActivity
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
import javax.inject.Inject

@OnScreen
class ReportViewModel @Inject constructor(
    private val completeOrdersRepository: CompleteOrdersRepository,
    private val reportRepository: ReportRepository,
    accountRepository: AccountRepository
): ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var timeComplete = UtilsMethods.getTodayDateString()

    private val idDriver = accountRepository.getAccount().id
    private var many = 0.0F

     //отчеты
    private val mReportsDay: MutableLiveData<List<ReportDay>> = MutableLiveData()
    //отчет
    private val mReportDay: MutableLiveData<ReportDay> = MutableLiveData()
    //расходы
    private val mExpenses: MutableLiveData<List<Expenses>> = MutableLiveData()
    //остались ли активные заказы
    private val mIsCompleteOrder: MutableLiveData<Boolean> = MutableLiveData()

    val reportsDay: LiveData<List<ReportDay>>
        get() = mReportsDay

    val reportDay: LiveData<ReportDay>
        get() = mReportDay

    val expenses: LiveData<List<Expenses>>
        get() = mExpenses

    val isCompleteOrder: LiveData<Boolean>
        get() = mIsCompleteOrder

    /**
     * Инициализироавать отчет за день
     */
    fun initThisReport() {
        uiScope.launch {
            mReportDay.value = ReportDay(
                timeComplete,
                completeOrdersRepository.getSumCashFullCompleteOrder(timeComplete),
                completeOrdersRepository.getSumCashCompleteOrder("Наличные", timeComplete),
                completeOrdersRepository.getSumCashCompleteOrder("На сайте", timeComplete),
                completeOrdersRepository.getSumCashCompleteOrder("Оплата через терминал", timeComplete),
                completeOrdersRepository.getSumCashCompleteOrder("Без наличные", timeComplete),
                completeOrdersRepository.getSumCashCompleteOrder("Наличные", timeComplete) - reportRepository.getSumOfCostExpenses(timeComplete),
                completeOrdersRepository.getTankCompleteOrder(timeComplete),
                completeOrdersRepository.getCountCompleteOrder(timeComplete) )
        }
    }

    /**
     * загрузить отчет за день из бд по дате
     */
    fun initDateReport(date: String) {
        uiScope.launch {
            mReportDay.value = reportRepository.loadReportFromDB(date)
        }
    }

    /**
     * установить передаваемые данные в црм для отчета
     */
    fun setPropertyGeneralReport(reportDay: ReportDay) {
        reportRepository.driverCloseDay.setPropertyGeneralReport(idDriver, reportDay)
    }

    /**
     * отправить отчет за день в црм
     */
    fun sendGeneralReport() {
        uiScope.launch {
            reportRepository.driverCloseDay.sendGeneralReport()
        }
    }

    /**
     * сохранить отчет за день
     */
    fun saveTodayReport() {
        uiScope.launch {
            reportRepository.saveReport(
                ReportDay(
                    timeComplete,
                    completeOrdersRepository.getSumCashFullCompleteOrder(timeComplete),
                    completeOrdersRepository.getSumCashCompleteOrder("Наличные", timeComplete),
                    completeOrdersRepository.getSumCashCompleteOrder("На сайте", timeComplete),
                    completeOrdersRepository.getSumCashCompleteOrder("Оплата через терминал", timeComplete),
                    completeOrdersRepository.getSumCashCompleteOrder("Без наличные", timeComplete),
                    completeOrdersRepository.getSumCashCompleteOrder("Наличные", timeComplete) - reportRepository.getSumOfCostExpenses(timeComplete),
                    completeOrdersRepository.getTankCompleteOrder(timeComplete),
                    completeOrdersRepository.getCountCompleteOrder(timeComplete) )
            )
        }
    }

    /**
     * загрузить все отчеты за день из бд
     */
    fun getReports() {
        uiScope.launch {
            val reports = reportRepository.loadAllReport()
            for (report in reports) {
                if (report.date == UtilsMethods.getTodayDateString()) {
                    reportRepository.deleteReport(report)
                }
            }
            mReportsDay.value = reports
        }
    }

    /**
     * загрузить все расходы за день из бд
     */
    fun getTodayExpenses() {
        uiScope.launch {
            mExpenses.value = reportRepository.loadExpenses(timeComplete)
        }
    }

    /**
     * загрузить все расходы за день из бд за выьпранную дату
     */
    fun getExpenses(date: String) {
        uiScope.launch {
            mExpenses.value = reportRepository.loadExpenses(date)
        }
    }

    /**
     * добавить расход в бд
     */
    fun addExpensesInBD(name: String, cost: Float) {
        uiScope.launch {
            reportRepository.saveExpenses(Expenses(timeComplete, name, cost))
        }
    }

    /**
     * отправить расход в в црм
     */
    fun sendExpenses(name: String, cost: Float) {
        reportRepository.addExpenses.setPropertyExpenses(idDriver, name, cost)
        uiScope.launch {
            reportRepository.addExpenses.sendExpenses()
        }
    }

    //отправить отчет в црм
    fun isSendReportDay() {
        uiScope.launch {
            mIsCompleteOrder.value = reportRepository.getSumCurrentOrder()
        }
    }

    /**
     * запустить экран с отчетами
     **/
    fun getReportActivity(context: Context, date: String) {
        val intent = Intent(context, ReportActivity::class.java)
        intent.putExtra("dateReport", date)
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