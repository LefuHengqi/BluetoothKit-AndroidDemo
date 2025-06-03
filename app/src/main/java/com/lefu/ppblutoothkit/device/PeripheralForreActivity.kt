package com.lefu.ppblutoothkit.device

import android.Manifest
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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.lefu.ppbase.PPBodyBaseModel
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.PPScaleDefine
import com.lefu.ppbase.util.Logger
import com.lefu.ppbase.util.PPUtil
import com.lefu.ppbase.vo.PPUnitType
import com.lefu.ppbase.vo.PPUserModel
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.calculate.Calculate4AC2ChannelActivitiy
import com.lefu.ppblutoothkit.calculate.Calculate4ACActivitiy
import com.lefu.ppblutoothkit.calculate.Calculate8Activitiy
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralForreInstance
import com.lefu.ppblutoothkit.util.DataUtil
import com.lefu.ppblutoothkit.util.FileUtil
import com.lefu.ppblutoothkit.view.MsgDialog
import com.lefu.ppcalculate.PPBodyFatModel
import com.peng.ppscale.business.ble.PPScaleHelper
import com.peng.ppscale.business.ble.listener.PPBleStateInterface
import com.peng.ppscale.business.ble.listener.PPDataChangeListener
import com.peng.ppscale.business.ble.listener.PPDeviceLogInterface
import com.peng.ppscale.business.ble.listener.PPDeviceSetInfoInterface
import com.peng.ppscale.business.ble.listener.PPTorreDeviceModeChangeInterface
import com.peng.ppscale.business.ota.OnOTAStateListener
import com.peng.ppscale.business.state.PPBleSwitchState
import com.peng.ppscale.business.state.PPBleWorkState
import com.peng.ppscale.business.torre.listener.OnDFUStateListener
import com.peng.ppscale.business.torre.listener.PPTorreConfigWifiInterface
import com.peng.ppscale.device.PeripheralForre.PPBlutoothPeripheralForreController

/**
 * 一定要先连接设备，确保设备在已连接状态下使用
 * 对应的协议: FORRE
 * 连接类型:连接
 * 设备类型 人体秤
 */
class PeripheralForreActivity : AppCompatActivity() {

    private var userModel: PPUserModel? = null
    private var weightTextView: TextView? = null
    private var logTxt: TextView? = null
    private var device_set_connect_state: TextView? = null
    private var weightMeasureState: TextView? = null
    val mTestStateTv : TextView? by lazy { findViewById<TextView>(R.id.mTestStateTv) }

    val REQUEST_CODE = 1024

    var dfuFilePath: String? = null//本地文件升级时使用

    private var whetherFullyDFUToggleBtn: ToggleButton? = null//控制是否全量升级，true开启全量

    var controller: PPBlutoothPeripheralForreController? = PPBlutoothPeripheralForreInstance.instance.controller

    companion object {
        var deviceModel: PPDeviceModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.peripheral_forre_layout)

