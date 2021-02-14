package ru.iwater.youwater.iwaterlogistic.response

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ksoap2.HeaderProperty
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE

const val URL = "http://dev.iwatercrm.ru/iwater_api/driver/server.php?wsdl"

interface DescriptionApi {
    val SOAP_ACTION: String
    val METHOD_NAME: String
    val NAME_SPACE: String
    abstract val request: SoapObject
    abstract val soapEnvelope: SoapSerializationEnvelope
    val httpTransport: HttpTransportSE

    fun getSoapEnvelop(request: SoapObject): SoapSerializationEnvelope {
        val soapEnvelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        soapEnvelope.setOutputSoapObject(request)
        return soapEnvelope
    }

    fun getHttpTransport(): List<HeaderProperty> = listOf(HeaderProperty("Accept-Encoding", "none"))

    fun getRequest(nameSpace: String, methodName: String): SoapObject = SoapObject(nameSpace, methodName)
}

class Authorisation(
    company: String,
    login: String,
    password: String,
    notification: String): DescriptionApi {
    override val SOAP_ACTION: String = "urn:authuser#auth"
    override val METHOD_NAME: String = "auth"
    override val NAME_SPACE: String = "urn:authuser"
    override val request: SoapObject
    override val soapEnvelope: SoapSerializationEnvelope
    override val httpTransport: HttpTransportSE = HttpTransportSE(URL, 3000)

    init {
        request = getRequest(NAME_SPACE, METHOD_NAME)
        request.addProperty("company", company)
        request.addProperty("login", login)
        request.addProperty("password", password)
        request.addProperty("notification", notification)
        soapEnvelope = getSoapEnvelop(request)
    }

    suspend fun auth(): Pair<Int, String> = withContext(Dispatchers.Default){
        httpTransport.call(SOAP_ACTION, soapEnvelope, getHttpTransport())
        val answer = soapEnvelope.response.toString().split(",")
        val error = answer[0].replace("[", "").toInt()
        val message = answer[1].replace("]", "")
        Pair(error, message)
    }

}