


package ru.iwater.youwater.iwaterlogistic.base

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import ru.iwater.youwater.iwaterlogistic.BuildConfig
import ru.iwater.youwater.iwaterlogistic.di.AccountStorageModule
import ru.iwater.youwater.iwaterlogistic.di.ContextModule
import ru.iwater.youwater.iwaterlogistic.di.DataBaseModule
import ru.iwater.youwater.iwaterlogistic.di.components.AppComponent
import ru.iwater.youwater.iwaterlogistic.di.components.DaggerAppComponent
import ru.iwater.youwater.iwaterlogistic.di.components.DaggerScreenComponent
import ru.iwater.youwater.iwaterlogistic.di.components.ScreenComponent
import ru.iwater.youwater.iwaterlogistic.util.ReleaseTree
import timber.log.Timber

/**
 * Базовый класс приложения.
 **/
const val CHANNEL_SERVICE_ID = "ru.iwater.logistic.service"

class App : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }

        createNotificationChanel()
        initAppComponent()
    }

    private fun initAppComponent() {
        appComponent = DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .dataBaseModule(DataBaseModule())
            .accountStorageModule(AccountStorageModule())
            .build()
    }

    private fun createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChanel = NotificationChannel(
                CHANNEL_SERVICE_ID,
                "Service Notification Chanel",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChanel)
        }
    }

    fun buildScreenComponent(): ScreenComponent {
        return DaggerScreenComponent.builder()
            .appComponent(appComponent)
            .build()
    }
}