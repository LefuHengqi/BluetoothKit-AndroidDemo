package com.lefu.ppblutoothkit.device.torre

import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.PPScaleDefine
import com.lefu.ppbase.util.Logger
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralBorreInstance
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralDorreInstance
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralIceInstance
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralTorreInstance
import com.lefu.ppblutoothkit.okhttp.NetUtil
import com.peng.ppscale.business.ble.configWifi.PPConfigStateMenu
import com.peng.ppscale.business.ble.configWifi.PPConfigWifiAppleStateMenu
import com.peng.ppscale.business.ble.configWifi.PPConfigWifiInfoInterface
import com.peng.ppscale.business.torre.listener.PPTorreConfigWifiInterface

class PeripheralTorreConfigWifiActivity : BaseImmersivePermissionActivity() {

    var configResultTV: TextView? = null
    var ivShowPassword: ImageView? = null

    var etWifiKey: EditText? = null

    private var isPasswordVisible = false

    companion object {
        var ssid = ""
        var deviceModel: PPDeviceModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_config_torre)

        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()

        // 初始化Toolbar
        initToolbar()

        findViewById<TextView>(R.id.etWifiName)?.text = ssid
        etWifiKey = findViewById<EditText>(R.id.etWifiKey)
        ivShowPassword = findViewById<ImageView>(R.id.ivShowPassword)

        configResultTV = findViewById<TextView>(R.id.configResultTV)

