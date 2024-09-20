package com.lefu.ppblutoothkit.device

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralAppleInstance
import com.lefu.ppblutoothkit.util.DataUtil
import com.lefu.ppblutoothkit.UserinfoActivity
import com.lefu.ppblutoothkit.calculate.Calculate4ACActivitiy
import com.lefu.ppblutoothkit.calculate.Calculate4DCActivitiy
import com.lefu.ppblutoothkit.view.MsgDialog
import com.lefu.ppblutoothkit.device.apple.BleConfigWifiActivity
import com.lefu.ppblutoothkit.okhttp.NetUtil
import com.peng.ppscale.business.ble.PPScaleHelper
import com.peng.ppscale.business.ble.configWifi.PPConfigWifiAppleStateMenu
import com.peng.ppscale.business.ble.configWifi.PPConfigWifiInfoInterface
import com.peng.ppscale.business.ble.listener.*
import com.peng.ppscale.business.state.PPBleSwitchState
import com.peng.ppscale.business.state.PPBleWorkState
import com.peng.ppscale.device.PeripheralApple.PPBlutoothPeripheralAppleController
import com.lefu.ppbase.util.PPUtil
import com.lefu.ppbase.PPBodyBaseModel
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.PPScaleDefine
import com.lefu.ppbase.util.Logger
import com.lefu.ppblutoothkit.calculate.Calculate4AC2ChannelActivitiy
import com.peng.ppscale.vo.PPScaleSendState
import kotlinx.android.synthetic.main.peripheral_apple_layout.wifiConfigLayout

/**
 * 对应的协议: 2.x
 * 连接类型:连接
 * 设备类型 人体秤
 */
class PeripheralAppleActivity : AppCompatActivity() {

    private var weightTextView: TextView? = null
    private var logTxt: TextView? = null
    private var device_set_connect_state: TextView? = null
    private var weightMeasureState: TextView? = null
    private val mCurrentHostUrl by lazy {
        findViewById<TextView>(R.id.mCurrentHostUrl)
    }
    var controller: PPBlutoothPeripheralAppleController? = PPBlutoothPeripheralAppleInstance.instance.controller

    companion object {
        var deviceModel: PPDeviceModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.peripheral_apple_layout)

        weightTextView = findViewById<TextView>(R.id.weightTextView)
        logTxt = findViewById<TextView>(R.id.logTxt)
        device_set_connect_state = findViewById<TextView>(R.id.device_set_connect_state)
        weightMeasureState = findViewById<TextView>(R.id.weightMeasureState)
        val nestedScrollViewLog = findViewById<NestedScrollView>(R.id.nestedScrollViewLog)

