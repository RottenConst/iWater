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
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
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

    val ordersList = mutableListOf<Order>() //загруженные заказы
    private val orderDao: OrderDao = IWaterDB.orderDao() //обьект бд


    /**
     * сохранить загруженые заказы в бд
     */
    suspend fun saveOrders(orders: List<Order>) {
        for (order in orders) {
            orderDao.save(order)
        }
    }

    //проверка на дублирование
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
                if (orderDb.date != UtilsMethods.getTodayDateString()) deleteOrder(orderDb)
            }
        }
        if (orders.isEmpty()) {
            for (orderDb in ordersDb) {
                deleteOrder(orderDb)
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
        for (order in orders) {
            if (order.status == 0) {
                currentOrder.add(order)
                TimeNotification.notifycationOrders.notifyOrders.add(
                    NotifyOrder(
                        order.id, order.timeEnd, order.date, order.address,
                        notification = false,
                        fail = false
                    )
                )
            }
        }
        return currentOrder
    }

    /**
     * возвращает выполненые заказы за сегодня
     */
    fun getCompleteOrder(): List<Order> {
        val orders = ordersList
        val completeOrder = mutableListOf<Order>()
        orders.sortBy { order -> order.timeEnd }
        orders.asReversed()
        for (order in orders) {
            if (order.status == 1) {
                completeOrder.add(order)
            }
        }
        return completeOrder
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
    suspend fun getDBOrderOnId(id: Int?): Order = withContext(Dispatchers.Default) {
        return@withContext orderDao.getOrderOnId(id)
    }

    /**
     * вернуть все не доставленные заказы из бд
     */
    suspend fun getDBLoadCurrentOrder(date: String): List<Order> = withContext(Dispatchers.Default) {
        return@withContext orderDao.getLoadCurrentOrder(date)
    }

    /**
     * получить фактический адресс
     */
    suspend fun getFactAddress(): List<String> {
        val answer = orderCurrent.getFactAddress()
        val infoCurrent = mutableListOf<String>()
        for (i in 0 until answer.propertyCount / 3) {
            val contact = if (answer.getPropertyAsString(0).equals("anyType{}")) "" else answer.getPropertyAsString(0)
            val factAddress = if (answer.getPropertyAsString(1).equals("anyType{}")) "" else answer.getPropertyAsString(1)
            infoCurrent.add(contact)
            infoCurrent.add(factAddress)
        }
        return infoCurrent
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
            val name = if (answer.getPropertyAsString(element + 1).equals("anyType")) "" else answer.getPropertyAsString(element + 1)
            val product = if (answer.getPropertyAsString(element + 2).equals("anyType{}")) "" else answer.getPropertyAsString(element + 2)
            val cash = if (answer.getPropertyAsString(element + 3).equals("anyType{}")) 0.0F else if (answer.getPropertyAsString(element + 3) == "NaN") 0.0F else answer.getPropertyAsString(element + 3).toFloat()
            val cash_b = if (answer.getPropertyAsString(element + 4).equals("anyType{}")) 0.0F else if (answer.getPropertyAsString(element + 4) == "NaN") 0.0F else answer.getPropertyAsString(element + 4).toFloat()
            val time = if (answer.getPropertyAsString(element + 5).equals("anyType{}")) "00:00-00:00".split("-") else answer.getPropertyAsString(element + 5).split("-")
            val contact = if (answer.getPropertyAsString(element + 6).equals("anyType{}")) "" else answer.getPropertyAsString(element + 6)
            val notice = if (answer.getPropertyAsString(element + 7).equals("anyType{}")) "" else answer.getPropertyAsString(element + 7)
            val date = if (answer.getPropertyAsString(element + 8).equals("anyType{}")) "" else answer.getPropertyAsString(element + 8)
            val period = if (answer.getPropertyAsString(element + 9).equals("anyType{}")) "" else answer.getPropertyAsString(element + 9)
            val address = if (answer.getPropertyAsString(element + 10).equals("anyType{}")) "" else answer.getPropertyAsString(element + 10)
            val coordinates = if (answer.getPropertyAsString(element + 11).equals("anyType{}")) "0.0,0.0".split(",") else answer.getPropertyAsString(element + 11).split(",")
            val status = answer.getPropertyAsString(element + 12).toIntOrNull()
            ordersList.add(
                Order(
                    id ?: 0,
                    name,
                    product,
                    cash,
                    cash_b,
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