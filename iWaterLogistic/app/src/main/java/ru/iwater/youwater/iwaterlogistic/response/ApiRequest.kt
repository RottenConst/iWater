package ru.iwater.youwater.iwaterlogistic.response

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.iwater.youwater.iwaterlogistic.domain.*
import ru.iwater.youwater.iwaterlogistic.domain.mapdata.MapData

interface ApiRequest {

    /**
     * авторризация
     */
    @POST("logistic/auth/{login}/{company}/{password}/{notification}/")
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
    @GET("logistic/drivers_orders_instructions/{session}")
    suspend fun getDriverOrders(
        @Header("X-Authorization") key: String,
        @Path("session") session: String
    ): List<WaterOrder>

    /**
     * получить подробную информацию о заказе по его id
     */
    @GET("logistic/iwaterOrders_detail/{id}/")
    suspend fun getOrderInfo(
        @Header("X-Authorization") key: String,
        @Path("id") idOrder: Int?
    ): OrderInfoNew

    /*
        получить тип клиента по id заказа
     */
    @GET("logistic/getTypeClient/{id}/")
    suspend fun getTypeClient(
        @Header("X-Authorization") key: String,
        @Path("id") idClient: Int?
    ): List<ClientType>

    /*
        отправить отчет по конкретному заказу
     */
    @Headers( "Content-Type: application/json" )
    @POST("logistic/iwaterDcontrol_list/")
    suspend fun reportOrderInsert(
         @Header("X-Authorization") key: String,
         @Body request: JsonObject
    ): Response<JsonObject>

    /*
        обновить статус заказа
    */
    @Headers( "Content-Type: application/json" )
    @POST("logistic/update-status/{id}/")
    suspend fun updateStatus(
        @Header("X-Authorization") key: String,
        @Path("id") id: Int?,
    ): AnswerUpdateStatus

    /**
     * Отправить расход
     */
    @Headers( "Content-Type: application/json" )
    @POST("logistic/iwaterExpenses_list/")
    suspend fun addExpenses(
        @Header("X-Authorization") key: String,
        @Body request: Expenses
    ): Response<Expenses>

    @Headers( "Content-Type: application/json" )
    @POST("logistic/iwaterReportApp_list/")
    suspend fun addReport(
        @Header("X-Authorization") key: String,
        @Body request: ReportOrder
    ) : ReportDay?

    @Headers( "Content-Type: application/json" )
    @POST("logistic/iwaterDriverCloseDay_list/")
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

    @Multipart
    @POST("photo/UploadZorder/")
    suspend fun sendZReport(
        @Header("X-Authorization") key: String,
        @Part id: MultipartBody.Part,
        @Part date: MultipartBody.Part,
        @Part name: MultipartBody.Part,
        @Part image: MultipartBody.Part
    )

    @Multipart
    @POST("photo/Upload_cheques/")
    suspend fun sendPhotoExpenses(
        @Header("X-Authorization") key: String,
        @Part id: MultipartBody.Part,
        @Part date: MultipartBody.Part,
        @Part name: MultipartBody.Part,
        @Part image: MultipartBody.Part
    )

    @Headers( "Content-Type: application/json" )
    @POST("logistic/open_work_shift/")
    suspend fun openWorkShift(
        @Header("X-Authorization") key: String,
        @Body driverShift: OpenDriverShift
    ): Response<JsonObject>

    @Headers( "Content-Type: application/json" )
    @POST("logistic/close_work_shift/")
    suspend fun closeWorkShift(
        @Header("X-Authorization") key: String,
        @Body closeDriverShift: CloseDriverShift
    ): Response<JsonObject>

    @Headers( "Content-Type: application/json" )
    @GET("logistic/open_work_shift/")
    suspend fun getWorkShift(
        @Header("X-Authorization") key: String,
    ): List<OpenDriverShift?>

    @Headers( "Content-Type: application/json" )
    @POST("logistic/spare_item/")
    suspend fun getAdvancedProduct(
        @Header("X-Authorization") key: String,
        @Body id: JsonObject
    ): Response<AdvancedProduct>

    @PUT("logistic/Get_the_container_dept/")
    suspend fun getContainerDept(
        @Header("X-Authorization") key: String,
        @Body returnTare: JsonObject
    ): Response<JsonObject>

    @POST("logistic/pick_up_empty_bottles/")
    suspend fun getClientInfo(
        @Header("X-Authorization") key: String,
        @Body returnTare: JsonObject
    ): Data?

    @GET("logistic/data_report_app/{id}/")
    suspend fun getReport(
        @Header("X-Authorization") key: String,
        @Path("id") idOrder: Int?
    ): ReportOrder?

    @GET("logistic/check_shift_closure/{id_driver}")
    suspend fun checkShiftDriver(
        @Header("X-Authorization") key: String,
        @Path("id_driver") idDriver: Int
    ):String?

    @POST("logistic/products_of_the_day")
    suspend fun getProduct(
        @Header("X-Authorization") key: String,
        @Body driverInfo: JsonObject
    ): List<Product>
}