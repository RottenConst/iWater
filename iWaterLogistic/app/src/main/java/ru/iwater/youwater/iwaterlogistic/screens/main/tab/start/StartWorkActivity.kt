package ru.iwater.youwater.iwaterlogistic.screens.main.tab.start

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity

class StartWorkActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_container_activity)
        val fragment = StartFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
            .commit()
    }

    companion object {
        fun start(context: Context) {
            ContextCompat.startActivity(
                context,
                Intent(context, StartWorkActivity::class.java),
                null
            )
        }
    }


}