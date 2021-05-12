package ru.iwater.youwater.iwaterlogistic.domain.vm

//import android.content.Context
//import android.content.Intent
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.SupervisorJob
//import kotlinx.coroutines.launch
//import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
//import ru.iwater.youwater.iwaterlogistic.domain.CompleteOrder
//import ru.iwater.youwater.iwaterlogistic.domain.Order
//import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
//import ru.iwater.youwater.iwaterlogistic.repository.CompleteOrdersRepository
//import ru.iwater.youwater.iwaterlogistic.repository.OrderListRepository
//import ru.iwater.youwater.iwaterlogistic.screens.main.tab.current.CompleteShipActivity
//import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
//import javax.inject.Inject

//@OnScreen
//class ShipmentsViewModel @Inject constructor(
//    private val orderListRepository: OrderListRepository,
//    private val completeOrdersRepository: CompleteOrdersRepository,
//    accountRepository: AccountRepository
//) : ViewModel() {
//
//    /**
//     * короутины дял асинхронных задач
//     */
//    private val viewModelJob = SupervisorJob()
//    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
//
//    private val mOrder: MutableLiveData<Order> = MutableLiveData()
//    private val mTypeClient: MutableLiveData<String> = MutableLiveData()
////    private var nameDriver: String = accountRepository.getAccount().login
////    private var company: String = accountRepository.getAccount().company
//
//    val typeClient: LiveData<String>
//        get() = mTypeClient
//
//    val order: LiveData<Order>
//        get() = mOrder
//
//    /**
//     * возвращает заказ по id
//     **/
//    fun getOrderInfo(id: Int?) {
//        uiScope.launch {
////            mOrder.value = orderListRepository.getDBOrderOnId(id)
//        }
//    }
//
////    fun setCompleteOrder(id: Int?, cash: Float, typeCash: String, tank: Int, timeComplete: String, noticeDriver: String, shipCoordinate: List<String>, shipCoord: String, context: Context?) {
////        if (id != null) {
////            completeOrdersRepository.accept.setProperty(id, tank, noticeDriver, shipCoord)
////        }
////        uiScope.launch {
////            val orderDelivered = completeOrdersRepository.getCompleteListOrders(UtilsMethods.getTodayDateString()).size
////            val totalMoney = completeOrdersRepository.getSumCashFullCompleteOrder(UtilsMethods.getTodayDateString())
////            completeOrdersRepository.reportInsert.setPropertyReport(nameDriver, id, typeClient.value, typeCash, cash, tank, orderDelivered, totalMoney, company)
////            val order = orderListRepository.getDBOrderOnId(id)
////            val completeOrder = CompleteOrder(
////                order.id,
////                order.name,
////                order.product,
////                cash, typeCash, tank,
////                order.timeStart,
////                order.timeEnd,
////                timeComplete,
////                order.contact,
////                order.notice,
////                noticeDriver,
////                order.date,
////                order.period,
////                order.address,
////                status = 1,
////                order.coordinates,
////                shipCoordinate )
////            val answer = completeOrdersRepository.accept.acceptOrder()
////            if (answer == "[0, Success.]") {
////                completeOrdersRepository.saveCompleteOrder(completeOrder)
////                completeOrdersRepository.reportInsert.sendReport()
////                orderListRepository.deleteOrder(order)
////            }
////            val intent = Intent(context, CompleteShipActivity::class.java)
////            intent.putExtra("id", id)
////            intent.putExtra("time", timeComplete)
////            intent.putExtra("answer", answer)
////            intent.putExtra("address", order.address)
////            CompleteShipActivity.start(context, intent)
////        }
////    }
//
////    fun getAcceptOrder(orderId: Int, tank: Int, comment: String, coordinate: String) {
////        completeOrdersRepository.accept.setProperty(orderId, tank, comment, coordinate)
////        uiScope.launch {
////            completeOrdersRepository.accept.acceptOrder()
////        }
////    }
//
//    /**
//     * запрашивает и устанавливает тип клиента
//     **/
////    fun getTypeClient(id: Int?) {
////        val typeClient = TypeClient()
////        typeClient.setProperty(id)
////        uiScope.launch {
////            when (typeClient.getTypeClient()) {
////                "0" -> {
////                    mTypeClient.value = "0"
////                }
////                "1" -> {
////                    mTypeClient.value = "1"
////                }
////                else -> {
////                    mTypeClient.value = "500"
////                }
////            }
////        }
////    }
//
//    /**
//     * закрываем и отменяем корутины
//     */
//    override fun onCleared() {
//        super.onCleared()
//        viewModelJob.cancel()
//    }
//}