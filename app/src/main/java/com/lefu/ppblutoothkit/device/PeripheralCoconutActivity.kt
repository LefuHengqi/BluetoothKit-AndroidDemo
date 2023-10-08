package com.lefu.ppblutoothkit.device

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralCoconutInstance
import com.lefu.ppscale.ble.R
import com.lefu.ppblutoothkit.util.DataUtil
import com.peng.ppscale.business.ble.listener.*
import com.peng.ppscale.business.device.PPUnitType
import com.peng.ppscale.business.state.PPBleSwitchState
import com.peng.ppscale.business.state.PPBleWorkState
import com.peng.ppscale.device.PeripheralCoconut.PPBlutoothPeripheralCoconutController
import com.peng.ppscale.util.PPUtil
import com.peng.ppscale.vo.PPBodyBaseModel
import com.peng.ppscale.vo.PPDeviceModel
import com.peng.ppscale.vo.PPScaleDefine
import com.peng.ppscale.vo.PPScaleSendState
import com.peng.ppscale.vo.PPUserModel

/**
 * 对应的协议: 3.x
 * 连接类型:连接
 * 设备类型 人体秤
 */
class PeripheralCoconutActivity : Activity() {

    private var weightTextView: TextView? = null
    private var logTxt: TextView? = null
    private var device_set_connect_state: TextView? = null
    private var weightMeasureState: TextView? = null

    var userModel: PPUserModel? = null

    var controller: PPBlutoothPeripheralCoconutController? = PPBlutoothPeripheralCoconutInstance.instance.controller

    companion object {
        var deviceModel: PPDeviceModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.peripheral_coconut_layout)

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
        userModel = DataUtil.util().userModel

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
            controller?.sendSyncTime(object : PPBleSendResultCallBack {
                override fun onResult(sendState: PPScaleSendState?) {
                    if (sendState == PPScaleSendState.PP_SEND_SUCCESS) {
                        addPrint("syncTime send success")
                    } else {
                        addPrint("syncTime send fail")
                    }
                }
            })
        }
        findViewById<Button>(R.id.syncUserInfo).setOnClickListener {
            addPrint("syncUserInfo")
            if (deviceModel?.deviceConnectType == PPScaleDefine.PPDeviceConnectType.PPDeviceConnectTypeDirect) {
                syncUserAndUnitData()
            } else {
                addPrint("syncUserInfo: Does not support sending user data")
            }
        }
        findViewById<Button>(R.id.syncUnit).setOnClickListener {
            addPrint("syncUnit ")
            if (deviceModel?.deviceConnectType == PPScaleDefine.PPDeviceConnectType.PPDeviceConnectTypeDirect) {
                syncUserAndUnitData()
            } else {
                controller?.sendSyncUserAndUnitData(PPUnitType.Unit_LB, userModel, object : PPBleSendResultCallBack {
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
            if (controller?.isSupportHistoryData(PeripheralAppleActivity.deviceModel) ?: false) {
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
            if (controller?.isSupportHistoryData(PeripheralAppleActivity.deviceModel) ?: false) {
                if (controller?.isSupportHistoryData(deviceModel) ?: false) {
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
            } else {
                addPrint("device does not support")
            }
        }
    }

    private fun syncUserAndUnitData() {
        addPrint("syncUserInfo and syncUnit")
        controller?.sendSyncUserAndUnitData(PPUnitType.Unit_LB, userModel, object : PPBleSendResultCallBack {
            override fun onResult(sendState: PPScaleSendState?) {
                if (sendState == PPScaleSendState.PP_SEND_SUCCESS) {
                    addPrint("syncUserInfo and syncUnit send success")
                } else {
                    addPrint("syncUserInfo and syncUnit send fail")
                }
            }
        })
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
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnectable) {
                addPrint(getString(R.string.Connectable))
            }
        }

        override fun monitorBluetoothSwitchState(ppBleSwitchState: PPBleSwitchState?) {
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                addPrint(getString(R.string.system_bluetooth_disconnect))
                Toast.makeText(this@PeripheralCoconutActivity, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show()
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                addPrint(getString(R.string.system_blutooth_on))
                Toast.makeText(this@PeripheralCoconutActivity, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        controller?.disConnect()
    }


}