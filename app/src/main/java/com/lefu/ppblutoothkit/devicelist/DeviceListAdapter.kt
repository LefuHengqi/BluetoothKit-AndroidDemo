package com.lefu.ppblutoothkit.devicelist

import android.annotation.SuppressLint
import android.view.View
import android.widget.LinearLayout
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
    @SuppressLint("SetTextI18n")
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
                    val device_calculateAPI = holder.getView<TextView>(R.id.device_calculateAPI)

                    val mWifiModeLL = holder.getView<LinearLayout>(R.id.mWifiModeLL)
                    val mWifiModeTitleTV = holder.getView<TextView>(R.id.mWifiModeTitleTV)

                    val mComputingLL = holder.getView<LinearLayout>(R.id.mComputingLL)
                    val mComputingTitleTV = holder.getView<TextView>(R.id.mComputingTitleTV)

                    nameText.setText("DeviceName:" + deviceModel.deviceName)
                    macText.setText("Mac:" + deviceModel.deviceMac)
                    device_AdvLen.setText("AdvLength: " + deviceModel.advLength)
                    device_sign.setText("Sign: " + deviceModel.sign)

                    if (deviceModel.deviceType == PPScaleDefine.PPDeviceType.PPDeviceTypeCF) {
                        //体脂秤
                        device_productType.setText("Product: " + CalculateUtil.getProduct(deviceModel.deviceCalcuteType.getType()))
                        device_calculateType.setText("CalculateType: " + deviceModel.deviceCalcuteType.name)
                        device_calculateAPI.text = "CalculateAPI: ${getCalculateAPI(deviceModel)}"
                        mComputingTitleTV?.visibility = View.VISIBLE
                        mComputingLL?.visibility = View.VISIBLE
                    } else {
                        //非体脂秤
                        mComputingTitleTV?.visibility = View.GONE
                        mComputingLL?.visibility = View.GONE
                    }

                    device_rssi.setText(String.format(Locale.getDefault(), "RSSI: %d dBm", deviceModel.rssi))
                    device_type.setText("PeripheralType:" + deviceModel.getDevicePeripheralType())

                    // 显示bit5: 鉴权
                    device_needAuth.text = "NeedAuth: " + if (deviceModel.needAuth) "YES" else "NO"

                    if (PPScaleHelper.isFuncTypeWifi(deviceModel.deviceFuncType)) {
                        // 显示bit6: 服务器类型
                        device_httpType.setText(
                            "HttpScheme: " + if (deviceModel.httpType == 1) "HTTPS" else "HTTP"
                        )
                        // 显示bit7: 无WIFI列表请求配网
                        device_supportADN.setText(
                            "SupportADN: " + if (deviceModel.isSupportADN) "YES" else "NO"
                        )
                        device_wifiProtocolType.text = "WifiProtocolType: ${getWifiProtocalType(deviceModel)}"
                        mWifiModeLL?.visibility = View.VISIBLE
                        mWifiModeTitleTV?.visibility = View.VISIBLE
                    } else {
                        mWifiModeLL?.visibility = View.GONE
                        mWifiModeTitleTV?.visibility = View.GONE
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

    private fun getWifiProtocalType(deviceModel: PPDeviceModel): String = if (deviceModel.deviceProtocolType == PPScaleDefine.PPDeviceProtocolType.PPDeviceProtocolTypeV2) {
        "V2.0/V3.0 Protocol"
    } else if (deviceModel.deviceProtocolType == PPScaleDefine.PPDeviceProtocolType.PPDeviceProtocolTypeV3) {
        "V2.0/V3.0 Protocol"
    } else if (deviceModel.deviceProtocolType == PPScaleDefine.PPDeviceProtocolType.PPDeviceProtocolTypeV4) {
        "Torre/V4.0 Protocol"
    } else if (PPScaleHelper.isTorre(deviceModel.deviceProtocolType.getType())
        || PPScaleHelper.isBorre(deviceModel.deviceProtocolType.getType())
        || deviceModel.deviceProtocolType == PPScaleDefine.PPDeviceProtocolType.PPDeviceProtocolTypeDorre
    ) {
        "Torre/V4.0 Protocol"
    } else {
        "Unknown"
    }

    /**
     * AC Four-Electrode Algorithm
     * DC Four-Electrode Algorithm
     * DC Four-Electrode Algorithm v2.0
     * Dual-Frequency AC Four-Electrode Algorithm
     * AC Eight-Electrode Algorithm
     * AC Eight-Electrode Algorithm Smooth
     */

    private fun getCalculateAPI(deviceModel: PPDeviceModel): String {
        val type = deviceModel.deviceCalcuteType
        return when (type) {
            PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeDirect -> "AC Four-Electrode Algorithm"
            PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate,
            PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeNormal -> "DC Four-Electrode Algorithm v2.0"
            PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_0 -> "DC Four-Electrode Algorithm"
            PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_1 -> "Dual-Frequency AC Four-Electrode Algorithm"
            PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_0,
            PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_1,
            PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_2,
            PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_3,
            PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_4,
            PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8 -> "AC Eight-Electrode Algorithm"
            PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_5 -> "AC Eight-Electrode Algorithm Smooth"
            else -> "DC Four-Electrode Algorithm"
        }
    }
}


