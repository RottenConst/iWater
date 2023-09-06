package ru.iwater.youwater.iwaterlogistic.screens.main.tab.start

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.databinding.StartWorkFragmentBinding
import ru.iwater.youwater.iwaterlogistic.domain.vm.OrderListViewModel
import ru.iwater.youwater.iwaterlogistic.domain.vm.OrderLoadStatus
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.ListOrdersAdapter
import ru.iwater.youwater.iwaterlogistic.screens.splash.SplashActivity
import ru.iwater.youwater.iwaterlogistic.util.HelpLoadingProgress
import ru.iwater.youwater.iwaterlogistic.util.HelpState
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
import javax.inject.Inject

class StartFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: OrderListViewModel by viewModels { factory }
    private val screenComponent = App().buildScreenComponent()
    lateinit var accountRepository: AccountRepository

    private var binding: StartWorkFragmentBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
        accountRepository = AccountRepository(screenComponent.accountStorage())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StartWorkFragmentBinding.inflate(inflater)
        binding?.srlRefreshCurrentOrders?.setOnRefreshListener(this)
        binding?.lifecycleOwner = this
        binding?.viewModel = viewModel
        binding?.rvListCurrentPreview?.adapter = ListOrdersAdapter(ListOrdersAdapter.OnClickListener {
            UtilsMethods.showToast(this.context, it.name)
        })
        viewModel.getLoadCurrent()
        observeVW()
        binding?.btnExitAccount?.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.confirmLogout)
                .setPositiveButton(
                    R.string.yes
                ) { _, _ ->
                    val intent = Intent(this.context, SplashActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    HelpLoadingProgress.setLoginProgress(requireContext(), HelpState.ACCOUNT_SAVED, true)
                    accountRepository.deleteAccount()
                    startActivity(intent)
                }
                .setNegativeButton(R.string.no) { dialog, _ ->
                    dialog.cancel()
                }.create().show()
        }
        binding?.btnStartDay?.setOnClickListener {
            viewModel.getWorkShift(this.context,activity)
//            val fragment = LoadDriveFragment.newInstance()
//            activity?.supportFragmentManager?.beginTransaction()
//                ?.replace(R.id.container, fragment)
//                ?.commit()
        }
        return binding?.root
    }

    override fun onRefresh() {
        viewModel.getLoadCurrent()
        binding?.srlRefreshCurrentOrders?.isRefreshing = false
    }

    private fun observeVW() {
        viewModel.status.observe(this.viewLifecycleOwner) { status: OrderLoadStatus ->
            when (status) {
                OrderLoadStatus.DONE -> {
                    binding?.apply {
                        "Плановые заказы на ${UtilsMethods.getTodayDateString()}".also {
                            tvTitle.text = it
                        }
                        rvListCurrentPreview.visibility = View.VISIBLE
                    }
                }

                OrderLoadStatus.ERROR -> {
                    binding?.apply {
                        tvTitle.text = "Плановых заказов пока нет"
                        rvListCurrentPreview.visibility = View.GONE
                    }
                }

                OrderLoadStatus.LOADING -> {
                }
            }
        }
    }

    companion object {
        fun newInstance(): StartFragment = StartFragment()
    }
}