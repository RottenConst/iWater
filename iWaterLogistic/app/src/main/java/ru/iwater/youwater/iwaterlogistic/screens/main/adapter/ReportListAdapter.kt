package ru.iwater.youwater.iwaterlogistic.screens.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.iwaterlogistic.databinding.ItemReportDayBinding
import ru.iwater.youwater.iwaterlogistic.domain.ReportDay

class ReportListAdapter(
    val reportDayList: MutableList<ReportDay> = mutableListOf()
) : RecyclerView.Adapter<ReportListAdapter.ReportListHolder>() {

    lateinit var onReportClick: ((ReportDay) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportListHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ReportListHolder(ItemReportDayBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ReportListHolder, position: Int) {
        holder.bindReport(reportDayList[position], position)
    }

    override fun getItemCount(): Int = reportDayList.size

    inner class ReportListHolder(val binding: ItemReportDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.cvReportCard.setOnClickListener { onReportClick.invoke(reportDayList[adapterPosition]) }
        }

        fun bindReport(reportDay: ReportDay, position: Int) {
            "${position + 2}".also { binding.tvNumReport.text = it }
            "Отчет за ${reportDay.date}".also { binding.tvNameReport.text = it }
        }
    }

}