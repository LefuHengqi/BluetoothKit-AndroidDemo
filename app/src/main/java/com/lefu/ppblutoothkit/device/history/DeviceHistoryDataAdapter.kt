package com.lefu.ppblutoothkit.device.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lefu.ppbase.PPBodyBaseModel
import com.lefu.ppblutoothkit.R
import java.util.Locale

/**
 * 一条设备历史数据
 *
 * @param bodyBaseModel SDK 回调的原始数据，点击后直接用于计算身体数据
 * @param dateTime      SDK 回调的测量时间
 * @param memberId      仅用户历史数据有成员ID；游客数据、Apple/Ice 数据为 null，不显示
 */
data class DeviceHistoryItem(
    val bodyBaseModel: PPBodyBaseModel,
    val dateTime: String?,
    val memberId: String?
)

class DeviceHistoryDataAdapter(
    private val onItemClick: (DeviceHistoryItem) -> Unit
) : RecyclerView.Adapter<DeviceHistoryDataAdapter.ViewHolder>() {

    private val items = ArrayList<DeviceHistoryItem>()

    fun add(item: DeviceHistoryItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_history_data, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val timeTV: TextView = itemView.findViewById(R.id.historyItemTime)
        private val weightTV: TextView = itemView.findViewById(R.id.historyItemWeight)
        private val memberIdTV: TextView = itemView.findViewById(R.id.historyItemMemberId)

        fun bind(item: DeviceHistoryItem) {
            val context = itemView.context
            timeTV.text = context.getString(R.string.history_item_time, item.dateTime.orEmpty())
            //weight 是放大100倍的 kg 值，getPpWeightKg() 已换算为 kg
            val weightKg = String.format(Locale.US, "%.2f", item.bodyBaseModel.getPpWeightKg())
            weightTV.text = context.getString(R.string.history_item_weight, weightKg)
            if (item.memberId.isNullOrEmpty()) {
                memberIdTV.visibility = View.GONE
            } else {
                memberIdTV.visibility = View.VISIBLE
                memberIdTV.text = context.getString(R.string.history_item_member_id, item.memberId)
            }
        }
    }
}
