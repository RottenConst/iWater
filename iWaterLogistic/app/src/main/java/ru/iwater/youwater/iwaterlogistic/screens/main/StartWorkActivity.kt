package ru.iwater.youwater.iwaterlogistic.screens.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseActivity
import ru.iwater.youwater.iwaterlogistic.databinding.ActivityStartWorkBinding
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.domain.vm.OrderListViewModel
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.ListCurrentOrdersPreview
import ru.iwater.youwater.iwaterlogistic.screens.splash.SplashActivity
import ru.iwater.youwater.iwaterlogistic.util.HelpLoadingProgress
import ru.iwater.youwater.iwaterlogistic.util.HelpState
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
import javax.inject.Inject

class StartWorkActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: OrderListViewModel by viewModels { factory }
    private val screenComponent = App().buildScreenComponent()
    lateinit var accountRepository: AccountRepository

    private lateinit var binding: ActivityStartWorkBinding
    private val adapter = ListCurrentOrdersPreview()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start_work)
        screenComponent.inject(this)
        accountRepository = AccountRepository(screenComponent.accountStorage())
        binding.srlRefreshCurrentOrders.setOnRefreshListener(this)
        initRecyclerView()
        observeVW()
        viewModel.getLoadCurrent()

        binding.btnExitAccount.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage(R.string.confirmLogout)
                .setPositiveButton(
                    R.string.yes
                ) { _, _ ->
                    val intent = Intent(applicationContext, SplashActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    HelpLoadingProgress.setLoginProgress(this, HelpState.ACCOUNT_SAVED, true)
                    accountRepository.deleteAccount()
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton(R.string.no) { dialog, _ ->
                    dialog.cancel()
                }.create().show()
        }

        binding.btnStartDay.setOnClickListener {
            viewModel.openDriverDay(this)
        }
    }

    override fun onRefresh() {
        viewModel.getLoadCurrent()
        binding.srlRefreshCurrentOrders.isRefreshing = false
    }

    private fun observeVW() {
        viewModel.listOrder.observe(this, {
            if (it.isNullOrEmpty()) {
                binding.tvTitle.text = "Плаеновых заказов пока нет"
                binding.rvListCurrentPreview.visibility = View.GONE
            } else {
                addCurrentOrders(it)
                "Плановые заказы на ${UtilsMethods.getTodayDateString()}".also {
                    binding.tvTitle.text = it
                }
                binding.rvListCurrentPreview.visibility = View.VISIBLE
            }
        })
    }

    private fun initRecyclerView() {
        binding.rvListCurrentPreview.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun addCurrentOrders(orders: List<Order>) {
        adapter.orders.clear()
        adapter.orders.addAll(orders)
        adapter.notifyDataSetChanged()
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