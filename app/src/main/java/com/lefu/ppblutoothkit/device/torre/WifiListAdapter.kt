package com.lefu.ppblutoothkit.device.torre

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lefu.ppblutoothkit.databinding.AdapterDeviceSearchItemLayoutBinding
import com.peng.ppscale.vo.PPWifiModel

class WifiListAdapter(var wifiList: List<PPWifiModel>) :
    RecyclerView.Adapter<WifiListAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterDeviceSearchItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wifiBean = wifiList[position]
        holder.bind(wifiBean, onItemClickListener)
    }

    override fun getItemCount(): Int = wifiList.size

    class ViewHolder(private val binding: AdapterDeviceSearchItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(wifiBean: PPWifiModel, listener: OnItemClickListener?) {
            binding.mWifiNameSsidSB.text = wifiBean.ssid
            binding.mWifiNameSignSB.text = wifiBean.sign.toString()
            
            binding.root.setOnClickListener {
                listener?.onItemClick(wifiBean)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(wifiBean: PPWifiModel)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }
}