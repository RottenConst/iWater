package ru.iwater.youwater.iwaterlogistic.screens.main.tab.report

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.databinding.FragmentReportDayBinding
import ru.iwater.youwater.iwaterlogistic.domain.vm.ReportViewModel
import ru.iwater.youwater.iwaterlogistic.domain.vm.Status
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.ExpensesAdapter
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

private const val REQUEST_IMAGE_CAPTURE = 1

class ReportFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: ReportViewModel by viewModels { factory }
    private val adapter = ExpensesAdapter()

    private val screenComponent = App().buildScreenComponent()
    private lateinit var binding: FragmentReportDayBinding
    private var currentPhotoPath: String = ""


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

        viewModel.status.observe(this.viewLifecycleOwner, { status ->
            when (status) {
                Status.DONE -> {
                    viewModel.addExpensesInBD(
                        binding.addExpensesDrawer.etNameExpenses.text.toString(),
                        binding.addExpensesDrawer.etSumExpenses.text.toString().toFloat(),
                        currentPhotoPath
                    )
                    UtilsMethods.showToast(
                        this.context,
                        binding.addExpensesDrawer.etNameExpenses.text.toString()
                    )
                }
                Status.ERROR -> {
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
                binding.addExpensesDrawer.apply {
                    viewModel.sendExpenses(
                        etNameExpenses.text.toString(),
                        etSumExpenses.text.toString().toFloat(),
                        currentPhotoPath
                    )
                }
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                viewModel.setStatusExpenses(Status.NONE)
                Timber.i(binding.addExpensesDrawer.ivPhotoChek.context.filesDir.path)
            } else {
                UtilsMethods.showToast(context, "Вы не заполнели все поля")
            }
        }

        binding.addExpensesDrawer.btnAddPhoto.setOnClickListener {
            try {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
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
                        currentPhotoPath = ""
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
        return binding.addExpensesDrawer.etNameExpenses.text.isNotBlank() && binding.addExpensesDrawer.etSumExpenses.text.isNotBlank() && (currentPhotoPath.isNotBlank())
    }

    private fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Фотка сделана, извлекаем миниатюру картинки
            if (data != null) {
                if (data.hasExtra("data")) {
                    val thumbnailBitmap = data.extras?.get("data") as Bitmap
                    saveImage(thumbnailBitmap)
                    binding.addExpensesDrawer.apply {
                        ivPhotoChek.setImageBitmap(thumbnailBitmap)
                        ivPhotoChek.visibility = View.VISIBLE
                    }

                }
            }
        }

    }

    private fun saveImage(bitmap: Bitmap) {
        val cw = ContextWrapper(screenComponent.appContext())
        val directory = cw.getDir("expenses", Context.MODE_APPEND)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val myPath = File(directory, "check_${System.currentTimeMillis()}.jpg")
        try {
            val fos = FileOutputStream(myPath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            currentPhotoPath = myPath.path
            Timber.i(myPath.path)
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
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

    companion object {
        fun newInstance(): ReportFragment {
            return ReportFragment()
        }
    }
}