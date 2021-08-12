package ru.iwater.youwater.iwaterlogistic.screens.main

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.domain.Product
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.ListOrdersAdapter
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods

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
            }else {
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
            "Наличные: ${order?.cash}".also { text = it }
        } else {
            "Безналичные: ${order?.cash_b}".also { text = it }
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