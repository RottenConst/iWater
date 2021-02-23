package ru.iwater.youwater.iwaterlogistic.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import timber.log.Timber

/**
 * Класс для логирования жизненных циклов Activity, Fragment
 **/
class Logger : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun logEvent(owner: LifecycleOwner, event: Lifecycle.Event) {
        val message = "${owner.javaClass.simpleName} $event"
        Timber.d(message)
    }
}