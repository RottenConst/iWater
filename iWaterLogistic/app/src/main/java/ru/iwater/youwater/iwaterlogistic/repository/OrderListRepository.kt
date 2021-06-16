package ru.iwater.youwater.iwaterlogistic.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.iwater.youwater.iwaterlogistic.bd.IWaterDB
import ru.iwater.youwater.iwaterlogistic.bd.OrderDao
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.domain.OrderInfo
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
//    private val productDao: ProductDao = IWaterDB.productDao()


    /**
     * сохранить загруженые заказы в бд
     */
//    suspend fun saveOrders(orders: List<Order>) {
//        for (order in orders) {
////            orderDao.save(order)
//        }
//    }

    //проверка на дублирование
//    suspend fun checkDbOrder() {
//        val ordersDb = getDBOrders()
//        val orders = getOrders()
//        if (orders.size < ordersDb.size) {
//            for (orderDb in ordersDb) {
//                for (order in orders) {
//                    if (order.id != orderDb.id) {
//                        deleteOrder(orderDb)
//                    }
//                }
//                if (orderDb.date != UtilsMethods.getTodayDateString()) deleteOrder(orderDb)
//            }
//        }
//        if (orders.isEmpty()) {
//            for (orderDb in ordersDb) {
//                deleteOrder(orderDb)
//            }
//        }
//    }

    /**
     * сохранить заказы в бд
     */
    suspend fun saveOrders(orders: List<Order>) {
        orderDao.save(orders)
    }

    /**
     * вернуть информацию о загруженых заказах
     */
//    fun getOrders(): List<Order> {
//        val orders = ordersList
//        val currentOrder = mutableListOf<Order>()
//        orders.sortBy { order -> order.timeEnd }
//        orders.asReversed()
//        TimeNotification.notifycationOrders.notifyOrders.clear()
//        for (order in orders) {
//            if (order.status == 0) {
//                currentOrder.add(order)
//                TimeNotification.notifycationOrders.notifyOrders.add(
//                    NotifyOrder(
//                        order.id, order.timeEnd, order.date, order.address,
//                        notification = false,
//                        fail = false
//                    )
//                )
//            }
//        }
//        return currentOrder
//    }

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
        while (iterator.hasNext()) {
            val order = iterator.next()
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

    suspend fun updateStatus(order: Order){
        orderDao.update(order)
    }

    suspend fun deleteOrder(order: Order) = withContext(Dispatchers.Default) {
//        orderDao.delete(order)
    }

    /**
     * вернуть заказы из бд
     */
//    suspend fun getDBOrders(): List<Order> = withContext(Dispatchers.Default){
////        return@withContext orderDao.load()
//    }

    /**
     * вернуть заказы из бд по id
     */
    suspend fun getDBOrderOnId(id: Int?): Order = withContext(Dispatchers.Default) {
        return@withContext orderDao.getOrderOnId(id)
    }

    /**
     * вернуть все не доставленные заказы из бд
     */
//    suspend fun getDBLoadCurrentOrder(date: String): List<Order> = withContext(Dispatchers.Default) {
////        return@withContext orderDao.getLoadCurrentOrder(date)
//    }

    /**
     * получить фактический адресс
     */
//    suspend fun getFactAddress(): List<String> {
//        val answer = orderCurrent.getFactAddress()
//        val infoCurrent = mutableListOf<String>()
//        for (i in 0 until answer.propertyCount / 3) {
//            val contact = if (answer.getPropertyAsString(0).equals("anyType{}")) "" else answer.getPropertyAsString(0)
//            val factAddress = if (answer.getPropertyAsString(1).equals("anyType{}")) "" else answer.getPropertyAsString(1)
//            infoCurrent.add(contact)
//            infoCurrent.add(factAddress)
//        }
//        return infoCurrent
//    }

    suspend fun getLoadCurrentOrders(session: String) {
        val answer = service.getDriverOrders(session)
        try {
            if (answer.isSuccessful) {
                ordersList.clear()
                ordersList.addAll(answer.body()!!)
                if (!ordersList.isNullOrEmpty()) {
                    checkCompleteOrder()
                    saveOrders(ordersList)
                }
            }
        } catch (e: HttpException) {
            Timber.d(e.message())
        }

    }

    suspend fun getLoadOrderInfo(orderId: Int?): OrderInfo? {
       val answer = service.getOrderInfo(orderId)
        try {
            if (answer.isSuccessful) {
                return answer.body()
            }
        }catch (e: HttpException) {
            Timber.e(e.message())
        }
        return null
    }

    suspend fun getTypeClient(clientId: Int?): String? {
        val answer = service.getTypeClient(clientId)
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