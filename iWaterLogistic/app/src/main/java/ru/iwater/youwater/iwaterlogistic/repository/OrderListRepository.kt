package ru.iwater.youwater.iwaterlogistic.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.bd.IWaterDB
import ru.iwater.youwater.iwaterlogistic.bd.OrderDao
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.domain.OrderInfo
import ru.iwater.youwater.iwaterlogistic.domain.mapdata.MapData
import ru.iwater.youwater.iwaterlogistic.response.ApiRequest
import ru.iwater.youwater.iwaterlogistic.response.RetrofitFactory
import timber.log.Timber
import javax.inject.Inject

@OnScreen
class  OrderListRepository @Inject constructor(
    IWaterDB: IWaterDB
)
{
//    val driverWayBill = DriverWayBill() //api запрос инфо о заказах
//    val orderCurrent = OrderCurrent()

    val service: ApiRequest = RetrofitFactory.makeRetrofit()
    var ordersList = mutableListOf<Order>() //загруженные заказы
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
     * возвращает выполненые заказы за сегодня
     */
    fun getCompleteOrder(): List<Order> {
        val orders = ordersList
        val completeOrder = mutableListOf<Order>()
        orders.sortBy { order -> order.time }
        orders.asReversed()
        for (order in orders) {
            if (order.status == 2) {
                completeOrder.add(order)
            }
        }
        return completeOrder
    }

    private fun checkCompleteOrder() {
        val iterator = ordersList.iterator()
        var num = 0
        while (iterator.hasNext()) {
            val order = iterator.next()
            num += 1
            order.num = num
            if (order.status == 2) {
                iterator.remove()
            }
        }
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

    /**
     * вернуть заказы из бд по id
     */
    suspend fun getDBOrderOnId(id: Int): Order = withContext(Dispatchers.Default) {
        return@withContext orderDao.getOrderOnId(id)
    }

    suspend fun getLoadCurrentOrders(session: String) {
        val answer = service.getDriverOrders("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", session)
        try {
            if (answer.isSuccessful) {
                ordersList.clear()
                ordersList.addAll(answer.body()!!)
                if (!ordersList.isNullOrEmpty()) {
                    ordersList.sortBy { order -> order.time }
                    checkCompleteOrder()
                }
            }
        } catch (e: HttpException) {
            Timber.d(e.message())
        }

    }

    suspend fun getLoadOrderInfo(orderId: Int?): OrderInfo? {
       val answer = service.getOrderInfo("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", orderId)
        try {
            if (answer.isSuccessful) {
                return answer.body()
            }
        }catch (e: HttpException) {
            Timber.e(e.message())
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

    suspend fun getTypeClient(orderId: Int?): String? {
        val answer = service.getTypeClient("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", orderId)
        try {
            if (answer.isSuccessful) {
                Timber.d("${answer.body()?.length}")
                return answer.body()?.get(7).toString()
            }
        }catch (e: HttpException) {
            Timber.e(e.message())
        }
        return "500"
    }
}