package ru.iwater.youwater.iwaterlogistic.util

import android.content.Context
import android.widget.Toast
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object UtilsMethods {
    fun timeDifference(time: String, formatedDate: List<String>): Long {
        var time = time
        var diff: Long = 0
        var date = ""
        if (time.replace("\\s+".toRegex(), "") == "00:00") time = "24:00"
        date += formatedDate[2] + "-" + formatedDate[1] + "-" + formatedDate[0]
        val orderTime =
            date.replace("\\s+".toRegex(), "") + " " + time.replace("\\s+".toRegex(), "")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        try {
            val date1 = dateFormat.parse(orderTime)
            diff = (date1.time - System.currentTimeMillis()) / 1000
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return diff
    }

    fun getFormatedDate(): List<String> {
        val currentDate = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val timeComplete = formatter.format(currentDate.time)
        return timeComplete.replace("\\s+".toRegex(), "").split("/")
    }

    fun getTodayDateString(): String {
        val currentDate = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(currentDate.time)
    }

    fun showToast(context: Context?, value: String) {
        Toast.makeText(context, value, Toast.LENGTH_LONG).show()
    }
}