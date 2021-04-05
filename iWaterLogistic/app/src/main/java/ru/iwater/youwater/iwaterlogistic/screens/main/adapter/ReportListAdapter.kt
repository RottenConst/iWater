package ru.iwater.youwater.iwaterlogistic.screens.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_report_day.view.*
import kotlinx.android.synthetic.main.item_report_day.view.*
import ru.iwater.youwater.iwaterlogistic.R
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.domain.ReportDay
import ru.iwater.youwater.iwaterlogistic.util.UtilsMethods

class ReportListAdapter(
    val reportDayList: MutableList<ReportDay> = mutableListOf()
) : RecyclerView.Adapter<ReportListAdapter.ReportListHolder>() {

    lateinit var onReportClick: ((ReportDay) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportListHolder {
        return ReportListHolder(LayoutInflater.from(parent.context), parent, R.layout.item_report_day)
    }

    override fun onBindViewHolder(holder: ReportListHolder, position: Int) {
        holder.bindReport(reportDayList[position], position)
    }

    override fun getItemCount(): Int = reportDayList.size

    inner class ReportListHolder(inflater: LayoutInflater, parent: ViewGroup, resource: Int):
        RecyclerView.ViewHolder(inflater.inflate(resource, parent, false)) {
            init {
                itemView.cv_report_card.setOnClickListener { onReportClick.invoke(reportDayList[adapterPosition]) }
            }

            fun bindReport(reportDay: ReportDay, position: Int) {
                    itemView.tv_num_report.text = "${position + 2}"
                    itemView.tv_name_report.text = "Отчет за ${reportDay.date}"
            }
    }

}