package ru.iwater.youwater.iwaterlogistic.util

import android.content.Context

enum class HelpProgressLoad {
    LOGIN
}

object HelpLoadingProgress {
    fun setLoginProgress(context: Context, showLoad: HelpProgressLoad, value: Boolean) {
        PreferencesUtils(context).setPrefBoolean(showLoad.ordinal, value)
    }

    fun getLoginShow(context: Context, show: HelpProgressLoad): Boolean = PreferencesUtils(context).getPrefBool(show.ordinal, true)
}