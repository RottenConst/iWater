package ru.iwater.youwater.iwaterlogistic.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import ru.iwater.youwater.iwaterlogistic.Receivers.TimeNotification
import timber.log.Timber

class TimeListenerService : Service() {

    private var timeNotification //отслеживание системного времени
            : TimeNotification = TimeNotification()

    override fun onCreate() {}

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d( "10000000000000000000000000000000000")
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_TIME_TICK)
        registerReceiver(timeNotification, intentFilter)
        Timber.d("START SERVICE")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Timber.d("STOP SERVICE")
        unregisterReceiver(timeNotification)
    }
}