package ru.iwater.youwater.iwaterlogistic.repository

import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.iwater.youwater.iwaterlogistic.bd.IWaterDB
import ru.iwater.youwater.iwaterlogistic.bd.OrderDao
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.domain.OpenDriverShift
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.domain.OrderInfo
import ru.iwater.youwater.iwaterlogistic.domain.mapdata.MapData
import ru.iwater.youwater.iwaterlogistic.response.ApiRequest
import ru.iwater.youwater.iwaterlogistic.response.RetrofitFactory
import timber.log.Timber
import javax.inject.Inject
import kotlin.Exception

@OnScreen
class  OrderListRepository @Inject constructor(
    IWaterDB: IWaterDB
)
{
    val service: ApiRequest = RetrofitFactory.makeRetrofit()
    private val orderDao: OrderDao = IWaterDB.orderDao() //обьект бд


    /**
     * сохранить заказы в бд
     */
    suspend fun saveOrders(orders: List<Order>) {
        orderDao.saveAll(orders)
    }

    /**
     * сохранить заказ в бд
     */
    suspend fun saveOrder(order: Order) {
        orderDao.save(order)
    }


    /**
     * обновить заказы в бд
     */
//    fun updateOrder() {
//        for (order in ordersList) {
//            orderDao.update(order)
//        }
//    }

    suspend fun updateOrder(order: Order){
        orderDao.update(order)
    }

    suspend fun deleteOrder(order: Order) = withContext(Dispatchers.Default) {
        orderDao.delete(order)
    }

    /**
     * вернуть заказы из бд
     */
    suspend fun getDBOrders(): List<Order> = withContext(Dispatchers.Default){
        return@withContext orderDao.load()
    }

    suspend fun getUpdateDBNum(order: Order) {
        orderDao.updateNum(order.num, order.id)
    }

    suspend fun getOpenDriverShift(openDriverShift: OpenDriverShift): Boolean {
        try {
            val list = service.getWorkShift("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX")
            val session = list.filter { it?.driverId == openDriverShift.driverId && it.date == openDriverShift.date }
            if (session.isNullOrEmpty() || session[0]?.session != null) {
                return true
            }
        }catch (e: Exception) {
            Timber.e(e)
        }
        return false
    }

    suspend fun setEmptyBottle(clientId: Int, returnTare: Int, orderId_id: Int, address: String): Boolean {
        val emptyBottle = JsonObject()
        emptyBottle.addProperty("client_id", clientId)
        emptyBottle.addProperty("return_tare", returnTare)
        emptyBottle.addProperty("order_id", orderId_id)
        emptyBottle.addProperty("address", address)
        try {
            val answer = service.getContainerDept("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", emptyBottle)
            return answer.isSuccessful
        } catch (e: Exception) {
            Timber.e(e)
        }
        return false
    }

    /**
     * вернуть заказы из бд по id
     */
    suspend fun getDBOrderOnId(id: Int): Order = withContext(Dispatchers.Default) {
        return@withContext orderDao.getOrderOnId(id)
    }

    suspend fun getLoadCurrentOrder(session: String): List<Order> {
        var currentOrders: List<Order> = emptyList()
        try {
            currentOrders = service.getDriverOrders("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", session)
            if (!currentOrders.isNullOrEmpty()) {
                var num = 0
                currentOrders.sortedBy { order -> order.time }
                return currentOrders.filter { it.status != 2 }.map {
                    num++
                    it.num += num
                    Order(
                        address = it.address,
                        cash = it.cash,
                        cash_b = it.cash_b,
                        contact = it.contact,
                        name = it.name,
                        notice = it.notice,
                        products = it.products,
                        id = it.id,
                        period = it.period,
                        status = it.status,
                        time = it.time,
                        location = it.location,
                        num = it.num
                    )
                }
            }
        }catch (e: Exception) {
            Timber.e(e)
            return currentOrders
        }
        return currentOrders
    }

    suspend fun getLoadOrderInfo(orderId: Int?): OrderInfo? {
        try {
            return service.getOrderInfo("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", orderId)
        }catch (e: Exception) {
            Timber.e(e)
        }
        return null
    }

    suspend fun getCoordinates(address: String): MapData? {
        val answer = service.getCoordinatesPlace(
            "https://maps.googleapis.com/maps/api/place/textsearch/json",
            address,
            "AIzaSyCOfJNzyHVWg8Ru0naTMQrbP9ECERZokTg"
        )
        try {
            if (answer.isSuccessful) {
                Timber.d("${answer.body()?.status}")
                return answer.body()
            }
        } catch (e: HttpException) {
            Timber.e(e.message())
        }
        return null
    }

    suspend fun getTypeClient(orderId: Int?): Int? {
        try {
            val answer = service.getTypeClient("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", orderId)
            return answer.first().type
        }catch (e: Exception) {
            Timber.e(e)
        }
        return 500
    }
}