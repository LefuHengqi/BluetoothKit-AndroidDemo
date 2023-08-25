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
import com.lefu.ppblutoothkit.instance.PPBlutoothPeripheralAppleInstance
import com.lefu.ppblutoothkit.instance.PPBlutoothPeripheralBananaInstance
import com.lefu.ppscale.ble.R
import com.peng.ppscale.business.ble.listener.PPBleStateInterface
import com.peng.ppscale.business.ble.listener.PPDataChangeListener
import com.peng.ppscale.business.state.PPBleSwitchState
import com.peng.ppscale.business.state.PPBleWorkState
import com.peng.ppscale.device.PeripheralApple.PPBlutoothPeripheralAppleController
import com.peng.ppscale.device.PeripheralBanana.PPBlutoothPeripheralBananaController
import com.peng.ppscale.util.PPUtil
import com.peng.ppscale.vo.PPBodyBaseModel
import com.peng.ppscale.vo.PPDeviceModel

/**
 * 对应的协议: 2.x
 * 连接类型:广播
 * 设备类型 人体秤
 */
class PeripheralBananaActivity : Activity() {

    private var weightTextView: TextView? = null
    private var logTxt: TextView? = null
    private var device_set_connect_state: TextView? = null
    private var weightMeasureState: TextView? = null

    var controller: PPBlutoothPeripheralBananaController? = PPBlutoothPeripheralBananaInstance.instance.controller

    companion object {
        var deviceModel: PPDeviceModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.peripheral_banana_layout)

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

        controller?.deviceModel = PeripheralAppleActivity.deviceModel

        initClick()

    }

    fun initClick() {
        findViewById<Button>(R.id.startSearch).setOnClickListener {
            addPrint("startSearch")
            controller?.registDataChangeListener(dataChangeListener)
            controller?.startSearch(bleStateInterface)
        }
    }

    fun addPrint(msg: String) {
        if (msg.isNotEmpty()) {
            logTxt?.append("$msg\n")
        }
    }

    val dataChangeListener = object : PPDataChangeListener() {

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

    val bleStateInterface = object : PPBleStateInterface() {
        override fun monitorBluetoothWorkState(ppBleWorkState: PPBleWorkState?, deviceModel: PPDeviceModel?) {
            if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
                device_set_connect_state?.text = getString(R.string.device_connecting)
                addPrint(getString(R.string.device_connecting))
            } else if (ppBleWorkState == PPBleWorkState.PPBleStateSearchCanceled) {
                device_set_connect_state?.text = getString(R.string.stop_scanning)
                addPrint(getString(R.string.stop_scanning))
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
                device_set_connect_state?.text = getString(R.string.scan_timeout)
                addPrint(getString(R.string.scan_timeout))
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                device_set_connect_state?.text = getString(R.string.scanning)
                addPrint(getString(R.string.scanning))
            }
        }

        override fun monitorBluetoothSwitchState(ppBleSwitchState: PPBleSwitchState?) {
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                addPrint(getString(R.string.system_bluetooth_disconnect))
                Toast.makeText(this@PeripheralBananaActivity, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show()
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                addPrint(getString(R.string.system_blutooth_on))
                Toast.makeText(this@PeripheralBananaActivity, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onPause() {
        super.onPause()
        controller?.stopSeach()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        controller?.stopSeach()
    }

}