        userModel = DataUtil.getUserModel()
        userModel?.userID = "0EFA1294-A2D4-4476-93DC-1C2A2D8F1FEE"
        userModel?.memberID = "0EFA1294-A2D4-4476-93DC-1C2A2D8F1FEE"
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
        deviceModel?.let { it1 -> controller?.startConnect(it1, bleStateInterface) }
        controller?.getTorreDeviceManager()?.registDataChangeListener(dataChangeListener)
    }

    fun initClick() {
        findViewById<Button>(R.id.startConnectDevice).setOnClickListener {
            addPrint("startConnect")
            deviceModel?.let { it1 -> controller?.startConnect(it1, bleStateInterface) }
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
                    val isFullyDFUState = whetherFullyDFUToggleBtn?.isChecked ?: true //是否全量升级
                    if (isFullyDFUState) {
                        addPrint("Start full upgrade")
                        controller?.getTorreDeviceManager()?.startDFU(dfuFilePath, onDFUStateListener)
                    } else {
                        addPrint("readDeviceInfo")
                        controller?.getTorreDeviceManager()?.readDeviceInfoFromCharacter(object : PPTorreDeviceModeChangeInterface {
                            /**
                             * 设备信息返回
                             *
                             * @param deviceModel
                             */
//                            override fun readDeviceInfoComplete(deviceModel: PPDeviceModel?) {}
                            override fun onReadDeviceInfo(deviceModel: PPDeviceModel?) {
                                if (deviceModel != null && deviceModel.firmwareVersion != null) {
                                    addPrint("firmwareVersion: ${deviceModel.firmwareVersion}")
                                    addPrint("Start smart DFU upgrade")
                                    controller?.getTorreDeviceManager()?.startSmartDFU(dfuFilePath, deviceModel.firmwareVersion, onDFUStateListener)
                                } else {
                                    addPrint("deviceModel or firmwareVersion is null ")
                                }
                            }
                        })
                    }
                } else {
                    //正在升级，请不要频繁调用//Upgrading in progress, please do not call frequently
                    addPrint("Upgrading in progress, please do not call frequently")
                }
            } else {
                //本地升级时未选则升级文件
                addPrint("Upgrade files if not selected during local upgrade")
            }
        }
        findViewById<Button>(R.id.device_set_startOTA).setOnClickListener {
            addPrint("Query distribution network status")
            controller?.getTorreDeviceManager()?.getWifiState(object : PPTorreConfigWifiInterface() {
                /**
                 * 配网状态
                 * @param state
                 *  0x00：未配网（设备端恢复出厂或APP解除设备配网后状态）
                 *  0x01：已配网（APP已配网状态）
                 */
                override fun configWifiState(state: Int) {
                    if (state == 1) {
                        addPrint("The device has been connected to the network and the user upgrade has been initiated")
                        //已配网
                        controller?.getTorreDeviceManager()?.startUserOTA(otaStateListener)
                    } else {
                        //请先给设备配网
                        addPrint("Please first equip the device with a network")
                    }
                }
            })
        }
        val device_ota_layout = findViewById<LinearLayout>(R.id.device_ota_layout)
        val device_dfu_layout = findViewById<LinearLayout>(R.id.device_dfu_layout)
        findViewById<Button>(R.id.device_set_startLocalOTA).setOnClickListener {
            //wifi 本地必须有Test的Wifi, 且密码是：12345678 秤会自动连接该Wifi
            //ssid: Test password: 12345678
            addPrint("startLocalOTA 本地必须有Test的Wifi, 且密码是：12345678 秤会自动连接该Wifi")
            controller?.getTorreDeviceManager()?.startLocalOTA(otaStateListener)
        }
        if (PPScaleHelper.isFuncTypeWifi(deviceModel?.deviceFuncType)) {
            device_ota_layout.visibility = View.VISIBLE
            device_dfu_layout.visibility = View.GONE
        } else {
            device_ota_layout.visibility = View.GONE
            device_dfu_layout.visibility = View.VISIBLE
        }
        findViewById<ToggleButton>(R.id.pregnancyModeToggleBtn).setOnCheckedChangeListener { buttonView, isChecked ->
            addPrint("maternity mode isChecked:$isChecked")
            //0打开 1关闭
            controller?.getTorreDeviceManager()?.controlImpendance(if (isChecked) 0 else 1, modeChangeInterface)
        }
        findViewById<ToggleButton>(R.id.switchModeToggleBtn).setOnCheckedChangeListener { buttonView, isChecked ->
            addPrint("maternity mode isChecked:$isChecked")
            /**
             * 模式切换
             * @param type 0设置 1获取
             * @param mode 模式切换 0工厂 1用户
             * @param sendResultCallBack
             */
            controller?.getTorreDeviceManager()?.switchMode(0, if (isChecked) 0 else 1, null)
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
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateCanBeConnected) {
                addPrint("startConnect")
                if (Companion.deviceModel != null) {
                    deviceModel?.let {
                        if (deviceModel.deviceMac.equals(Companion.deviceModel!!.deviceMac)) {
                            mTestStateTv?.text = getString(R.string.device_be_connected)
                            addPrint(getString(R.string.device_be_connected))
                            controller?.startConnect(it, this)
                        }
                    }
                }
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
            }
        }

        override fun monitorBluetoothSwitchState(ppBleSwitchState: PPBleSwitchState?) {
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                addPrint(getString(R.string.system_bluetooth_disconnect))
                Toast.makeText(this@PeripheralForreActivity, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show()
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                addPrint(getString(R.string.system_blutooth_on))
                Toast.makeText(this@PeripheralForreActivity, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show()
            }
        }

        /**
         * PeripheralTorre 类型设备特有
         * PeripheralTorre设备在monitorMtuChange()后发送蓝牙指令
         * 其他的PeripheralType 设备要在PPBleWorkStateWritable中下发数据
         */
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
            val weightStr = PPUtil.getWeightValueD(bodyBaseModel?.unit, bodyBaseModel?.getPpWeightKg()?.toDouble() ?: 0.0, deviceModel!!.deviceAccuracyType.getType(), true)
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
                addPrint(getString(R.string.heartrate_mesuring))
                val weightStr = PPUtil.getWeightValueD(bodyBaseModel?.unit, bodyBaseModel?.getPpWeightKg()?.toDouble() ?: 0.0, deviceModel!!.deviceAccuracyType.getType(), true)
                weightTextView?.text = "lock:$weightStr ${PPUtil.getWeightUnit(bodyBaseModel?.unit)}"
                weightMeasureState?.text = ""
            } else {
                addPrint(getString(R.string.measure_complete))
                val weightStr = PPUtil.getWeightValueD(bodyBaseModel?.unit, bodyBaseModel?.getPpWeightKg()?.toDouble() ?: 0.0, deviceModel!!.deviceAccuracyType.getType(), true)
                weightTextView?.text = "lock:$weightStr ${PPUtil.getWeightUnit(bodyBaseModel?.unit)}"
                weightMeasureState?.text = ""
                //Before calling the computing library, it is necessary to assign the personal information of the user who is currently being weighed as a value
                //在调用计算库之前必须赋值成当前称重的用户的个人信息
                bodyBaseModel?.userModel = userModel
                //Calling the calculation library to calculate body fat information
                //调用计算库计算体脂信息
                Logger.d("PeripheralBorreActivity 四电极 双频 impedance:${bodyBaseModel?.impedance} impedance100EnCode:${bodyBaseModel?.ppImpedance100EnCode}")
                val fatModel = bodyBaseModel?.let { PPBodyFatModel(it) }
                addPrint("体脂计算完成 错误码：${fatModel?.errorType} 体脂率${fatModel?.ppFat} 心率${fatModel?.ppHeartRate}")
                bodyBaseModel?.let { showCalculateDialog(deviceModel, it) }
            }

        }

        override fun monitorDataFail(bodyBaseModel: PPBodyBaseModel?, deviceModel: PPDeviceModel?) {
            addPrint("monitorDataFail")
        }

        /**
         * 阻抗测量状态，
         */
        override fun onImpedanceFatting() {
            weightMeasureState?.text = getString(R.string.Impedance_measurement)
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
                ) {
                    //8电极交流算法  48项数据
                    val intent = Intent(this@PeripheralForreActivity, Calculate8Activitiy::class.java)
                    intent.putExtra("bodyDataModel", "bodyDataModel")
                    startActivity(intent)
                } else if (deviceModel.deviceCalcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_1) {
                    Logger.d("PeripheralBorreActivity 四电极 双频 impedance1:${DataUtil.bodyBaseModel?.impedance} impedance100EnCode:${DataUtil.bodyBaseModel?.ppImpedance100EnCode}")
                    //4电极交流算法  24项数据
                    val intent = Intent(this@PeripheralForreActivity, Calculate4AC2ChannelActivitiy::class.java)
                    intent.putExtra("bodyDataModel", "bodyDataModel")
                    startActivity(intent)
                } else {
                    //4电极交流算法  24项数据
                    val intent = Intent(this@PeripheralForreActivity, Calculate4ACActivitiy::class.java)
                    intent.putExtra("bodyDataModel", "bodyDataModel")
                    startActivity(intent)
                }
            })
            .show()
    }

    fun addPrint(msg: String) {
        if (msg.isNotEmpty()) {
            Logger.d(msg)
            logTxt?.append("$msg\n")
        }
    }



    val modeChangeInterface = object : PPTorreDeviceModeChangeInterface {

        override fun onReadDeviceInfo(deviceModel: PPDeviceModel?) {
            deviceModel?.let {
                addPrint(it.toString())
            }
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
        override fun readDeviceUnitCallBack(type: Int, state: Int, unitType: PPUnitType?) {
            addPrint("getUnit success unitType:${PPUtil.getWeightUnit(unitType)}")
        }

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

        override fun onDfuStart() {
            addPrint("onDfuStart")
//            PPBlutoothKit.setDebug(false)
        }

        override fun onDfuFail(errorType: String?) {
            addPrint("onDfuFail $errorType")
//            PPBlutoothKit.setDebug(true)
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
//            PPBlutoothKit.setDebug(true)
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

    val deviceLogInterface = object : PPDeviceLogInterface {
        override fun syncLogStart() {
            addPrint("syncLogStart")
        }

        override fun syncLoging(progress: Int) {
            addPrint("syncLoging progress:$progress")
        }

        override fun syncLogEnd(logFilePath: String?) {

            addPrint("syncLogEnd ")
            addPrint("logFilePath: $logFilePath")

            Toast.makeText(this@PeripheralForreActivity, "Sync Log Success", Toast.LENGTH_SHORT).show()
            FileUtil.sendEmail(this@PeripheralForreActivity, logFilePath)
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

        override fun monitorLanguageReviseSuccess() {
            addPrint("monitorLanguageReviseSuccess")
        }

        override fun monitorLanguageReviseFail() {
            addPrint("monitorLanguageReviseFail")
        }

        override fun monitorLanguageValueChange(language: Int) {
            //0x00：中文简体 //0x01：英文 //0x02：中文繁体 //0x03：日语 //0x04：西班牙语  //0x05：葡萄牙语  //0x06：阿拉伯语  //0x07：韩语
            addPrint("monitorLanguageRevise language:$language")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                performFileSearch()
            } else {
                Toast.makeText(this@PeripheralForreActivity, "存储权限获取失败", Toast.LENGTH_SHORT).show()
            }
        } else if (resultCode == RESULT_OK && data != null) {
            //当单选选了一个文件后返回
            if (data.data != null) {
                handleSingleDocument(data)
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
                Toast.makeText(this@PeripheralForreActivity, "存储权限获取失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
//        PPBlutoothKit.setDebug(true)
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