package ru.iwater.youwater.iwaterlogistic.domain.vm

//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.SupervisorJob
//import kotlinx.coroutines.launch
//import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
//import ru.iwater.youwater.iwaterlogistic.domain.Order
//import ru.iwater.youwater.iwaterlogistic.repository.OrderListRepository
//import javax.inject.Inject

//@OnScreen
//class InfoOrderViewModel @Inject constructor(
//    private val orderListRepository: OrderListRepository,
//) : ViewModel() {
//
//    /**
//     * короутины дял асинхронных задач
//     */
//    private val viewModelJob = SupervisorJob()
//    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
//
//    private var orderInfo: Order = Order()
//    private val mOrder: MutableLiveData<Order> = MutableLiveData()
//
//    val order: LiveData<Order>
//        get() = mOrder
//
//    /**
//     * возвращает заказ по id
//     **/
////    fun getOrderInfo(id: Int?) {
////        orderListRepository.orderCurrent.setProperty(id)
////        uiScope.launch {
////            val infoAddress = orderListRepository.getFactAddress()
////            orderInfo = orderListRepository.getDBOrderOnId(id)
////            orderInfo.address = "${infoAddress[1]}; \n${infoAddress[0]}"
////            mOrder.value = orderInfo
////        }
////    }
//
//    /**
//     * сохранить заказ
//     **/
//    fun saveOrder() {
//        uiScope.launch {
//            orderListRepository.saveOrder(orderInfo)
//        }
//    }
//
//    /**
//     * парсит в моссив телефоны клиента
//     **/
//    fun getPhoneNumberClient(): Array<String> {
//        var contacts = mutableListOf<String>()
//        if (orderInfo.contact.isNotEmpty()) {
//            contacts = when {
//                orderInfo.contact.contains(";") -> {
//                    orderInfo.contact.split(";") as MutableList<String>
//                }
//                orderInfo.contact.contains(",") -> {
//                    orderInfo.contact.split(",") as MutableList<String>
//                }
//                else -> {
//                    arrayListOf(orderInfo.contact)
//                }
//            }
//        }
//        return contacts.toTypedArray()
//    }
//
//    /**
//     * закрываем и отменяем корутины
//     */
//    override fun onCleared() {
//        super.onCleared()
//        viewModelJob.cancel()
//    }
//}