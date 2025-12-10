package com.lefu.ppblutoothkit.device

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralJambulInstance
import com.peng.ppscale.business.ble.listener.PPBleStateInterface
import com.peng.ppscale.business.ble.listener.PPDataChangeListener
import com.peng.ppscale.business.state.PPBleSwitchState
import com.peng.ppscale.business.state.PPBleWorkState
import com.peng.ppscale.device.PeripheralJambul.PPBlutoothPeripheralJambulController
import com.lefu.ppbase.util.PPUtil
import com.lefu.ppbase.PPBodyBaseModel
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.PPScaleDefine
import com.lefu.ppbase.util.Logger
import com.lefu.ppbase.vo.PPUserModel
import com.lefu.ppblutoothkit.calculate.Calculate4AC2ChannelActivitiy
import com.lefu.ppblutoothkit.calculate.Calculate4ACActivitiy
import com.lefu.ppblutoothkit.calculate.Calculate4DCActivitiy
import com.lefu.ppblutoothkit.calculate.Calculate8Activitiy
import com.lefu.ppblutoothkit.device.PeripheralBorreActivity
import com.lefu.ppblutoothkit.util.DataUtil
import com.lefu.ppblutoothkit.view.MsgDialog

/**
 * 对应的协议: 3.x
 * 连接类型:广播
 * 设备类型 人体秤
 */
class PeripheralJambulActivity : BaseImmersivePermissionActivity() {

    private var weightTextView: TextView? = null
    private var logTxt: TextView? = null
    private var device_set_connect_state: TextView? = null
    private var weightMeasureState: TextView? = null

    var controller: PPBlutoothPeripheralJambulController? = PPBlutoothPeripheralJambulInstance.instance.controller

    companion object {
        var deviceModel: PPDeviceModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.peripheral_jambul_layout)

        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()

        // 初始化Toolbar
        initToolbar()

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

        initClick()

    }

    private fun initToolbar() {
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        toolbar?.let {
            setupUnifiedToolbar(
                toolbar = it,
                title = "Jambul设备",
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

        /**
         * 锁定数据
         *
         * @param bodyBaseModel
         */
        override fun monitorLockData(bodyBaseModel: PPBodyBaseModel?, deviceModel: PPDeviceModel?) {
            addPrint(getString(R.string.measure_complete))
            val weightStr = PPUtil.getWeightValueD(bodyBaseModel?.unit, bodyBaseModel?.getPpWeightKg()?.toDouble() ?: 0.0, deviceModel!!.deviceAccuracyType.getType(), true)
            weightTextView?.text = "lock:$weightStr ${PPUtil.getWeightUnit(bodyBaseModel?.unit)}"
            weightMeasureState?.text = ""

            addPrint("weightKg:${bodyBaseModel?.getPpWeightKg()}")
//            addPrint("impedance:${bodyBaseModel?.impedance}")
            addPrint("deviceCalcuteType:${bodyBaseModel?.deviceModel?.deviceCalcuteType}")


            var userModel: PPUserModel? = null
            userModel = DataUtil.getUserModel()
            userModel?.userID = "0EFA1294-A2D4-4476-93DC-1C2A2D8F1FEE"
            userModel?.memberID = "0EFA1294-A2D4-4476-93DC-1C2A2D8F1FEE"
            userModel?.userName = "AB"

            bodyBaseModel?.userModel = userModel

            bodyBaseModel?.let { showCalculateDialog(deviceModel, it) }

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
                Toast.makeText(this@PeripheralJambulActivity, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show()
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                addPrint(getString(R.string.system_blutooth_on))
                Toast.makeText(this@PeripheralJambulActivity, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show()
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

    private fun showCalculateDialog(deviceModel: PPDeviceModel, bodyBaseModel: PPBodyBaseModel) {
        DataUtil.bodyBaseModel = bodyBaseModel
        Logger.d("DataUtil.bodyBaseModel:${DataUtil.bodyBaseModel.hashCode()} bodyBaseModel:${bodyBaseModel.hashCode()}")
        Logger.d("PeripheralBorreActivity showCalculateDialog 四电极 双频 impedance:${bodyBaseModel.impedance} impedance100EnCode:${bodyBaseModel.ppImpedance100EnCode}")
        MsgDialog.init(supportFragmentManager)
            .setTitle(getString(R.string.tips))
            .setMessage(getString(R.string.is_body_fat_calculated))
            .setAnimStyle(R.style.dialog_)
            .setCancelableAll(true)
            .setNegativeButton(getString(R.string.cancel))
            .setPositiveButton(getString(R.string.confirm), View.OnClickListener() {
                if (deviceModel.deviceCalcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_0
                    || deviceModel.deviceCalcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8
                    || deviceModel.deviceCalcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_1
                    || deviceModel.deviceCalcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_2
                    || deviceModel.deviceCalcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_3
                    || deviceModel.deviceCalcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_4
                    || deviceModel.deviceCalcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_5
                ) {
                    //8电极交流算法  48项数据
                    val intent = Intent(this@PeripheralJambulActivity, Calculate8Activitiy::class.java)
                    intent.putExtra("bodyDataModel", "bodyDataModel")
                    startActivity(intent)
                } else if (deviceModel.deviceCalcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_1) {
                    Logger.d("PeripheralBorreActivity 四电极 双频 impedance1:${DataUtil.bodyBaseModel?.impedance} impedance100EnCode:${DataUtil.bodyBaseModel?.ppImpedance100EnCode}")
                    //4电极交流算法  24项数据
                    val intent = Intent(this@PeripheralJambulActivity, Calculate4AC2ChannelActivitiy::class.java)
                    intent.putExtra("bodyDataModel", "bodyDataModel")
                    startActivity(intent)
                } else if (deviceModel.deviceCalcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeDirect) {
                    //4电极直流算法
                    val intent = Intent(this@PeripheralJambulActivity, Calculate4DCActivitiy::class.java)
                    intent.putExtra("bodyDataModel", "bodyDataModel")
                    startActivity(intent)
                } else {
                    //4电极交流算法  24项数据
                    val intent = Intent(this@PeripheralJambulActivity, Calculate4ACActivitiy::class.java)
                    intent.putExtra("bodyDataModel", "bodyDataModel")
                    startActivity(intent)
                }
            })
            .show()
    }

}