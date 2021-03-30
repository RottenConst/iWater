package ru.iwater.youwater.iwaterlogistic.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.iwater.youwater.iwaterlogistic.Receivers.TimeNotification
import ru.iwater.youwater.iwaterlogistic.bd.IWaterDB
import ru.iwater.youwater.iwaterlogistic.bd.OrderDao
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.domain.NotifyOrder
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.response.DriverWayBill
import ru.iwater.youwater.iwaterlogistic.response.OrderCurrent
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@OnScreen
class  OrderListRepository @Inject constructor(
    IWaterDB: IWaterDB
)
{
    val driverWayBill = DriverWayBill() //api запрос инфо о заказах
    val orderCurrent = OrderCurrent()

    private val ordersList = mutableListOf<Order>() //загруженные заказы
    private val orderDao: OrderDao = IWaterDB.orderDao() //обьект бд


    /**
     * сохранить загруженые заказы в бд
     */
    suspend fun saveOrders(orders: List<Order>) {
        for (order in orders) {
            orderDao.save(order)
        }
    }

    suspend fun checkDbOrder() {
        val ordersDb = getDBOrders()
        val orders = getOrders()
        if (orders.size < ordersDb.size) {
            for (orderDb in ordersDb) {
                for (order in orders) {
                    if (order.id != orderDb.id) {
                        deleteOrder(orderDb)
                    }
                }
            }
        }
    }

    /**
     * сохранить заказ в бд
     */
    suspend fun saveOrder(order: Order) {
        orderDao.save(order)
    }

    /**
     * вернуть информацию о загруженых заказах
     */
    fun getOrders(): List<Order> {
        val orders = ordersList
        val currentOrder = mutableListOf<Order>()
        orders.sortBy { order -> order.timeEnd }
        orders.asReversed()
        TimeNotification.notifycationOrders.notifyOrders.clear()
        for (i in 0 until orders.size - 1) {
            if (orders[i].id != orders[i + 1].id && orders[i].status == 0) {
                currentOrder.add(orders[i])
                TimeNotification.notifycationOrders.notifyOrders.add(
                    NotifyOrder(
                        orders[i].id, orders[i].timeEnd, orders[i].date, orders[i].address,
                        notification = false,
                        fail = false
                    )
                )
            }
        }
        return currentOrder
    }

    /**
     * обновить заказы в бд
     */
    fun updateOrder() {
        for (order in ordersList) {
            orderDao.update(order)
        }
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

    /**
     * вернуть заказы из бд по id
     */
    suspend fun getDBOrderOnId(id: Int): Order = withContext(Dispatchers.Default) {
        return@withContext orderDao.getOrderOnId(id)
    }

    /**
     * вернуть все не доставленные заказы из бд
     */
    suspend fun getDBLoadCurrentOrder(date: String): List<Order> = withContext(Dispatchers.Default) {
        return@withContext orderDao.getLoadCurrentOrder(date)
    }

    suspend fun getFactAddress(): String {
        return orderCurrent.getFactAddress()
    }

    /**
     * запрос информации о текущих заказах
     */
    suspend fun getLoadOrderList() {
        val answer = driverWayBill.loadOrders()
        ordersList.clear()
        var element = 0
        Timber.d("${answer.propertyCount}")
        for (i in 0 until answer.propertyCount / 13) {
            val id = answer.getPropertyAsString(element).toIntOrNull()
            val name = answer.getPropertyAsString(element + 1) ?: ""
            val product = answer.getPropertyAsString(element + 2) ?: ""
            val cash = answer.getPropertyAsString(element + 3).toFloatOrNull()
            val cash_b = answer.getPropertyAsString(element + 4).toFloatOrNull()
            val time = answer.getPropertyAsString(element + 5).split("-")
            val contact = if (answer.getPropertyAsString(element + 6).equals("anyType{}")) "" else answer.getPropertyAsString(
                element + 6
            )
            val notice = if (answer.getPropertyAsString(element + 7).equals("anyType{}")) "" else answer.getPropertyAsString(
                element + 7
            )
            val date = answer.getPropertyAsString(element + 8) ?: ""
            val period = answer.getPropertyAsString(element + 9) ?: ""
            val address = answer.getPropertyAsString(element + 10) ?: ""
            val coordinates = answer.getPropertyAsString(element + 11).split(",")
            val status = answer.getPropertyAsString(element + 12).toIntOrNull()
            ordersList.add(
                Order(
                    id ?: 0,
                    name,
                    product,
                    cash ?: 0.00F,
                    cash_b ?: 0.00F,
                    time[0],
                    time[1],
                    contact,
                    notice,
                    date,
                    period,
                    address,
                    status ?: 0,
                    coordinates
                )
            )
            element += 13
        }
    }
}