package ru.iwater.youwater.iwaterlogistic.repository

import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.iwater.youwater.iwaterlogistic.BuildConfig
import ru.iwater.youwater.iwaterlogistic.bd.IWaterDB
import ru.iwater.youwater.iwaterlogistic.bd.OrderNewItemDao
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.domain.*
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
    private val orderDao: OrderNewItemDao = IWaterDB.orderNewItemDao() //обьект бд


    /**
     * сохранить заказы в бд
     */
    suspend fun saveOrders(orders: List<OrderNewItem>) {
        orderDao.saveAll(orders)
    }

    /**
     * сохранить заказ в бд
     */
    suspend fun saveOrder(order: OrderNewItem) {
        orderDao.save(order)
    }

    suspend fun updateOrder(order: OrderNewItem){
        orderDao.update(order)
    }

    suspend fun deleteOrder(order: OrderNewItem) = withContext(Dispatchers.Default) {
        orderDao.delete(order)
    }

    /**
     * вернуть заказы из бд
     */
    suspend fun getDBOrders(): List<OrderNewItem> = withContext(Dispatchers.Default){
        return@withContext orderDao.load()
    }

    suspend fun getUpdateDBNum(order: OrderNewItem) {
        orderDao.updateNum(order.num, order.order_id)
    }

    suspend fun updateDBLocation(order: OrderNewItem, location: String?) {
        orderDao.updateLocation(location, order.order_id)
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

    suspend fun getAddressBottle(clientId: Int?, address: String): ClientInfo? {
        val infoClient = JsonObject()
        infoClient.addProperty("client_id", clientId)
        infoClient.addProperty("address", address)
        try {
            val data = service.getClientInfo("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", infoClient)
            if (data?.message != "success")
            return data?.data?.get(0)
        } catch (e: Exception) {
            Timber.e(e)
        }
        return ClientInfo("", 0)
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
    suspend fun getDBOrderOnId(id: Int): OrderNewItem = withContext(Dispatchers.Default) {
        return@withContext orderDao.getOrderOnId(id)
    }

    suspend fun getLoadNotCurrentOrder(session: String): List<Int> {
        val currentOrders: List<OrderNewItem>
        try {
            currentOrders = service.getDriverOrders("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", session)
            if (!currentOrders.isNullOrEmpty()) {
                return currentOrders.filter { it.status == 2 }.map { it.order_id }
            }
        }catch (e: Exception) {
            Timber.e(e)
            return emptyList()
        }
        return emptyList()
    }

    suspend fun getLoadOrder(session: String): List<OrderNewItem> {
        var currentOrders: List<OrderNewItem> = emptyList()
        try {
            currentOrders = service.getDriverOrders("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", session)
            if (!currentOrders.isNullOrEmpty()) {
                var num = 0
                return currentOrders.sortedBy { order ->
                    order.time.split("-").last()
                }.filter { it.status != 2}.map {
                    num++
                    it.num += num
                    OrderNewItem(
                        address = it.address,
                        cash = it.cash,
                        cash_b = it.cash_b,
                        contact = it.contact,
                        date = it.date,
                        mobile = it.mobile,
                        name = it.name,
                        notice = it.notice,
                        order = it.order,
                        order_id = it.order_id,
                        period = it.period,
                        status = it.status,
                        time = it.time,
                        type = it.type,
                        coords = it.coords,
                        num = if (it.type == "1") "П.${num}" else num.toString()
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
            BuildConfig.GOOGLE_MAPS_API_KEY
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