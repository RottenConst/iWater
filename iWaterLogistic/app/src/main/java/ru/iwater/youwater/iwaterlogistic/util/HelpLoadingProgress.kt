package ru.iwater.youwater.iwaterlogistic.util

import android.content.Context

enum class HelpStateLogin() {
    ACCOUNT_SAVED,
    IS_WORK_START
}

object HelpLoadingProgress {
    fun setLoginProgress(context: Context, showLogin: HelpStateLogin, value: Boolean) {
        PreferencesUtils(context).setPrefBoolean(showLogin.ordinal, value)
    }

    fun getLoginShow(context: Context, show: HelpStateLogin): Boolean =
        PreferencesUtils(context).getPrefBool(show.ordinal,  true)

    fun getStartDayShow(context: Context, show: HelpStateLogin): Boolean =
        PreferencesUtils(context).getPrefBool(show.ordinal, true)
}