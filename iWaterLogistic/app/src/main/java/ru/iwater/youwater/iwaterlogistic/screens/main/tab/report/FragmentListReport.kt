package ru.iwater.youwater.iwaterlogistic.screens.main.tab.report

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.databinding.FragmentListReportBinding
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

    private val screenComponent = App().buildScreenComponent()
    private lateinit var binding: FragmentListReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_report, container, false)
        viewModel.getReports()
        binding.viewModelReports = viewModel
        binding.lifecycleOwner = this
        binding.rvListReportDay.adapter = ReportListAdapter(ReportListAdapter.OnClickListener {
            viewModel.getReportActivity(this.requireContext(), it.date)
        })
        binding.todayReport.constraint.setBackgroundColor(resources.getColor(R.color.green_day))
        "Текущий отчет за ${UtilsMethods.getTodayDateString()}".also {
            binding.todayReport.tvNameReport.text = it
        }

        binding.todayReport.cvReportCard.setOnClickListener {
            this.context?.let { it1 ->
                viewModel.getReportActivity(
                    it1,
                    UtilsMethods.getTodayDateString()
                )
            }
        }
        return binding.root
    }

    companion object {
        fun newInstance(): FragmentListReport {
            return FragmentListReport()
        }
    }
}