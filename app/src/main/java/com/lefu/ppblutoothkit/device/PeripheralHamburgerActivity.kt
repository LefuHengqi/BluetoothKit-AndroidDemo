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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralHamburgerInstance
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.device.foodscale.FoodScaleCacluteHelper
import com.peng.ppscale.business.ble.listener.FoodScaleDataChangeListener
import com.peng.ppscale.business.ble.listener.PPBleStateInterface
import com.peng.ppscale.business.state.PPBleSwitchState
import com.peng.ppscale.business.state.PPBleWorkState
import com.peng.ppscale.device.PeripheralHamburger.PPBlutoothPeripheralHamburgerController
import com.peng.ppscale.vo.LFFoodScaleGeneral
import com.lefu.ppbase.PPDeviceModel

/**
 * 对应的协议: 3.x
 * 连接类型:广播
 * 设备类型 厨房秤
 */
class PeripheralHamburgerActivity : BaseImmersivePermissionActivity() {

    private var weightTextView: TextView? = null
    private var logTxt: TextView? = null
    private var device_set_connect_state: TextView? = null

    var controller: PPBlutoothPeripheralHamburgerController? = PPBlutoothPeripheralHamburgerInstance.instance.controller

    companion object {
        var deviceModel: PPDeviceModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.peripheral_hamburger_layout)
        
        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()
        
        // 初始化Toolbar
        initToolbar()
        
        weightTextView = findViewById<TextView>(R.id.weightTextView)
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

        controller?.deviceModel = deviceModel

        initClick()

    }
    
    private fun initToolbar() {
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        toolbar?.let {
            setupUnifiedToolbar(
                toolbar = it,
                title = "Hamburger设备",
                showBackButton = true
            )
        }
    }

    fun initClick() {
        findViewById<Button>(R.id.startSearch).setOnClickListener {
            addPrint("startSearch")
            controller?.registDataChangeListener(dataChangeListener)
            deviceModel?.deviceMac?.let { it1 -> controller?.startSearch(it1, bleStateInterface) }
        }
        findViewById<Button>(R.id.stopSearch).setOnClickListener {
            addPrint("stopSearch")
            controller?.registDataChangeListener(null)
            controller?.stopSeach()
        }
    }

    fun addPrint(msg: String) {
        if (msg.isNotEmpty()) {
            logTxt?.append("$msg\n")
        }
    }

    val dataChangeListener = object : FoodScaleDataChangeListener() {

        override fun processData(foodScaleGeneral: LFFoodScaleGeneral, deviceModel: PPDeviceModel?) {
            foodScaleGeneral?.let {
                weightTextView?.text = "process:${getFoodValue(it, deviceModel)}"
            }
        }

        override fun lockedData(foodScaleGeneral: LFFoodScaleGeneral, deviceModel: PPDeviceModel?) {
            foodScaleGeneral?.let {
                weightTextView?.text = "lock:${getFoodValue(it, deviceModel)}"
            }
        }
    }

    val bleStateInterface = object : PPBleStateInterface() {
        override fun monitorBluetoothWorkState(ppBleWorkState: PPBleWorkState?, deviceModel: PPDeviceModel?) {
           if (ppBleWorkState == PPBleWorkState.PPBleStateSearchCanceled) {
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
                Toast.makeText(this@PeripheralHamburgerActivity, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show()
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                addPrint(getString(R.string.system_blutooth_on))
                Toast.makeText(this@PeripheralHamburgerActivity, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getFoodValue(it: LFFoodScaleGeneral, deviceModel: PPDeviceModel?) = FoodScaleCacluteHelper.getValue(
        this@PeripheralHamburgerActivity, it.lfWeightKg.toFloat(), it.unit, deviceModel
    )

    override fun onPause() {
        super.onPause()
        controller?.stopSeach()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        controller?.stopSeach()
    }


}