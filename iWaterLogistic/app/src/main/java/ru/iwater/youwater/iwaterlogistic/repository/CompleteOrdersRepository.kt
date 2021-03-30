package ru.iwater.youwater.iwaterlogistic.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.iwater.youwater.iwaterlogistic.bd.CompleteOrderDao
import ru.iwater.youwater.iwaterlogistic.bd.ExpensesDao
import ru.iwater.youwater.iwaterlogistic.bd.IWaterDB
import ru.iwater.youwater.iwaterlogistic.bd.OrderDao
import ru.iwater.youwater.iwaterlogistic.domain.CompleteOrder
import ru.iwater.youwater.iwaterlogistic.domain.Expenses
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.response.Accept
import ru.iwater.youwater.iwaterlogistic.response.ReportInsert
import timber.log.Timber
import javax.inject.Inject

class CompleteOrdersRepository @Inject constructor(
    iWaterDB: IWaterDB
) {

    val accept = Accept()
    val reportInsert = ReportInsert()

    private val completeOrderDao: CompleteOrderDao = iWaterDB.completeOrderDao()
    private val expensesDao: ExpensesDao = iWaterDB.ExpensesDao()
    private val orderDao: OrderDao = iWaterDB.orderDao()


   suspend fun saveCompleteOrder(completeOrder: CompleteOrder) {
        completeOrderDao.save(completeOrder)
    }

    suspend fun getCompleteOrder(id: Int): CompleteOrder = withContext(Dispatchers.Default){
        return@withContext completeOrderDao.getCompleteOrderById(id)
    }

    suspend fun getCompleteListOrders(date: String): List<CompleteOrder> = withContext(Dispatchers.Default) {
        return@withContext completeOrderDao.load(date)
    }

    suspend fun getCountCompleteOrder(date: String): Int = withContext(Dispatchers.Default) {
        return@withContext completeOrderDao.getCountCompleteOrder(date)
    }

    suspend fun getSumCashCompleteOrder(typeOfCash: String, date: String): Float = withContext(Dispatchers.Default) {
        return@withContext completeOrderDao.getSumCashOf(typeOfCash, date)
    }

    suspend fun getSumCashFullCompleteOrder(date: String): Float = withContext(Dispatchers.Default) {
        return@withContext completeOrderDao.getSumCashFull(date)
    }

    suspend fun getTankCompleteOrder(date: String): Int = withContext(Dispatchers.Default) {
        return@withContext completeOrderDao.getTankOfOrders(date)
    }

    suspend fun getSumOfCostExpenses(date: String): Float = withContext(Dispatchers.Default) {
        return@withContext expensesDao.sumCost(date)
    }

    suspend fun saveExpenses(expenses: Expenses) {
        expensesDao.save(expenses)
    }

    suspend fun loadExpenses(date: String) : List<Expenses> = withContext(Dispatchers.Default) {
        return@withContext expensesDao.load(date)
    }

    /**
     * вернуть не завершенные заказы из бд
     */
    suspend fun getDBOrders(): List<Order> = withContext(Dispatchers.Default){
        return@withContext orderDao.load()
    }

    suspend fun getSumCurrentOrder(): Boolean {
        val order = getDBOrders().size
        Timber.d("$order")
        if (order > 0) {
            return false
        } else return true
    }

    suspend fun getAccept() {
        accept.acceptOrder()
    }

}