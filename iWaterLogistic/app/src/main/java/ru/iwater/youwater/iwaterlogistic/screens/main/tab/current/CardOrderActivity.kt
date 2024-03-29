package ru.iwater.youwater.iwaterlogistic.screens.main.tab.current

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity
import timber.log.Timber

class CardOrderActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container_order)
        val intent = intent
        val id = intent.getIntExtra("id", 0)
        Timber.d("get order id $id")
        supportActionBar?.title = "Карточка заказа #$id"
        getFragment(id)
    }

    private fun getFragment(id: Int) {
        val fragment = AboutOrderFragment.newInstance()
        val bundle = Bundle()
        bundle.putInt("id", id)
        fragment.arguments = bundle
        if (supportFragmentManager.fragments.isEmpty()) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fl_card_order_container, fragment)
                .commit()
        }
    }

    fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    companion object {
        fun start(context: Context, intent: Intent) {
            ContextCompat.startActivity(context, intent, null)
        }
    }
}