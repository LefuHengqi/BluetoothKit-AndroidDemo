package com.lefu.ppblutoothkit.device.apple

import android.text.TextUtils
import android.widget.Toast
import com.lefu.base.SettingManager
import com.lefu.ppscale.db.dao.DBManager
import com.lefu.ppscale.wifi.R
import com.lefu.ppblutoothkit.okhttp.DataTask
import com.lefu.ppblutoothkit.okhttp.NetUtil
import com.lefu.ppblutoothkit.okhttp.RetCallBack
import com.peng.ppscale.business.ble.configWifi.PPConfigWifiInfoInterface
import com.peng.ppscale.util.Logger
import com.peng.ppscale.vo.PPDeviceModel
import java.util.HashMap

val BleConfigWifiActivity.configWifiInfoInterface: PPConfigWifiInfoInterface
    get() = object : PPConfigWifiInfoInterface {

        override fun monitorConfigSn(sn: String?, deviceModel: PPDeviceModel?) {
            tvNext!!.isEnabled = true
            //拿到sn 处理业务逻辑
            Logger.e("xxxxxxxxxxxx-$sn")
            Logger.e("xxxxxxxxxxxx-deviceName = " + deviceModel?.deviceName + " mac = " + deviceModel?.deviceMac)
            snTv?.text = "sn:$sn"
            val map: MutableMap<String, String?> = HashMap()
            map["sn"] = sn
            map["uid"] = SettingManager.get().uid
            //All networks need to be equipped with network interfaces that are compatible with the body fat scale,

            DataTask.post(NetUtil.SAVE_WIFI_GROUP, map, object : RetCallBack<SaveWifiGroupBean>(SaveWifiGroupBean::class.java) {

                override fun onResponse(response: SaveWifiGroupBean?, p1: Int) {
                    response?.let {
                        if (response.isStatus) {
                            Toast.makeText(this@configWifiInfoInterface, R.string.config_wifi_success, Toast.LENGTH_SHORT).show()
                            val device = DBManager.manager().getDevice(address)
                            if (device != null) {
                                device.sn = sn
                                device.ssid = ssid ?: ""
                                DBManager.manager().updateDevice(device)
                            }
                        } else {
                            val content = if (TextUtils.isEmpty(response.msg)) getString(R.string.config_wifi_fail) else response.msg
                            Toast.makeText(this@configWifiInfoInterface, content, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onError(p0: okhttp3.Call?, p1: java.lang.Exception?, p2: Int) {
                    Toast.makeText(this@configWifiInfoInterface, R.string.config_wifi_fail, Toast.LENGTH_SHORT).show()
                }
            }
            )
        }

        override fun monitorModifyServerDomainSuccess() {
            Logger.d("monitorModifyServerDomainSuccess")
            startConfigWifi()
        }

        override fun monitorConfigFail() {
            Logger.e("configwifi  monitorConfigFail")
        }
    }



