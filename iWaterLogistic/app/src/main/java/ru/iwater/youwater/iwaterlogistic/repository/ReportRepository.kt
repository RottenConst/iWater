package ru.iwater.youwater.iwaterlogistic.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import ru.iwater.youwater.iwaterlogistic.bd.*
import ru.iwater.youwater.iwaterlogistic.domain.*
import ru.iwater.youwater.iwaterlogistic.response.ApiRequest
import ru.iwater.youwater.iwaterlogistic.response.RetrofitFactory
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class ReportRepository @Inject constructor(
    iWaterDB: IWaterDB
){
    val service: ApiRequest = RetrofitFactory.makeRetrofit()

    private val expensesDao: ExpensesDao = iWaterDB.ExpensesDao()
    private val orderDao: WaterOrderDao = iWaterDB.waterOrderDao()
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

    fun deleteExpenses(expenses: Expenses) {
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
    suspend fun getDBOrders(): List<WaterOrder> = withContext(Dispatchers.Default){
        return@withContext orderDao.load()
    }

    fun deleteOrder(waterOrder: WaterOrder) {
        orderDao.delete(waterOrder)
    }

    suspend fun getLoadTotalOrder(session: String): List<WaterOrder> {
        var currentOrders: List<WaterOrder> = emptyList()
        try {
            currentOrders = service.getDriverOrders("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", session)
            if (!currentOrders.isNullOrEmpty()) {
                return currentOrders.filter { it.status == 2 || it.status == 0 }
            }
        }catch (e: Exception) {
            Timber.e(e)
            return currentOrders
        }
        return currentOrders
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

    suspend fun sendPhoto(id: MultipartBody.Part, date: MultipartBody.Part, name: MultipartBody.Part, image: MultipartBody.Part) {
        try {
            service.sendZReport("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", id, date, name, image)
        }catch (e: Exception) {
            Timber.e(e)
        }
    }

    suspend fun sendExpensesPhoto(id: MultipartBody.Part, date: MultipartBody.Part, name: MultipartBody.Part, image: MultipartBody.Part): Boolean {
        try {
            service.sendPhotoExpenses("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", id, date, name, image)
            return true
        }catch (e: Exception) {
            Timber.e(e)
        }
        return false
    }

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

    suspend fun closeDriverShift(closeDriverShift: CloseDriverShift): Boolean {
        try {
            val answer = service.closeWorkShift("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", closeDriverShift)
            if (answer.isSuccessful) {
                if ("The shift is already close or shift not find" == answer.body()?.get("message")?.asString || "Status close shift sent" == answer.body()?.get("message")?.asString)
                 return true
            } else return false
        }catch (e: Exception) {
            Timber.e(e)
        }
        return false
    }

    suspend fun getOpenLastShiftDay(): String? {
        try {
            val list = service.getWorkShift("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX")
            val session = list.filter { !it?.session.isNullOrEmpty() }
            if (!session.isNullOrEmpty()) {
                return session.last()?.date
            }
        }catch (e: Exception) {
            Timber.e(e)
        }
        return ""
    }
}