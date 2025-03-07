package com.lefu.ppblutoothkit.device

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.UserinfoActivity
import com.lefu.ppblutoothkit.calculate.Calculate4ACActivitiy
import com.lefu.ppblutoothkit.calculate.Calculate8Activitiy
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralIceInstance
import com.lefu.ppblutoothkit.device.torre.PeripheralTorreSearchWifiListActivity
import com.lefu.ppblutoothkit.okhttp.NetUtil
import com.lefu.ppblutoothkit.util.DataUtil
import com.lefu.ppblutoothkit.util.FileUtil
import com.lefu.ppblutoothkit.view.MsgDialog
import com.peng.ppscale.business.ble.PPScaleHelper
import com.peng.ppscale.business.ble.configWifi.PPConfigWifiInfoInterface
import com.peng.ppscale.business.ble.listener.*
import com.peng.ppscale.business.ota.OnOTAStateListener
import com.peng.ppscale.business.state.PPBleSwitchState
import com.peng.ppscale.business.state.PPBleWorkState
import com.peng.ppscale.device.PeripheralIce.PPBlutoothPeripheralIceController
import com.lefu.ppbase.util.Logger
import com.lefu.ppbase.util.PPUtil
import com.lefu.ppbase.PPBodyBaseModel
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.PPScaleDefine
import com.peng.ppscale.vo.PPScaleSendState

/**
 * 对应的协议: 4.x
 * 连接类型:连接
 * 设备类型 人体秤
 */
class PeripheralIceActivity : AppCompatActivity() {

    private var weightTextView: TextView? = null
    private var logTxt: TextView? = null
    private var device_set_connect_state: TextView? = null
    private var weightMeasureState: TextView? = null
    var dfuFilePath: String? = null//本地文件升级时使用
    private var whetherFullyDFUToggleBtn: ToggleButton? = null//控制是否全量升级，true开启全量

    val REQUEST_CODE = 1024

    private val mCurrentHostUrl by lazy {
        findViewById<TextView>(R.id.mCurrentHostUrl)
    }
    var controller: PPBlutoothPeripheralIceController? = PPBlutoothPeripheralIceInstance.instance.controller

