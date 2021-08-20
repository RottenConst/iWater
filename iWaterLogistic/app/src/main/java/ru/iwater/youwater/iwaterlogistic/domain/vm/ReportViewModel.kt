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

enum class Status {
    NONE,
    DONE,
    ERROR
}

@OnScreen
class ReportViewModel @Inject constructor(
    private val completeOrdersRepository: CompleteOrdersRepository,
    private val reportRepository: ReportRepository,
    accountRepository: AccountRepository,
) : ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var timeComplete = UtilsMethods.getTodayDateString()

    private val idDriver = accountRepository.getAccount().id
    private val company = accountRepository.getAccount().company

    private val _status: MutableLiveData<Status> = MutableLiveData()
    val status: LiveData<Status>
        get() = _status

    //отчеты
    private val _reportsDay: MutableLiveData<List<ReportDay>> = MutableLiveData()
    val reportsDay: LiveData<List<ReportDay>>
        get() = _reportsDay

    //отчет
    private val _reportDay: MutableLiveData<ReportDay> = MutableLiveData()
    val reportDay: LiveData<ReportDay>
        get() = _reportDay

    //расходы
    private val _expenses: MutableLiveData<List<Expenses>> = MutableLiveData()
    val expenses: LiveData<List<Expenses>>
        get() = _expenses

    //остались ли активные заказы
    private val _isCompleteOrder: MutableLiveData<Boolean> = MutableLiveData()
    val isCompleteOrder: LiveData<Boolean>
        get() = _isCompleteOrder


    init {
        initDateReport()
        isSendReportDay()
        _status.value = Status.NONE
    }

    /**
     * загрузить отчет за день из бд по дате
     */
    fun initDateReport(date: String = "") {
        uiScope.launch {
            if (date != timeComplete && date.isNotBlank()) {
                _reportDay.value = reportRepository.loadReportFromDB(date)
            } else {
                _reportDay.value = ReportDay(
                    timeComplete,
                    completeOrdersRepository.getSumCashFullCompleteOrder(timeComplete),
                    completeOrdersRepository.getSumCashCompleteOrder("Наличные"),
                    completeOrdersRepository.getSumCashCompleteOrder("На сайте"),
                    completeOrdersRepository.getSumCashCompleteOrder("Оплата через терминал"),
                    completeOrdersRepository.getSumCashCompleteOrder("Без наличные"),
                    completeOrdersRepository.getSumCashCompleteOrder("Наличные") - reportRepository.getSumOfCostExpenses(
                        timeComplete
                    ),
                    completeOrdersRepository.getTankCompleteOrder(),
                    completeOrdersRepository.getCountCompleteOrder(timeComplete)
                )
            }
        }
    }

    /**
     * установить передаваемые данные в црм для отчета
     */
//    fun sendGeneralReport(reportDay: ReportDay) {
//        uiScope.launch {
//            if (reportRepository.addReport(reportDay)) {
//                _status.value = Status.DONE
//            } else {
//                _status.value = Status.ERROR
//            }
//        }
//    }

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
                    completeOrdersRepository.getSumCashCompleteOrder("Наличные") - reportRepository.getSumOfCostExpenses(
                        timeComplete
                    ),
                    completeOrdersRepository.getTankCompleteOrder(),
                    completeOrdersRepository.getCountCompleteOrder(timeComplete)
                )
            )
        }
    }

    fun getDriverCloseMonitor(): DayReport {
        val report = _reportDay.value
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
            if (reportRepository.sendDayReport(dayReport)) {
                _status.value = Status.DONE
            } else {
                _status.value = Status.ERROR
            }
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
                val dateReport = reports[0].date
                val expenses = reportRepository.loadExpenses(dateReport)
                expenses.forEach { reportRepository.deleteExpenses(it) }
                reportRepository.deleteReport(reports[0])
            }
            _reportsDay.value = reports
        }
    }

    /**
     * загрузить все расходы за день из бд за выьпранную дату
     */
    fun getExpenses(date: String) {
        uiScope.launch {
            if (date != timeComplete) _expenses.value = reportRepository.loadExpenses(date)
            else _expenses.value = reportRepository.loadExpenses(timeComplete)
        }
    }

    /**
     * добавить расход в бд
     */
    fun addExpensesInBD(name: String, cost: Float, fileName: String) {
        uiScope.launch {
            reportRepository.saveExpenses(
                Expenses(
                    idDriver,
                    timeComplete,
                    name,
                    cost,
                    fileName,
                    company
                )
            )
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
    fun sendExpenses(name: String, cost: Float, fileName: String) {
        uiScope.launch {
            if (reportRepository.sendExpenses(
                    Expenses(
                        idDriver,
                        timeComplete,
                        name,
                        cost,
                        fileName,
                        company
                    )
                )
            ) {
                _status.value = Status.DONE
            } else {
                _status.value = Status.ERROR
            }
        }
    }

    fun setStatusExpenses(status: Status) {
        _status.value = status
    }

    //остались еще не законченые заказы
    private fun isSendReportDay() {
        uiScope.launch {
            _isCompleteOrder.value = reportRepository.getSumCurrentOrder()
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