package ru.iwater.youwater.iwaterlogistic.domain.vm

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.domain.CloseDriverShift
import ru.iwater.youwater.iwaterlogistic.domain.DayReport
import ru.iwater.youwater.iwaterlogistic.domain.Expenses
import ru.iwater.youwater.iwaterlogistic.domain.ReportDay
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.repository.CompleteOrdersRepository
import ru.iwater.youwater.iwaterlogistic.repository.ReportRepository
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.report.ReportActivity
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

enum class Status {
    NONE,
    DONE,
    ERROR
}

enum class LoadPhoto {
    LOADING,
    DONE,
    ERROR
}

@OnScreen
class ReportViewModel @Inject constructor(
    private val completeOrdersRepository: CompleteOrdersRepository,
    private val reportRepository: ReportRepository,
    accountRepository: AccountRepository,
) : ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var timeComplete = UtilsMethods.getTodayDateString()

    private val idDriver = accountRepository.getAccount().id
    private val driverName = accountRepository.getAccount().login
    private val company = accountRepository.getAccount().company
    private val session = accountRepository.getAccount().session

    var currentPhotoPath: String = ""

    private val _status: MutableLiveData<Status> = MutableLiveData()
    val status: LiveData<Status>
        get() = _status

    private val _statusLoad: MutableLiveData<LoadPhoto> = MutableLiveData()
    val statusLoad: LiveData<LoadPhoto>
        get() = _statusLoad

    //отчеты
    private val _reportsDay: MutableLiveData<List<ReportDay>> = MutableLiveData()
    val reportsDay: LiveData<List<ReportDay>>
        get() = _reportsDay

    //отчет
    private val _reportDay: MutableLiveData<ReportDay> = MutableLiveData()
    val reportDay: LiveData<ReportDay>
        get() = _reportDay

    //расходы
    private val _expenses: MutableLiveData<List<Expenses>> = MutableLiveData()
    val expenses: LiveData<List<Expenses>>
        get() = _expenses

    //остались ли активные заказы
    private val _isCompleteOrder: MutableLiveData<Boolean> = MutableLiveData()
    val isCompleteOrder: LiveData<Boolean>
        get() = _isCompleteOrder

    private val _countOrder: MutableLiveData<String> = MutableLiveData()
    val countOrder: LiveData<String>
        get() = _countOrder


    init {
        initDateReport()
        isSendReportDay()
        _status.value = Status.NONE
    }

    /**
     * загрузить отчет за день из бд по дате
     */
    fun initDateReport(date: String = "") {
        uiScope.launch {
            Timber.d("Date = $date timeComplete = $timeComplete")
            if (date != timeComplete && date.isNotBlank()) {
                _reportDay.value = reportRepository.loadReportFromDB(date)
            } else {
                _reportDay.value = ReportDay(
                    timeComplete,
                    completeOrdersRepository.getSumCashFullCompleteOrder(),
                    completeOrdersRepository.getSumCashCompleteOrder("Наличные"),
                    completeOrdersRepository.getSumCashCompleteOrder("На сайте"),
                    completeOrdersRepository.getSumCashCompleteOrder("Оплата через терминал"),
                    completeOrdersRepository.getSumCashCompleteOrder("Без наличные"),
                    completeOrdersRepository.getSumCashCompleteOrder("Наличные") - reportRepository.getSumOfCostExpenses(
                        timeComplete
                    ),
                    completeOrdersRepository.getTankCompleteOrder(),
                    completeOrdersRepository.getCountCompleteOrder()
                )
            }
        }
    }

    /**
     * сохранить отчет за день
     */
    fun saveTodayReport() {
        uiScope.launch {
            reportRepository.saveReport(
                ReportDay(
                    timeComplete,
                    completeOrdersRepository.getSumCashFullCompleteOrder(),
                    completeOrdersRepository.getSumCashCompleteOrder("Наличные"),
                    completeOrdersRepository.getSumCashCompleteOrder("На сайте"),
                    completeOrdersRepository.getSumCashCompleteOrder("Оплата через терминал"),
                    completeOrdersRepository.getSumCashCompleteOrder("Без наличные"),
                    completeOrdersRepository.getSumCashCompleteOrder("Наличные") - reportRepository.getSumOfCostExpenses(
                        timeComplete
                    ),
                    completeOrdersRepository.getTankCompleteOrder(),
                    completeOrdersRepository.getCountCompleteOrder()
                )
            )
        }
    }

    fun getDriverCloseMonitor(): DayReport {
        val report = _reportDay.value
        val dateCreate = Calendar.getInstance().timeInMillis / 1000
        return DayReport(
            idDriver,
            report?.tank ?: 0,
            report?.orderComplete ?: 0,
            report?.cashOnSite ?: 0.0F,
            report?.cashOnTerminal ?: 0.0F,
            report?.cashMoney ?: 0.0F,
            report?.noCashMoney ?: 0.0F,
            report?.moneyDelivery ?: 0.0F,
            report?.totalMoney,
            dateCreate.toString(),
            report?.date,
            company
        )
    }

    fun driverCloseDay(dayReport: DayReport) {
        uiScope.launch {
            if (reportRepository.sendDayReport(dayReport)) {
                closeShift()
            } else {
                _status.value = Status.ERROR
            }
        }
    }

    fun sendPhotoZReport(photos: List<String>) {
        _statusLoad.value = LoadPhoto.LOADING
        for (photo in photos) {
            val image = BitmapFactory.decodeFile(photo)
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 75, stream)
            val byteArray = stream.toByteArray()
            val body = MultipartBody.Part.createFormData(
                "image", "JPEG${timeStamp}.jpg",
                byteArray.toRequestBody("multipart/form-data".toMediaTypeOrNull(),  0, byteArray.size)
            )
            val id = MultipartBody.Part.createFormData("driver_id", idDriver.toString())
            val date = MultipartBody.Part.createFormData("date", timeComplete.replace("/", "-"))
            val driverName = MultipartBody.Part.createFormData("driver_name", driverName)
            uiScope.launch {
                try {
                    reportRepository.sendPhoto( id , date , driverName, body)
                }catch (e: Exception) {
                    _statusLoad.value = LoadPhoto.ERROR
                }
            }
        }
        _statusLoad.value = LoadPhoto.DONE
    }

    private fun closeShift() {
        uiScope.launch {
            val unix = System.currentTimeMillis() / 1000L
            val date = reportRepository.getOpenLastShiftDay()
            if (date != null) {
                val closeDriverShift = CloseDriverShift(
                    idDriver,
                    unix.toString(),
                    date
                )
                if (reportRepository.closeDriverShift(closeDriverShift)) {
                    _status.value = Status.DONE
                } else {
                    _status.value = Status.ERROR
                }
            }

        }
    }

    /**
     * загрузить все отчеты за день из бд
     */
    fun getReports() {
        uiScope.launch {
            val reports = reportRepository.loadAllReport()
            Timber.d("TODAY = ${UtilsMethods.getTodayDateString()}")
            if (reports.size > 1) {
                val dateReport = reports[0].date
                val expenses = reportRepository.loadExpenses(dateReport)
                expenses.forEach {
                    val filePath = it.fileName
                    if (!filePath.isNullOrBlank()) {
                        val file = File(filePath)
                        if (file.exists()) {
                            file.delete()
                        }
                    }
                    reportRepository.deleteExpenses(it)
                }
                reportRepository.deleteReport(reports[0])
            }
            _reportsDay.value = reports
        }
    }

    /**
     * загрузить все расходы за день из бд за выьпранную дату
     */
    fun getExpenses(date: String) {
        uiScope.launch {
            if (date != timeComplete) _expenses.value = reportRepository.loadExpenses(date)
            else _expenses.value = reportRepository.loadExpenses(timeComplete)
        }
    }

    //очистить выполненные заказы
    fun clearOldCompleteOrder() {
        uiScope.launch {
            val completeOrders = completeOrdersRepository.getAllCompleteOrders()
            for (completeOrder in completeOrders) {
                completeOrdersRepository.deleteCompleteOrder(completeOrder)
            }
            val orders = reportRepository.getDBOrders()
            if (!orders.isNullOrEmpty()) {
                for (order in orders) {
                    reportRepository.deleteOrder(order)
                }
            }
        }
    }

    /**
     * отправить расход в в црм
     */
    fun sendExpenses(name: String, cost: Float, fileName: String) {
        uiScope.launch {
            if (reportRepository.sendExpenses(Expenses(idDriver, timeComplete, name, cost, fileName, company))) {
                reportRepository.saveExpenses(Expenses(idDriver, timeComplete, name, cost, fileName, company))
                _statusLoad.value = LoadPhoto.DONE
            } else {
                _statusLoad.value = LoadPhoto.ERROR
            }
        }
    }

    fun sendPhotoExpenses(photo: String) {
        val image = BitmapFactory.decodeFile(photo)
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 75, stream)
        val byteArray = stream.toByteArray()
        val body = MultipartBody.Part.createFormData(
            "image", "CHECK${timeStamp}.jpg",
            byteArray.toRequestBody("multipart/form-data".toMediaTypeOrNull(),  0, byteArray.size)
        )
        val id = MultipartBody.Part.createFormData("driver_id", idDriver.toString())
        val date = MultipartBody.Part.createFormData("date", timeComplete.replace("/", "-"))
        val driverName = MultipartBody.Part.createFormData("driver_name", driverName)
        uiScope.launch {
            if (reportRepository.sendExpensesPhoto(id, date, driverName, body)) {
                _statusLoad.value = LoadPhoto.LOADING
            } else {
                _statusLoad.value = LoadPhoto.ERROR
            }
        }

    }

    fun setStatusExpenses(status: Status) {
        _status.value = status
    }

    //остались еще не законченые заказы
    private fun isSendReportDay() {
        uiScope.launch {
            val completeOrders = completeOrdersRepository.getAllCompleteOrders()
            val currentOrders = reportRepository.getLoadTotalOrder(session)
            _countOrder.value = "${completeOrders.filter { it.typeOfCash != "-" }.size} из ${currentOrders.size}"
            _isCompleteOrder.value = completeOrders.filter { it.typeOfCash != "-" }.size == currentOrders.size
        }
    }

    /**
     * запустить экран с отчетами
     **/
    fun getReportActivity(context: Context, date: String) {
        val intent = Intent(context, ReportActivity::class.java)
        intent.putExtra("dateReport", date)
        ReportActivity.start(context, intent)
    }

    /**
     * создать файл
     **/
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply { // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }



    /**
     * закрываем и отменяем корутины
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}