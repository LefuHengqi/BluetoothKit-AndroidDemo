package com.lefu.ppblutoothkit.device

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralIceInstance
import com.lefu.ppblutoothkit.util.DataUtil
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.UserinfoActivity
import com.lefu.ppscale.wifi.activity.BleConfigWifiActivity
import com.peng.ppscale.business.ble.PPScaleHelper
import com.peng.ppscale.business.ble.configWifi.PPConfigWifiInfoInterface
import com.peng.ppscale.business.ble.listener.*
import com.peng.ppscale.business.state.PPBleSwitchState
import com.peng.ppscale.business.state.PPBleWorkState
import com.peng.ppscale.device.PeripheralIce.PPBlutoothPeripheralIceController
import com.peng.ppscale.util.PPUtil
import com.peng.ppscale.vo.PPBodyBaseModel
import com.peng.ppscale.vo.PPDeviceModel
import com.peng.ppscale.vo.PPScaleSendState

/**
 * 对应的协议: 4.x
 * 连接类型:连接
 * 设备类型 人体秤
 */
class PeripheralIceActivity : Activity() {

    private var weightTextView: TextView? = null
    private var logTxt: TextView? = null
    private var device_set_connect_state: TextView? = null
    private var weightMeasureState: TextView? = null

    var controller: PPBlutoothPeripheralIceController? = PPBlutoothPeripheralIceInstance.instance.controller

    companion object {
        var deviceModel: PPDeviceModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.peripheral_ice_layout)

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
        setTitle("${deviceModel?.getDevicePeripheralType()}")

        initClick()

    }

    fun initClick() {
        findViewById<Button>(R.id.startConnectDevice).setOnClickListener {
            addPrint("startConnect")
            controller?.registDataChangeListener(dataChangeListener)
            deviceModel?.let { it1 -> controller?.startConnect(it1, bleStateInterface) }
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
            controller?.syncUnit(DataUtil.util().unit, object : PPBleSendResultCallBack {
                override fun onResult(sendState: PPScaleSendState?) {
                    if (sendState == PPScaleSendState.PP_SEND_SUCCESS) {
                        addPrint("syncUnit send success")
                    } else {
                        addPrint("syncUnit send fail")
                    }
                }
            })
        }
        findViewById<Button>(R.id.syncUserHistoryData).setOnClickListener {
            addPrint("syncUserHistoryData")
            if (PPScaleHelper.isSupportHistoryData(deviceModel?.deviceFuncType)) {
                controller?.getHistory(object : PPHistoryDataInterface() {
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
            if (PPScaleHelper.isSupportHistoryData(deviceModel?.deviceFuncType) ?: false) {
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
                controller?.disConnect()
                val intent = Intent(this@PeripheralIceActivity, BleConfigWifiActivity::class.java)
                intent.putExtra("address", deviceModel?.deviceMac)
                startActivity(intent)
            } else {
                addPrint("device does not support")
            }
        }
        findViewById<Button>(R.id.getWifiInfo).setOnClickListener {
            addPrint("getWifiInfo")
            if (PPScaleHelper.isFuncTypeWifi(deviceModel?.deviceFuncType) ?: false) {
                controller?.getWifiInfo(configWifiInfoInterface)
            } else {
                addPrint("device does not support")
            }
        }
        findViewById<Button>(R.id.setUserInfo).setOnClickListener {
            addPrint("start UserInfo pager")
            startActivity(Intent(this, UserinfoActivity::class.java))
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
            TODO("Not yet implemented")
        }

        /**
         * 锁定数据
         *
         * @param bodyBaseModel
         */
        override fun monitorLockData(bodyBaseModel: PPBodyBaseModel?, deviceModel: PPDeviceModel?) {
            val weightStr = PPUtil.getWeightValueD(bodyBaseModel?.unit, bodyBaseModel?.getPpWeightKg()?.toDouble() ?: 0.0, deviceModel!!.deviceAccuracyType.getType())
            weightTextView?.text = "lock:$weightStr ${PPUtil.getWeightUnit(bodyBaseModel?.unit)}"
            weightMeasureState?.text = ""
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
            }
        }

        override fun monitorBluetoothSwitchState(ppBleSwitchState: PPBleSwitchState?) {
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                addPrint(getString(R.string.system_bluetooth_disconnect))
                Toast.makeText(this@PeripheralIceActivity, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show()
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                addPrint(getString(R.string.system_blutooth_on))
                Toast.makeText(this@PeripheralIceActivity, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        controller?.disConnect()
    }


}