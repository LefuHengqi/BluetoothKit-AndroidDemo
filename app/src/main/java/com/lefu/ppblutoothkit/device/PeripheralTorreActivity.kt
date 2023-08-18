package com.lefu.ppblutoothkit.device

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.TextView
import android.widget.ToggleButton
import com.lefu.ppblutoothkit.function.PeripheralTorreConfigWifiActivity
import com.lefu.ppblutoothkit.instance.PPBlutoothPeripheralTorreInstance
import com.lefu.ppscale.ble.R
import com.lefu.ppscale.ble.util.DataUtil
import com.peng.ppscale.business.ble.configWifi.PPConfigStateMenu
import com.peng.ppscale.business.ble.listener.*
import com.peng.ppscale.business.device.PPUnitType
import com.peng.ppscale.business.ota.OnOTAStateListener
import com.peng.ppscale.business.torre.listener.OnDFUStateListener
import com.peng.ppscale.business.torre.listener.PPClearDataInterface
import com.peng.ppscale.business.torre.listener.PPTorreConfigWifiInterface
import com.peng.ppscale.device.PeripheralTorre.PPBlutoothPeripheralTorreController
import com.peng.ppscale.vo.PPBodyBaseModel
import com.peng.ppscale.vo.PPDeviceModel
import com.peng.ppscale.vo.PPUserModel
import com.peng.ppscale.vo.PPWifiModel

class PeripheralTorreActivity : Activity() {

    private var userModel: PPUserModel? = null
    private var weightTextView: TextView? = null
    private var wifi_name: TextView? = null

    private val dfuFilePath: String? = null//本地文件升级时使用

    private var whetherFullyDFUToggleBtn: ToggleButton? = null//控制是否全量升级，true开启全量

    var controller: PPBlutoothPeripheralTorreController? = PPBlutoothPeripheralTorreInstance.instance.controller

    companion object {
        var deviceModel: PPDeviceModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.device_set_torre_layout)

        userModel = DataUtil.util().userModel
        userModel?.userID = "1006451068@qq.com"
        userModel?.memberID = "4C2D82A7-AA9B-46F2-99BB-8B82A1F63626"
        userModel?.userName = "AB"

