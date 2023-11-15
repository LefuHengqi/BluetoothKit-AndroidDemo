package com.lefu.ppblutoothkit.device

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.lefu.ppblutoothkit.device.torre.PeripheralTorreSearchWifiListActivity
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralTorreInstance
import com.lefu.ppscale.ble.R
import com.lefu.ppblutoothkit.util.DataUtil
import com.peng.ppscale.business.ble.listener.*
import com.peng.ppscale.business.device.PPUnitType
import com.peng.ppscale.business.ota.OnOTAStateListener
import com.peng.ppscale.business.state.PPBleSwitchState
import com.peng.ppscale.business.state.PPBleWorkState
import com.peng.ppscale.business.torre.listener.OnDFUStateListener
import com.peng.ppscale.business.torre.listener.PPClearDataInterface
import com.peng.ppscale.business.torre.listener.PPTorreConfigWifiInterface
import com.peng.ppscale.device.PeripheralTorre.PPBlutoothPeripheralTorreController
import com.peng.ppscale.util.Logger
import com.peng.ppscale.util.PPUtil
import com.peng.ppscale.vo.PPBodyBaseModel
import com.peng.ppscale.vo.PPBodyFatModel
import com.peng.ppscale.vo.PPDeviceModel
import com.peng.ppscale.vo.PPUserModel

/**
 * 一定要先连接设备，确保设备在已连接状态下使用
 * 对应的协议: TORRE
 * 连接类型:连接
 * 设备类型 人体秤
 */
class PeripheralTorreActivity : Activity() {

    private var userModel: PPUserModel? = null
    private var weightTextView: TextView? = null
    private var logTxt: TextView? = null
    private var device_set_connect_state: TextView? = null
    private var weightMeasureState: TextView? = null

    val REQUEST_CODE = 1024

    var dfuFilePath: String? = null//本地文件升级时使用

    private var whetherFullyDFUToggleBtn: ToggleButton? = null//控制是否全量升级，true开启全量

    var controller: PPBlutoothPeripheralTorreController? = PPBlutoothPeripheralTorreInstance.instance.controller

    companion object {
        var deviceModel: PPDeviceModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.peripheral_torre_layout)

        userModel = DataUtil.util().userModel
        userModel?.userID = "1006451068@qq.com"
        userModel?.memberID = "4C2D82A7-AA9B-46F2-99BB-8B82A1F63626"
        userModel?.userName = "AB"

        weightTextView = findViewById<TextView>(R.id.weightTextView)
        logTxt = findViewById<TextView>(R.id.logTxt)
        whetherFullyDFUToggleBtn = findViewById<ToggleButton>(R.id.whetherFullyDFUToggleBtn)
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

