package com.lefu.ppblutoothkit.device.usermanage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lefu.ppblutoothkit.R

/**
 * 设备上的一个成员
 *
 * @param name 预设测试用户的名字（按 userID + memberID 匹配），匹配不到为空，显示“--”
 */
data class DeviceMemberItem(
    val userId: String,
    val memberId: String,
    val name: String
)

/**
 * 成员列表：Borre/Dorre 获取用户列表返回完整成员，每行显示 UserId、MemberId、Name 和删除按钮
 */
class DeviceMemberAdapter(
    private val onDeleteClick: (DeviceMemberItem) -> Unit
) : RecyclerView.Adapter<DeviceMemberAdapter.ViewHolder>() {

    private val items = ArrayList<DeviceMemberItem>()

    /** 当前列表中的成员，用于判断预设用户是否已在设备上 */
    val members: List<DeviceMemberItem> get() = items

    /** 有操作进行中时禁用删除按钮，避免并发的用户指令互相覆盖 */
    var deleteEnabled: Boolean = true
        set(value) {
            if (field == value) return
            field = value
            notifyDataSetChanged()
        }

    fun setData(members: List<DeviceMemberItem>) {
        items.clear()
        items.addAll(members)
        notifyDataSetChanged()
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_member, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context
        holder.userIdTV.text = context.getString(R.string.device_user_item_id, item.userId)
        holder.memberIdTV.text = context.getString(R.string.device_member_item_member_id, item.memberId)
        holder.nameTV.text = context.getString(R.string.device_member_item_name, item.name.ifEmpty { "--" })
        holder.deleteBtn.isEnabled = deleteEnabled
        holder.deleteBtn.alpha = if (deleteEnabled) 1f else 0.5f
        holder.deleteBtn.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userIdTV: TextView = itemView.findViewById(R.id.memberItemUserId)
        val memberIdTV: TextView = itemView.findViewById(R.id.memberItemMemberId)
        val nameTV: TextView = itemView.findViewById(R.id.memberItemName)
        val deleteBtn: TextView = itemView.findViewById(R.id.memberItemDelete)
    }
}