        weightTextView = findViewById<TextView>(R.id.weightTextView)
        wifi_name = findViewById<TextView>(R.id.wifi_name)
        whetherFullyDFUToggleBtn = findViewById<ToggleButton>(R.id.whetherFullyDFUToggleBtn)

    }

    fun initClick() {
        findViewById<Button>(R.id.device_set_getFilePath).setOnClickListener {

        }
        findViewById<TextView>(R.id.device_set_connect_state).setOnClickListener {

        }
        findViewById<Button>(R.id.device_set_light).setOnClickListener {
            val light = (Math.random() * 100).toInt()
            controller?.getTorreDeviceManager()?.setLight(light, deviceSetInterface)
        }
        findViewById<Button>(R.id.device_set_sync_log).setOnClickListener {
            controller?.getTorreDeviceManager()?.syncLog(deviceLogInterface)
        }
        findViewById<Button>(R.id.device_set_sync_time).setOnClickListener {
            controller?.getTorreDeviceManager()?.syncTime { }
        }
        findViewById<Button>(R.id.device_set_reset).setOnClickListener {
            controller?.getTorreDeviceManager()?.resetDevice(deviceSetInterface)
        }
        findViewById<Button>(R.id.device_set_synchistory).setOnClickListener {
            controller?.getTorreDeviceManager()?.syncHistory(userModel, historyDataInterface)
        }
        findViewById<Button>(R.id.device_set_sync_userinfo).setOnClickListener {
            controller?.getTorreDeviceManager()?.syncUserInfo(userModel)
        }
        findViewById<Button>(R.id.device_set_wifi_list).setOnClickListener {
            controller?.getTorreDeviceManager()?.getWifiList(configWifiInterface)
        }
        findViewById<Button>(R.id.device_set_startConfigWifi).setOnClickListener {
            startActivity(Intent(this, PeripheralTorreConfigWifiActivity::class.java))

            val ssid = "IT32"
            val password = "whs123456"
            val domainName = "http://nat.lefuenergy.com:10081"
            controller?.getTorreDeviceManager()?.configWifi(ssid, password, domainName, configWifiInterface)
        }
        findViewById<Button>(R.id.device_set_exitConfigWifi).setOnClickListener {
            controller?.getTorreDeviceManager()?.exitConfigWifi()
        }
        findViewById<Button>(R.id.device_set_delete_userinfo).setOnClickListener {
            controller?.getTorreDeviceManager()?.deleteAllUserInfo(userModel, userInfoInterface)
        }
        findViewById<Button>(R.id.device_set_confirm_current_userinfo).setOnClickListener {
            controller?.getTorreDeviceManager()?.confirmCurrentUser(userModel, userInfoInterface)
        }
        findViewById<Button>(R.id.device_set_get_userinfo_list).setOnClickListener {
            controller?.getTorreDeviceManager()?.getUserList(userInfoInterface)
        }
        findViewById<Button>(R.id.device_set_startDFU).setOnClickListener {
            if (dfuFilePath.isNullOrBlank().not()) {
                if (controller?.getTorreDeviceManager()?.isDFU?.not() ?: false) {
                    val isFullyDFUState = whetherFullyDFUToggleBtn?.isChecked ?: true //是否全量升级
                    controller?.getTorreDeviceManager()?.startDFU(isFullyDFUState, dfuFilePath, onDFUStateListener)
                } else {
                    //正在升级，请不要频繁调用
                }
            } else {
                //本地升级时未选则升级文件

            }
        }
        findViewById<Button>(R.id.device_set_startOTA).setOnClickListener {
            controller?.getTorreDeviceManager()?.getWifiState(object : PPTorreConfigWifiInterface() {
                /**
                 * 配网状态
                 * @param state
                 *  0x00：未配网（设备端恢复出厂或APP解除设备配网后状态）
                 *  0x01：已配网（APP已配网状态）
                 */
                override fun configWifiState(state: Int) {
                    if (state == 1) {
                        //已配网
                        controller?.getTorreDeviceManager()?.startUserOTA(otaStateListener)

                    } else {
                        //请先给设备配网
                    }
                }
            })
        }
        findViewById<Button>(R.id.device_set_startLocalOTA).setOnClickListener {
            //wifi 本地必须有Test的Wifi, 且密码是：12345678 秤会自动连接该Wifi
            //ssid: Test password: 12345678
            controller?.getTorreDeviceManager()?.startLocalOTA(otaStateListener)
        }
        findViewById<Button>(R.id.startMeasureBtn).setOnClickListener {
            controller?.getTorreDeviceManager()?.startMeasure({})
        }
        findViewById<ToggleButton>(R.id.pregnancyModeToggleBtn).setOnCheckedChangeListener { buttonView, isChecked ->
            //0打开 1关闭
            controller?.getTorreDeviceManager()?.controlImpendance(if (isChecked) 0 else 1, modeChangeInterface)
        }
        findViewById<Button>(R.id.getWifiSSID).setOnClickListener {
            controller?.getTorreDeviceManager()?.getWifiSSID(configWifiInterface)
        }
        findViewById<Button>(R.id.device_set_clearUser).setOnClickListener {
            controller?.getTorreDeviceManager()?.clearDeviceUserInfo(clearDataInterface)
        }
    }

    val clearDataInterface = object : PPClearDataInterface {
        override fun onClearSuccess() {

        }

        override fun onClearFail() {

        }

    }

    val modeChangeInterface = object : PPTorreDeviceModeChangeInterface() {
        override fun onIlluminationChange(illumination: Int) {}

        override fun readDeviceInfoComplete(deviceModel: PPDeviceModel?) {}

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

        }


        override fun readDeviceWifiMacCallBack(wifiMac: String?) {}

        /**
         * 启动测量结果回调
         * 0x00：成功
         * 0x01：设备配网中，开始测量失败
         * 0x02：设备OTA中，开始测量失败
         *
         * @param state
         */
        override fun startMeasureCallBack(state: Int) {}

        /**
         * 设备绑定状态
         *
         * @param type  0x01：设置  0x02：获取
         * @param state 0x00：设置成功 0x01：设置失败
         * 0x00：设备未绑定 0x01：设备已绑定
         */
        override fun bindStateCallBack(type: Int, state: Int) {}
    }

    val onDFUStateListener = object : OnDFUStateListener {
        override fun onDfuFail(errorType: String?) {}

        override fun onInfoOout(outInfo: String?) {}

        override fun onStartSendDfuData() {}

        override fun onDfuProgress(progress: Int) {}

        override fun onDfuSucess() {}

    }

    val userInfoInterface = object : PPUserInfoInterface {
        override fun getUserListSuccess(memberIDs: MutableList<String>?) {

        }

        override fun syncUserInfoSuccess() {

        }

        override fun syncUserInfoFail() {

        }

        override fun deleteUserInfoSuccess(userModel: PPUserModel?) {

        }

        override fun deleteUserInfoFail(userModel: PPUserModel?) {

        }

        override fun confirmCurrentUserInfoSuccess() {

        }

        override fun confirmCurrentUserInfoFail() {

        }

    }

    val configWifiInterface = object : PPTorreConfigWifiInterface() {
        override fun configResult(configStateMenu: PPConfigStateMenu?, resultCode: String?) {
        }

        override fun monitorWiFiListSuccess(wifiModels: MutableList<PPWifiModel>?) {
        }

        /**
         * @param ssid
         * @param state 0 成功 1失败
         */
        override fun readDeviceSsidCallBack(ssid: String?, state: Int) {

        }

        /**
         * 配网状态
         * 0x00：未配网（设备端恢复出厂或APP解除设备配网后状态）
         * 0x01：已配网（APP已配网状态）
         *
         * @param state
         */
        override fun configWifiState(state: Int) {

        }


    }

    val otaStateListener = object : OnOTAStateListener() {}

    val historyDataInterface = object : PPHistoryDataInterface() {

        override fun monitorHistoryData(bodyBaseModel: PPBodyBaseModel?, dateTime: String?) {

        }

        override fun monitorHistoryEnd(deviceModel: PPDeviceModel?) {

        }

        override fun monitorHistoryFail() {

        }

        /**
         * 历史数据发生改变
         */
        override fun onHistoryDataChange() {

        }

    }

    val deviceLogInterface = object : PPDeviceLogInterface {
        override fun syncLogStart() {

        }

        override fun syncLoging(progress: Int) {

        }

        override fun syncLogEnd(logFilePath: String?) {

        }
    }

    val deviceSetInterface = object : PPDeviceSetInfoInterface {
        override fun monitorResetStateSuccess() {

        }

        override fun monitorResetStateFail() {

        }

        override fun monitorLightValueChange(light: Int) {

        }

        override fun monitorLightReviseSuccess() {

        }

        override fun monitorLightReviseFail() {

        }

        override fun monitorMTUChange(deviceModel: PPDeviceModel?) {

        }

    }


}