package com.lefu.test260h

import android.bluetooth.BluetoothAdapter
import android.graphics.Color
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.lefu.test260h.vo.Devcie

class DeviceAdapter() : BaseQuickAdapter<Devcie, BaseViewHolder>(R.layout.device_list_item) {

    var count: Int = 20

    override fun convert(helper: BaseViewHolder, item: Devcie) {
        val deviceNameTV = helper.getView<TextView>(R.id.deviceNameTV)
        val deviceMacTV = helper.getView<TextView>(R.id.deviceMacTV)
        val rssiTv = helper.getView<TextView>(R.id.rssiTv)
        val countTv = helper.getView<TextView>(R.id.countTv)
        val countLockTv = helper.getView<TextView>(R.id.countLockTv)
        val item_layout = helper.getView<LinearLayout>(R.id.item_layout)

        deviceNameTV.text = item.name
        deviceMacTV.text = item.mac

        rssiTv.text = "rssi:${item.rssi}"
        countTv.text = "count:${item.countProcess}"

        countLockTv.text = "lock:${item.countLock}"

        if ((item.countProcess?.plus(item.countLock ?: 0) ?: 0) < count) {//红色
            item_layout.setBackgroundColor(Color.RED)
        } else {
            item_layout.setBackgroundColor(Color.GREEN)
        }
    }

}