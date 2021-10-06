package ru.iwater.youwater.iwaterlogistic.repository

import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.iwater.youwater.iwaterlogistic.bd.CompleteOrderDao
import ru.iwater.youwater.iwaterlogistic.bd.IWaterDB
import ru.iwater.youwater.iwaterlogistic.domain.*

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
    suspend fun getCompleteListOrders(): List<CompleteOrder> = withContext(Dispatchers.Default) {
        return@withContext completeOrderDao.load()
    }

    suspend fun getAllCompleteOrders():List<CompleteOrder> = withContext(Dispatchers.Default) {
        return@withContext completeOrderDao.loadAll()
    }

    /**
     * Получить колличество выполненных заказов за выбранную дату
     */
    suspend fun getCountCompleteOrder(date: String): Int = withContext(Dispatchers.Default) {
        return@withContext completeOrderDao.getCountCompleteOrder()
    }

    /**
     * Получить сумму деннег из таблицы выполненных заказов полученных определённым образом
     */
    suspend fun getSumCashCompleteOrder(typeOfCash: String): Float = withContext(Dispatchers.Default) {
        return@withContext completeOrderDao.getSumCashOf(typeOfCash)
    }

    /**
     * Получить сумму деннег из таблицы выполненных заказов
     */
    suspend fun getSumCashFullCompleteOrder(date: String): Float = withContext(Dispatchers.Default) {
        return@withContext completeOrderDao.getSumCashFull()
    }

    /**
     * Получить сумму забранных пустых бутелей у клиентов из таблицы выполненных заказов
     */
    suspend fun getTankCompleteOrder(): Int = withContext(Dispatchers.Default) {
        return@withContext completeOrderDao.getTankOfOrders()
    }

    suspend fun getLoadCompleteOrder(session: String): List<Order> {
        val completeOrders: List<Order>
        try {
            completeOrders = service.getDriverOrders("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", session)
            if (!completeOrders.isNullOrEmpty()) {
                return completeOrders.filter { it.status == 2 }
            }
        }catch (e: Exception) {
            Timber.e(e)
            return emptyList()
        }
        return emptyList()
    }

    suspend fun getReportOrder(idOrder: Int): ReportOrder? {
        try {
            val reportOrder = service.getReport("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", idOrder)
            if (reportOrder != null) return reportOrder
        }catch (e: Exception) {
            Timber.e(e)
        }
        return null
    }

    suspend fun addDecontrol(decontrolReport: DecontrolReport) : Boolean {
        val report = JsonObject()
        report.addProperty("order_id", decontrolReport.order_id)
        report.addProperty("time", decontrolReport.time)
        report.addProperty("coord", decontrolReport.coord)
        report.addProperty("tank", decontrolReport.tank)
        report.addProperty("notice", decontrolReport.notice)
        try {
            val answer = service.reportOrderInsert("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", report)
            return if (answer.isSuccessful) {
                Timber.d("${answer.body()}")
                true
            } else {
                false
            }
        }catch (e: Exception) {
            Timber.e(e)
        }
        return false
    }

    suspend fun addReport(reportOrder: ReportOrder): Boolean {
        try {
            val answer = service.addReport("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", reportOrder)
            Timber.i("AAAAAAAAAAAAAAANSWER ${answer?.totalMoney}")
            if (answer != null) {
                return true
            }
        }catch (e: java.lang.Exception) {
            Timber.d(e)
        }
        return false
    }

    suspend fun updateStatusOrder(idOrder: Int?): AnswerUpdateStatus {
        try {
            return service.updateStatus("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", idOrder)
        } catch (e: Exception) {
            Timber.e(e)
        }
        return AnswerUpdateStatus(0, 1, "не удалось обновить заказ")
    }

}