package ru.iwater.youwater.iwaterlogistic.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.iwater.youwater.iwaterlogistic.bd.CompleteOrderDao
import ru.iwater.youwater.iwaterlogistic.bd.IWaterDB
import ru.iwater.youwater.iwaterlogistic.domain.CompleteOrder
import ru.iwater.youwater.iwaterlogistic.response.Accept
import javax.inject.Inject

class CompleteOrdersRepository @Inject constructor(
    iWaterDB: IWaterDB
) {

    val accept = Accept()

    private val completeOrderDao: CompleteOrderDao = iWaterDB.completeOrderDao()


   suspend fun saveCompleteOrder(completeOrder: CompleteOrder) {
        completeOrderDao.save(completeOrder)
    }

    suspend fun getCompleteOrder(id: Int): CompleteOrder = withContext(Dispatchers.Default){
        return@withContext completeOrderDao.getCompleteOrderById(id)
    }

    suspend fun getCompleteListOrders(date: String): List<CompleteOrder> = withContext(Dispatchers.Default) {
        return@withContext completeOrderDao.load(date)
    }

    suspend fun getAccept() {
        accept.acceptOrder()
    }

}