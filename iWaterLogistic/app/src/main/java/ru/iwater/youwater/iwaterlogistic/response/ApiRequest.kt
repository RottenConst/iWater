package ru.iwater.youwater.iwaterlogistic.response

import retrofit2.Response
import retrofit2.http.*
import ru.iwater.youwater.iwaterlogistic.domain.Account
import ru.iwater.youwater.iwaterlogistic.domain.Order

interface ApiRequest {

    @GET("auth/{login}/{company}/{password}/{notification}/")
    suspend fun authDriver(
        @Path("login") login: String,
        @Path("company") company: String,
        @Path("password") password: String,
        @Path("notification") notification: String
    ): Response<Account>

    @GET("getDriverList/{session}/")
    suspend fun getDriverOrders(
        @Path("session") session: String
    ): Response<List<Order>>
}