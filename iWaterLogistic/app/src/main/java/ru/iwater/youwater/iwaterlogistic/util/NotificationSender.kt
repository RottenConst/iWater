package ru.iwater.youwater.iwaterlogistic.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import ru.iwater.youwater.iwaterlogistic.BuildConfig
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.screens.main.MainActivity

class NotificationSender(private val context: Context?) {
    var notificationIntent: Intent = Intent(context, MainActivity::class.java)
    var notificationManager: NotificationManager? = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    var notificationChannel: NotificationChannel? = null
    var contentIntent: PendingIntent = PendingIntent.getActivity(
        context,
        0,
        notificationIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    fun sendNotification(text: String?, NOTIFY_ID: Int, isNotify: Boolean) {
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANEL_ID)
        } else {
            Notification.Builder(context)
        }
        builder.setContentIntent(contentIntent)
            .setSmallIcon(R.drawable.ic_notification_small) //                    .setColor(context.getResources().getColor(R.color.colorPrimary))
            .setContentText(text) // Текст уведомления
            .setContentTitle(context?.resources?.getString(R.string.app_name))
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context?.resources,
                    R.drawable.ic_launcher_square
                )
            )
            .setWhen(System.currentTimeMillis())
            .setStyle(Notification.BigTextStyle().bigText(text))
            .setAutoCancel(true) // автоматически закрыть уведомление после нажатия
        val notification = builder.build()
        if (BuildConfig.DEBUG && notificationManager == null) {
            error("Assertion failed")
        }
        if (isNotify) {
            notificationManager!!.cancel(NOTIFY_ID)
        } else {
            notificationManager!!.notify(NOTIFY_ID, notification)
        }
    }

    fun createChanelIfNeeded(manager: NotificationManager?, ChanelID: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(ChanelID, ChanelID, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel!!.enableLights(true)
            notificationChannel!!.lightColor = Color.GREEN
            notificationChannel!!.enableVibration(true)
            manager!!.createNotificationChannel(notificationChannel!!)
        }
    }

    companion object {
        private const val CHANEL_ID = "ru.iwather.yourwater.notification"
    }

    init {
        createChanelIfNeeded(notificationManager, CHANEL_ID)
    }
}