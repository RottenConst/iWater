package ru.iwater.youwater.iwaterlogistic.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.domain.NotifyOrder
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.repository.OrderListRepository
import ru.iwater.youwater.iwaterlogistic.util.NotificationSender
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat

class TimeNotification : BroadcastReceiver() {

    private lateinit var orderListRepository: OrderListRepository
    private lateinit var accountRepository: AccountRepository
    private val iWaterDB = App.appComponent.dataBase()

    object notifycationOrders {
        val notifyOrders = mutableListOf<NotifyOrder>()
        val isNotify = mutableListOf<Int>()
        val failList = mutableListOf<Int>()
    }


    var orderNet = 0
    private var dbOrders = 0

    lateinit var notificationSender: NotificationSender

    override fun onReceive(context: Context?, intent: Intent?) {
        orderListRepository = OrderListRepository(iWaterDB)
        accountRepository = AccountRepository(App.appComponent.accountStorage())
        notificationSender = NotificationSender(context)
        orderListRepository.driverWayBill.setProperty(accountRepository.getAccount().session)
        CoroutineScope(Dispatchers.Main).launch {
            orderListRepository.getLoadOrderList()
            dbOrders = orderListRepository.getDBOrders().size
            orderNet = orderListRepository.getOrders().size
        }

        if (orderNet > dbOrders) {
            notificationSender.sendNotification(
                "Появились новые заказы, пожалуйста обновите список заказов",
                orderNet + 100,
                false
            )
        }
        Timber.d("Order net $orderNet, OrderDB ${notifycationOrders.notifyOrders.size}")
        for (dbOrder in notifycationOrders.notifyOrders) {
            for (notify in notifycationOrders.isNotify) {
                if (notify == dbOrder.id) dbOrder.notification = true
            }
            for (fail in notifycationOrders.failList) {
                if (fail == dbOrder.id) dbOrder.fail = true
            }
            if (dbOrder.timeEnd.isNotEmpty()) {
                val formatedDate = dbOrder.date.replace("\\s+".toRegex(), "").split("/")
                Timber.d("${dbOrder.id} ${dbOrder.fail}")
                if (UtilsMethods.timeDifference(dbOrder.timeEnd, formatedDate) in 1..3600 && !dbOrder.notification) {
                    notificationSender.sendNotification("Через 1 час истекает заказ ${dbOrder.address}" , dbOrder.id, false)
                    notifycationOrders.isNotify.add(dbOrder.id)
                } else if (UtilsMethods.timeDifference(dbOrder.timeEnd, formatedDate) < 0 && !dbOrder.fail) {
                    notificationSender.sendNotification("Время истекло. Адрес:  ${dbOrder.address}" , dbOrder.id, false)
                    notifycationOrders.failList.add(dbOrder.id)
                }
            }
        }
    }

}