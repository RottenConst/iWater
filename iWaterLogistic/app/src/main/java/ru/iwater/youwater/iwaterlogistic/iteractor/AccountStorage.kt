package ru.iwater.youwater.iwaterlogistic.iteractor

import android.content.Context
import ru.iwater.youwater.iwaterlogistic.domain.Account

const val STATE_ACCOUNT = "ACCOUNT_STATE"

class AccountStorage(context: Context) : StorageStateAccount {

    private val preferencesStateAccount =
        context.getSharedPreferences(STATE_ACCOUNT, Context.MODE_PRIVATE)

    override fun save(data: Account) {
        val editor = preferencesStateAccount.edit()
        editor.putInt(STATE_ACCOUNT, data.id)
        editor.putString(STATE_ACCOUNT + "company", data.company)
        editor.putString(STATE_ACCOUNT + "login", data.login)
        editor.putString(STATE_ACCOUNT + "session", data.session)
        editor.apply()
    }

    override fun get(): Account {
        val id = preferencesStateAccount.getInt(STATE_ACCOUNT, 0)
        val company = preferencesStateAccount.getString(STATE_ACCOUNT + "company", "").toString()
        val login = preferencesStateAccount.getString(STATE_ACCOUNT + "login", "").toString()
        val session = preferencesStateAccount.getString(STATE_ACCOUNT + "session", "").toString()

        return Account(id, login, session, company)
    }

    override fun remove() {
        val editor = preferencesStateAccount.edit()
        editor.clear()
        editor.apply()
    }

}