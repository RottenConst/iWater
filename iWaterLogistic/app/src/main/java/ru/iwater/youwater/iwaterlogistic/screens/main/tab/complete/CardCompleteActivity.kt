package ru.iwater.youwater.iwaterlogistic.screens.main.tab.complete

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity

class CardCompleteActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container_order)
        val intent = intent
        val id = intent.getIntExtra("id", 0)
        getFragment(id)
    }

    private fun getFragment(id: Int) {
        val fragment = FragmentCompleteOrderInfo.newInstance()
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
        fun start(context: Context?, intent: Intent) {
            if (context != null) {
                ContextCompat.startActivity(context, intent, null)
            }
        }
    }
}