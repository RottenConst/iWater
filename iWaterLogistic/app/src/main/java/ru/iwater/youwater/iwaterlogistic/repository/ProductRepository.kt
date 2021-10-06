package ru.iwater.youwater.iwaterlogistic.repository

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import ru.iwater.youwater.iwaterlogistic.bd.IWaterDB
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.domain.OpenDriverShift
import ru.iwater.youwater.iwaterlogistic.domain.Product
import ru.iwater.youwater.iwaterlogistic.response.ApiRequest
import ru.iwater.youwater.iwaterlogistic.response.RetrofitFactory
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@OnScreen
class ProductRepository @Inject constructor() {
    val service: ApiRequest = RetrofitFactory.makeRetrofit()

    suspend fun getLoadProductFromOrder(session: String): List<Product> {
        val products = mutableListOf<Product>()
        try {
            val orders = service.getDriverOrders("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", session)
            if (!orders.isNullOrEmpty()) {
                val currentProducts = orders.filter { it.status != 2}
                currentProducts.forEach {
                    products.addAll(it.products)
                }
                return products
            }
        }catch (e: Exception) {
            Timber.e(e)
            return emptyList()
        }
        return emptyList()
    }

    suspend fun getAdvancedProduct(id: Int): List<Product> {
        val product = mutableListOf<Product>()
        val driver = JsonObject()
        driver.addProperty("id", id)
        try {
            val load = service.getAdvancedProduct("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", driver)
            if (load.isSuccessful) {
                Timber.i("plk ${load.body()?.pln}, pln ${load.body()?.plk}")
                val pln = load.body()?.pln
                product.add(Product (pln!!, "pln"))
                product.add(Product(load.body()!!.plk, "plk"))
                return product
            }
        }catch (e: Exception) {
            Timber.e(e)
            return emptyList()
        }
        return emptyList()
    }

    suspend fun openDriverShift(driverShift: OpenDriverShift): String? {
        var message: String? = ""
        try {
            val answer = service.openWorkShift("3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX", driverShift)
            Timber.i(answer.body()?.get("message").toString())
            if (answer.isSuccessful) {
                message = answer.body()?.get("message")?.asString
                return message
            }
        } catch (e: Exception) {
            Timber.e(e)
            return "Error"
        }
        return message
    }
}