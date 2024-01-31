package com.lefu.ppblutoothkit.device.torre

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralTorreInstance
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralIceInstance
import com.lefu.ppblutoothkit.okhttp.NetUtil
import com.peng.ppscale.business.ble.configWifi.PPConfigStateMenu
import com.peng.ppscale.business.ble.configWifi.PPConfigWifiInfoInterface
import com.peng.ppscale.business.torre.listener.PPTorreConfigWifiInterface
import com.peng.ppscale.util.Logger
import com.peng.ppscale.vo.PPDeviceModel
import com.peng.ppscale.vo.PPScaleDefine

class PeripheralTorreConfigWifiActivity : Activity() {

    var configResultTV: TextView? = null

    var etWifiKey: EditText? = null

    companion object {
        var ssid = ""
        var deviceModel: PPDeviceModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_config_torre)

        findViewById<TextView>(R.id.etWifiName)?.text = ssid
        etWifiKey = findViewById<EditText>(R.id.etWifiKey)

        configResultTV = findViewById<TextView>(R.id.configResultTV)

        findViewById<Button>(R.id.tvNext).setOnClickListener {
            var pwd = ""
            if (etWifiKey?.text != null) {
                pwd = etWifiKey?.text.toString()
            }
            //注意：域名在您自己的App中需要换成你App自己的服务器域名，并确保服务器已完成Wifi体脂秤相关功能开发，
            //https://uniquehealth.lefuenergy.com/unique-open-web/#/document
            //找到"乐福体脂秤自建服务器接入方案"-> PeripheralIce/PeripheralTorre 对应"Torre系列产品"
//            val domainName = "http://nat.lefuenergy.com:10082"
            val domainName = NetUtil.SCALE_DOMAIN
//            val domainName = "http://test-mirrorapi.ruleye.com"
            configResultTV?.text =getString(R.string.start_config_net)
            if (deviceModel?.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralIce) {
                var mDomain = domainName
                if (mDomain.contains("http://")) {
                    mDomain = domainName.replace("http://", "")
                }
                PPBlutoothPeripheralIceInstance.instance.controller?.sendModifyServerDomain(mDomain, configWifiInfoInterface)
            } else if (deviceModel?.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralTorre) {
                PPBlutoothPeripheralTorreInstance.instance.controller?.getTorreDeviceManager()?.configWifi(domainName, ssid, pwd, configWifiInterface)
            }
        }

    }

    val configWifiInterface = object : PPTorreConfigWifiInterface() {

        override fun configResult(configStateMenu: PPConfigStateMenu?, resultCode: String?) {
            configResultTV?.text = "configResult configStateMenu: $configStateMenu\nresultCode: $resultCode"
        }

    }

    val configWifiInfoInterface = object : PPConfigWifiInfoInterface {

        override fun monitorConfigSn(sn: String?, deviceModel: PPDeviceModel?) {
            addPrint("getWifiInfo sn:$sn")
            configResultTV?.text = "configResult Success sn: $sn"
        }

        override fun monitorModifyServerDomainSuccess() {
            addPrint("monitorModifyServerDomainSuccess")
            configResultTV?.text = "monitorModifyServerDomainSuccess"
            if (!TextUtils.isEmpty(ssid)) {
                var pwd = ""
                if (etWifiKey?.text != null) {
                    pwd = etWifiKey?.text.toString()
                }
                PPBlutoothPeripheralIceInstance.instance.controller?.configWifiData(ssid, pwd, this)
            } else {
                configResultTV?.text = "ssid is null"
                Logger.e("configwifi monitorModifyServerDomainSuccess onConfigResultFail ssid is null")
            }
        }

        override fun monitorConfigFail() {
            Logger.e("configwifi  monitorConfigFail")
            configResultTV?.text = "configResult monitorConfigFail"
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (deviceModel?.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralIce) {
            PPBlutoothPeripheralIceInstance.instance.controller?.exitConfigWifi()
        } else if (deviceModel?.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralTorre) {
            PPBlutoothPeripheralTorreInstance.instance.controller?.getTorreDeviceManager()?.exitConfigWifi()
        }
    }

    fun addPrint(msg: String) {
        Logger.d("msg:$msg")
    }

}