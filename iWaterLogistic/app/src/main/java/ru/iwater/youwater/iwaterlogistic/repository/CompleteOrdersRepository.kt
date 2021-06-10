package ru.iwater.youwater.iwaterlogistic.repository

import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import ru.iwater.youwater.iwaterlogistic.bd.CompleteOrderDao
import ru.iwater.youwater.iwaterlogistic.bd.IWaterDB
import ru.iwater.youwater.iwaterlogistic.domain.AnswerUpdateStatus

import ru.iwater.youwater.iwaterlogistic.domain.CompleteOrder
import ru.iwater.youwater.iwaterlogistic.domain.DecontrolReport
import ru.iwater.youwater.iwaterlogistic.response.ApiRequest
import ru.iwater.youwater.iwaterlogistic.response.RetrofitFactory
import timber.log.Timber
import javax.inject.Inject

class CompleteOrdersRepository @Inject constructor(
    iWaterDB: IWaterDB
) {

    private val completeOrderDao: CompleteOrderDao = iWaterDB.completeOrderDao()
    val service: ApiRequest = RetrofitFactory.makeRetrofit()

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
//    suspend fun getCompleteListOrders(date: String): List<CompleteOrder> = withContext(Dispatchers.Default) {
//        return@withContext completeOrderDao.load(date)
//    }

    suspend fun getAllCompleteOrders():List<CompleteOrder> = withContext(Dispatchers.Default) {
        return@withContext completeOrderDao.loadAll()
    }

    /**
     * Получить колличество выполненных заказов за выбранную дату
     */
//    suspend fun getCountCompleteOrder(date: String): Int = withContext(Dispatchers.Default) {
//        return@withContext completeOrderDao.getCountCompleteOrder(date)
//    }

    /**
     * Получить сумму деннег из таблицы выполненных заказов полученных определённым образом
     */
//    suspend fun getSumCashCompleteOrder(typeOfCash: String, date: String): Float = withContext(Dispatchers.Default) {
//        return@withContext completeOrderDao.getSumCashOf(typeOfCash, date)
//    }

    /**
     * Получить сумму деннег из таблицы выполненных заказов
     */
//    suspend fun getSumCashFullCompleteOrder(date: String): Float = withContext(Dispatchers.Default) {
//        return@withContext completeOrderDao.getSumCashFull(date)
//    }

    /**
     * Получить сумму забранных пустых бутелей у клиентов из таблицы выполненных заказов
     */
//    suspend fun getTankCompleteOrder(date: String): Int = withContext(Dispatchers.Default) {
//        return@withContext completeOrderDao.getTankOfOrders(date)
//    }

    suspend fun addReport(reportOrder: DecontrolReport) {
        val answer = service.reportOrderInsert(reportOrder)
        try {
            if (answer.isSuccessful) {
                Timber.d("${answer.code()}")
            }
        }catch (e: HttpException) {
            Timber.e(e.message())
        }
    }

    suspend fun updateStatusOrder(idOrder: Int?): AnswerUpdateStatus? {
        val answer = service.updateStatus(idOrder)
        try {
            if (answer.isSuccessful) {
                return answer.body()
            }
        } catch (e: HttpException) {
            Timber.e(e.message())
        }
        return AnswerUpdateStatus(0, 1, "не удалось обновить заказ")
    }

}