package ru.iwater.youwater.iwaterlogistic.util

import android.content.Context

class PreferencesUtils(private val context: Context) {

    fun getPref(res: Int): Any? {
        return context.getSharedPreferences(res.toString(), Context.MODE_PRIVATE)
                .all[res.toString()]
    }

    fun getPrefBool(res: Int, defValue: Boolean) = context.getSharedPreferences(res.toString(), Context.MODE_PRIVATE)
            .getBoolean(res.toString(), defValue)

    fun getPrefInt(res: Int, defValue: Int) = context.getSharedPreferences(res.toString(), Context.MODE_PRIVATE)
            .getInt(res.toString(), defValue)

    fun getPrefFloat(res: Int, defValue: Float) = context.getSharedPreferences(res.toString(), Context.MODE_PRIVATE)
            .getFloat(res.toString(), defValue)

    fun getPrefLong(res: Int, defValue: Long) = context.getSharedPreferences(res.toString(), Context.MODE_PRIVATE)
            .getLong(res.toString(), defValue)

    fun getPrefStringSet(res: Int, defValue: MutableSet<String>): MutableSet<String> =
            context.getSharedPreferences(res.toString(), Context.MODE_PRIVATE)
                    .getStringSet(res.toString(), defValue)!!

    fun setPrefBoolean(res: Int, value: Boolean) {
        context.getSharedPreferences(res.toString(), Context.MODE_PRIVATE)
                .edit()
                .putBoolean(res.toString(), value)
                .apply()
    }

    fun setPrefInt(res: Int, value: Int) {
        context.getSharedPreferences(res.toString(), Context.MODE_PRIVATE)
                .edit()
                .putInt(res.toString(), value)
                .apply()
    }

    fun setPrefFloat(res: Int, value: Float) {
        context.getSharedPreferences(res.toString(), Context.MODE_PRIVATE)
                .edit()
                .putFloat(res.toString(), value)
                .apply()
    }

    fun setPrefLong(res: Int, value: Long) {
        context.getSharedPreferences(res.toString(), Context.MODE_PRIVATE)
                .edit()
                .putLong(res.toString(), value)
                .apply()
    }

    fun setPrefString(res: Int, value: String) {
        context.getSharedPreferences(res.toString(), Context.MODE_PRIVATE)
                .edit()
                .putString(res.toString(), value)
                .apply()
    }

    fun setPrefStringSet(res: Int, value: MutableSet<String>) {
        context.getSharedPreferences(res.toString(), Context.MODE_PRIVATE)
                .edit()
                .putStringSet(res.toString(), value)
                .apply()
    }
}