    companion object {
        var deviceModel: PPDeviceModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.peripheral_ice_layout)

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
        setTitle("${deviceModel?.getDevicePeripheralType()}")
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
            controller?.syncUnit(DataUtil.unit, object : PPBleSendResultCallBack {
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
            addPrint("startConfigWifi pager")
            if (PPScaleHelper.isFuncTypeWifi(deviceModel?.deviceFuncType)) {
                controller?.readDeviceBattery(object : PPDeviceInfoInterface() {

                    /**
                     * 设备电量返回/Device power return
                     *
                     * @param power -1失败，0-100  /  -1 for failure, 0-100
                     * @param state -1不支持 0正常 1充电中  /  -1 Not supported 0 Normal 1 Charging
                     */
                    override fun readDevicePower(power: Int, state: Int) {
                        if (state == -1) {
                            addPrint("The device does not support reading battery level")
                        } else if (state == 0) {
                            addPrint("readDevicePower power:$power")
                            if (power > 20) {
                                PeripheralTorreSearchWifiListActivity.deviceModel = deviceModel
                                startActivity(Intent(this@PeripheralIceActivity, PeripheralTorreSearchWifiListActivity::class.java))
                            } else {
                                addPrint("Low battery, please charge first")
                            }
                        } else if (state == 1) {
                            addPrint("The device is charging")
                        }
                    }
                })
            } else {
                addPrint("device does not support")
            }

        }
        findViewById<Button>(R.id.getWifiInfo).setOnClickListener {
            addPrint("getWifiInfo")
            if (PPScaleHelper.isFuncTypeWifi(deviceModel?.deviceFuncType)) {
                controller?.getWifiInfo(configWifiInfoInterface)
            } else {
                addPrint("device does not support")
            }
        }
        findViewById<Button>(R.id.setUserInfo).setOnClickListener {
            addPrint("start UserInfo pager")
            startActivity(Intent(this, UserinfoActivity::class.java))
        }
        findViewById<Button>(R.id.readDeviceBattery).setOnClickListener {
            addPrint("readDeviceBattery")
            controller?.readDeviceBattery(object : PPDeviceInfoInterface() {
                /**
                 * 设备电量返回/Device power return
                 *
                 * @param power -1失败，0-100  /  -1 for failure, 0-100
                 * @param state -1不支持 0正常 1充电中  /  -1 Not supported 0 Normal 1 Charging
                 */
                override fun readDevicePower(power: Int, state: Int) {
                    if (state == -1) {
                        addPrint("The device does not support reading battery level")
                    } else if (state == 0) {
                        addPrint("readDevicePower power:$power")
                    } else if (state == 1) {
                        addPrint("The device is charging")
                    }
                }
            })
        }
        findViewById<Button>(R.id.device_set_sync_log).setOnClickListener {
            addPrint("syncLog")
            //logFilePath 指定文件存储路径，必传例如：val fileFath = context.filesDir.absolutePath + "/Log/DeviceLog"
            val fileFath = filesDir.absolutePath + "/Log/DeviceLog"
            controller?.syncLog(fileFath, deviceLogInterface)
        }
        findViewById<Button>(R.id.device_set_reset).setOnClickListener {
            addPrint("resetDevice")
            controller?.resetDevice(deviceSetInterface)
        }
        findViewById<Button>(R.id.startKeepAlive).setOnClickListener {
            addPrint("startKeepAlive")
            controller?.startKeepAlive()
        }
        findViewById<Button>(R.id.setNetHost).setOnClickListener {
            addPrint("setNetHost")
            startActivity(Intent(this, SetHostActivity::class.java))
        }
        findViewById<Button>(R.id.device_set_startOTA).setOnClickListener {
            addPrint("Query distribution network status")
            //电量低于20%时提醒用户
            controller?.readDeviceBattery(object : PPDeviceInfoInterface() {

                /**
                 * 设备电量返回/Device power return
                 *
                 * @param power -1失败，0-100  /  -1 for failure, 0-100
                 * @param state -1不支持 0正常 1充电中  /  -1 Not supported 0 Normal 1 Charging
                 */
                override fun readDevicePower(power: Int, state: Int) {
                    if (state == -1) {
                        addPrint("The device does not support reading battery level")
                    } else if (state == 0) {
                        Logger.d("设备电量检测结果打印，如果低于20%拦截并提示用户充电  power：$power")
                        if (power < 20) {
                            addPrint("设备电量低")
                        } else {
                            controller?.startUserOTA(object : OnOTAStateListener() {
                                override fun onStartUpdate() {
                                    addPrint("onStartUpdate")
                                }

                                /**
                                 * @param state 0普通的失败 1设备已在升级中不能再次启动升级 2设备低电量无法启动升级 3未配网 4 充电中
                                 */
                                override fun onUpdateFail(state: Int) {
                                    addPrint("onUpdateFail")
                                }

                                override fun onUpdateSucess() {
                                    addPrint("onUpdateSucess")
                                }
                            })
                        }
                    } else if (state == 1) {
                        addPrint("The device is charging")
                    }
                }
            })
        }
        findViewById<Button>(R.id.device_set_startLocalOTA).setOnClickListener {
            addPrint("Query distribution network status")
            //电量低于20%时提醒用户
            controller?.readDeviceBattery(object : PPDeviceInfoInterface() {

                /**
                 * 设备电量返回/Device power return
                 *
                 * @param power -1失败，0-100  /  -1 for failure, 0-100
                 * @param state -1不支持 0正常 1充电中  /  -1 Not supported 0 Normal 1 Charging
                 */
                override fun readDevicePower(power: Int, state : Int) {
                    if (state == -1) {
                        addPrint("The device does not support reading battery level")
                    } else if (state == 0) {
                        Logger.d("设备电量检测结果打印，如果低于20%拦截并提示用户充电  power：$power")
                        if (power < 20) {
                            addPrint("设备电量低")
                        } else {
                            controller?.startLocalOTA(object : OnOTAStateListener() {
                                override fun onStartUpdate() {
                                    addPrint("onStartUpdate")
                                }

                                /**
                                 * @param state 0普通的失败 1设备已在升级中不能再次启动升级 2设备低电量无法启动升级 3未配网 4 充电中
                                 */
                                override fun onUpdateFail(state: Int) {
                                    addPrint("onUpdateFail")
                                }

                                override fun onUpdateSucess() {
                                    addPrint("onUpdateSucess")
                                }
                            })
                        }
                    } else if (state == 1) {
                        addPrint("The device is charging")
                    }
                }

            })
        }
//        val device_ota_layout = findViewById<LinearLayout>(R.id.device_ota_layout)
//        if (PPScaleHelper.isFuncTypeWifi(deviceModel?.deviceFuncType)) {
//            device_ota_layout.visibility = View.VISIBLE
//        } else {
//            device_ota_layout.visibility = View.GONE
//        }
    }

