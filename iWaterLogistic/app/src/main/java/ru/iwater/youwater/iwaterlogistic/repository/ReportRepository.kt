package ru.iwater.youwater.iwaterlogistic.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.iwater.youwater.iwaterlogistic.bd.*
import ru.iwater.youwater.iwaterlogistic.domain.DayReport
import ru.iwater.youwater.iwaterlogistic.domain.Expenses
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.domain.ReportDay
import ru.iwater.youwater.iwaterlogistic.response.ApiRequest
import ru.iwater.youwater.iwaterlogistic.response.RetrofitFactory
import timber.log.Timber
import javax.inject.Inject

class ReportRepository @Inject constructor(
    iWaterDB: IWaterDB
){

//    val addExpenses = AddExpenses()
//    val driverCloseDay = DriverCloseDay()
    val service: ApiRequest = RetrofitFactory.makeRetrofit()

    private val expensesDao: ExpensesDao = iWaterDB.ExpensesDao()
    private val orderDao: OrderDao = iWaterDB.orderDao()
    private val reportDayDao: ReportDayDao = iWaterDB.reportDayDao()

    /**
     * сохранить отчет в бд
     */
    suspend fun saveReport(reportDay: ReportDay) {
        reportDayDao.save(reportDay)
    }

    /**
     * загрузить отчет из бд за указанную дату
     */
    suspend fun loadReportFromDB(date: String): ReportDay = withContext(Dispatchers.Default) {
        return@withContext reportDayDao.loadReportDays(date)
    }

    /**
     * загрузить все отчеты
     */
    suspend fun loadAllReport(): List<ReportDay> = withContext(Dispatchers.Default) {
        return@withContext reportDayDao.loadAllReports()
    }

    /**
     * удалить отчет
     */
    suspend fun deleteReport(reportDay: ReportDay) = withContext(Dispatchers.Default) {
        reportDayDao.delete(reportDay)
    }

    /**
     * получить сумму расходов за сегодняшний день
     */
    suspend fun getSumOfCostExpenses(date: String): Float = withContext(Dispatchers.Default) {
        return@withContext expensesDao.sumCost(date)
    }

    /**
     * сохранить расход
     */
    suspend fun saveExpenses(expenses: Expenses) {
        expensesDao.save(expenses)
    }

    /**
     * загрузить расходы за текущий день
     */
    suspend fun loadExpenses(date: String) : List<Expenses> = withContext(Dispatchers.Default) {
        return@withContext expensesDao.load(date)
    }

    /**
     * вернуть не завершенные заказы из бд
     */
    suspend fun getDBOrders(): List<Order> = withContext(Dispatchers.Default){
        return@withContext orderDao.load()
    }

    /**
     * узнать остались еще активные заказы
     */
    suspend fun getSumCurrentOrder(): Boolean {
        val orders = getDBOrders()
        var size = orders.size
        for (order in orders) {
            if (order.status == 2) {
                size -= 1
            }
        }
        Timber.d("$size!!!!!!!!!!!")
        return size <= 0
    }

    suspend fun sendExpenses(expenses: Expenses) {
        val answer = service.addExpenses(expenses)
        try {
            if (answer.isSuccessful) {
                Timber.d("OK! + ${answer.body()?.date_created}")
            }
        }catch (e: HttpException) {
            Timber.e(e.message())
        }
    }

    suspend fun addReport(reportDay: ReportDay) {
        val answer = service.addReport(reportDay)
        try {
            if (answer.isSuccessful) {
                Timber.d("OK! + ${answer.body()?.totalMoney}")
            }
        }catch (e: HttpException) {
            Timber.d(e.message())
        }
    }

    suspend fun sendDayReport(dayReport: DayReport) {
        val answer = service.sendTotalReport(dayReport)
        try {
            if (answer.isSuccessful) {
                Timber.d("${answer.body()?.date}")
            }
        }catch (e: HttpException) {
            Timber.e(e.message())
        }
    }
}