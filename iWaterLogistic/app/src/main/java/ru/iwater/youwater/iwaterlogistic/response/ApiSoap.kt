package ru.iwater.youwater.iwaterlogistic.response

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ksoap2.HeaderProperty
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpResponseException
import org.ksoap2.transport.HttpTransportSE
import timber.log.Timber

const val URL = "http://dev.iwatercrm.ru/iwater_api/driver/server.php?wsdl"

/**
 * базоаый класс для связи с api
 **/
interface DescriptionApi {
    val SOAP_ACTION: String
    val METHOD_NAME: String
    val NAME_SPACE: String
    val request: SoapObject
    var soapEnvelope: SoapSerializationEnvelope
    val httpTransport: HttpTransportSE

    fun getSoapEnvelop(request: SoapObject): SoapSerializationEnvelope {
        val soapEnvelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        soapEnvelope.setOutputSoapObject(request)
        return soapEnvelope
    }

    fun getHttpTransport(): List<HeaderProperty> = listOf(HeaderProperty("Accept-Encoding", "none"))

    fun getRequest(nameSpace: String, methodName: String): SoapObject =
        SoapObject(nameSpace, methodName)
}

/**
 * Класс для связи api soap
 * принимает код компании, логин, пароль, нотифткацию(время входа)
 **/
class Authorisation(
    company: String,
    login: String,
    password: String,
    notification: String
) : DescriptionApi {
    override val SOAP_ACTION: String = "urn:authuser#auth"
    override val METHOD_NAME: String = "auth"
    override val NAME_SPACE: String = "urn:authuser"
    override val request: SoapObject = getRequest(NAME_SPACE, METHOD_NAME)
    override lateinit var soapEnvelope: SoapSerializationEnvelope
    override val httpTransport: HttpTransportSE = HttpTransportSE(URL, 3000)

    init {
        request.addProperty("company", company)
        request.addProperty("login", login)
        request.addProperty("password", password)
        request.addProperty("notification", notification)
        soapEnvelope = getSoapEnvelop(request)
    }

    /**
     * метод для авторизации - возвращаут код ошибки(1, 0) сессию и id водителя при успешной авторизации
     **/
    suspend fun auth(): Pair<Int, String> = withContext(Dispatchers.Default) {
        try {
            httpTransport.call(SOAP_ACTION, soapEnvelope, getHttpTransport())
            val answer = soapEnvelope.response.toString().split(",")
            val error = answer[0].replace("[", "").toInt()
            val message = answer[1].replace("]", "")
            Timber.d("$error, $message")
            return@withContext Pair(error, message)
        } catch (e: HttpResponseException) {
            Timber.e(e.fillInStackTrace())
        }
        return@withContext Pair(1, "")
    }
}

/**
 * Класс для связи api soap
 * принимает сессию водителя
 **/
class DriverWayBill(
    var session: String = ""
) : DescriptionApi {
    override val SOAP_ACTION: String = "urn:info#testlist"
    override val METHOD_NAME: String = "testwaybill"
    override val NAME_SPACE: String = "urn:info"
    override val request: SoapObject = getRequest(NAME_SPACE, METHOD_NAME)
    override lateinit var soapEnvelope: SoapSerializationEnvelope
    override val httpTransport: HttpTransportSE = HttpTransportSE(URL)

    fun setProperty(session: String) {
        request.addProperty("session", session)
        soapEnvelope = getSoapEnvelop(request)
    }

    /**
     * принимает сессию водителя и возвращает информацию о заказах за текущий день
     **/
    suspend fun loadOrders(): SoapObject = withContext(Dispatchers.Default) {
        try {
            httpTransport.call(SOAP_ACTION, soapEnvelope, getHttpTransport())
            val answer = soapEnvelope.response as SoapObject
            return@withContext answer.getProperty(0) as SoapObject
        } catch (e: HttpResponseException) {
            Timber.e(e.fillInStackTrace(), "Status code ${e.statusCode}")
        }
        return@withContext SoapObject()
    }
}