package com.lefu.ppblutoothkit.device.apple

import android.text.TextUtils
import android.widget.Toast
import com.lefu.ppblutoothkit.util.SettingManager
import com.lefu.ppblutoothkit.R
import com.lefu.ppscale.db.dao.DBManager
import com.lefu.ppblutoothkit.okhttp.DataTask
import com.lefu.ppblutoothkit.okhttp.NetUtil
import com.lefu.ppblutoothkit.okhttp.RetCallBack
import com.peng.ppscale.business.ble.configWifi.PPConfigWifiAppleStateMenu
import com.peng.ppscale.business.ble.configWifi.PPConfigWifiInfoInterface
import com.lefu.ppbase.util.Logger
import com.lefu.ppbase.PPDeviceModel
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

        override fun monitorConfigFail(stateMenu: PPConfigWifiAppleStateMenu?) {
            when (stateMenu) {
                PPConfigWifiAppleStateMenu.CONFIG_STATE_LOW_BATTERY_LEVEL -> {
                    addPrint("Config wifi fail because: Low battery level")
                }

                PPConfigWifiAppleStateMenu.CONFIG_STATE_REGIST_FAIL -> {
                    addPrint("Config wifi fail because: login has failed")
                }

                PPConfigWifiAppleStateMenu.CONFIG_STATE_GET_CONFIG_FAIL -> {
                    addPrint("Config wifi fail because: Failed to obtain configuration")
                }

                PPConfigWifiAppleStateMenu.CONFIG_STATE_ROUTER_FAIL -> {
                    addPrint("Config wifi fail because: Unable to find route")
                }

                PPConfigWifiAppleStateMenu.CONFIG_STATE_PASSWORD_ERR -> {
                    addPrint("Config wifi fail because: Password error")
                }

                PPConfigWifiAppleStateMenu.CONFIG_STATE_OTHER_FAIL -> {
                    addPrint("Config wifi fail because: Other errors (app can be ignored)")
                }

                else -> {
                    addPrint("Config wifi fail")
                }
            }
        }
    }

fun addPrint(msg: String) {
    if (msg.isNotEmpty()) {
        Logger.e("$msg")
    }
}





