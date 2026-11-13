package com.lefu.ppblutoothkit.device.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lefu.ppbase.PPBodyBaseModel
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.util.Logger
import com.lefu.ppbase.vo.PPUserModel
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.calculate.BodyDataAutoCalculateHelper
import com.lefu.ppblutoothkit.device.auto.isDeviceConnected
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralAppleInstance
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralBorreInstance
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralDorreInstance
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralIceInstance
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralTorreInstance
import com.lefu.ppblutoothkit.util.DataUtil
import com.peng.ppscale.business.ble.PPScaleHelper
import com.peng.ppscale.business.ble.listener.PPBleSendResultCallBack
import com.peng.ppscale.business.ble.listener.PPHistoryDataInterface
import com.peng.ppscale.business.torre.TorreDeviceManager
import com.peng.ppscale.vo.PPScaleSendState

/**
 * 设备历史数据（离线测量数据）
 *
 * 设备在断开蓝牙连接、且未配网的情况下上秤测量，数据会保存在设备中（历史数据）。
 * 再次连接设备后，调用获取历史数据的方法即可拉取这些数据。
 *
 * - Torre/Borre/Dorre：支持按用户区分
 *     获取用户历史数据 -> TorreDeviceManager.syncUserHistory(userModel, PPHistoryDataInterface)
 *     获取游客历史数据 -> TorreDeviceManager.syncTouristHistory(PPHistoryDataInterface)
 * - Apple/Ice：不区分用户（回调数据没有 Member ID）
 *     获取设备历史数据 -> Apple: controller.getHistoryData(...) / Ice: controller.getHistory(...)
 *     删除设备历史数据 -> controller.deleteHistoryData(PPBleSendResultCallBack)
 *
 * 进入页面默认为空列表，只有点击顶部按钮才会下发指令。点击列表项会用该条数据计算身体数据并进入结果页。
 * 注意：本页面依赖设备主页面已经建立的连接，不负责连接设备。
 */
class DeviceHistoryDataActivity : BaseImmersivePermissionActivity() {

    enum class Protocol(val supportUser: Boolean) {
        TORRE(true),
        BORRE(true),
        DORRE(true),
        APPLE(false),
        ICE(false)
    }

    companion object {
        private const val EXTRA_PROTOCOL = "extra_protocol"
        private const val EXTRA_USER_MODEL = "extra_user_model"

        /**
         * @param deviceModel 当前已连接的设备
         * @param userModel   获取用户历史数据时使用的用户（仅 Torre/Borre/Dorre 需要，需与同步到设备的用户一致）
         */
        fun start(context: Context, protocol: Protocol, deviceModel: PPDeviceModel?, userModel: PPUserModel? = null) {
            Companion.deviceModel = deviceModel
            val intent = Intent(context, DeviceHistoryDataActivity::class.java)
            intent.putExtra(EXTRA_PROTOCOL, protocol.name)
            userModel?.let { intent.putExtra(EXTRA_USER_MODEL, it) }
            context.startActivity(intent)
        }

        var deviceModel: PPDeviceModel? = null
    }

    private lateinit var protocol: Protocol
    private var userModel: PPUserModel? = null

    /**
     * 当前这次获取的数据是否显示 Member ID：
     * 只有「获取用户历史数据」显示；游客历史数据、Apple/Ice 设备历史数据都不显示
     */
    private var showMemberId = false

