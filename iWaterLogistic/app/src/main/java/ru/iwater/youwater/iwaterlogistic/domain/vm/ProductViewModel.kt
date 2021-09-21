package ru.iwater.youwater.iwaterlogistic.domain.vm

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.domain.OpenDriverShift
import ru.iwater.youwater.iwaterlogistic.domain.Product
import ru.iwater.youwater.iwaterlogistic.repository.AccountRepository
import ru.iwater.youwater.iwaterlogistic.repository.ProductRepository
import ru.iwater.youwater.iwaterlogistic.screens.main.MainActivity
import ru.iwater.youwater.iwaterlogistic.util.HelpLoadingProgress
import ru.iwater.youwater.iwaterlogistic.util.HelpState
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@OnScreen
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    accountRepository: AccountRepository
): ViewModel() {
    private val account = accountRepository.getAccount()

    private val _products: MutableLiveData<List<Product>> = MutableLiveData()
    val product: LiveData<List<Product>>
        get() = _products

    private val _advancedProducts: MutableLiveData<List<Product>> = MutableLiveData()
    val advancedProduct: LiveData<List<Product>>
        get() = _advancedProducts

    init {
        getAdvancedProduct()
        getProductDay()
    }

    private fun getProductDay() {
        viewModelScope.launch {
            val products = productRepository.getLoadProductFromOrder(account.session)
            val correctProducts = mutableListOf<Product>()
            products.groupBy { it.name }.forEach {
                var count = 0
                it.value.forEach { product ->
                    count += product.count
                }
                correctProducts.add(Product(count, it.key))
            }
            _products.value = correctProducts
        }
    }

    private fun getAdvancedProduct() {
        viewModelScope.launch {
            val product = productRepository.getAdvancedProduct(account.id)
            _advancedProducts.value = product
        }
    }

    fun openDriverShift(context: Context) {
        viewModelScope.launch {
            val driverShift = OpenDriverShift(
                account.id,
                account.login,
                account.company,
                Calendar.getInstance().timeInMillis.toString(),
                UtilsMethods.getTodayDateString(),
                account.session
            )
            val message = productRepository.openDriverShift(driverShift)
            Timber.i("dasdadsasdadsadsadasda $message")
            if (message == "Status open shift sent" || message?.isEmpty() == true) {
                openDriverDay(context)
            }
            else {
                UtilsMethods.showToast(context, "Возможны проблемы с интернетом, проверте соединение и повторите попытку")
            }
        }
    }

    private fun openDriverDay(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        HelpLoadingProgress.setLoginProgress(context, HelpState.IS_WORK_START, false)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        ContextCompat.startActivity(context, intent, null)
    }
}