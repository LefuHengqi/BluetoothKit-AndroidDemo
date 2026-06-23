package com.lefu.ppblutoothkit.devicelist

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.PPScaleDefine
import com.lefu.ppblutoothkit.R
import com.lefu.ppcalculate.calcute.CalculateUtil
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
                    val device_productType = holder.getView<TextView>(R.id.device_productType)
                    val device_calculateType = holder.getView<TextView>(R.id.device_calculateType)
                    nameText.setText("DeviceName:" + deviceModel.deviceName)
                    macText.setText("Mac:" + deviceModel.deviceMac)
                    device_AdvLen.setText("AdvLength: " + deviceModel.advLength)
                    device_sign.setText("Sign: " + deviceModel.sign)
                    device_productType.setText("Product: " + CalculateUtil.getProduct(deviceModel.deviceCalcuteType.getType()))
                    device_calculateType.setText("CalculateType: " + deviceModel.deviceCalcuteType.name)

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
