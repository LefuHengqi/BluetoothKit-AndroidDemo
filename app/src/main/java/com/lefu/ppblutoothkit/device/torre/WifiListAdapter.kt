package com.lefu.ppblutoothkit.device.torre

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lefu.ppblutoothkit.R
import com.peng.ppscale.vo.PPWifiModel
import kotlinx.android.synthetic.main.adapter_device_search_item_layout.view.*

class WifiListAdapter : RecyclerView.Adapter<WifiListAdapter.ViewHolder>() {

    var users: MutableList<PPWifiModel> = mutableListOf()
    var onItemClickViewInsideListener: OnItemClickViewInsideListener? = null

    override fun getItemViewType(position: Int) = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_device_search_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
        holder.itemView.setOnClickListener {
            onItemClickViewInsideListener?.onItemClickViewInside(position, holder.itemView)
        }
    }

    override fun getItemCount() = users.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(wifiModel: PPWifiModel) {
            itemView.mWifiNameSsidSB.text = wifiModel.ssid
            itemView.mWifiNameSignSB.text = "rssi:${wifiModel.sign}"
        }
    }

    fun setOnClickInItemLisenter(onItemClickViewInsideListener: OnItemClickViewInsideListener) {
        this.onItemClickViewInsideListener = onItemClickViewInsideListener
    }

    interface OnItemClickViewInsideListener {
        /**
         * @param position 列表项在列表中的位置
         * @param v        列表项被点击的子视图
         */
        fun onItemClickViewInside(position: Int, v: View?)
    }


}