        ivShowPassword?.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                // 显示密码（明文）
                etWifiKey?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                // 隐藏密码（密文）
                etWifiKey?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            // 确保光标保持在末尾
            etWifiKey?.setSelection(etWifiKey?.text?.length ?: 0)
        }

        val tvNext = findViewById<Button>(R.id.tvNext);
        tvNext.setOnClickListener {
            var pwd = ""
            if (etWifiKey?.text != null) {
                pwd = etWifiKey?.text.toString()
            }
            //注意：域名在您自己的App中需要换成你App自己的服务器域名，并确保服务器已完成Wifi体脂秤相关功能开发，
            //https://uniquehealth.lefuenergy.com/unique-open-web/#/document
            //找到"乐福体脂秤自建服务器接入方案"-> PeripheralIce/PeripheralTorre 对应"Torre系列产品"
//            val domainName = "http://nat.lefuenergy.com:10082"
            val domainName = NetUtil.getScaleDomain()
//            val domainName = "http://test-mirrorapi.ruleye.com"
            addLog(getString(R.string.start_config_net))
            tvNext.visibility = View.GONE
            if (deviceModel?.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralIce) {
                if (PPBlutoothPeripheralIceInstance.instance.controller?.connectState() == false) {
                    addLog("Device not connected")
                    return@setOnClickListener
                }
                var mDomain = domainName
                if (mDomain.contains("http://")) {
                    mDomain = domainName.replace("http://", "")
                } else if (mDomain.contains("https://")) {
                    mDomain = domainName.replace("https://", "")
                }
                addLog("configwifi domainName: $mDomain")
                PPBlutoothPeripheralIceInstance.instance.controller?.sendModifyServerDomain(mDomain, configWifiInfoInterface)
            } else if (deviceModel?.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralTorre) {
                if (PPBlutoothPeripheralTorreInstance.instance.controller?.connectState() == false) {
                    addLog("Device not connected")
                    return@setOnClickListener
                }
                addLog("configwifi domainName: $domainName")
                addLog("configwifi ssid: $ssid")
                addLog("configwifi pwd: $pwd")
                PPBlutoothPeripheralTorreInstance.instance.controller?.getTorreDeviceManager()?.configWifi(domainName, ssid, pwd, configWifiInterface)
            } else if (deviceModel?.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralBorre) {
                if (PPBlutoothPeripheralBorreInstance.instance.controller?.connectState() == false) {
                    addLog("Device not connected")
                    return@setOnClickListener
                }
                addLog("configwifi domainName: $domainName")
                addLog("configwifi ssid: $ssid")
                addLog("configwifi pwd: $pwd")
                PPBlutoothPeripheralBorreInstance.instance.controller?.getTorreDeviceManager()?.configWifi(domainName, ssid, pwd, configWifiInterface)
            } else if (deviceModel?.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralDorre) {
                if (PPBlutoothPeripheralDorreInstance.instance.controller?.connectState() == false) {
                    addLog("Device not connected")
                    return@setOnClickListener
                }
                addLog("configwifi domainName: $domainName")
                addLog("configwifi ssid: $ssid")
                addLog("configwifi pwd: $pwd")
                PPBlutoothPeripheralDorreInstance.instance.controller?.getTorreDeviceManager()?.configWifi(domainName, ssid, pwd, configWifiInterface)
            } else {
                addLog("Unsupported device type")
            }
        }

    }

    private fun initToolbar() {
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        toolbar?.let {
            setupUnifiedToolbar(
                toolbar = it,
                title = "WiFi配置",
                showBackButton = true
            )
        }
    }

    val configWifiInterface = object : PPTorreConfigWifiInterface() {

        override fun configResult(configStateMenu: PPConfigStateMenu?, resultCode: String?) {
            addLog("configResult configStateMenu: $configStateMenu\nresultCode: $resultCode")
        }

    }

    val configWifiInfoInterface = object : PPConfigWifiInfoInterface {

        override fun monitorConfigSn(sn: String?, deviceModel: PPDeviceModel?) {
            addLog("configResult Success sn: $sn")
        }

        override fun monitorModifyServerDomainSuccess() {
            addLog("monitorModifyServerDomainSuccess")
            if (!TextUtils.isEmpty(ssid)) {
                var pwd = ""
                if (etWifiKey?.text != null) {
                    pwd = etWifiKey?.text.toString()
                }
                PPBlutoothPeripheralIceInstance.instance.controller?.configWifiData(ssid, pwd, this)
            } else {
                addLog("configwifi onConfigResultFail ssid is null")
            }
        }

        override fun monitorConfigFail(stateMenu: PPConfigWifiAppleStateMenu?) {
            when (stateMenu) {
                PPConfigWifiAppleStateMenu.CONFIG_STATE_LOW_BATTERY_LEVEL -> {
                    addLog("Config wifi fail because: Low battery level")
                }

                PPConfigWifiAppleStateMenu.CONFIG_STATE_REGIST_FAIL -> {
                    addLog("Config wifi fail because: login has failed")
                }

                PPConfigWifiAppleStateMenu.CONFIG_STATE_GET_CONFIG_FAIL -> {
                    addLog(
                        "Config wifi fail because: Failed to obtain configuration"
                    )
                }

                PPConfigWifiAppleStateMenu.CONFIG_STATE_ROUTER_FAIL -> {
                    addLog("Config wifi fail because: Unable to find route")
                }

                PPConfigWifiAppleStateMenu.CONFIG_STATE_PASSWORD_ERR -> {
                    addLog("Config wifi fail because: Password error")
                }

                PPConfigWifiAppleStateMenu.CONFIG_STATE_OTHER_FAIL -> {
                    addLog(
                        "Config wifi fail because: Other errors (app can be ignored)"
                    )
                }

                else -> {
                    addLog("Config wifi fail because: Other errors")
                }
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (deviceModel?.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralIce) {
            PPBlutoothPeripheralIceInstance.instance.controller?.exitConfigWifi()
        } else if (deviceModel?.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralTorre) {
            PPBlutoothPeripheralTorreInstance.instance.controller?.getTorreDeviceManager()
                ?.exitConfigWifi()
        } else if (deviceModel?.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralBorre) {
            PPBlutoothPeripheralBorreInstance.instance.controller?.getTorreDeviceManager()
                ?.exitConfigWifi()
        } else if (deviceModel?.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralDorre) {
            PPBlutoothPeripheralDorreInstance.instance.controller?.getTorreDeviceManager()
                ?.exitConfigWifi()
        }
    }

    fun addPrint(msg: String) {
        Logger.d("msg:$msg")
    }

    fun addLog(msg: String) {
        Logger.d("msg:$msg")
        configResultTV?.append(msg + "\n")
    }


}