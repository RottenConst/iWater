package ru.iwater.youwater.iwaterlogistic.screens.main.tab.report

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.databinding.FragmentReportDayBinding
import ru.iwater.youwater.iwaterlogistic.domain.vm.LoadPhoto
import ru.iwater.youwater.iwaterlogistic.domain.vm.ReportViewModel
import ru.iwater.youwater.iwaterlogistic.domain.vm.Status
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.ExpensesAdapter
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

private const val REQUEST_IMAGE_CAPTURE = 1

class ReportFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: ReportViewModel by viewModels { factory }
    private val adapter = ExpensesAdapter()

    private val screenComponent = App().buildScreenComponent()
    private lateinit var binding: FragmentReportDayBinding
    private var outputUri: Uri?  = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_report_day,
            container,
            false
        )
        val arg = arguments
        val date = arg?.getString("date")
        "Отчет за $date".also { binding.tvReportTitle.text = it }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.addExpensesDrawer.addPhoto)
        bottomSheetBehavior.setPeekHeight(0, true)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        binding.rvExpenses.adapter = adapter

        date?.let {
            observeReportDate(it, binding)
            observeExpenses(it, binding)
            if (date != UtilsMethods.getTodayDateString()) {
                binding.btnEndSession.visibility = View.GONE
                binding.btnSetCost.visibility = View.GONE
            }
        }

        binding.btnSetCost.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.btnEndSession.setOnClickListener {
            viewModel.isCompleteOrder.observe(this.viewLifecycleOwner, {
                val context = this.context
                if (context != null)
                    if (it) {
                        AlertDialog.Builder(context)
                            .setMessage(R.string.confirmEndDay)
                            .setPositiveButton(
                                R.string.yes
                            ) { _, _ ->
                                val fragment = ReportCheckFragment.newInstance()
                                this.activity?.supportFragmentManager?.beginTransaction()
                                    ?.replace(R.id.fl_card_order_container, fragment)
                                    ?.commit()
                            }
                            .setNegativeButton(R.string.no) { dialog, _ ->
                                dialog.cancel()
                            }.create().show()

                    } else {
                        AlertDialog.Builder(context)
                            .setMessage(R.string.confirmEndOrder)
                            .setPositiveButton(
                                R.string.ok
                            ) { dialog, _ ->
                                dialog.cancel()
                            }.create().show()
                    }
            })
        }

        viewModel.statusLoad.observe(this.viewLifecycleOwner, { status ->
            when (status) {
                LoadPhoto.LOADING -> {
                    binding.addExpensesDrawer.apply {
                        viewModel.sendExpenses(
                            etNameExpenses.text.toString(),
                            etSumExpenses.text.toString().toFloat(),
                            viewModel.currentPhotoPath
                        )
                    }
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }

                LoadPhoto.DONE -> {
                    UtilsMethods.showToast(
                        this.context,
                        "Расход отправлен"
                    )
                }
                LoadPhoto.ERROR -> {
                    UtilsMethods.showToast(
                        this.context,
                        "Ошибка отправки"
                    )
                }
                else -> {}
            }
        })

        binding.addExpensesDrawer.btnPositive.setOnClickListener {
            if (chekExpenses(binding)) {
                viewModel.sendPhotoExpenses(
                    viewModel.currentPhotoPath
                )
//                viewModel.setStatusExpenses(Status.NONE)
                Timber.i(binding.addExpensesDrawer.ivPhotoChek.context.filesDir.path)
            } else {
                UtilsMethods.showToast(context, "Вы не заполнели все поля")
            }
        }

        binding.addExpensesDrawer.btnAddPhoto.setOnClickListener {
            try {
                dispatchTakePictureIntent(context)
//                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }


        }

        binding.addExpensesDrawer.btnNegativ.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    hideKeyboard(bottomSheet)
                    if (date != null) {
                        observeExpenses(date, binding)
                        observeReportDate(date, binding)
                    }
                    binding.addExpensesDrawer.apply {
                        etNameExpenses.text.clear()
                        etSumExpenses.text.clear()
                        ivPhotoChek.visibility = View.GONE
                        viewModel.currentPhotoPath = ""
                    }
                    Timber.i("hide")
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        return binding.root
    }

    private fun chekExpenses(
        binding: FragmentReportDayBinding,
    ): Boolean {
        return binding.addExpensesDrawer.etNameExpenses.text.isNotBlank() && binding.addExpensesDrawer.etSumExpenses.text.isNotBlank() && (binding.addExpensesDrawer.ivPhotoChek.isVisible)
    }

    private fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Фотка сделана, извлекаем миниатюру картинки
            binding.addExpensesDrawer.apply {
                ivPhotoChek.setImageURI(outputUri)
                ivPhotoChek.visibility = View.VISIBLE
            }
        }

    }

    private fun observeReportDate(date: String, binding: FragmentReportDayBinding) {
            viewModel.initDateReport(date)
            viewModel.reportDay.observe(viewLifecycleOwner, { reportDay ->
                binding.tvNumTotalOrders.text = "${reportDay.orderComplete}"
                binding.tvTankReport.text = "${reportDay.tank}"
                "${reportDay.totalMoney}руб.".also { binding.tvTotalMoney.text = it }
                "${reportDay.cashOnSite + reportDay.cashOnTerminal + reportDay.cashMoney}руб.".also {
                    binding.tvCashNumTotal.text = it
                }
                "${reportDay.cashOnSite}руб.".also { binding.tvCashNumOnSite.text = it }
                "${reportDay.cashOnTerminal}руб.".also { binding.tvCashNumOnTerminal.text = it }
                "${reportDay.cashMoney}руб.".also { binding.tvCashNumMoney.text = it }
                "${reportDay.noCashMoney}руб.".also { binding.tvNoCashNum.text = it }
                "${reportDay.cashOnSite}руб.".also { binding.tvCashNumOnSite.text = it }
                "${reportDay.moneyDelivery}руб.".also { binding.tvNumCashManyReport.text = it }
            })
    }

    private fun observeExpenses(date: String, binding: FragmentReportDayBinding) {
            viewModel.getExpenses(date)
            viewModel.expenses.observe(viewLifecycleOwner, {
                if (it.isNotEmpty()) {
                    binding.tvExpensesTitle.text = "Расходы"
                    adapter.submitList(it)
                } else {
                    binding.tvExpensesTitle.text = "Расходов нет"
                }
            })
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
        fun newInstance(): ReportFragment {
            return ReportFragment()
        }
    }
}