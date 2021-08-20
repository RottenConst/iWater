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
import java.lang.Exception
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

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

    suspend fun deleteExpenses(expenses: Expenses) {
        expensesDao.delete(expenses)
    }

    /**
     * загрузить расходы за текущий дату
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

    suspend fun sendExpenses(expenses: Expenses): Boolean {
        try {
            val answer = service.addExpenses("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", expenses)
            return if (answer.isSuccessful) {
                Timber.d("OK! + ${answer.body()?.date_created}")
                true
            } else {
                false
            }
        }catch (e: Exception) {
            Timber.e(e)
        }
        return false
    }

//    suspend fun addReport(reportDay: ReportDay): Boolean {
//        try {
//            val answer = service.addReport("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", reportDay)
//            return if (answer.isSuccessful) {
//                Timber.d("OK! + ${answer.body()?.totalMoney}")
//                true
//            } else {
//                false
//            }
//        }catch (e: Exception) {
//            Timber.d(e)
//        }
//        return false
//    }

    suspend fun sendDayReport(dayReport: DayReport): Boolean {
        try {
            val answer = service.sendTotalReport("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", dayReport)
            return if (answer.isSuccessful) {
                Timber.d("${answer.body()?.date}")
                true
            } else false
        }catch (e: Exception) {
            Timber.e(e)
        }
        return false
    }
}