package ru.iwater.youwater.iwaterlogistic.response

//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import org.ksoap2.HeaderProperty
//import org.ksoap2.SoapEnvelope
//import org.ksoap2.serialization.SoapObject
//import org.ksoap2.serialization.SoapPrimitive
//import org.ksoap2.serialization.SoapSerializationEnvelope
//import org.ksoap2.transport.HttpResponseException
//import org.ksoap2.transport.HttpTransportSE
//import ru.iwater.youwater.iwaterlogistic.domain.ReportDay
//import timber.log.Timber
//
////const val URL = "http://dev.iwatercrm.ru/iwater_api/driver/server.php?wsdl" //test
//const val URL = "http://dev.iwatercrm.ru/iwater_logistic/driver/server.php?wsdl" //prod
//
///**
// * базоаый класс для связи с api
// **/
//interface DescriptionApi {
//    val SOAP_ACTION: String
//    val METHOD_NAME: String
//    val NAME_SPACE: String
//    val request: SoapObject
//    var soapEnvelope: SoapSerializationEnvelope
//    val httpTransport: HttpTransportSE
//
//    fun getSoapEnvelop(request: SoapObject): SoapSerializationEnvelope {
//        val soapEnvelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
//        soapEnvelope.setOutputSoapObject(request)
//        return soapEnvelope
//    }
//
//    fun getHttpTransport(): List<HeaderProperty> = listOf(HeaderProperty("Accept-Encoding", "none"))
//
//    fun getRequest(nameSpace: String, methodName: String): SoapObject =
//        SoapObject(nameSpace, methodName)
//}
//
///**
// * Класс для связи api soap
// * принимает код компании, логин, пароль, нотифткацию(время входа)
// **/
//class Authorisation(
//    company: String,
//    login: String,
//    password: String,
//    notification: String
//) : DescriptionApi {
//    override val SOAP_ACTION: String = "urn:authuser#auth"
//    override val METHOD_NAME: String = "auth"
//    override val NAME_SPACE: String = "urn:authuser"
//    override val request: SoapObject = getRequest(NAME_SPACE, METHOD_NAME)
//    override lateinit var soapEnvelope: SoapSerializationEnvelope
//    override val httpTransport: HttpTransportSE = HttpTransportSE(URL, 3000)
//
//    init {
//        request.addProperty("company", company)
//        request.addProperty("login", login)
//        request.addProperty("password", password)
//        request.addProperty("notification", notification)
//        soapEnvelope = getSoapEnvelop(request)
//    }
//
//    /**
//     * метод для авторизации - возвращаут код ошибки(1, 0) сессию и id водителя при успешной авторизации
//     **/
//    suspend fun auth(): Pair<Int, String> = withContext(Dispatchers.Default) {
//        try {
//            httpTransport.call(SOAP_ACTION, soapEnvelope, getHttpTransport())
//            val answer = soapEnvelope.response.toString().split(",")
//            val error = answer[0].replace("[", "").toInt()
//            val message = answer[1].replace("]", "")
//            Timber.d("$error, $message")
//            return@withContext Pair(error, message)
//        } catch (e: HttpResponseException) {
//            Timber.e(e.fillInStackTrace())
//        }
//        return@withContext Pair(1, "")
//    }
//}
//
///**
// * Класс для связи api soap
// * возвращает заказы за текущий деть
// **/
//class DriverWayBill : DescriptionApi {
//    override val SOAP_ACTION: String = "urn:info#testlist"
//    override val METHOD_NAME: String = "testwaybill"
//    override val NAME_SPACE: String = "urn:info"
//    override val request: SoapObject = getRequest(NAME_SPACE, METHOD_NAME)
//    override lateinit var soapEnvelope: SoapSerializationEnvelope
//    override val httpTransport: HttpTransportSE = HttpTransportSE(URL)
//
//    /**
//     * устанавливает сессию водителя в запрос
//     **/
//    fun setProperty(session: String) {
//        request.addProperty("session", session)
//        soapEnvelope = getSoapEnvelop(request)
//    }
//
//    /**
//     * возвращает информацию о заказах за текущий день
//     **/
//    suspend fun loadOrders(): SoapObject = withContext(Dispatchers.Default) {
//        try {
//            httpTransport.call(SOAP_ACTION, soapEnvelope, getHttpTransport())
//            val answer = soapEnvelope.response as SoapObject
//            return@withContext answer.getProperty(0) as SoapObject
//        } catch (e: Exception) {
//            Timber.e(e)
//        }
//        return@withContext SoapObject()
//    }
//}
//
///**
// * Класс для связи api soap
// * возвращает тип клиетеа заказа
// **/
//class TypeClient: DescriptionApi {
//    override val SOAP_ACTION: String = "urn:info#typeClient"
//    override val METHOD_NAME: String = "typeClient"
//    override val NAME_SPACE: String = "urn:info"
//    override val request: SoapObject = getRequest(NAME_SPACE, METHOD_NAME)
//    override lateinit var soapEnvelope: SoapSerializationEnvelope
//    override val httpTransport: HttpTransportSE = HttpTransportSE(URL)
//
//    /**
//     * устанавливает id заказа в запрос
//     **/
//    fun setProperty(idOrder: Int?) {
//        request.addProperty("id", idOrder)
//        soapEnvelope = getSoapEnvelop(request)
//    }
//
//    /**
//     * возвращает тип клиента заказа:
//     * 0 - физ.лицо;
//     * 1 - юр лицо;
//     **/
//    suspend fun getTypeClient(): String = withContext(Dispatchers.Default) {
//        try {
//            httpTransport.call(SOAP_ACTION, soapEnvelope, getHttpTransport())
//            val answer = soapEnvelope.response as SoapObject
//            Timber.d("type client ${answer.getProperty(0)}")
//
//            val type = answer.getProperty(0) as SoapObject
//            return@withContext type.getPropertyAsString("period")
//        } catch (e: HttpResponseException) {
//            Timber.e(e.fillInStackTrace(), "Status code ${e.statusCode}")
//        }
//        return@withContext ""
//    }
//}
//
//
///**
// * Класс для связи api soap
// * для отгрузки товара передачи параметров на сервер
// **/
//class Accept: DescriptionApi {
//    override val SOAP_ACTION: String = "urn:authuser#accept"
//    override val METHOD_NAME: String = "accept"
//    override val NAME_SPACE: String = "urn:authuser"
//    override val request: SoapObject = getRequest(NAME_SPACE, METHOD_NAME)
//    override lateinit var soapEnvelope: SoapSerializationEnvelope
//    override val httpTransport: HttpTransportSE = HttpTransportSE(URL)
//
//    /**
//     * устанавливает параметры заказа при отгрузки
//     **/
//    fun setProperty(id: Int, tank: Int, comment: String, coordinates: String) {
//        request.addProperty("id", id)
//        request.addProperty("tank", tank)
//        request.addProperty("coment", comment)
//        request.addProperty("coord", coordinates)
//        soapEnvelope = getSoapEnvelop(request)
//    }
//
//    suspend fun acceptOrder(): String = withContext(Dispatchers.Default) {
//        try {
//            httpTransport.call(SOAP_ACTION, soapEnvelope, getHttpTransport())
//            val answer = soapEnvelope.response.toString()
//            Timber.d(answer)
//            return@withContext answer
//        } catch (e: HttpResponseException) {
//            Timber.e(e.fillInStackTrace(), "Status code ${e.statusCode}")
//        }
//        return@withContext ""
//    }
//}
//
///**
// * Класс для связи api soap
// * для получения фактического адресса
// **/
//class OrderCurrent: DescriptionApi {
//    override val SOAP_ACTION: String = "urn:info#infoCurrent"
//    override val METHOD_NAME: String = "infoCurrent"
//    override val NAME_SPACE: String = "urn:info"
//    override val request: SoapObject = getRequest(NAME_SPACE, METHOD_NAME)
//    override lateinit var soapEnvelope: SoapSerializationEnvelope
//    override val httpTransport: HttpTransportSE = HttpTransportSE(URL)
//
//    fun setProperty(idOrder: Int?) {
//        request.addProperty("id", idOrder)
//        soapEnvelope = getSoapEnvelop(request)
//    }
//
//    suspend fun getFactAddress(): SoapObject = withContext(Dispatchers.Default) {
//        try {
//            httpTransport.call(SOAP_ACTION, soapEnvelope, getHttpTransport())
//            val answer = soapEnvelope.response as SoapObject
//            val factAddress = answer.getProperty(0) as SoapObject
//            Timber.d(factAddress.toString())
//            return@withContext factAddress
//        } catch (e: HttpResponseException) {
//            Timber.e(e.fillInStackTrace(), "Status code${e.statusCode}")
//        }
//        return@withContext SoapObject()
//    }
//}
//
///**
// * Класс для связи api soap
// * для отправки отчета конкретного заказа
// **/
//class ReportInsert: DescriptionApi {
//    override val SOAP_ACTION: String = "urn:info#reportInserts"
//    override val METHOD_NAME: String = "reportInserts"
//    override val NAME_SPACE: String = "urn:info"
//    override val request: SoapObject = getRequest(NAME_SPACE, METHOD_NAME)
//    override lateinit var soapEnvelope: SoapSerializationEnvelope
//    override val httpTransport: HttpTransportSE = HttpTransportSE(URL)
//
//    fun setPropertyReport(nameDriver: String, orderId: Int?, typeClient: String?, paymentType: String, payment: Float, numberContainers: Int, ordersDelivered: Int, totalMoney: Float, company: String) {
//        request.addProperty("name", nameDriver)
//        request.addProperty("order_id", orderId)
//        request.addProperty("type_client", typeClient)
//        request.addProperty("payment_type", paymentType)
//        request.addProperty("payment", payment.toString())
//        request.addProperty("number_containers", numberContainers)
//        request.addProperty("orders_delivered", ordersDelivered + 1)
//        request.addProperty("total_money", (totalMoney + payment).toString())
//        request.addProperty("company", company)
//        soapEnvelope = getSoapEnvelop(request)
//    }
//
//    suspend fun sendReport(): String = withContext(Dispatchers.Default) {
//        try {
//            httpTransport.call(SOAP_ACTION, soapEnvelope, getHttpTransport())
//            val answer = soapEnvelope.response.toString()
//            Timber.d("$answer")
//            return@withContext answer
//        } catch (e: HttpResponseException) {
//            Timber.e(e.fillInStackTrace(), "Status code${e.statusCode}")
//        }
//        return@withContext ""
//    }
//}
//
///**
// * Класс для связи api soap
// * для отправки расхода
// **/
//class AddExpenses: DescriptionApi {
//    override val SOAP_ACTION: String = "urn:info#expenses"
//    override val METHOD_NAME: String = "expenses"
//    override val NAME_SPACE: String = "urn:info"
//    override val request: SoapObject = getRequest(NAME_SPACE, METHOD_NAME)
//    override lateinit var soapEnvelope: SoapSerializationEnvelope
//    override val httpTransport: HttpTransportSE = HttpTransportSE(URL)
//
//    fun setPropertyExpenses(driverId: Int, nameExpenses: String, money: Float) {
//        request.addProperty("driver_id", driverId)
//        request.addProperty("expens", nameExpenses)
//        request.addProperty("money", money.toString())
//        soapEnvelope = getSoapEnvelop(request)
//    }
//
//    suspend fun sendExpenses(): String = withContext(Dispatchers.Default) {
//        try {
//            httpTransport.call(SOAP_ACTION, soapEnvelope, getHttpTransport())
//            val answer = soapEnvelope.response.toString()
//            Timber.d("$answer")
//            return@withContext answer
//        } catch (e: HttpResponseException) {
//            Timber.e(e.fillInStackTrace(), "Status code${e.statusCode}")
//        }
//        return@withContext ""
//    }
//}
//
///**
// * Класс для связи api soap
// * для отправки общего отчета
// **/
//class DriverCloseDay: DescriptionApi {
//    override val SOAP_ACTION: String = "urn:info#driverCloseday"
//    override val METHOD_NAME: String = "driverCloseday"
//    override val NAME_SPACE: String = "urn:info"
//    override val request: SoapObject = getRequest(NAME_SPACE, METHOD_NAME)
//    override lateinit var soapEnvelope: SoapSerializationEnvelope
//    override val httpTransport: HttpTransportSE = HttpTransportSE(URL)
//
//    fun setPropertyGeneralReport(driverId: Int, reportDay: ReportDay) {
//        request.addProperty("driver_id", driverId)
//        request.addProperty("taken_bottles", reportDay.tank)
//        request.addProperty("orders_completed", reportDay.orderComplete)
//        request.addProperty("money_site", reportDay.cashOnSite.toString())
//        request.addProperty("money_terminal", reportDay.cashOnTerminal.toString())
//        request.addProperty("money_cash", reportDay.cashMoney.toString())
//        request.addProperty("money_cash_b", reportDay.noCashMoney.toString())
//        request.addProperty("cash_delivery", reportDay.moneyDelivery.toString())
//        request.addProperty("cash_sum", (reportDay.cashMoney + reportDay.cashOnSite + reportDay.cashOnTerminal).toString())
//        request.addProperty("total_money", reportDay.totalMoney.toString())
//        soapEnvelope = getSoapEnvelop(request)
//    }
//
//    suspend fun sendGeneralReport(): String = withContext(Dispatchers.Default) {
//        try {
//            httpTransport.call(SOAP_ACTION, soapEnvelope, getHttpTransport())
//            val answer = soapEnvelope.response.toString()
//            Timber.d("$answer")
//            return@withContext answer
//        } catch (e: HttpResponseException) {
//            Timber.e(e.fillInStackTrace(), "Status code${e.statusCode}")
//        }
//        return@withContext ""
//    }
//}
//
//class MonitorDriverOpening: DescriptionApi {
//    override val SOAP_ACTION: String = "urn:info#monitor_driver_opening"
//    override val METHOD_NAME: String = "monitor_driver_opening"
//    override val NAME_SPACE: String = "urn:info"
//    override val request: SoapObject = getRequest(NAME_SPACE, METHOD_NAME)
//    override lateinit var soapEnvelope: SoapSerializationEnvelope
//    override val httpTransport: HttpTransportSE = HttpTransportSE(URL)
//
//    fun setMonitorDriverOpening(driverId: Int) {
//        request.addProperty("driver_id", driverId)
//        soapEnvelope = getSoapEnvelop(request)
//    }
//
//    suspend fun driverOpeningDay(): String = withContext(Dispatchers.Default) {
//        try {
//            httpTransport.call(SOAP_ACTION, soapEnvelope, getHttpTransport())
//            val answer = soapEnvelope.response
//            answer
//            Timber.d("${answer.toString()}")
//            return@withContext answer.toString()
//        } catch (e: HttpResponseException) {
//            Timber.e(e.fillInStackTrace(), "Status code${e.statusCode}")
//        }
//        return@withContext ""
//    }
//}
//
//
//class MonitorDriverDay: DescriptionApi {
//    override val SOAP_ACTION: String = "urn:info#get_monitor_driver"
//    override val METHOD_NAME: String = "get_monitor_driver"
//    override val NAME_SPACE: String = "urn:info"
//    override val request: SoapObject = getRequest(NAME_SPACE, METHOD_NAME)
//    override lateinit var soapEnvelope: SoapSerializationEnvelope
//    override val httpTransport: HttpTransportSE = HttpTransportSE(URL)
//
//    fun setMonitorDriverClose(driverId: Int) {
//        request.addProperty("id", driverId)
//        soapEnvelope = getSoapEnvelop(request)
//    }
//
//    suspend fun driverDay(): String = withContext(Dispatchers.Default) {
//        try {
//            httpTransport.call(SOAP_ACTION, soapEnvelope, getHttpTransport())
//            val answer = soapEnvelope.response
//            Timber.d("${answer}")
//            return@withContext answer.toString()
//        } catch (e: HttpResponseException) {
//            Timber.e(e.fillInStackTrace(), "Status code${e.statusCode}")
//        }
//        return@withContext ""
//    }
//}
//
//
//class DriverDayClose: DescriptionApi {
//    override val SOAP_ACTION: String = "urn:info#monitor_driver_close"
//    override val METHOD_NAME: String = "monitor_driver_close"
//    override val NAME_SPACE: String = "urn:info"
//    override val request: SoapObject = getRequest(NAME_SPACE, METHOD_NAME)
//    override lateinit var soapEnvelope: SoapSerializationEnvelope
//    override val httpTransport: HttpTransportSE = HttpTransportSE(URL)
//
//    fun setDriverDayClose(id: Int) {
//        request.addProperty("id", id)
//        soapEnvelope = getSoapEnvelop(request)
//    }
//
//    suspend fun driverDayClose(): String = withContext(Dispatchers.Default) {
//        try {
//            httpTransport.call(SOAP_ACTION, soapEnvelope, getHttpTransport())
//            val answer = soapEnvelope.response
//            Timber.d("${answer}")
//            return@withContext answer.toString()
//        } catch (e: HttpResponseException) {
//            Timber.e(e.fillInStackTrace(), "Status code${e.statusCode}")
//        }
//        return@withContext ""
//    }
//}