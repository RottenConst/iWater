package ru.iwater.youwater.iwaterlogistic.util

import android.content.Context

enum class HelpState() {
    ACCOUNT_SAVED,
    IS_WORK_START,
    IS_NOTIFICATION
}

object HelpLoadingProgress {
    fun setLoginProgress(context: Context, show: HelpState, value: Boolean) {
        PreferencesUtils(context).setPrefBoolean(show.ordinal, value)
    }

    fun getLoginShow(context: Context, show: HelpState): Boolean =
        PreferencesUtils(context).getPrefBool(show.ordinal,  true)

    fun getStartDayShow(context: Context, show: HelpState): Boolean =
        PreferencesUtils(context).getPrefBool(show.ordinal, true)
}

object HelpNotification {
    fun saveNotification(context: Context?, res: Int, notificationText: String) {
        context?.let { PreferencesUtils(it).setPrefString(HelpState.IS_NOTIFICATION.ordinal + res, notificationText) }
    }

    fun  showSavedNotification(context: Context, res: Int): String? =
        PreferencesUtils(context).getPrefString(HelpState.IS_NOTIFICATION.ordinal + res, "")
}