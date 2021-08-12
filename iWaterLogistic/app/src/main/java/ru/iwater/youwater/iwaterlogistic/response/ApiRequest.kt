package ru.iwater.youwater.iwaterlogistic.response

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*
import ru.iwater.youwater.iwaterlogistic.domain.*
import ru.iwater.youwater.iwaterlogistic.domain.mapdata.MapData

interface ApiRequest {

    /**
     * авторризация
     */
    @POST("auth/{login}/{company}/{password}/{notification}/")
    suspend fun authDriver(
        @Header("X-Authorization") key: String,
        @Path("login") login: String,
        @Path("company") company: String,
        @Path("password") password: String,
        @Path("notification") notification: String
    ): Account?

    /**
     * получить список заказов водителя по его сессии
     */
    @GET("getDriverList/{session}/")
    suspend fun getDriverOrders(
        @Header("X-Authorization") key: String,
        @Path("session") session: String
    ): Response<List<Order>>

    @GET("getDriverList/{session}/")
    suspend fun getDriverOrders2(
        @Header("X-Authorization") key: String,
        @Path("session") session: String
    ): List<Order>

    /**
     * получить подробную информацию о заказе по его id
     */
    @GET("iwaterOrders_detail/{id}/")
    suspend fun getOrderInfo(
        @Header("X-Authorization") key: String,
        @Path("id") idOrder: Int?
    ): OrderInfo

    /*
        получить тип клиента по id заказа
     */
    @GET("/iwater/getTypeClient/{id}/")
    suspend fun getTypeClient(
        @Header("X-Authorization") key: String,
        @Path("id") idClient: Int?
    ): Response<String>

    /*
        отправить отчет по конкретному заказу
     */
    @Headers( "Content-Type: application/json" )
    @POST("iwaterDcontrol_list/")
    suspend fun reportOrderInsert(
         @Header("X-Authorization") key: String,
         @Body request: DecontrolReport
    ): Response<JsonObject>

    /*
        обновить статус заказа
     */
    @Headers( "Content-Type: application/json" )
    @POST("update-status/{id}/")
    suspend fun updateStatus(
        @Header("X-Authorization") key: String,
        @Path("id") id: Int?,
    ): Response<AnswerUpdateStatus>

    @Headers( "Content-Type: application/json" )
    @POST("iwaterExpenses_list/")
    suspend fun addExpenses(
        @Header("X-Authorization") key: String,
        @Body request: Expenses
    ): Response<Expenses>

    @Headers( "Content-Type: application/json" )
    @POST("iwaterReportApp_list/")
    suspend fun addReport(
        @Header("X-Authorization") key: String,
        @Body request: ReportDay
    ) : Response<ReportDay>

    @Headers( "Content-Type: application/json" )
    @POST("iwaterDriverCloseDay_list/")
    suspend fun sendTotalReport(
        @Header("X-Authorization") key: String,
        @Body request: DayReport
    ): Response<DayReport>

    @GET
    suspend fun getCoordinatesPlace(
        @Url url: String,
        @Query("query") address: String,
        @Query("key") key: String
    ): Response<MapData>
}