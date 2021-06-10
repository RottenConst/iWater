package ru.iwater.youwater.iwaterlogistic.response

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*
import ru.iwater.youwater.iwaterlogistic.domain.*

interface ApiRequest {

    /**
     * авторризация
     */
    @POST("auth/{login}/{company}/{password}/{notification}/")
    suspend fun authDriver(
        @Path("login") login: String,
        @Path("company") company: String,
        @Path("password") password: String,
        @Path("notification") notification: String
    ): Response<Account>

    /**
     * получить список заказов водителя по его сессии
     */
    @GET("getDriverList/{session}/")
    suspend fun getDriverOrders(
        @Path("session") session: String
    ): Response<List<Order>>

    /**
     * получить подробную информацию о заказе по его id
     */
    @GET("iwaterOrders_detail/{id}/")
    suspend fun getOrderInfo(
        @Path("id") idOrder: Int?
    ): Response<OrderInfo>

    @GET("/iwater/getTypeClient/{id}/")
    suspend fun getTypeClient(
        @Path("id") idClient: Int?
    ): Response<String>

    @Headers( "Content-Type: application/json" )
    @POST("iwaterDcontrol_list/")
    suspend fun reportOrderInsert(
         @Body request: DecontrolReport
    ): Response<JsonObject>

    @Headers( "Content-Type: application/json" )
    @POST("update-status/{id}/")
    suspend fun updateStatus(
        @Path("id") id: Int?,
    ): Response<AnswerUpdateStatus>
}