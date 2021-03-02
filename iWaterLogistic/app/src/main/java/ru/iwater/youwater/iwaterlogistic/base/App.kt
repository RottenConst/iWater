package ru.iwater.youwater.iwaterlogistic.base

import android.app.Application
import ru.iwater.youwater.iwaterlogistic.di.AccountStorageModule
import ru.iwater.youwater.iwaterlogistic.di.ContextModule
import ru.iwater.youwater.iwaterlogistic.di.DataBaseModule
import ru.iwater.youwater.iwaterlogistic.di.components.AppComponent
import ru.iwater.youwater.iwaterlogistic.di.components.DaggerAppComponent
import ru.iwater.youwater.iwaterlogistic.di.components.DaggerScreenComponent
import ru.iwater.youwater.iwaterlogistic.di.components.ScreenComponent
import timber.log.Timber

/**
 * Базовый класс приложения.
 **/
class App : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        initAppComponent()
    }

    private fun initAppComponent() {
        appComponent = DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .dataBaseModule(DataBaseModule())
            .accountStorageModule(AccountStorageModule())
            .build()
    }

    fun buildScreenComponent(): ScreenComponent {
        return DaggerScreenComponent.builder()
            .appComponent(appComponent)
            .build()
    }
}