package com.lefu.ppblutoothkit.device.usermanage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lefu.ppblutoothkit.R

/**
 * 设备用户列表：Torre 设备获取用户列表只返回 UserId，每行显示一个 UserId 和删除按钮
 */
class DeviceUserAdapter(
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<DeviceUserAdapter.ViewHolder>() {

    private val items = ArrayList<String>()

    /** 当前列表中的 UserId，用于判断预设用户是否已在设备上 */
    val userIds: List<String> get() = items

    /** 有操作进行中时禁用删除按钮，避免并发的用户指令互相覆盖 */
    var deleteEnabled: Boolean = true
        set(value) {
            if (field == value) return
            field = value
            notifyDataSetChanged()
        }

    fun setData(userIds: List<String>) {
        items.clear()
        items.addAll(userIds)
        notifyDataSetChanged()
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userId = items[position]
        holder.idTV.text = holder.itemView.context.getString(R.string.device_user_item_id, userId)
        holder.deleteBtn.isEnabled = deleteEnabled
        holder.deleteBtn.alpha = if (deleteEnabled) 1f else 0.5f
        holder.deleteBtn.setOnClickListener { onDeleteClick(userId) }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTV: TextView = itemView.findViewById(R.id.userItemId)
        val deleteBtn: TextView = itemView.findViewById(R.id.userItemDelete)
    }
}
