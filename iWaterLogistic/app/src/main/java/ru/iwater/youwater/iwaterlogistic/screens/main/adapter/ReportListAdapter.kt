package ru.iwater.youwater.iwaterlogistic.screens.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.iwaterlogistic.databinding.ItemReportDayBinding
import ru.iwater.youwater.iwaterlogistic.domain.ReportDay

class ReportListAdapter(private val onClickListener: OnClickListener) : ListAdapter<ReportDay, ReportListAdapter.ReportListHolder>(ReportDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportListHolder {
        return ReportListHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ReportListHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(item)
        }
        holder.bindReport(item, position)
    }

    class ReportListHolder(val binding: ItemReportDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindReport(reportDay: ReportDay, position: Int) {
            binding.reportDay = reportDay
            binding.executePendingBindings()
            "${position + 2}".also { binding.tvNumReport.text = it }
            "Отчет за ${reportDay.date}".also { binding.tvNameReport.text = it }
        }

        companion object {
            fun from(parent: ViewGroup): ReportListHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemReportDayBinding.inflate(layoutInflater, parent, false)
                return ReportListHolder(binding)
            }
        }
    }

    companion object ReportDiffCallback : DiffUtil.ItemCallback<ReportDay>() {
        override fun areItemsTheSame(oldItem: ReportDay, newItem: ReportDay): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ReportDay, newItem: ReportDay): Boolean {
            return oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (reportDay: ReportDay) -> Unit) {
        fun onClick(reportDay: ReportDay) = clickListener(reportDay)
    }

}