    override fun onResume() {
        super.onResume()
        mCurrentHostUrl?.text = "当前域名：${NetUtil.getScaleDomain()}"
    }

    val deviceSetInterface = object : PPDeviceSetInfoInterface {
        override fun monitorResetStateSuccess() {
            addPrint("monitorResetStateSuccess")
        }

        override fun monitorResetStateFail() {
            addPrint("monitorResetStateFail")
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

            Toast.makeText(this@PeripheralIceActivity, "Sync Log Success", Toast.LENGTH_SHORT).show()
            FileUtil.sendEmail(this@PeripheralIceActivity, logFilePath)
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
            val weightStr = PPUtil.getWeightValueD(bodyBaseModel?.unit, bodyBaseModel?.getPpWeightKg()?.toDouble() ?: 0.0, deviceModel!!.deviceAccuracyType.getType(), true)
            weightTextView?.text = "lock:$weightStr ${PPUtil.getWeightUnit(bodyBaseModel?.unit)}"

            //这里要填称重用户的个人信息
            val userModel = DataUtil.getUserModel()
            bodyBaseModel?.userModel = userModel
            if (bodyBaseModel?.isHeartRating ?: false) {
                //心率测量中
                weightMeasureState?.text = getString(R.string.heartrate_mesuring)
            } else {
                //测量结束
                weightMeasureState?.text = getString(R.string.measure_complete)
                MsgDialog.init(supportFragmentManager)
                    .setTitle(getString(R.string.tips))
                    .setMessage(getString(R.string.is_body_fat_calculated))
                    .setAnimStyle(R.style.dialog_)
                    .setCancelableAll(true)
                    .setNegativeButton(getString(R.string.cancel))
                    .setPositiveButton(getString(R.string.confirm), View.OnClickListener {
                        DataUtil.bodyBaseModel = bodyBaseModel
                        if (deviceModel.deviceCalcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_0
                            || deviceModel.deviceCalcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8
                            || deviceModel.deviceCalcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_1
                            || deviceModel.deviceCalcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_3
                            || deviceModel.deviceCalcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_4
                        ) {
                            //8电极交流算法  48项数据
                            val intent = Intent(this@PeripheralIceActivity, Calculate8Activitiy::class.java)
                            intent.putExtra("bodyDataModel", "bodyDataModel")
                            startActivity(intent)
                        } else {
                            //4电极交流算法  24项数据
                            val intent = Intent(this@PeripheralIceActivity, Calculate4ACActivitiy::class.java)
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

        override fun monitorModifyServerDomainSuccess() {
            addPrint("ModifyServerDNSSuccess")
        }

    }

    override fun onPause() {
        super.onPause()
//        PPBlutoothKit.setDebug(true)
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

        /**
         * PeripheralIce/Torre/Borre/Dorre 类型设备在monitorMtuChange()后发送蓝牙指令
         * 其他的PeripheralType 设备要在PPBleWorkStateWritable中下发数据
         */
        override fun monitorMtuChange(deviceModel: PPDeviceModel?) {
            addPrint("monitorMtuChange mtu:${deviceModel?.mtu}")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        controller?.disConnect()
    }


}