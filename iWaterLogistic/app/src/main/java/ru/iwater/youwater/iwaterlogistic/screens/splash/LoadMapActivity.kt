package ru.iwater.youwater.iwaterlogistic.screens.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity
import ru.iwater.youwater.iwaterlogistic.domain.vm.OrderListViewModel
import ru.iwater.youwater.iwaterlogistic.domain.vm.OrderLoadStatus
import ru.iwater.youwater.iwaterlogistic.screens.map.MapsActivity
import javax.inject.Inject

class LoadMapActivity : BaseActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: OrderListViewModel by viewModels { factory }
    private val screenComponent = App().buildScreenComponent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_layout)
        screenComponent.inject(this)
        viewModel.getLoadOrderFromDB()
        viewModel.status.observe(this) { status ->
            if (status == OrderLoadStatus.DONE) {
                val intent = Intent(this.applicationContext, MapsActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        viewModel.dbListOrder.observe(this, {
            viewModel.loadCoordinate(it)
        })
    }
}