package ru.iwater.youwater.iwaterlogistic.base

import android.app.Application
import timber.log.Timber

/**
 * Базовый класс приложения.
 **/
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}