package com.lefu.ppblutoothkit.device.torre

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.lefu.ppblutoothkit.instance.PPBlutoothPeripheralTorreInstance
import com.lefu.ppscale.ble.R
import com.peng.ppscale.business.ble.configWifi.PPConfigStateMenu
import com.peng.ppscale.business.torre.listener.PPTorreConfigWifiInterface

class PeripheralTorreConfigWifiActivity : Activity() {

    var configResultTV: TextView? = null

    companion object {
        var ssid = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_config_torre)

        findViewById<TextView>(R.id.etWifiName)?.text = ssid
        val etWifiKey = findViewById<EditText>(R.id.etWifiKey)

        configResultTV = findViewById<TextView>(R.id.configResultTV)

        findViewById<Button>(R.id.tvNext).setOnClickListener {
            var pwd = ""
            if (etWifiKey.text != null) {
                pwd = etWifiKey.text.toString()
            }
            val domainName = "http://nat.lefuenergy.com:10081"
            configResultTV?.text = "开始配网"
            PPBlutoothPeripheralTorreInstance.instance.controller?.getTorreDeviceManager()?.configWifi(ssid, pwd, domainName, configWifiInterface)
        }

    }

    val configWifiInterface = object : PPTorreConfigWifiInterface() {

        override fun configResult(configStateMenu: PPConfigStateMenu?, resultCode: String?) {
            configResultTV?.text = "configResult configStateMenu: $configStateMenu\nresultCode: $resultCode"
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        PPBlutoothPeripheralTorreInstance.instance.controller?.getTorreDeviceManager()?.exitConfigWifi()
    }

    override fun onDestroy() {
        super.onDestroy()
        PPBlutoothPeripheralTorreInstance.instance.controller?.getTorreDeviceManager()?.exitConfigWifi()
    }

}