        logTxt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                nestedScrollViewLog.fullScroll(View.FOCUS_DOWN)
            }
        })

        controller?.registDataChangeListener(dataChangeListener)
        deviceModel?.let { it1 ->
            addPrint("startConnect")
            controller?.startConnect(it1, bleStateInterface)
        }

        initClick()

    }

    fun initClick() {
        findViewById<Button>(R.id.startConnectDevice).setOnClickListener {
            controller?.stopSeach()
            addPrint("startConnect")
            controller?.registDataChangeListener(dataChangeListener)
            deviceModel?.let { it1 -> controller?.startConnect(it1, bleStateInterface) }
        }
        findViewById<Button>(R.id.startReceiveBroadcastData).setOnClickListener {
            addPrint("startReceiveBroadcastData")
            controller?.registDataChangeListener(dataChangeListener)
            deviceModel?.let { it1 -> controller?.startSearch(it1.deviceMac, bleStateInterface) }
        }
        findViewById<Button>(R.id.syncTime).setOnClickListener {
            addPrint("syncTime")
            controller?.syncTime(object : PPBleSendResultCallBack {
                override fun onResult(sendState: PPScaleSendState?) {
                    if (sendState == PPScaleSendState.PP_SEND_SUCCESS) {
                        addPrint("syncTime send success")
                    } else {
                        addPrint("syncTime send fail")
                    }
                }
            })
        }
        findViewById<Button>(R.id.getDeviceTime).setOnClickListener {
            addPrint("getDeviceTime")
            controller?.getDeviceTime(object : PPBleSendResultCallBack {
                override fun onResult(sendState: PPScaleSendState?) {
                    if (sendState == PPScaleSendState.PP_SEND_SUCCESS) {
                        addPrint("getDeviceTime send success")
                    } else {
                        addPrint("getDeviceTime send fail")
                    }
                }
            }, object : PPDeviceInfoInterface() {

                override fun getDeviceTime(time: String?) {
                    addPrint("deviceTime:$time")
                }

            })
        }
        findViewById<Button>(R.id.readDeviceInfo).setOnClickListener {
            addPrint("readDeviceInfo")
            controller?.readDeviceInfo(object : PPDeviceInfoInterface() {
                override fun readDeviceInfoComplete(deviceModel: PPDeviceModel?) {
                    addPrint(deviceModel.toString())
                }
            })
        }
        findViewById<Button>(R.id.syncUnit).setOnClickListener {
            addPrint("syncUnit")
            val userModel = DataUtil.getUserModel()

            userModel?.let { it1 ->
                controller?.syncUnit(DataUtil.unit, it1, object : PPBleSendResultCallBack {
                    override fun onResult(sendState: PPScaleSendState?) {
                        if (sendState == PPScaleSendState.PP_SEND_SUCCESS) {
                            addPrint("syncUnit send success")
                        } else {
                            addPrint("syncUnit send fail")
                        }
                    }
                })
            }
        }
        findViewById<Button>(R.id.syncUserHistoryData).setOnClickListener {
            addPrint("syncUserHistoryData")
            if (PPScaleHelper.isSupportHistoryData(deviceModel?.deviceFuncType)) {
                controller?.getHistoryData(object : PPHistoryDataInterface() {
                    override fun monitorHistoryData(bodyBaseModel: PPBodyBaseModel?, dateTime: String?) {
                        addPrint("monitorHistoryData weight: ${bodyBaseModel?.weight}" + " dateTime:$dateTime")
                    }

                    override fun monitorHistoryEnd(deviceModel: PPDeviceModel?) {
                        addPrint("monitorHistoryEnd")

                    }

                    override fun monitorHistoryFail() {
                        addPrint("monitorHistoryFail")
                    }
                })
            } else {
                addPrint("device does not support")
            }
        }
        findViewById<Button>(R.id.deleteHistory).setOnClickListener {
            addPrint("deleteHistory")
            if (PPScaleHelper.isSupportHistoryData(deviceModel?.deviceFuncType)) {
                controller?.deleteHistoryData(object : PPBleSendResultCallBack {
                    override fun onResult(sendState: PPScaleSendState?) {
                        if (sendState == PPScaleSendState.PP_SEND_SUCCESS) {
                            addPrint("deleteHistory success")
                        } else {
                            addPrint("deleteHistory fail")
                        }
                    }
                })
            } else {
                addPrint("device does not support")
            }
        }
        findViewById<Button>(R.id.startConfigWifi).setOnClickListener {
            addPrint("startConfigWifi")
            if (PPScaleHelper.isFuncTypeWifi(deviceModel?.deviceFuncType)) {
                val intent = Intent(this@PeripheralAppleActivity, BleConfigWifiActivity::class.java)
                intent.putExtra("address", deviceModel?.deviceMac)
                startActivity(intent)
            } else {
                addPrint("device does not support")
            }
        }
        findViewById<Button>(R.id.getWifiInfo).setOnClickListener {
            addPrint("getWifiInfo")
            if (PPScaleHelper.isFuncTypeWifi(deviceModel?.deviceFuncType) ?: false) {
                controller?.getWiFiParmameters(configWifiInfoInterface)
            } else {
                addPrint("device does not support")
            }
        }
        findViewById<Button>(R.id.setUserInfo).setOnClickListener {
            addPrint("start UserInfo pager")
            startActivity(Intent(this, UserinfoActivity::class.java))
        }
        findViewById<Button>(R.id.setNetHost).setOnClickListener {
            addPrint("setNetHost")
            startActivity(Intent(this, SetHostActivity::class.java))
        }

    }

    override fun onResume() {
        super.onResume()
        if (PPScaleHelper.isFuncTypeWifi(deviceModel?.deviceFuncType)) {
            wifiConfigLayout?.visibility = View.VISIBLE
            mCurrentHostUrl?.text = "当前域名：${NetUtil.getScaleDomain()}"
        } else {
            wifiConfigLayout?.visibility = View.GONE
        }
    }

    val dataChangeListener = object : PPDataChangeListener {

        /**
         * 监听过程数据
         *
         * @param bodyBaseModel
         * @param deviceModel
         */
        override fun monitorProcessData(bodyBaseModel: PPBodyBaseModel?, deviceModel: PPDeviceModel?) {
            val weightStr = PPUtil.getWeightValueD(bodyBaseModel?.unit, bodyBaseModel?.getPpWeightKg()?.toDouble() ?: 0.0, deviceModel!!.deviceAccuracyType.getType())
            weightTextView?.text = "process:$weightStr ${PPUtil.getWeightUnit(bodyBaseModel?.unit)}"
            weightMeasureState?.text = ""
        }

        override fun monitorDataFail(bodyBaseModel: PPBodyBaseModel?, deviceModel: PPDeviceModel?) {

        }

        /**
         * 锁定数据
         *
         * @param bodyBaseModel
         */
        override fun monitorLockData(bodyBaseModel: PPBodyBaseModel?, deviceModel: PPDeviceModel?) {
            val weightStr = PPUtil.getWeightValueD(bodyBaseModel?.unit, bodyBaseModel?.getPpWeightKg()?.toDouble() ?: 0.0, deviceModel!!.deviceAccuracyType.getType())
            weightTextView?.text = "lock:$weightStr ${PPUtil.getWeightUnit(bodyBaseModel?.unit)}"
            if (bodyBaseModel?.isHeartRating ?: false) {
                //心率测量中
                weightMeasureState?.text = getString(R.string.heartrate_mesuring)
            } else {
                //测量结束
                weightMeasureState?.text = getString(R.string.measure_complete)
                //这里要填称重用户的个人信息
                val userModel = DataUtil.getUserModel()
                bodyBaseModel?.userModel = userModel

                MsgDialog.init(supportFragmentManager)
                    .setTitle(getString(R.string.tips))
                    .setMessage(getString(R.string.is_body_fat_calculated))
                    .setAnimStyle(R.style.dialog_)
                    .setCancelableAll(true)
                    .setNegativeButton(getString(R.string.cancel))
                    .setPositiveButton(getString(R.string.confirm), View.OnClickListener {
                        DataUtil.bodyBaseModel = bodyBaseModel
                        if (deviceModel.deviceCalcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeDirect) {
                            //4电极直流算法  24项数据
                            val intent = Intent(this@PeripheralAppleActivity, Calculate4DCActivitiy::class.java)
                            intent.putExtra("bodyDataModel", "bodyDataModel")
                            startActivity(intent)
                        } else if (deviceModel.deviceCalcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_1) {
                            Logger.i("PeripheralAppleActivity 四电极 双频 impedance1:${DataUtil.bodyBaseModel?.impedance} impedance100EnCode:${DataUtil.bodyBaseModel?.ppImpedance100EnCode}")
                            //4电极交流算法  24项数据
                            val intent = Intent(this@PeripheralAppleActivity, Calculate4AC2ChannelActivitiy::class.java)
                            intent.putExtra("bodyDataModel", "bodyDataModel")
                            startActivity(intent)
                        } else {
                            //4电极交流算法  24项数据
                            val intent = Intent(this@PeripheralAppleActivity, Calculate4ACActivitiy::class.java)
                            intent.putExtra("bodyDataModel", "bodyDataModel")
                            startActivity(intent)
                        }

                    })
                    .show()

            }

        }

        /**
         * 超重
         */
        override fun monitorOverWeight() {
            weightMeasureState?.text = "超重"
        }

    }

    val configWifiInfoInterface = object : PPConfigWifiInfoInterface {

        override fun monitorConfigSn(sn: String?, deviceModel: PPDeviceModel?) {
            addPrint("getWifiInfo sn:$sn")
        }

        override fun monitorConfigSsid(ssid: String?, deviceModel: PPDeviceModel?) {
            addPrint("getWifiInfo ssid:$ssid")
        }

        override fun monitorConfigPassword(password: String?, deviceModel: PPDeviceModel?) {
            addPrint("getWifiInfo password:$password")
        }

        override fun monitorModifyServerDomainSuccess() {
            addPrint("ModifyServerDNSSuccess")
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
            logTxt?.append("$msg\n")
        }
    }

    val bleStateInterface = object : PPBleStateInterface() {
        override fun monitorBluetoothWorkState(ppBleWorkState: PPBleWorkState?, deviceModel: PPDeviceModel?) {
            if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
                device_set_connect_state?.text = getString(R.string.device_connected)
                addPrint(getString(R.string.device_connected))
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateCanBeConnected) {
                device_set_connect_state?.text = getString(R.string.can_be_connected)
                addPrint(getString(R.string.can_be_connected))
                //如果你想扫描到设备后，直接连接，可调用下面两行。
                controller?.stopSeach()
                deviceModel?.let { controller?.startConnect(it, this) }
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
                device_set_connect_state?.text = getString(R.string.device_connecting)
                addPrint(getString(R.string.device_connecting))
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
                device_set_connect_state?.text = getString(R.string.device_disconnected)
                addPrint(getString(R.string.device_disconnected))
            } else if (ppBleWorkState == PPBleWorkState.PPBleStateSearchCanceled) {
                device_set_connect_state?.text = getString(R.string.stop_scanning)
                addPrint(getString(R.string.stop_scanning))
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
                device_set_connect_state?.text = getString(R.string.scan_timeout)
                addPrint(getString(R.string.scan_timeout))
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                device_set_connect_state?.text = getString(R.string.scanning)
                addPrint(getString(R.string.scanning))
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateWritable) {
                addPrint(getString(R.string.writable))
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnectFailed) {
//                addPrint(getString(R.string.writable))
            }
        }

        override fun monitorBluetoothSwitchState(ppBleSwitchState: PPBleSwitchState?) {
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                addPrint(getString(R.string.system_bluetooth_disconnect))
                Toast.makeText(this@PeripheralAppleActivity, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show()
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                addPrint(getString(R.string.system_blutooth_on))
                Toast.makeText(this@PeripheralAppleActivity, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        controller?.disConnect()
    }


}