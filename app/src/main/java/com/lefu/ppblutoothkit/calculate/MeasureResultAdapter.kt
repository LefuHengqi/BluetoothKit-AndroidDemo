package com.lefu.ppblutoothkit.calculate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lefu.ppblutoothkit.R

class MeasureResultAdapter : RecyclerView.Adapter<MeasureResultAdapter.ViewHolder>() {

    private var items: List<MeasureResultItem> = emptyList()

    fun submit(newItems: List<MeasureResultItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_measure_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val nameTV: TextView = itemView.findViewById(R.id.measureResultName)
        private val valueTV: TextView = itemView.findViewById(R.id.measureResultValue)
        private val unitTV: TextView = itemView.findViewById(R.id.measureResultUnit)
        private val statusTV: TextView = itemView.findViewById(R.id.measureResultStatus)

        fun bind(item: MeasureResultItem) {
            nameTV.text = item.name
            valueTV.text = item.value
            valueTV.textSize = if (item.isTextValue) 16f else 24f
            unitTV.text = item.unit
            unitTV.visibility = if (item.unit.isBlank()) View.GONE else View.VISIBLE
            statusTV.text = item.status
            //保留状态行高度，避免同一行卡片高度不一致
            statusTV.visibility = if (item.status.isBlank()) View.INVISIBLE else View.VISIBLE
        }
    }
}