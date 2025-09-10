package com.lefu.ppblutoothkit.devicelist

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.PPScaleDefine
import com.lefu.ppblutoothkit.R
import java.util.Locale

class DeviceListAdapter : BaseQuickAdapter<DeviceVo?, BaseViewHolder?>(R.layout.activity_scan_list_item) {
    override fun convert(holder: BaseViewHolder?, deviceVo: DeviceVo?) {
        deviceVo?.let {
            holder?.let {
                val deviceModel = deviceVo.getDeviceModel()
                if (deviceModel != null) {
                    val nameText = holder.getView<TextView>(R.id.device_name)
                    val macText = holder.getView<TextView>(R.id.device_mac)
                    val device_rssi = holder.getView<TextView>(R.id.device_rssi)
                    val device_type = holder.getView<TextView>(R.id.device_type)
                    val device_AdvLen = holder.getView<TextView>(R.id.device_AdvLen)
                    val device_sign = holder.getView<TextView>(R.id.device_sign)
                    nameText.setText("devName:" + deviceModel.deviceName)
                    macText.setText("Mac:" + deviceModel.deviceMac)
                    device_AdvLen.setText("advLength: " + deviceModel.advLength)
                    device_sign.setText("sign: " + deviceModel.sign)

                    device_rssi.setText(String.format(Locale.getDefault(), "RSSI: %d dBm", deviceModel.rssi))
                    device_type.setText("PeripheralType:" + deviceModel.getDevicePeripheralType())
                }
            }

        }

    }

    fun isFuncTypeWifi(device: PPDeviceModel?): Boolean {
        if (device != null) {
            return ((device.deviceFuncType and PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeWifi.getType())
                    == PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeWifi.getType())
        } else {
            return false
        }
    }
}
