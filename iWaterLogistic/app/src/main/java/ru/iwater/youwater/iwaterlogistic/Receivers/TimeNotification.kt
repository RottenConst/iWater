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
import ru.iwater.youwater.iwaterlogistic.util.HelpNotification
import ru.iwater.youwater.iwaterlogistic.util.NotificationSender
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
import timber.log.Timber

class TimeNotification : BroadcastReceiver() {

    private lateinit var orderListRepository: OrderListRepository
    private lateinit var accountRepository: AccountRepository
    private val iWaterDB = App.appComponent.dataBase()

    object notifycationOrders {
        val notifyOrders = mutableListOf<NotifyOrder>()
        val isNotify = mutableListOf<Int>()
        val failList = mutableListOf<Int>()
        var coutNotifycation = 0
    }


    private lateinit var ordersNet: List<Order>
    private lateinit var dbOrders: List<Order>

    lateinit var notificationSender: NotificationSender

    override fun onReceive(context: Context?, intent: Intent?) {
        orderListRepository = OrderListRepository(iWaterDB)
        accountRepository = AccountRepository(App.appComponent.accountStorage())
        notificationSender = NotificationSender(context)

        CoroutineScope(Dispatchers.Main).launch {
            orderListRepository.getLoadCurrentOrders(accountRepository.getAccount().session)
            dbOrders = orderListRepository.getDBOrders()
            ordersNet = orderListRepository.ordersList
            notifycationOrders.notifyOrders.clear()
            for (order in ordersNet) {
                notifycationOrders.notifyOrders.add(
                    NotifyOrder(
                        order.id, order.time.split("-")[1], order.address,
                        notification = false,
                        fail = false
                    )
                )
            }
//            Timber.d("Notification order ${notifycationOrders.notifyOrders.size}")
//            Timber.d("Order net ${ordersNet.size}, OrderDB ${dbOrders.size}")
            if (ordersNet.size > dbOrders.size) {
                for (orderNet in ordersNet) {
                    for (dbOrder in dbOrders) {
                        if (dbOrder.id != orderNet.id) {
                            notificationSender.sendNotification(
                                "Появились новые заказы, пожалуйста обновите список заказов",
                                ordersNet.size + 100,
                                false
                            )
                        }
                    }
                }

            }
        }

//        if (UtilsMethods.timeDifference("20:00", UtilsMethods.getFormatedDate()) < 0 && notifycationOrders.coutNotifycation != 3) {
//            notificationSender.sendNotification("По завершению всех заказов не забудьте закончить день и отправить отчет" , ordersNet.size + 200, false)
//            notifycationOrders.coutNotifycation++
//        }

        Timber.d(" OrderDB ${notifycationOrders.notifyOrders.size}")
        for (dbOrder in notifycationOrders.notifyOrders) {
            for (notify in notifycationOrders.isNotify) {
                if (notify == dbOrder.id) dbOrder.notification = true
            }
            for (fail in notifycationOrders.failList) {
                if (fail == dbOrder.id) dbOrder.fail = true
            }
            if (dbOrder.timeEnd.isNotEmpty()) {
                Timber.d("${dbOrder.id} ${dbOrder.fail}")
                if (UtilsMethods.timeDifference(dbOrder.timeEnd, UtilsMethods.getFormatedDate()) in 1..3600 && !dbOrder.notification) {
                    notificationSender.sendNotification("Через 1 час истекает заказ ${dbOrder.address}" , dbOrder.id, false)
                    notifycationOrders.isNotify.add(dbOrder.id)
                } else if (UtilsMethods.timeDifference(dbOrder.timeEnd, UtilsMethods.getFormatedDate()) < 0 && !dbOrder.fail) {
                    notificationSender.sendNotification("Время истекло. Адрес:  ${dbOrder.address}" , dbOrder.id, false)
                    notifycationOrders.failList.add(dbOrder.id)
                }
            }
        }
    }

}