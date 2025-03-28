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
import com.lefu.ppbase.PPBodyBaseModel
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.vo.PPUserModel
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralDurianInstance
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.util.DataUtil
import com.peng.ppscale.business.ble.listener.*
import com.lefu.ppbase.vo.PPUnitType
import com.peng.ppscale.business.state.PPBleSwitchState
import com.peng.ppscale.business.state.PPBleWorkState
import com.peng.ppscale.device.PeripheralDurian.PPBlutoothPeripheralDurianController
import com.lefu.ppbase.util.PPUtil
import com.lefu.ppbase.vo.PPBodyFatInScaleVo
import com.peng.ppscale.vo.*


/**
 * 对应的协议: 2.x
 * 连接类型:设备端计算的连接
 * 设备类型 人体秤
 */
class PeripheralDurianActivity : Activity() {

    private var weightTextView: TextView? = null
    private var logTxt: TextView? = null
    private var device_set_connect_state: TextView? = null
    private var weightMeasureState: TextView? = null

    var userModel: PPUserModel? = null

    var controller: PPBlutoothPeripheralDurianController? = PPBlutoothPeripheralDurianInstance.instance.controller

    companion object {
        var deviceModel: PPDeviceModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.peripheral_dutian_layout)

        weightTextView = findViewById<TextView>(R.id.weightTextView)
        logTxt = findViewById<TextView>(R.id.logTxt)
        device_set_connect_state = findViewById<TextView>(R.id.device_set_connect_state)
        weightMeasureState = findViewById<TextView>(R.id.weightMeasureState)
        val nestedScrollViewLog = findViewById<NestedScrollView>(R.id.nestedScrollViewLog)

        userModel = DataUtil.getUserModel()

        logTxt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                nestedScrollViewLog.fullScroll(View.FOCUS_DOWN)
            }
        })

        controller?.userModel = userModel     //必传参数 This is a required parameter
        addPrint("startConnect")
        controller?.registDataChangeListener(dataChangeListener)
        deviceModel?.let { it1 -> controller?.startConnect(it1, bleStateInterface) }
        initClick()

    }

    fun initClick() {
        findViewById<Button>(R.id.startConnectDevice).setOnClickListener {
            addPrint("startConnect")
            controller?.registDataChangeListener(dataChangeListener)
            deviceModel?.let { it1 -> controller?.startConnect(it1, bleStateInterface) }
        }
        findViewById<Button>(R.id.syncUserInfo).setOnClickListener {
            syncUnitAndSyncUser()
        }
        findViewById<Button>(R.id.syncUnit).setOnClickListener {
            syncUnitAndSyncUser()
        }
    }

    private fun syncUnitAndSyncUser() {
        addPrint("syncUserInfo and syncUnit")
        addPrint("syncUnit unit:$ PPUnitType.Unit_LB")
        addPrint("userModel:${userModel.toString()}")
        controller?.syncUnit(PPUnitType.Unit_LB, userModel!!, object : PPBleSendResultCallBack {
            override fun onResult(sendState: PPScaleSendState?) {
                if (sendState == PPScaleSendState.PP_SEND_SUCCESS) {
                    addPrint("syncUserInfo and syncUnit success")
                } else {
                    addPrint("syncUserInfo and syncUnit success")
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
            val weightStr = PPUtil.getWeightValueD(bodyBaseModel?.unit, bodyBaseModel?.getPpWeightKg()?.toDouble() ?: 0.0, deviceModel!!.deviceAccuracyType.getType(), true)
            weightTextView?.text = "process:$weightStr ${PPUtil.getWeightUnit(bodyBaseModel?.unit)}"
            weightMeasureState?.text = ""
        }

        override fun monitorDataFail(bodyBaseModel: PPBodyBaseModel?, deviceModel: PPDeviceModel?) {

        }

        override fun monitorLockDataByCalculateInScale(bodyFatInScaleVo: PPBodyFatInScaleVo?) {
            bodyFatInScaleVo?.let {
//                val bodyFatModel = CalculateHelper4.calcuteTypeInScale(bodyFatInScaleVo)
                val bodyBaseModel = it.ppBodyBaseModel
                val weightStr = PPUtil.getWeightValueD(bodyBaseModel?.unit, bodyBaseModel?.getPpWeightKg()?.toDouble() ?: 0.0, deviceModel!!.deviceAccuracyType.getType(), true)
                weightTextView?.text = "lock:$weightStr ${PPUtil.getWeightUnit(bodyBaseModel?.unit)}"
                weightMeasureState?.text = ""
                addPrint(it.toString())
            }
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
                syncUnitAndSyncUser()
            }
        }

        override fun monitorBluetoothSwitchState(ppBleSwitchState: PPBleSwitchState?) {
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                addPrint(getString(R.string.system_bluetooth_disconnect))
                Toast.makeText(this@PeripheralDurianActivity, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show()
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                addPrint(getString(R.string.system_blutooth_on))
                Toast.makeText(this@PeripheralDurianActivity, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        controller?.disConnect()
    }

}