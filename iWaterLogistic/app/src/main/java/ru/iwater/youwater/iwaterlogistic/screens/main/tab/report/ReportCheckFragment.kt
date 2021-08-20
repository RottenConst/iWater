package ru.iwater.youwater.iwaterlogistic.screens.main.tab.report

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.databinding.FragmentEndDayBinding
import ru.iwater.youwater.iwaterlogistic.domain.ReportDay
import ru.iwater.youwater.iwaterlogistic.domain.vm.ReportViewModel
import ru.iwater.youwater.iwaterlogistic.domain.vm.Status
import ru.iwater.youwater.iwaterlogistic.screens.splash.SplashActivity
import ru.iwater.youwater.iwaterlogistic.service.TimeListenerService
import ru.iwater.youwater.iwaterlogistic.util.HelpLoadingProgress
import ru.iwater.youwater.iwaterlogistic.util.HelpState
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
import javax.inject.Inject

private const val REQUEST_IMAGE_CAPTURE = 1

class ReportCheckFragment: BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: ReportViewModel by viewModels { factory }
    private val screenComponent = App().buildScreenComponent()
    lateinit var reportDay: ReportDay

    private lateinit var binding: FragmentEndDayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_end_day, container, false)
        binding.lifecycleOwner = this
        binding.viewModelReport = viewModel

        binding.btnAddCheck.setOnClickListener {
            try {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }catch (e: Exception) {
                e.printStackTrace()
                UtilsMethods.showToast(this.context, "Не удалось добавить фотографию")
            }
        }

        viewModel.reportDay.observe(viewLifecycleOwner, {
            reportDay = it
        })

        viewModel.status.observe(this.viewLifecycleOwner, { status ->
            when (status) {
                Status.DONE -> {
                    viewModel.saveTodayReport()
                    val intent = Intent(this.context, SplashActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    HelpLoadingProgress.setLoginProgress(
                        this.requireContext(),
                        HelpState.IS_WORK_START,
                        true
                    )
                    viewModel.clearOldCompleteOrder()
                    viewModel.setStatusExpenses(Status.NONE)
                    val service = Intent(
                        activity?.applicationContext,
                        TimeListenerService::class.java
                    )
                    activity?.stopService(service)
                    activity?.finish()
                    startActivity(intent)
                }
                Status.ERROR -> {
                    UtilsMethods.showToast(this.context, "Ошибка отправки отчета не удается передать данные")
                }
                Status.NONE -> {}
            }
        })

        binding.btnSendReport.setOnClickListener {
            if (binding.ivPhotoCheckOne.isVisible) {
                val report = viewModel.getDriverCloseMonitor()
                viewModel.driverCloseDay(report)
            } else {
                val context = this.context
                if (context != null) AlertDialog.Builder(context)
                    .setMessage("Прикрепите фото чека")
                    .setPositiveButton(R.string.yes) {
                        dialog, _ ->
                        dialog.cancel()
                    }.create().show()
            }
        }

        return binding.root
    }

    private fun initPhoto(bitmap: Bitmap) {
        if (!binding.ivPhotoCheckOne.isVisible) {
            binding.ivPhotoCheckOne.apply {
                setImageBitmap(bitmap)
                visibility = View.VISIBLE
            }
        }
        else if (binding.ivPhotoCheckOne.isVisible && !binding.ivPhotoCheckTwo.isVisible) {
            binding.ivPhotoCheckTwo.apply {
                setImageBitmap(bitmap)
                visibility = View.VISIBLE
            }
        }
        else if (binding.ivPhotoCheckTwo.isVisible && !binding.ivPhotoCheckTree.isVisible) {
            binding.ivPhotoCheckTree.apply {
                setImageBitmap(bitmap)
                visibility = View.VISIBLE
            }
        }
        else if (binding.ivPhotoCheckTree.isVisible && !binding.ivPhotoCheckFour.isVisible) {
            binding.ivPhotoCheckFour.apply {
                setImageBitmap(bitmap)
                visibility = View.VISIBLE
            }
        }
        else if (binding.ivPhotoCheckFour.isVisible && !binding.ivPhotoCheckFive.isVisible) {
            binding.ivPhotoCheckFive.apply {
                setImageBitmap(bitmap)
                visibility = View.VISIBLE
                UtilsMethods.showToast(context, "Достаточно, можно отправлять отчет")
            }
            binding.btnAddCheck.isEnabled = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // Фотка сделана, извлекаем миниатюру картинки
            if (data != null) {
                if (data.hasExtra("data")) {
                    val thumbnailBitmap = data.extras?.get("data") as Bitmap
                    initPhoto(thumbnailBitmap)
                }
            }
        }
    }


    companion object {
        fun newInstance(): ReportCheckFragment {
            return ReportCheckFragment()
        }
    }
}