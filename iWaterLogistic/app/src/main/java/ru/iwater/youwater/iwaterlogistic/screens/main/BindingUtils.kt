package ru.iwater.youwater.iwaterlogistic.screens.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
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
 * Detail waterOrder binding
 **/

@BindingAdapter("titleOrder")
fun TextView.bindDateTitle(waterOrder: WaterOrder?) {
    if (waterOrder != null) {
        "Карточка заказа № ${waterOrder.order_id};".also { text = it }
    }
}

@BindingAdapter("addressOrder")
fun TextView.bindAddressOrder(address: String?) {
    text = address
}

@BindingAdapter("productOrder")
fun TextView.bindProductThis(waterOrder: WaterOrder?) {
    if (waterOrder != null) {
        val products = waterOrder.order
        when (waterOrder.type == "1") {
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
}

@BindingAdapter("setTypeOrder")
fun TextView.bindTypeOrder(waterOrder: WaterOrder?) {
    visibility = if (waterOrder?.type == "1") {
        View.GONE
    } else View.VISIBLE
}

@BindingAdapter("cashOrder")
fun TextView.bindCashOrder(waterOrder: WaterOrder?) {
    if (waterOrder != null) {
        if (waterOrder.cash?.isNotBlank() == true) {
            "Наличные: ${waterOrder.cash}".also { text = it }
        } else {
            if (waterOrder.cash_b.isNullOrEmpty()){
                "Безналичные: 0".also { text = it }
            } else "Безналичные: ${waterOrder.cash_b}".also { text = it }
        }
    } else text = ""
}

@BindingAdapter("pointNum")
fun TextView.bindPointNum(num: String?) {
    if (num != null) {
        "Точка #$num".also { text = it }
    } else text = "Ошибка"

}

@BindingAdapter("infoClient")
fun TextView.bindInfoClient(waterOrder: WaterOrder?) {
    if (waterOrder != null) {
        "${waterOrder.name};".also { text = it }
    } else text = ""
}

@BindingAdapter("timeOrder")
fun TextView.bindTimeOrder(waterOrder: WaterOrder?) {
    if (waterOrder != null) {
        "${waterOrder.time};".also { text = it }
    } else text = ""
}

@BindingAdapter("countBottle")
fun TextView.bindEmptyBottle(clientInfo: ClientInfo?) {
    if (clientInfo != null) {
        "Тары у клиента: ${clientInfo.return_tare};".also { text = it }
    } else text = "0"
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
fun bindRecycleView(recyclerView: RecyclerView, data: List<WaterOrder>?) {
    val adapter = recyclerView.adapter as ListOrdersAdapter
    adapter.submitList(data)
}

@BindingAdapter("descriptionOrder")
fun TextView.setDescriptionOrder(item: WaterOrder) {
    if (item.type == "0") {
        "Заказ ${item.time},\n${item.address}".also { text = it }
        for (product in item.order) {
            append("\n${product.name} - ${product.count}шт.")
        }
    } else {
        "Поручение ${item.time},\n${item.address},\n${item.notice}".also { text = it }
    }
}

@BindingAdapter("numOrder")
fun TextView.setNumOrder(item: WaterOrder) {
    text = "${item.num}"
    val time = item.time.split("-").last()
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
fun TextView.bindDateTitleInfo(order: OrderInfoNew?) {
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
    if (cash.isNullOrBlank()) {
        "Цена заказа: 0".also { text = it }
    } else "Цена заказа: $cash".also { text = it }
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
        TypeClient.ERROR -> View.VISIBLE
        else -> View.GONE
    }
}

/**
 * Reports screen binding
 **/

@BindingAdapter("cashMoney")
fun TextView.bindCashMoney(reportDay: ReportDay?) {
    if (reportDay != null) {
        val cash = reportDay.run {
            cashMoney + cashOnTerminal + cashOnSite
        }
        text = "$cash"
    }
}

@BindingAdapter("cashOnTerminal")
fun TextView.cashOnTerminal(cashOnTerminal: Float) {
    text = "$cashOnTerminal"
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

@BindingAdapter("setColorCard")
fun CardView.bindColor(typeOfCash: String) {
    if (typeOfCash == "-") setCardBackgroundColor(Color.RED) else setCardBackgroundColor(Color.GRAY)
}

/**
 * Products screen binding
 **/

@BindingAdapter("setNameProduct")
fun TextView.bindProductLabel(product: Product) {
    text = product.name
}

@BindingAdapter("setCountProduct")
fun TextView.bindProductCount(product: Product) {
    text = product.count.toString()
}