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

class TimeNotification : BroadcastReceiver() {

    private lateinit var orderListRepository: OrderListRepository
    private lateinit var accountRepository: AccountRepository
    private val iWaterDB = App.appComponent.dataBase()

    object NotificationOrders {
        val notifyOrders = mutableListOf<NotifyOrder>()
        val isNotify = mutableListOf<Int>()
        val failList = mutableListOf<Int>()
    }


    private lateinit var ordersNet: List<Order>
    private lateinit var dbOrders: List<Order>

    lateinit var notificationSender: NotificationSender

    override fun onReceive(context: Context?, intent: Intent?) {
        orderListRepository = OrderListRepository(iWaterDB)
        accountRepository = AccountRepository(App.appComponent.accountStorage())
        notificationSender = NotificationSender(context)

        CoroutineScope(Dispatchers.Main).launch {
            dbOrders = orderListRepository.getDBOrders()
            ordersNet = orderListRepository.getLoadCurrentOrder(accountRepository.getAccount().session)
            NotificationOrders.notifyOrders.clear()
            for (order in ordersNet) {
                NotificationOrders.notifyOrders.add(
                    NotifyOrder(
                        order.id, order.time.split("-").last(), order.address,
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

//        if (UtilsMethods.timeDifference("20:00", UtilsMethods.getFormatedDate()) < 0 && NotificationOrders.countNotification != 3) {
//            notificationSender.sendNotification("По завершению всех заказов не забудьте закончить день и отправить отчет" , ordersNet.size + 200, false)
//            NotificationOrders.countNotification++
//        }

//        Timber.d(" OrderDB ${NotificationOrders.notifyOrders.size}")
        for (dbOrder in NotificationOrders.notifyOrders) {
            for (notify in NotificationOrders.isNotify) {
                if (notify == dbOrder.id) dbOrder.notification = true
            }
            for (fail in NotificationOrders.failList) {
                if (fail == dbOrder.id) dbOrder.fail = true
            }
            if (dbOrder.timeEnd.isNotEmpty()) {
                Timber.d("${dbOrder.id} ${dbOrder.fail}")
                if (UtilsMethods.timeDifference(dbOrder.timeEnd, UtilsMethods.getFormatedDate()) in 1..3600 && !dbOrder.notification) {
                    notificationSender.sendNotification("Через 1 час истекает заказ ${dbOrder.address}" , dbOrder.id, false)
                    NotificationOrders.isNotify.add(dbOrder.id)
                } else if (UtilsMethods.timeDifference(dbOrder.timeEnd, UtilsMethods.getFormatedDate()) < 0 && !dbOrder.fail) {
                    notificationSender.sendNotification("Время истекло. Адрес:  ${dbOrder.address}" , dbOrder.id, false)
                    NotificationOrders.failList.add(dbOrder.id)
                }
            }
        }
    }

}