package ru.iwater.youwater.iwaterlogistic.screens.main

import android.annotation.SuppressLint
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.domain.*
import ru.iwater.youwater.iwaterlogistic.domain.vm.TypeClient
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.CompleteListOrdersAdapter
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.ListOrdersAdapter
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.ReportListAdapter
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods
import java.text.SimpleDateFormat
import java.util.*

/**
 * Detail order binding
 **/

@BindingAdapter("titleOrder")
fun TextView.bindDateTitle(order: Order?) {
    if (order != null) {
        "№ ${order.id}, ${order.time}".also { text = it }
    }
}

@BindingAdapter("addressOrder")
fun TextView.bindAddressOrder(address: String?) {
    text = address
}

@BindingAdapter("productOrder")
fun TextView.bindProductThis(products: List<Product>?) {
    when (products == null) {
        true -> {
            visibility = View.GONE
        }
        else -> {
            visibility = View.VISIBLE
            if (products.size > 1) {
                text = ""
                for (product in products) {
                    append("${product.name} - ${product.count}шт.\n")
                }
            } else {
                "${products[0].name} - ${products[0].count}шт.".also {
                    text = it
                }
            }
        }
    }

}

@BindingAdapter("cashOrder")
fun TextView.bindCashOrder(order: Order?) {
    if (order != null) {
        if (order.cash.isNotBlank()) {
            "Наличные: ${order.cash}".also { text = it }
        } else {
            "Безналичные: ${order.cash_b}".also { text = it }
        }
    } else text = ""
}

@BindingAdapter("pointNum")
fun TextView.bindPointNum(num: Int?) {
    if (num != null) {
        "Точка #$num".also { text = it }
    } else text = "Ошибка"

}

@BindingAdapter("infoClient")
fun TextView.bindInfoClient(order: Order?) {
    if (order != null) {
        "${order.name}; \n${order.address};".also { text = it }
    } else text = ""
}

@BindingAdapter("phoneClient")
fun TextView.bindPhoneClient(contact: String?) {
    text = contact ?: ""
}

@BindingAdapter("notice")
fun TextView.bindNotice(notice: String?) {
    text = notice ?: ""

}


/**
 * LisOrderAdapter binding
 **/

@BindingAdapter("listOrder")
fun bindRecycleView(recyclerView: RecyclerView, data: List<Order>?) {
    val adapter = recyclerView.adapter as ListOrdersAdapter
    adapter.submitList(data)
}

@BindingAdapter("descriptionOrder")
fun TextView.setDescriptionOrder(item: Order) {
    "Заказ ${item.time},\n${item.address}".also { text = it }
    for (product in item.products) {
        append("\n${product.name} - ${product.count}шт.")
    }
}

@BindingAdapter("numOrder")
fun TextView.setNumOrder(item: Order) {
    text = "${item.num}"
    val time = item.time.split("-")[1]
    if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) > 7200) {
        setBackgroundResource(R.drawable.circle_green)
    }

    if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) in 3601..7199) {
        setBackgroundResource(R.drawable.circle_yellow)
    }

    if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) < 3600) {
        setBackgroundResource(R.drawable.circle_red)
    }

    if (UtilsMethods.timeDifference(time, UtilsMethods.getFormatedDate()) < 0) {
        setBackgroundResource(R.drawable.circle_grey)
    }
}

/**
 * About and Shipments screen binding
 **/

@BindingAdapter("titleOrderInfo")
fun TextView.bindDateTitleInfo(order: OrderInfo?) {
    if (order != null) {
        "№ ${order.id}, ${order.time}".also { text = it }
    }
}

@BindingAdapter("addressOrderInfo")
fun TextView.bindAddressOrderInfo(address: String?) {
    text = address
}

@BindingAdapter("cashOrderInfo")
fun TextView.bindCashOrderInfo(cash: String?) {
    "Цена заказа: $cash".also { text = it }
}

@BindingAdapter("IvVisOfType")
fun ImageView.bindTypeClient(typeClient: TypeClient?) {
    visibility = when (typeClient) {
        TypeClient.JURISTIC -> View.VISIBLE
        TypeClient.PHYSICS -> View.GONE
        TypeClient.ERROR -> View.GONE
        else -> View.GONE
    }
}
@BindingAdapter("tvVisOfType")
fun TextView.bindTypeClient(typeClient: TypeClient?) {
    visibility = when (typeClient) {
        TypeClient.JURISTIC -> View.VISIBLE
        TypeClient.PHYSICS -> View.GONE
        TypeClient.ERROR -> View.GONE
        else -> View.GONE
    }
}
@BindingAdapter("isCheckDocument")
fun bindCheckBox(documents: CheckBox, typeClient: TypeClient?) {
    documents.visibility = when (typeClient) {
        TypeClient.JURISTIC -> View.VISIBLE
        TypeClient.PHYSICS -> View.GONE
        TypeClient.ERROR -> View.GONE
        else -> View.GONE
    }
}

@BindingAdapter("isCheckRadio")
fun bindRadioGroup(typeCash: RadioGroup, typeClient: TypeClient?) {
    typeCash.visibility = when (typeClient) {
        TypeClient.JURISTIC -> View.GONE
        TypeClient.PHYSICS -> View.VISIBLE
        TypeClient.ERROR -> View.GONE
        else -> View.GONE
    }
}

/**
 * Reports screen binding
 **/

@BindingAdapter("totalMoney")
fun TextView.bindTotalMoney(totalMoney: Float) {
    text = "$totalMoney"
}

@BindingAdapter("cashMoney")
fun TextView.bindCashMoney(reportDay: ReportDay?) {
    if (reportDay != null) {
        val cash = reportDay.run {
            cashMoney + cashOnTerminal + cashOnSite
        }
        text = "$cash"
    }
}

@BindingAdapter("noCashMoney")
fun TextView.bindNoCashMoney(noCashMoney: Float) {
    text = "$noCashMoney"
}

@BindingAdapter("moneyDelivery")
fun TextView.bindMoneyDelivery(moneyDelivery: Float){
    text = "$moneyDelivery"
}

@BindingAdapter("listReport")
fun bindRecycleReport(recyclerView: RecyclerView, data: List<ReportDay>?) {
    val adapter = recyclerView.adapter as ReportListAdapter
    adapter.submitList(data)
}

/**
 * CompleteOrders screen binding
 **/
@BindingAdapter("listComplete")
fun bindRecycleCompleteOrder(recyclerView: RecyclerView, data: List<CompleteOrder>?) {
    val adapter = recyclerView.adapter as CompleteListOrdersAdapter
    adapter.submitList(data)
}

@SuppressLint("SimpleDateFormat")
@BindingAdapter("completeDescription")
fun TextView.bindCompleteOrderInfo(completeOrder: CompleteOrder?) {
    if (completeOrder != null) {
        val sdf = SimpleDateFormat("HH:mm:ss")
        val time = Date(completeOrder.timeComplete * 1000)
        val date = sdf.format(time)
        "№${completeOrder.id} ${completeOrder.time} Завершен в ${date}, ${completeOrder.address}".also {
            text = it
        }
    }
}