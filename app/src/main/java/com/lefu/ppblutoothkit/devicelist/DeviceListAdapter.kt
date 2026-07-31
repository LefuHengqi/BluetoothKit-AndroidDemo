package com.lefu.ppblutoothkit.devicelist

import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.PPScaleDefine
import com.lefu.ppblutoothkit.R
import com.peng.ppscale.business.ble.PPScaleHelper
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
                    val device_needAuth = holder.getView<TextView>(R.id.device_needAuth)
                    val device_httpType = holder.getView<TextView>(R.id.device_httpType)
                    val device_supportADN = holder.getView<TextView>(R.id.device_supportADN)
                    val device_wifiProtocolType = holder.getView<TextView>(R.id.device_wifiProtocolType)

                    nameText.setText("DeviceName:" + deviceModel.deviceName)
                    macText.setText("Mac:" + deviceModel.deviceMac)
                    device_AdvLen.setText("AdvLength: " + deviceModel.advLength)
                    device_sign.setText("Sign: " + deviceModel.sign)

                    if (deviceModel.deviceType == PPScaleDefine.PPDeviceType.PPDeviceTypeCF) {
                        //体脂秤
                        device_productType.setText("Product: " + CalculateUtil.getProduct(deviceModel.deviceCalcuteType.getType()))
                        device_productType?.visibility = View.VISIBLE
                        device_calculateType.setText("CalculateType: " + deviceModel.deviceCalcuteType.name)
                        device_calculateType?.visibility = View.VISIBLE
                    } else {
                        //非体脂秤
                        device_productType?.visibility = View.GONE
                        device_calculateType?.visibility = View.GONE
                    }

                    device_rssi.setText(String.format(Locale.getDefault(), "RSSI: %d dBm", deviceModel.rssi))
                    device_type.setText("PeripheralType:" + deviceModel.getDevicePeripheralType())

                    // 显示bit5: 鉴权
                    device_needAuth.setText(
                        "NeedAuth: " + if (deviceModel.needAuth)
                            mContext.getString(R.string.support)
                        else
                            mContext.getString(R.string.no_support)
                    )

                    if (com.peng.ppscale.business.ble.PPScaleHelper.isFuncTypeWifi(deviceModel.deviceFuncType)) {
                        // 显示bit6: 服务器类型
                        device_httpType.setText(
                            "HttpScheme: " + if (deviceModel.httpType == 1)
                                "HTTPS"
                            else
                                "HTTP"
                        )
                        device_httpType?.visibility = View.VISIBLE

                        // 显示bit7: 无WIFI列表请求配网
                        device_supportADN.setText(
                            "SupportADN: " + if (deviceModel.isSupportADN)
                                mContext.getString(R.string.support)
                            else
                                mContext.getString(R.string.no_support)
                        )
                        device_supportADN?.visibility = View.VISIBLE
                        device_wifiProtocolType.setText(
                            "WifiProtocolType: " +
                                    if (deviceModel.deviceProtocolType == PPScaleDefine.PPDeviceProtocolType.PPDeviceProtocolTypeV2) {
                                        "V2.0"
                                    } else if (deviceModel.deviceProtocolType == PPScaleDefine.PPDeviceProtocolType.PPDeviceProtocolTypeV3) {
                                        "V3.0"
                                    } else if (deviceModel.deviceProtocolType == PPScaleDefine.PPDeviceProtocolType.PPDeviceProtocolTypeV4) {
                                        "V4.0"
                                    } else if (PPScaleHelper.isTorre(deviceModel.deviceProtocolType.getType())
                                        || PPScaleHelper.isBorre(deviceModel.deviceProtocolType.getType())
                                        || deviceModel.deviceProtocolType == PPScaleDefine.PPDeviceProtocolType.PPDeviceProtocolTypeDorre
                                       ) {
                                        "Torre"
                                    } else {
                                        "Unknown"
                                    }
                        )
                        device_wifiProtocolType?.visibility = View.VISIBLE
                    } else {
                        device_httpType.visibility = View.GONE
                        device_supportADN.visibility = View.GONE
                        device_wifiProtocolType.visibility = View.GONE
                    }

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
