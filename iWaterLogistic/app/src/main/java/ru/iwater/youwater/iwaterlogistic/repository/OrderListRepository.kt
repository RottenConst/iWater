package ru.iwater.youwater.iwaterlogistic.repository

import android.widget.Toast
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ksoap2.serialization.SoapObject
import ru.iwater.youwater.iwaterlogistic.bd.IWaterDB
import ru.iwater.youwater.iwaterlogistic.bd.OrderDao
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.response.DriverWayBill
import timber.log.Timber
import javax.inject.Inject

@OnScreen
class OrderListRepository @Inject constructor(
    IWaterDB: IWaterDB
)
{
    val driverWayBill = DriverWayBill() //api запрос инфо о заказах

    private val ordersList = mutableListOf<Order>() //загруженные заказы
    private val orderDao: OrderDao = IWaterDB.orderDao() //обьект бд


    /**
     * сохранить загруженые заказы в бд
     */
    suspend fun saveOrders() {
        for (order in ordersList) {
            orderDao.save(order)
        }
    }

    /**
     * вернуть информацию о загруженых заказах
     */
    fun getOrders(): List<Order> = ordersList

    /**
     * обновить заказы в бд
     */
    fun updateOrder() {
        for (order in ordersList) {
            orderDao.update(order)
        }
    }

    /**
     * вернуть заказы из бд
     */
    suspend fun getDBOrders(): List<Order> = withContext(Dispatchers.Default){
        return@withContext orderDao.load()
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
            val contact = answer.getPropertyAsString(element + 6) ?: ""
            val notice = answer.getPropertyAsString(element + 7) ?: ""
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