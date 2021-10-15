package ru.iwater.youwater.iwaterlogistic.screens.main.tab.report

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.databinding.FragmentEndDayBinding
import ru.iwater.youwater.iwaterlogistic.domain.ReportDay
import ru.iwater.youwater.iwaterlogistic.domain.vm.LoadPhoto
import ru.iwater.youwater.iwaterlogistic.domain.vm.ReportViewModel
import ru.iwater.youwater.iwaterlogistic.domain.vm.Status
import ru.iwater.youwater.iwaterlogistic.screens.splash.SplashActivity
import ru.iwater.youwater.iwaterlogistic.service.TimeListenerService
import ru.iwater.youwater.iwaterlogistic.util.HelpLoadingProgress
import ru.iwater.youwater.iwaterlogistic.util.HelpState
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject

private const val REQUEST_IMAGE_CAPTURE = 1

class ReportCheckFragment: BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: ReportViewModel by viewModels { factory }
    private val screenComponent = App().buildScreenComponent()
    lateinit var reportDay: ReportDay
    private var outputUri: Uri?  = null

    private val photos = mutableListOf<String>()

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
               dispatchTakePictureIntent(context)
            }catch (e: Exception) {
                e.printStackTrace()
                UtilsMethods.showToast(this.context, "Не удалось добавить фотографию")
            }
        }

        viewModel.reportDay.observe(viewLifecycleOwner, {
            reportDay = it
        })

        viewModel.statusLoad.observe(this.viewLifecycleOwner, { load ->
            when (load) {
                LoadPhoto.LOADING -> {
                    binding.pbForLoad.visibility = View.VISIBLE
                    binding.btnSendReport.isEnabled = false
                }
                LoadPhoto.DONE -> {
                    val report = viewModel.getDriverCloseMonitor()
                    viewModel.driverCloseDay(report)
                }
                LoadPhoto.ERROR -> {
                    binding.pbForLoad.visibility = View.GONE
                    binding.btnSendReport.isEnabled = true
                    UtilsMethods.showToast(this.context, "Ошибка отправки отчета не удается передать данные")
                }
            }

        })

        viewModel.status.observe(this.viewLifecycleOwner, { status ->
            when (status) {
                Status.DONE -> {
                    viewModel.saveTodayReport()
                    binding.btnSendReport.isEnabled = true
                    val intent = Intent(this.context, SplashActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    HelpLoadingProgress.setLoginProgress(
                        this.requireContext(),
                        HelpState.IS_WORK_START,
                        true
                    )
                    binding.pbForLoad.visibility = View.GONE
                    viewModel.clearOldCompleteOrder()
                    context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.delete()
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
                viewModel.sendPhotoZReport(photos)
            } else {
                val context = this.context
                if (context != null) AlertDialog.Builder(context)
                    .setMessage("Прикрепите фото z-отчета")
                    .setPositiveButton(R.string.yes) {
                        dialog, _ ->
                        dialog.cancel()
                    }.create().show()
            }
        }

        return binding.root
    }

    private fun initPhoto(bitmap: Uri) {
        if (!binding.ivPhotoCheckOne.isVisible) {
            binding.ivPhotoCheckOne.apply {
                setImageURI(bitmap)
                visibility = View.VISIBLE
            }
        }
        else if (binding.ivPhotoCheckOne.isVisible && !binding.ivPhotoCheckTwo.isVisible) {
            binding.ivPhotoCheckTwo.apply {
                setImageURI(bitmap)
                visibility = View.VISIBLE
            }
        }
        else if (binding.ivPhotoCheckTwo.isVisible && !binding.ivPhotoCheckTree.isVisible) {
            binding.ivPhotoCheckTree.apply {
                setImageURI(bitmap)
                visibility = View.VISIBLE
            }
        }
        else if (binding.ivPhotoCheckTree.isVisible && !binding.ivPhotoCheckFour.isVisible) {
            binding.ivPhotoCheckFour.apply {
                setImageURI(bitmap)
                visibility = View.VISIBLE
            }
        }
        else if (binding.ivPhotoCheckFour.isVisible && !binding.ivPhotoCheckFive.isVisible) {
            binding.ivPhotoCheckFive.apply {
                setImageURI(bitmap)
                visibility = View.VISIBLE
                UtilsMethods.showToast(context, "Достаточно, можно отправлять отчет")
            }
            binding.btnAddCheck.isEnabled = false
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            outputUri?.let { initPhoto(it) }
            if (viewModel.currentPhotoPath.isNotBlank()) {
                photos.add(viewModel.currentPhotoPath)
                Timber.i(viewModel.currentPhotoPath)
                Timber.i(outputUri?.encodedPath)
            }

        }

    }

    /**
     * сохранить изображение в созданный ранее файл
     **/
    private fun dispatchTakePictureIntent(context: Context?): Intent {
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(context?.packageManager!!)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    viewModel.createImageFile(context)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                        outputUri = FileProvider.getUriForFile(
                        context,
                        "ru.iwater.yourwater.iwaterlogistic.provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
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