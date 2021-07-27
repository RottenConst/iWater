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
import ru.iwater.youwater.iwaterlogistic.domain.DayReport
import ru.iwater.youwater.iwaterlogistic.domain.Expenses
import ru.iwater.youwater.iwaterlogistic.domain.ReportDay
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.repository.CompleteOrdersRepository
import ru.iwater.youwater.iwaterlogistic.repository.ReportRepository
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.report.ReportActivity
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
import timber.log.Timber
import java.util.*
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
    private val company = accountRepository.getAccount().company
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
                completeOrdersRepository.getSumCashCompleteOrder("Наличные"),
                completeOrdersRepository.getSumCashCompleteOrder("На сайте"),
                completeOrdersRepository.getSumCashCompleteOrder("Оплата через терминал"),
                completeOrdersRepository.getSumCashCompleteOrder("Без наличные"),
                completeOrdersRepository.getSumCashCompleteOrder("Наличные") - reportRepository.getSumOfCostExpenses(timeComplete),
                completeOrdersRepository.getTankCompleteOrder(),
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
    fun sendGeneralReport(reportDay: ReportDay) {
        uiScope.launch {
            reportRepository.addReport(reportDay)
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
                    completeOrdersRepository.getSumCashCompleteOrder("Наличные"),
                    completeOrdersRepository.getSumCashCompleteOrder("На сайте"),
                    completeOrdersRepository.getSumCashCompleteOrder("Оплата через терминал"),
                    completeOrdersRepository.getSumCashCompleteOrder("Без наличные"),
                    completeOrdersRepository.getSumCashCompleteOrder("Наличные") - reportRepository.getSumOfCostExpenses(timeComplete),
                    completeOrdersRepository.getTankCompleteOrder(),
                    completeOrdersRepository.getCountCompleteOrder(timeComplete) )
            )
        }
    }

    fun getDriverCloseMonitor(): DayReport {
        val report = mReportDay.value
        val dateCreate = Calendar.getInstance().timeInMillis / 1000
        return DayReport(
            idDriver,
            report?.tank ?: 0,
            report?.orderComplete ?: 0,
            report?.cashOnSite ?: 0.0F,
            report?.cashOnTerminal ?: 0.0F,
            report?.cashMoney ?: 0.0F,
            report?.noCashMoney ?: 0.0F,
            report?.moneyDelivery ?: 0.0F,
            report?.totalMoney,
            dateCreate.toString(),
            report?.date,
            company
        )
    }

    fun driverCloseDay(dayReport: DayReport) {
        uiScope.launch {
            reportRepository.sendDayReport(dayReport)
        }
    }

    /**
     * загрузить все отчеты за день из бд
     */
    fun getReports() {
        uiScope.launch {
            val reports = reportRepository.loadAllReport()
            Timber.d("TODAY = ${UtilsMethods.getTodayDateString()}")
            if (reports.size > 1) {
                reportRepository.deleteReport(reports[0])
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
            reportRepository.saveExpenses(Expenses(idDriver,timeComplete, name, cost, company))
        }
    }

    //очистить выполненные заказы
    fun clearOldCompleteOrder() {
        uiScope.launch {
            val completeOrders = completeOrdersRepository.getAllCompleteOrders()
            for (completeOrder in completeOrders) {
                completeOrdersRepository.deleteCompleteOrder(completeOrder)
            }
        }
    }

    /**
     * отправить расход в в црм
     */
    fun sendExpenses(name: String, cost: Float) {
        uiScope.launch {
            reportRepository.sendExpenses(Expenses(idDriver, timeComplete, name, cost, company))
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