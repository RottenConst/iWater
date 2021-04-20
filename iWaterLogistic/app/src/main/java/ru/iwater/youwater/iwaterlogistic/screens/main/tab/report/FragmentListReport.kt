package ru.iwater.youwater.iwaterlogistic.screens.main.tab.report

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list_report.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.domain.ReportDay
import ru.iwater.youwater.iwaterlogistic.domain.vm.ReportViewModel
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.ReportListAdapter
import ru.iwater.youwater.iwaterlogistic.screens.splash.SplashActivity
import ru.iwater.youwater.iwaterlogistic.service.TimeListenerService
import ru.iwater.youwater.iwaterlogistic.util.HelpLoadingProgress
import ru.iwater.youwater.iwaterlogistic.util.HelpState
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
import timber.log.Timber
import javax.inject.Inject

class FragmentListReport : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: ReportViewModel by viewModels { factory }
    private val adapter = ReportListAdapter()

    private val screenComponent = App().buildScreenComponent()
    private var isComplete: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_list_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRv()
        observeVM()
        viewModel.getReports()
        tv_name_this_day_report.text = "Текущий отчет за ${UtilsMethods.getTodayDateString()}"
        cv_this_day_report.setOnClickListener {
            this.context?.let { it1 -> viewModel.getReportActivity(it1, UtilsMethods.getTodayDateString()) }
        }

        btn_end_this_day.setOnClickListener {
            if (isComplete) {
                this.context?.let { it1 ->
                    AlertDialog.Builder(it1)
                        .setMessage(R.string.confirmEndDay)
                        .setPositiveButton(
                            R.string.yes
                        ) { _, _ ->
                            val intent = Intent(it1, SplashActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            HelpLoadingProgress.setLoginProgress(
                                it1,
                                HelpState.IS_WORK_START,
                                true
                            )
                            viewModel.sendGeneralReport()
//                            viewModel.clearOldCompleteOrder()
                            viewModel.saveTodayReport()
                            val service = Intent(
                                activity?.applicationContext,
                                TimeListenerService::class.java
                            )
                            activity?.stopService(service)
                            activity?.finish()
                            startActivity(intent)
                        }
                        .setNegativeButton(R.string.no) { dialog, _ ->
                            dialog.cancel()
                        }.create().show()
                }
            } else {
                this.context?.let { it1 ->
                    AlertDialog.Builder(it1)
                        .setMessage(R.string.confirmEndOrder)
                        .setPositiveButton(
                            R.string.ok
                        ) { dialog, _ ->
                            dialog.cancel()
                        }.create().show()
                }
            }
        }
    }

    private fun observeVM() {
        viewModel.reportsDay.observe(viewLifecycleOwner, {
            Timber.d("${it.size}")
            if (!it.isNullOrEmpty()) {
                addReports(it)
            }
        })
        viewModel.isSendReportDay()
        viewModel.isCompleteOrder.observe(viewLifecycleOwner, {
            isComplete = it
        })
        viewModel.getTodayExpenses()
        viewModel.initThisReport()
        viewModel.reportDay.observe(viewLifecycleOwner, {
            viewModel.setPropertyGeneralReport(it)
        })
    }

    private fun initRv() {
        rv_list_report_day.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        adapter.notifyDataSetChanged()
        rv_list_report_day.adapter = adapter
        adapter.onReportClick = {
            this.context?.let { it1 -> viewModel.getReportActivity(it1, it.date) }
        }
    }

    private fun addReports(reports: List<ReportDay>) {
        adapter.reportDayList.clear()
        adapter.reportDayList.addAll(reports)
        adapter.notifyDataSetChanged()
    }

    companion object {
        fun newInstance(): FragmentListReport {
            return FragmentListReport()
        }
    }
}