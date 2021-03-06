package ru.test.internet.test

import android.util.Log
import okhttp3.HttpUrl

interface NetworkAccess {
    fun fetchData(httpUrlBuilder: HttpUrl.Builder, parameterName: String)
    fun terminate()
    fun logOut(message: String) {
        Log.d("Track", "$message ${Thread.currentThread()}")
    }
}