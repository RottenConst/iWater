package ru.iwater.youwater.iwaterlogistic.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.iwater.youwater.iwaterlogistic.bd.CompleteOrderDao
import ru.iwater.youwater.iwaterlogistic.bd.IWaterDB
import ru.iwater.youwater.iwaterlogistic.domain.CompleteOrder
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


    /**
     * сохранить выполненный заказ в базу
     */
   suspend fun saveCompleteOrder(completeOrder: CompleteOrder) {
        completeOrderDao.save(completeOrder)
    }

    suspend fun deleteCompleteOrder(completeOrder: CompleteOrder) {
        completeOrderDao.delete(completeOrder)
    }

    /**
     * Получить выполненный заказ из базы по id
     */
    suspend fun getCompleteOrder(id: Int?): CompleteOrder = withContext(Dispatchers.Default){
        return@withContext completeOrderDao.getCompleteOrderById(id)
    }

    /**
     * Получить все выполненные заказы из базы за выбранную дату
     */
    suspend fun getCompleteListOrders(date: String): List<CompleteOrder> = withContext(Dispatchers.Default) {
        return@withContext completeOrderDao.load(date)
    }

    suspend fun getAllCompleteOrders():List<CompleteOrder> = withContext(Dispatchers.Default) {
        return@withContext completeOrderDao.loadAll()
    }

    /**
     * Получить колличество выполненных заказов за выбранную дату
     */
    suspend fun getCountCompleteOrder(date: String): Int = withContext(Dispatchers.Default) {
        return@withContext completeOrderDao.getCountCompleteOrder(date)
    }

    /**
     * Получить сумму деннег из таблицы выполненных заказов полученных определённым образом
     */
    suspend fun getSumCashCompleteOrder(typeOfCash: String, date: String): Float = withContext(Dispatchers.Default) {
        return@withContext completeOrderDao.getSumCashOf(typeOfCash, date)
    }

    /**
     * Получить сумму деннег из таблицы выполненных заказов
     */
    suspend fun getSumCashFullCompleteOrder(date: String): Float = withContext(Dispatchers.Default) {
        return@withContext completeOrderDao.getSumCashFull(date)
    }

    /**
     * Получить сумму забранных пустых бутелей у клиентов из таблицы выполненных заказов
     */
    suspend fun getTankCompleteOrder(date: String): Int = withContext(Dispatchers.Default) {
        return@withContext completeOrderDao.getTankOfOrders(date)
    }

    suspend fun getAccept() {
        accept.acceptOrder()
    }

}