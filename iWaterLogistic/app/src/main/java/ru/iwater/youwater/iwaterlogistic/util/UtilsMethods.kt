package ru.iwater.youwater.iwaterlogistic.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import ru.iwater.youwater.iwaterlogistic.domain.Product
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
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

    fun productToStringMap(products: List<Product>): String {
        var nameProduct = ""
        val iterator = products.iterator()
        while (iterator.hasNext()) {
            val product = iterator.next()
            nameProduct += "${product.name} - ${product.count}шт.+"
        }
        return nameProduct
    }

    private const val IMG_MAX_SIDE_SIZE = 200000

    fun decodeBitmap(uri: Uri, activity: Activity?): Bitmap? {
        return try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(activity?.contentResolver?.openInputStream(uri), null, options)
            val scale = 1
//            while (options.outWidth / scale / 2 >= IMG_MAX_SIDE_SIZE && options.outHeight / scale / 2 >= IMG_MAX_SIDE_SIZE) scale *= 2

            val scaleOptions = BitmapFactory.Options()
            scaleOptions.inSampleSize = scale
            BitmapFactory.decodeStream(activity?.contentResolver?.openInputStream(uri), null, scaleOptions)
        } catch (e: FileNotFoundException) {
            null
        }
    }
}

fun Intent.createBitmapFromResult(activity: Activity?): Bitmap? {
    val intentBundle = this.extras
    val intentUri = this.data
    var bitmap: Bitmap? = null
    if (intentBundle != null) {
        bitmap = (intentBundle.get("data") as? Bitmap)?.apply {
            compress(Bitmap.CompressFormat.JPEG, 100, ByteArrayOutputStream())
        }
    }

    if (bitmap == null && intentUri != null) {
        intentUri.let { bitmap = UtilsMethods.decodeBitmap(intentUri, activity) }
    }
    return bitmap
}