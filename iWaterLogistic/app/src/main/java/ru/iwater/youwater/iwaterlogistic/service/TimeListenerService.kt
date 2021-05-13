package ru.iwater.youwater.iwaterlogistic.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.Receivers.TimeNotification
import ru.iwater.youwater.iwaterlogistic.base.CHANEL_SERVICE_ID
import timber.log.Timber

class TimeListenerService : Service() {
    val chanel = "ru.iwater.service"
    private var timeNotification //отслеживание системного времени
            : TimeNotification = TimeNotification()

    override fun onCreate() {
        val builder = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANEL_SERVICE_ID)
                .setSmallIcon(R.drawable.ic_notification_small)
        } else {
            Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_small)
        }

        val notification = builder.build()
        startForeground(7, notification)
//        val hideIntent = Intent(this, HideNotificationService::class.java)
    }

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