    private lateinit var leftBtn: TextView
    private lateinit var rightBtn: TextView
    private lateinit var emptyTips: TextView
    private lateinit var adapter: DeviceHistoryDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_history_data)

        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()

        // 初始化Toolbar
        initToolbar()

        protocol = runCatching { Protocol.valueOf(intent.getStringExtra(EXTRA_PROTOCOL) ?: "") }.getOrNull() ?: run {
            finish()
            return
        }
        userModel = intent.getSerializableExtra(EXTRA_USER_MODEL) as? PPUserModel

        leftBtn = findViewById(R.id.historyActionLeftBtn)
        rightBtn = findViewById(R.id.historyActionRightBtn)
        emptyTips = findViewById(R.id.historyEmptyTips)

        val recyclerView = findViewById<RecyclerView>(R.id.historyDataRV)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DeviceHistoryDataAdapter { item -> openBodyData(item) }
        recyclerView.adapter = adapter

        initActions()
        refreshEmptyState()
    }

    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setupUnifiedToolbar(
            toolbar = toolbar,
            title = getString(R.string.device_history_data),
            showBackButton = true
        )
    }

    private fun initActions() {
        if (protocol.supportUser) {
            leftBtn.setText(R.string.get_user_history_data)
            rightBtn.setText(R.string.get_tourist_history_data)
            leftBtn.setOnClickListener {
                if (!checkConnected()) return@setOnClickListener
                selectButton(leftBtn)
                syncUserHistory()
            }
            rightBtn.setOnClickListener {
                if (!checkConnected()) return@setOnClickListener
                selectButton(rightBtn)
                syncTouristHistory()
            }
        } else {
            leftBtn.setText(R.string.get_device_history_data)
            rightBtn.setText(R.string.delete_device_history_data)
            leftBtn.setOnClickListener {
                if (!checkConnected() || !checkSupportHistory()) return@setOnClickListener
                selectButton(leftBtn)
                getDeviceHistory()
            }
            rightBtn.setOnClickListener {
                if (!checkConnected() || !checkSupportHistory()) return@setOnClickListener
                deleteDeviceHistory()
            }
        }
    }

    // region Torre/Borre/Dorre

    /**
     * Torre/Borre/Dorre 的 DeviceManager 都继承自 TorreDeviceManager，历史数据接口一致
     */
    private fun torreDeviceManager(): TorreDeviceManager? = when (protocol) {
        Protocol.TORRE -> PPBlutoothPeripheralTorreInstance.instance.controller?.getTorreDeviceManager()
        Protocol.BORRE -> PPBlutoothPeripheralBorreInstance.instance.controller?.getTorreDeviceManager()
        Protocol.DORRE -> PPBlutoothPeripheralDorreInstance.instance.controller?.getTorreDeviceManager()
        else -> null
    }

    private fun syncUserHistory() {
        val user = userModel ?: DataUtil.getUserModel()
        Logger.d("DeviceHistoryData syncUserHistory userID:${user.userID} memberID:${user.memberID}")
        startFetch(showMemberId = true)
        torreDeviceManager()?.syncUserHistory(user, historyDataInterface)
    }

    private fun syncTouristHistory() {
        Logger.d("DeviceHistoryData syncTouristHistory")
        startFetch(showMemberId = false)
        torreDeviceManager()?.syncTouristHistory(historyDataInterface)
    }

    // endregion

    // region Apple/Ice

    private fun getDeviceHistory() {
        Logger.d("DeviceHistoryData getDeviceHistory protocol:$protocol")
        startFetch(showMemberId = false)
        when (protocol) {
            Protocol.APPLE -> PPBlutoothPeripheralAppleInstance.instance.controller?.getHistoryData(historyDataInterface)
            Protocol.ICE -> PPBlutoothPeripheralIceInstance.instance.controller?.getHistory(historyDataInterface)
            else -> Unit
        }
    }

    private fun deleteDeviceHistory() {
        Logger.d("DeviceHistoryData deleteDeviceHistory protocol:$protocol")
        val callBack = object : PPBleSendResultCallBack {
            override fun onResult(sendState: PPScaleSendState?) {
                runOnUiThread {
                    if (isFinishing || isDestroyed) return@runOnUiThread
                    Logger.d("DeviceHistoryData deleteDeviceHistory result:$sendState")
                    val msg = if (sendState == PPScaleSendState.PP_SEND_SUCCESS) {
                        R.string.history_delete_success
                    } else {
                        R.string.history_delete_fail
                    }
                    Toast.makeText(this@DeviceHistoryDataActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
        when (protocol) {
            Protocol.APPLE -> PPBlutoothPeripheralAppleInstance.instance.controller?.deleteHistoryData(callBack)
            Protocol.ICE -> PPBlutoothPeripheralIceInstance.instance.controller?.deleteHistoryData(callBack)
            else -> Unit
        }
    }

    private fun checkSupportHistory(): Boolean {
        if (PPScaleHelper.isSupportHistoryData(deviceModel?.deviceFuncType)) return true
        Toast.makeText(this, R.string.device_not_supported, Toast.LENGTH_SHORT).show()
        return false
    }

    // endregion

    /**
     * 历史数据回调：每条数据回调一次 monitorHistoryData，全部返回后回调 monitorHistoryEnd
     */
    private val historyDataInterface = object : PPHistoryDataInterface() {

        override fun monitorHistoryData(bodyBaseModel: PPBodyBaseModel?, dateTime: String?) {
            bodyBaseModel ?: return
            Logger.d("DeviceHistoryData monitorHistoryData weight:${bodyBaseModel.getPpWeightKg()} dateTime:$dateTime")
            //用户历史数据的成员ID在 bodyBaseModel.userModel 中；游客数据和 Apple/Ice 数据不显示
            val memberId = if (showMemberId) {
                bodyBaseModel.memberId.ifEmpty { bodyBaseModel.userModel?.memberID.orEmpty() }
            } else {
                null
            }
            runOnUiThread {
                if (isFinishing || isDestroyed) return@runOnUiThread
                adapter.add(DeviceHistoryItem(bodyBaseModel, dateTime, memberId))
                refreshEmptyState()
            }
        }

        override fun monitorHistoryEnd(deviceModel: PPDeviceModel?) {
            Logger.d("DeviceHistoryData monitorHistoryEnd count:${adapter.itemCount}")
            runOnUiThread {
                if (isFinishing || isDestroyed) return@runOnUiThread
                Toast.makeText(
                    this@DeviceHistoryDataActivity,
                    getString(R.string.history_fetch_end, adapter.itemCount),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun monitorHistoryFail() {
            Logger.e("DeviceHistoryData monitorHistoryFail")
            runOnUiThread {
                if (isFinishing || isDestroyed) return@runOnUiThread
                Toast.makeText(this@DeviceHistoryDataActivity, R.string.history_fetch_fail, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startFetch(showMemberId: Boolean) {
        this.showMemberId = showMemberId
        adapter.clear()
        refreshEmptyState()
        Toast.makeText(this, R.string.history_fetching, Toast.LENGTH_SHORT).show()
    }

    private fun checkConnected(): Boolean {
        if (isDeviceConnected(deviceModel?.deviceMac)) return true
        Toast.makeText(this, R.string.history_device_not_connected, Toast.LENGTH_SHORT).show()
        return false
    }

    private fun selectButton(button: View) {
        leftBtn.isSelected = button === leftBtn
        rightBtn.isSelected = button === rightBtn
    }

    private fun refreshEmptyState() {
        emptyTips.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    /**
     * 点击历史数据：用该条数据计算身体数据并进入结果页
     */
    private fun openBodyData(item: DeviceHistoryItem) {
        val bodyBaseModel = item.bodyBaseModel
        val user = bodyBaseModel.userModel
        //游客数据的 userModel 没有身高/年龄，计算时使用当前用户信息
        if (user == null || user.age <= 0 || user.userHeight <= 0) {
            bodyBaseModel.userModel = DataUtil.getUserModel()
        }
        BodyDataAutoCalculateHelper.calculateAndOpen(this, bodyBaseModel, deviceModel)
    }
}