    fun initClick() {
        findViewById<Button>(R.id.startConnectDevice).setOnClickListener {
            addPrint("startConnect")
            deviceModel?.let { it1 -> controller?.startConnect(it1, bleStateInterface) }
        }
        findViewById<Button>(R.id.startMeasureBtn).setOnClickListener {
            addPrint("startMeasure")
            controller?.getTorreDeviceManager()?.registDataChangeListener(dataChangeListener)
            controller?.getTorreDeviceManager()?.startMeasure() {}
        }
        findViewById<Button>(R.id.stopMeasureBtn).setOnClickListener {
            addPrint("stopMeasure")
            controller?.getTorreDeviceManager()?.unRegistDataChangeListener()
            controller?.getTorreDeviceManager()?.stopMeasure() {}
        }
        findViewById<Button>(R.id.device_set_light).setOnClickListener {
            val light = (Math.random() * 100).toInt()
            addPrint("setLight light:$light")
            controller?.getTorreDeviceManager()?.setLight(light, deviceSetInterface)
        }
        findViewById<Button>(R.id.device_set_sync_log).setOnClickListener {
            addPrint("syncLog")
            //logFilePath 指定文件存储路径，必传例如：val fileFath = context.filesDir.absolutePath + "/Log/DeviceLog"
            val fileFath = filesDir.absolutePath + "/Log/DeviceLog"
            controller?.getTorreDeviceManager()?.syncLog(fileFath, deviceLogInterface)
        }
        findViewById<Button>(R.id.syncTime).setOnClickListener {
            addPrint("syncTime")
            controller?.getTorreDeviceManager()?.syncTime {
                addPrint("syncTime Success")
            }
        }
        findViewById<Button>(R.id.syncUserHistoryData).setOnClickListener {
            addPrint("syncUserHistoryData userID:${userModel?.userID}")
            controller?.getTorreDeviceManager()?.syncUserHistory(userModel, userHistoryDataInterface)
        }
        findViewById<Button>(R.id.syncTouristHistoryData).setOnClickListener {
            addPrint("syncTouristHistoryData username:游客")
            controller?.getTorreDeviceManager()?.syncTouristHistory(touristHistoryDataInterface)
        }
        findViewById<Button>(R.id.device_set_sync_userinfo).setOnClickListener {
            addPrint("syncUserInfo userName:${userModel?.userName}")
            controller?.getTorreDeviceManager()?.syncUserInfo(userModel, userInfoInterface)
        }
        findViewById<Button>(R.id.deleteUserinfo).setOnClickListener {
            //根据userID去删除该userId下的所有子成员
            addPrint("deleteAllUserInfo userID:${userModel?.userID}")
            controller?.getTorreDeviceManager()?.deleteAllUserInfo(userModel, userInfoInterface)
            //删除单个用户,根据memberID去删除
//            controller?.getTorreDeviceManager()?.deleteUserInfo(userModel, userInfoInterface)
        }
        findViewById<Button>(R.id.device_set_confirm_current_userinfo).setOnClickListener {
            //下发当前称重用户
            addPrint("confirmCurrentUser userName:${userModel?.userName}")
            controller?.getTorreDeviceManager()?.confirmCurrentUser(userModel, userInfoInterface)
        }
//        findViewById<Button>(R.id.device_set_wifi_list).setOnClickListener {
//            addPrint("getWifiList")
//            controller?.getTorreDeviceManager()?.getWifiList(configWifiInterface)
//        }
        findViewById<Button>(R.id.startConfigWifi).setOnClickListener {
            addPrint("startConfigWifi pager")
            startActivity(Intent(this, PeripheralTorreSearchWifiListActivity::class.java))
        }
        findViewById<Button>(R.id.getWifiInfo).setOnClickListener {
            addPrint("getWifiSSID")
            controller?.getTorreDeviceManager()?.getWifiSSID(configWifiInterface)
        }
        findViewById<Button>(R.id.getWifiMac).setOnClickListener {
            addPrint("getWifiMac")
            controller?.getTorreDeviceManager()?.getWifiMac(configWifiInterface)
        }
        findViewById<Button>(R.id.device_set_get_userinfo_list).setOnClickListener {
            addPrint("getUserList")
            controller?.getTorreDeviceManager()?.getUserList(userInfoInterface)
        }
        findViewById<Button>(R.id.startKeepAlive).setOnClickListener {
            addPrint("startKeepAlive")
            controller?.getTorreDeviceManager()?.startKeepAlive()
        }
        findViewById<Button>(R.id.device_set_getFilePath).setOnClickListener {
            addPrint("check sdCard read and write permission")
            requestPermission()
        }
        findViewById<Button>(R.id.device_set_startDFU).setOnClickListener {
            if (dfuFilePath.isNullOrBlank().not()) {
                if (controller?.getTorreDeviceManager()?.isDFU?.not() ?: false) {
                    addPrint("启动DFU升级")
                    val isFullyDFUState = whetherFullyDFUToggleBtn?.isChecked ?: true //是否全量升级
                    controller?.getTorreDeviceManager()?.startDFU(isFullyDFUState, dfuFilePath, onDFUStateListener)
                } else {
                    //正在升级，请不要频繁调用
                    addPrint("正在升级，请不要频繁调用")
                }
            } else {
                //本地升级时未选则升级文件
                addPrint("本地升级时未选则升级文件")
            }
        }
        findViewById<Button>(R.id.device_set_startOTA).setOnClickListener {
            addPrint("查询配网状态")
            controller?.getTorreDeviceManager()?.getWifiState(object : PPTorreConfigWifiInterface() {
                /**
                 * 配网状态
                 * @param state
                 *  0x00：未配网（设备端恢复出厂或APP解除设备配网后状态）
                 *  0x01：已配网（APP已配网状态）
                 */
                override fun configWifiState(state: Int) {
                    if (state == 1) {
                        addPrint("设备已配网，启动用户升级")
                        //已配网
                        controller?.getTorreDeviceManager()?.startUserOTA(otaStateListener)
                    } else {
                        //请先给设备配网
                        addPrint("请先给设备配网")
                    }
                }
            })
        }
        findViewById<Button>(R.id.device_set_startLocalOTA).setOnClickListener {
            //wifi 本地必须有Test的Wifi, 且密码是：12345678 秤会自动连接该Wifi
            //ssid: Test password: 12345678
            addPrint("startLocalOTA 本地必须有Test的Wifi, 且密码是：12345678 秤会自动连接该Wifi")
            controller?.getTorreDeviceManager()?.startLocalOTA(otaStateListener)
        }
        findViewById<ToggleButton>(R.id.pregnancyModeToggleBtn).setOnCheckedChangeListener { buttonView, isChecked ->
            addPrint("maternity mode isChecked:$isChecked")
            //0打开 1关闭
            controller?.getTorreDeviceManager()?.controlImpendance(if (isChecked) 0 else 1, modeChangeInterface)
        }
        findViewById<Button>(R.id.device_set_clearUser).setOnClickListener {
            addPrint("clear Device User Info")
            controller?.getTorreDeviceManager()?.clearDeviceUserInfo(clearDataInterface)
        }
        findViewById<Button>(R.id.device_set_reset).setOnClickListener {
            addPrint("resetDevice")
            controller?.getTorreDeviceManager()?.resetDevice(deviceSetInterface)
        }
        findViewById<Button>(R.id.readDeviceInfo).setOnClickListener {
            addPrint("readDeviceInfo")
            controller?.getTorreDeviceManager()?.readDeviceInfoFromCharacter(modeChangeInterface)
        }
        findViewById<Button>(R.id.readDeviceBattery).setOnClickListener {
            addPrint("readDeviceBattery")
            controller?.getTorreDeviceManager()?.readDeviceBattery(modeChangeInterface)
        }
        findViewById<Button>(R.id.readLight).setOnClickListener {
            addPrint("readLight")
            controller?.getTorreDeviceManager()?.getLight(deviceSetInterface)
        }
        findViewById<Button>(R.id.getUnit).setOnClickListener {
            addPrint("getUnit")
            controller?.getTorreDeviceManager()?.getUnit(modeChangeInterface)
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
                Toast.makeText(this@PeripheralTorreActivity, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show()
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                addPrint(getString(R.string.system_blutooth_on))
                Toast.makeText(this@PeripheralTorreActivity, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show()
            }
        }

        override fun monitorMtuChange(deviceModel: PPDeviceModel?) {
            addPrint("monitorMtuChange mtu:${deviceModel?.mtu}")
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

        /**
         * 锁定数据
         *
         * @param bodyBaseModel
         */
        override fun monitorLockData(bodyBaseModel: PPBodyBaseModel?, deviceModel: PPDeviceModel?) {
            if (bodyBaseModel?.isHeartRating ?: false) {
                addPrint("心率测量中...")
                val weightStr = PPUtil.getWeightValueD(bodyBaseModel?.unit, bodyBaseModel?.getPpWeightKg()?.toDouble() ?: 0.0, deviceModel!!.deviceAccuracyType.getType())
                weightTextView?.text = "lock:$weightStr ${PPUtil.getWeightUnit(bodyBaseModel?.unit)}"
                weightMeasureState?.text = ""
            } else {
                addPrint("测量完成")
                val weightStr = PPUtil.getWeightValueD(bodyBaseModel?.unit, bodyBaseModel?.getPpWeightKg()?.toDouble() ?: 0.0, deviceModel!!.deviceAccuracyType.getType())
                weightTextView?.text = "lock:$weightStr ${PPUtil.getWeightUnit(bodyBaseModel?.unit)}"
                weightMeasureState?.text = ""
                bodyBaseModel?.userModel = userModel //在调用计算库之前必须赋值成当前称重的用户的个人信息
                //调用计算库计算体脂信息
                val fatModel = bodyBaseModel?.let { PPBodyFatModel(it) }
                addPrint("体脂计算完成 错误码：${fatModel?.errorType} 体脂率${fatModel?.ppFat} 心率${fatModel?.ppHeartRate}")
            }

        }

        override fun monitorDataFail(bodyBaseModel: PPBodyBaseModel?, deviceModel: PPDeviceModel?) {
            addPrint("monitorDataFail")
        }

        /**
         * 阻抗测量状态，
         */
        override fun onImpedanceFatting() {
            weightMeasureState?.text = "阻抗测量中"
        }

        /**
         * 超重
         */
        override fun monitorOverWeight() {
            weightMeasureState?.text = "超重"
        }

        /**
         * 设备关机回调，目前只在Torre设备上生效
         */
        override fun onDeviceShutdown() {
            weightMeasureState?.text = "关机"
            addPrint("关机")
        }

        /**
         * 历史数据发生改变,目前只在Torre设备上生效
         */
        override fun onHistoryDataChange() {
            addPrint("有新的历史数据")
        }

    }

    fun addPrint(msg: String) {
        if (msg.isNotEmpty()) {
            Logger.d(msg)
            logTxt?.append("$msg\n")
        }
    }

    val clearDataInterface = object : PPClearDataInterface {
        override fun onClearSuccess() {
            addPrint("onClearSuccess")
        }

        override fun onClearFail() {
            addPrint("onClearFail")
        }

    }

    val modeChangeInterface = object : PPTorreDeviceModeChangeInterface {

        override fun readDeviceInfoComplete(deviceModel: PPDeviceModel?) {
            addPrint("readDeviceInfoComplete ${deviceModel.toString()}")
        }


        override fun readDevicePower(power: Int) {
            addPrint("readDevicePower power:$power")
        }

        /**
         * 设置/获取设备单位
         *
         * @param type  1设置单位 2获取单位
         * @param state 0设置成功 1设置失败
         */
        override fun readDeviceUnitCallBack(type: Int, state: Int, unitType: PPUnitType?) {}

        /**
         * 心率开关状态
         *
         * @param type  1设置开关 2获取开关
         * @param state 0打开 1关闭
         */
        override fun readHeartRateStateCallBack(type: Int, state: Int) {}

        override fun switchBabyModeCallBack(state: Int) {}

        /**
         * @param type  0x01：设置开关 0x02：获取开关
         * @param state 0x00：设置成功 0x01：设置失败
         * 获取开关 0x00：阻抗测量打开 0x01：阻抗测量关闭
         */
        override fun controlImpendanceCallBack(type: Int, state: Int) {
            if (type == 0) {//设置开关
                if (state == 0) {
                    addPrint("maternity mode success")
                } else {
                    addPrint("maternity mode fail")
                }
            } else {//获取开关
                if (state == 0) {
                    addPrint("maternity mode is on")
                } else {
                    addPrint("maternity mode is off")
                }
            }
        }

        /**
         * 启动测量结果回调
         * 0x00：成功
         * 0x01：设备配网中，开始测量失败
         * 0x02：设备OTA中，开始测量失败
         *
         * @param state
         */
        override fun startMeasureCallBack(state: Int) {
            addPrint("startMeasureCallBack state $state")
        }

        /**
         * 设备绑定状态
         *
         * @param type  0x01：设置  0x02：获取
         * @param state 0x00：设置成功 0x01：设置失败
         * 0x00：设备未绑定 0x01：设备已绑定
         */
        override fun bindStateCallBack(type: Int, state: Int) {
            addPrint("bindStateCallBack type $type state$state")
        }
    }

    val onDFUStateListener = object : OnDFUStateListener {

        override fun onDfuFail(errorType: String?) {
            addPrint("onDfuFail $errorType")
        }

        override fun onInfoOout(outInfo: String?) {
            addPrint("onInfoOout $outInfo")
        }

        override fun onStartSendDfuData() {
            addPrint("onStartSendDfuData")
        }

        override fun onDfuProgress(progress: Int) {
            addPrint("onDfuProgress progress:$progress")
        }

        override fun onDfuSucess() {
            addPrint("onDfuSucess")
        }

    }

    val otaStateListener = object : OnOTAStateListener() {

        /**
         * @param state 0普通的失败 1设备已在升级中不能再次启动升级 2设备低电量无法启动升级
         */
        override fun onUpdateFail(state: Int) {
            addPrint("onUpdateFail state:$state")
        }

        override fun onStartUpdate() {
            addPrint("onStartUpdate")
        }

    }

    val userInfoInterface = object : PPUserInfoInterface {
        override fun getUserListSuccess(memberIDs: MutableList<String>?) {
            addPrint("getUserListSuccess ${memberIDs.toString()}")
        }

        override fun syncUserInfoSuccess() {
            addPrint("syncUserInfoSuccess")
        }

        override fun syncUserInfoFail() {
            addPrint("syncUserInfoFail")
        }

        override fun deleteUserInfoSuccess(userModel: PPUserModel?) {
            //这里自行根据调用的方法去判断是删除的所有用户还是删除的单个用户
            addPrint("deleteUserInfoSuccess userID:${userModel?.userID} userName:${userModel?.userName}")
        }

        override fun deleteUserInfoFail(userModel: PPUserModel?) {
            addPrint("deleteUserInfoFail userID:${userModel?.userID} userName:${userModel?.userName}")
        }

        override fun confirmCurrentUserInfoSuccess() {
            addPrint("confirmCurrentUserInfoSuccess")
        }

        override fun confirmCurrentUserInfoFail() {
            addPrint("confirmCurrentUserInfoFail")
        }

    }

    val configWifiInterface = object : PPTorreConfigWifiInterface() {

        /**
         * @param ssid
         * @param state 0 成功 1失败
         */
        override fun readDeviceSsidCallBack(ssid: String?, state: Int) {
            addPrint("readDeviceSsidCallBack ssid:$ssid state:$state")
        }

        override fun readDeviceWifiMacCallBack(wifiMac: String) {
            addPrint("readDeviceWifiMacCallBack wifiMac $wifiMac")
        }

        /**
         * 配网状态
         * 0x00：未配网（设备端恢复出厂或APP解除设备配网后状态）
         * 0x01：已配网（APP已配网状态）
         *
         * @param state
         */
        override fun configWifiState(state: Int) {
            addPrint("configWifiState state:$state")
        }
    }

    val touristHistoryDataInterface = object : PPHistoryDataInterface() {

        override fun monitorHistoryData(bodyBaseModel: PPBodyBaseModel?, dateTime: String?) {
            addPrint("touristHistoryData: weight:${bodyBaseModel?.getPpWeightKg()} dateTime:$dateTime")
        }

        override fun monitorHistoryEnd(deviceModel: PPDeviceModel?) {
            addPrint("touristHistoryDataEnd")
        }

        override fun monitorHistoryFail() {
            addPrint("touristHistoryDataFail")
        }

    }

    val userHistoryDataInterface = object : PPHistoryDataInterface() {

        override fun monitorHistoryData(bodyBaseModel: PPBodyBaseModel?, dateTime: String?) {
            addPrint("monitorHistoryData: weight:${bodyBaseModel?.getPpWeightKg()} dateTime:$dateTime")
        }

        override fun monitorHistoryEnd(deviceModel: PPDeviceModel?) {
            addPrint("monitorHistoryEnd")
        }

        override fun monitorHistoryFail() {
            addPrint("monitorHistoryFail")
        }

    }

    val deviceLogInterface = object : PPDeviceLogInterface {
        override fun syncLogStart() {
            addPrint("syncLogStart")
        }

        override fun syncLoging(progress: Int) {
            addPrint("syncLoging progress:$progress")
        }

        override fun syncLogEnd(logFilePath: String?) {
            addPrint("syncLogEnd")
        }
    }

    val deviceSetInterface = object : PPDeviceSetInfoInterface {
        override fun monitorResetStateSuccess() {
            addPrint("monitorResetStateSuccess")
        }

        override fun monitorResetStateFail() {
            addPrint("monitorResetStateFail")
        }

        override fun monitorLightValueChange(light: Int) {
            addPrint("monitorLightValueChange light：" + light)
        }

        override fun monitorLightReviseSuccess() {
            addPrint("monitorLightReviseSuccess")
        }

        override fun monitorLightReviseFail() {
            addPrint("monitorLightReviseFail")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                performFileSearch()
            } else {
                Toast.makeText(this@PeripheralTorreActivity, "存储权限获取失败", Toast.LENGTH_SHORT).show()
            }
        } else if (resultCode == RESULT_OK && data != null) {
            //当单选选了一个文件后返回
            if (data.data != null) {
                handleSingleDocument(data)
            } else {
                //多选
                val clipData = data.clipData
                if (clipData != null) {
                    val uris = arrayOfNulls<Uri>(clipData.itemCount)
                    for (i in 0 until clipData.itemCount) {
                        uris[i] = clipData.getItemAt(i).uri
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            ) {
                performFileSearch()
            } else {
                Toast.makeText(this@PeripheralTorreActivity, "存储权限获取失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (controller?.getTorreDeviceManager()?.isDFU ?: false) {
            controller?.getTorreDeviceManager()?.stopDFU()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        controller?.stopSeach()
        controller?.disConnect()
    }


}