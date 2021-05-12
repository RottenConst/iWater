package ru.iwater.youwater.iwaterlogistic.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.iwater.youwater.iwaterlogistic.bd.*
import ru.iwater.youwater.iwaterlogistic.domain.Expenses
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.domain.ReportDay
import timber.log.Timber
import javax.inject.Inject

//class ReportRepository @Inject constructor(
////    iWaterDB: IWaterDB
//){
//
////    val addExpenses = AddExpenses()
////    val driverCloseDay = DriverCloseDay()
//
////    private val expensesDao: ExpensesDao = iWaterDB.ExpensesDao()
////    private val orderDao: OrderDao = iWaterDB.orderDao()
////    private val reportDayDao: ReportDayDao = iWaterDB.reportDayDao()
//
//    /**
//     * сохранить отчет в бд
//     */
//    suspend fun saveReport(reportDay: ReportDay) {
////        reportDayDao.save(reportDay)
//    }
//
//    /**
//     * загрузить отчет из бд за указанную дату
//     */
////    suspend fun loadReportFromDB(date: String): ReportDay = withContext(Dispatchers.Default) {
//////        return@withContext reportDayDao.loadReportDays(date)
////    }
//
//    /**
//     * загрузить все отчеты
//     */
////    suspend fun loadAllReport(): List<ReportDay> = withContext(Dispatchers.Default) {
//////        return@withContext reportDayDao.loadAllReports()
////    }
//
//    /**
//     * удалить отчет
//     */
//    suspend fun deleteReport(reportDay: ReportDay) = withContext(Dispatchers.Default) {
////        reportDayDao.delete(reportDay)
//    }
//
//    /**
//     * получить сумму расходов за сегодняшний день
//     */
////    suspend fun getSumOfCostExpenses(date: String): Float = withContext(Dispatchers.Default) {
//////        return@withContext expensesDao.sumCost(date)
////    }
//
//    /**
//     * сохранить расход
//     */
//    suspend fun saveExpenses(expenses: Expenses) {
////        expensesDao.save(expenses)
//    }
//
//    /**
//     * загрузить расходы за текущий день
//     */
////    suspend fun loadExpenses(date: String) : List<Expenses> = withContext(Dispatchers.Default) {
////        return@withContext expensesDao.load(date)
////    }
//
//    /**
//     * вернуть не завершенные заказы из бд
//     */
////    suspend fun getDBOrders(): List<Order> = withContext(Dispatchers.Default){
////        return@withContext orderDao.load()
////    }
//
//    /**
//     * узнать остались еще активные заказы
//     */
////    suspend fun getSumCurrentOrder(): Boolean {
//////        val order = getDBOrders().size
//////        Timber.d("$order")
//////        return order <= 0
////    }
//}