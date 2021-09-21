package ru.iwater.youwater.iwaterlogistic.response

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitFactory {
    const val BASE_URL = "http://api.iwatercrm.ru/" //test
//    const val BASE_URL = "http://app.iwatercrm.ru/" //prod

    fun makeRetrofit(): ApiRequest {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor { chain ->
//                val request = chain.request().newBuilder().addHeader("X-Authorization", AUTH_KEY).build()
//                return@addInterceptor chain.proceed(request)
//            }
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()


        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiRequest::class.java)
    }
}