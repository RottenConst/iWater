package ru.iwater.youwater.iwaterlogistic.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.iwater.youwater.iwaterlogistic.bd.IWaterDB
import ru.iwater.youwater.iwaterlogistic.bd.OrderDao
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.response.DriverWayBill
import javax.inject.Inject

class OrderListRepository @Inject constructor(
    IWaterDB: IWaterDB
)
{
    private val orderDao: OrderDao = IWaterDB.orderDao()

    val driverWayBill = DriverWayBill()

    suspend fun saveOrders() {
        val orders = getLoadOrderList()
        for (order in orders) {
            orderDao.save(order)
        }
    }

    suspend fun getOrders(): List<Order> = withContext(Dispatchers.Default){
        return@withContext orderDao.load()
    }

    private suspend fun getLoadOrderList(): List<Order> {
        val answer = driverWayBill.loadOrders()
        val ordersList = mutableListOf<Order>()
        var element = 0

        for (i in 0 until answer.propertyCount / 13) {
            val id = answer.getPropertyAsString(element).toIntOrNull()
            val name = answer.getPropertyAsString(element + 1) ?: ""
            val product = answer.getPropertyAsString(element + 2) ?: ""
            val cash = answer.getPropertyAsString(element + 3).toFloatOrNull()
            val cash_b = answer.getPropertyAsString(element + 4).toFloatOrNull()
            val time = answer.getPropertyAsString(element + 5).split("-")
            val contact = answer.getPropertyAsString(element + 6) ?: ""
            val notice = if (answer.getPropertyAsString(element + 7).equals("anyType{}")) "" else answer.getPropertyAsString(element + 7)
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

        return ordersList
    }
}