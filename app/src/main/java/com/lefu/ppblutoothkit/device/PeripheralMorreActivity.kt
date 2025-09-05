package com.lefu.ppblutoothkit.device

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.vo.PPUnitType
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.device.instance.PPBluetoothPeripheralMorreInstance
import com.lefu.ppblutoothkit.device.morre.foodScaleDataChangeListener
import com.lefu.ppblutoothkit.util.FileUtil
import com.peng.ppscale.business.ble.listener.PPBleSendResultCallBack
import com.peng.ppscale.business.ble.listener.PPBleStateInterface
import com.peng.ppscale.business.ble.listener.PPDeviceLogInterface
import com.peng.ppscale.business.ble.listener.PPTorreDeviceModeChangeInterface
import com.peng.ppscale.business.state.PPBleSwitchState
import com.peng.ppscale.business.state.PPBleWorkState
import com.peng.ppscale.vo.PPScaleSendState

/**
 * 一定要先连接设备，确保设备在已连接状态下使用
 * 对应的协议: 3.x
 * 连接类型:连接
 * 设备类型 厨房秤
 */
class PeripheralMorreActivity : BaseImmersivePermissionActivity() {

    var weightTextView: TextView? = null
    var logTxt: TextView? = null
    var device_set_connect_state: TextView? = null
    var foodNoTv: TextView? = null
    var foodRemoteIdTv: TextView? = null

    companion object {
        var deviceModel: PPDeviceModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.peripheral_morre_layout)

        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()

        // 初始化Toolbar
        initToolbar()

        // 注册新的返回键处理机制
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPressed()
            }

        })

        weightTextView = findViewById<TextView>(R.id.weightTextView)
        foodNoTv = findViewById<TextView>(R.id.foodNoTv)
        foodRemoteIdTv = findViewById<TextView>(R.id.foodRemoteIdTv)
        logTxt = findViewById<TextView>(R.id.logTxt)
        device_set_connect_state = findViewById<TextView>(R.id.device_set_connect_state)
        val nestedScrollViewLog = findViewById<NestedScrollView>(R.id.nestedScrollViewLog)

        logTxt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                nestedScrollViewLog.fullScroll(View.FOCUS_DOWN)
            }
        })
        addPrint("startConnect")
        initClick()
        deviceModel?.let { it1 -> PPBluetoothPeripheralMorreInstance.startConnect(it1, foodScaleDataChangeListener, bleStateInterface) }
    }

    private fun initToolbar() {
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        toolbar?.let {
            setupUnifiedToolbar(
                toolbar = it,
                title = "Morre:${deviceModel?.deviceName}",
                showBackButton = true
            )
        }
    }

    fun initClick() {
        findViewById<Button>(R.id.startConnectDevice).setOnClickListener {
            addPrint("startConnect")
            deviceModel?.let { it1 -> PPBluetoothPeripheralMorreInstance.startConnect(it1, foodScaleDataChangeListener, bleStateInterface) }
        }
        findViewById<Button>(R.id.syncUnit).setOnClickListener {
            addPrint("syncUnit")
            PPBluetoothPeripheralMorreInstance.syncUnit(PPUnitType.Unit_LB, object : PPBleSendResultCallBack {
                override fun onResult(sendState: PPScaleSendState?) {
                    if (sendState == PPScaleSendState.PP_SEND_SUCCESS) {
                        addPrint("syncUnit send success")
                    } else {
                        addPrint("syncUnit send fail")
                    }
                }
            })
        }
        findViewById<Button>(R.id.device_set_sync_log).setOnClickListener {
            addPrint("syncLog")
            //logFilePath 指定文件存储路径，必传例如：val fileFath = context.filesDir.absolutePath + "/Log/DeviceLog"
            val fileFath = filesDir.absolutePath + "/Log/DeviceLog"
            PPBluetoothPeripheralMorreInstance.syncLog(fileFath, deviceLogInterface)
        }

        findViewById<Button>(R.id.readDeviceBattery).setOnClickListener {
            addPrint("readDeviceBattery")
            PPBluetoothPeripheralMorreInstance.readDeviceBattery(object : PPTorreDeviceModeChangeInterface {
                override fun readDevicePower(power: Int) {
                    addPrint("readDevicePower power:$power")
                }
            })
        }
        findViewById<Button>(R.id.readDeviceInfo).setOnClickListener {
            addPrint("readDeviceInfo")
            PPBluetoothPeripheralMorreInstance.readDeviceInfo(object : PPTorreDeviceModeChangeInterface {
                override fun onReadDeviceInfo(deviceModel: PPDeviceModel?) {
                    deviceModel?.let {
                        addPrint(it.toString())
                    }
                }
            })
        }
    }

    fun addPrint(msg: String) {
        runOnUiThread {
            logTxt?.append("$msg\n")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PPBluetoothPeripheralMorreInstance.disConnect()
    }

    private val bleStateInterface = object : PPBleStateInterface() {
        override fun monitorBluetoothWorkState(ppBleWorkState: PPBleWorkState?, deviceModel: PPDeviceModel?) {
            if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
                addPrint(getString(R.string.device_connected))
                device_set_connect_state?.text = getString(R.string.device_connected)
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateCanBeConnected) {
                addPrint(getString(R.string.can_be_connected))
                device_set_connect_state?.text = getString(R.string.can_be_connected)
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
                addPrint(getString(R.string.device_connecting))
                device_set_connect_state?.text = getString(R.string.device_connecting)
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
                addPrint(getString(R.string.device_disconnected))
                device_set_connect_state?.text = getString(R.string.device_disconnected)
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateWritable) {
                addPrint(getString(R.string.writable))
            }
        }

        override fun monitorBluetoothSwitchState(ppBleSwitchState: PPBleSwitchState?) {
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                addPrint(getString(R.string.system_bluetooth_disconnect))
                Toast.makeText(this@PeripheralMorreActivity, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show()
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                addPrint(getString(R.string.system_blutooth_on))
                Toast.makeText(this@PeripheralMorreActivity, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val deviceLogInterface = object : PPDeviceLogInterface {
        override fun syncLoging(progress: Int) {
            addPrint("syncLoging progress:$progress")
        }

        override fun syncLogEnd(logFilePath: String?) {
            addPrint("syncLogEnd ")
            addPrint("logFilePath: $logFilePath")
            Toast.makeText(this@PeripheralMorreActivity, "Sync Log Success", Toast.LENGTH_SHORT).show()
            FileUtil.sendEmail(this@PeripheralMorreActivity, logFilePath)
        }

        override fun syncLogStart() {
            addPrint("syncLogStart")
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        handleBackPressed()
    }

    private fun handleBackPressed() {
        PPBluetoothPeripheralMorreInstance.disConnect()
        finish()
    }

}