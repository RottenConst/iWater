package ru.iwater.youwater.iwaterlogistic.iteractor

import android.content.Context
import ru.iwater.youwater.iwaterlogistic.domain.Account

const val STATE_ACCOUNT = "ACCOUNT_STATE"

class AccountStorage(context: Context) : StorageStateAccount {

    private val preferencesStateAccount =
        context.getSharedPreferences(STATE_ACCOUNT, Context.MODE_PRIVATE)

    override fun save(data: Account) {
        val editor = preferencesStateAccount.edit()
        editor.putString(STATE_ACCOUNT + "session", data.session)
        editor.putInt(STATE_ACCOUNT, data.id)
        editor.putString(STATE_ACCOUNT + "company", data.company)
        editor.putString(STATE_ACCOUNT + "status", data.status)
//        editor.putString(STATE_ACCOUNT + "login", data.login)

        editor.apply()
    }

    override fun get(): Account {
        val session = preferencesStateAccount.getString(STATE_ACCOUNT + "session", "").toString()
        val id = preferencesStateAccount.getInt(STATE_ACCOUNT, 0)
        val company = preferencesStateAccount.getString(STATE_ACCOUNT + "company", "").toString()
        val status = preferencesStateAccount.getString(STATE_ACCOUNT + "status", "").toString()
//        val login = preferencesStateAccount.getString(STATE_ACCOUNT + "login", "").toString()

        return Account(session, id, company, status)
    }

    override fun remove() {
        val editor = preferencesStateAccount.edit()
        editor.clear()
        editor.apply()
    }

}