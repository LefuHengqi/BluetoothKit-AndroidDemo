package com.lefu.ppblutoothkit.device.usermanage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.util.Logger
import com.lefu.ppbase.vo.DeviceAdvanceHelper
import com.lefu.ppbase.vo.PPUserModel
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.device.auto.isDeviceConnected
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralBorreInstance
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralDorreInstance
import com.peng.ppscale.business.ble.PPScaleHelper
import com.peng.ppscale.business.ble.listener.PPUserInfoInterface
import com.peng.ppscale.business.torre.TorreDeviceManager
import com.peng.ppscale.business.torre.listener.PPClearDataInterface
import com.peng.ppscale.vo.PPSyncUserErrorType

/**
 * 成员管理（Borre/Dorre）
 *
 * 把设备主页上分散的用户管理功能整合到一个页面：
 * - 进入页面自动获取成员列表 -> TorreDeviceManager.getUserList()，Borre/Dorre 回调 getUserListByUserModelSuccess 返回完整成员
 * - 列表项删除 -> TorreDeviceManager.deleteUserInfo()，按 userID + memberID 删除单个成员
 * - 同步用户 -> TorreDeviceManager.syncUserInfo()，新增或更新一个成员（userID + memberID）
 * - 清空用户 -> TorreDeviceManager.clearDeviceUserInfo()，清空整台秤的所有用户
 *
 * 注意：
 * 1. 本页面依赖设备主页面已经建立的连接，不负责连接设备。
 * 2. 部分设备不支持获取成员列表指令，此时只能等超时，列表为空并显示提示。
 * 3. SDK 中用户相关的回调接口、待删除用户都是单例上的单一字段，同时发起两个用户操作会互相覆盖，
 *    所以这里同一时间只允许一个操作；SDK 分包发送失败时没有回调，用超时兜底解除。
 */
class DeviceMemberManageActivity : BaseImmersivePermissionActivity() {

    enum class Protocol {
        BORRE,
        DORRE
    }

    companion object {
        private const val EXTRA_PROTOCOL = "extra_protocol"

        /** 单个操作的超时时间 */
        private const val OPERATION_TIMEOUT_MS = 10_000L

        /** 同步开始确认失败只回调 syncUserInfoFail，同步结果失败会先回调 syncUserInfoFail 再回调 syncUserInfoEnd，等一下以 syncUserInfoEnd 为准 */
        private const val SYNC_FAIL_WAIT_END_MS = 300L

        /**
         * @param deviceModel 当前已连接的设备
         */
        fun start(context: Context, protocol: Protocol, deviceModel: PPDeviceModel?) {
            Companion.deviceModel = deviceModel
            val intent = Intent(context, DeviceMemberManageActivity::class.java)
            intent.putExtra(EXTRA_PROTOCOL, protocol.name)
            context.startActivity(intent)
        }

        var deviceModel: PPDeviceModel? = null
    }

    private enum class Operation { NONE, GET_LIST, SYNC, DELETE, CLEAR }

    private var operation = Operation.NONE

    private lateinit var protocol: Protocol

    private val mainHandler = Handler(Looper.getMainLooper())
    private val timeoutRunnable = Runnable {
        Logger.e("DeviceMemberManage operation timeout:$operation")
        finishOperation()
        toast(R.string.device_user_timeout)
    }

    private lateinit var syncBtn: TextView
    private lateinit var clearBtn: TextView
    private lateinit var emptyTips: TextView
    private lateinit var adapter: DeviceMemberAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_member_manage)

        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()

        // 初始化Toolbar
        initToolbar()

        // 处理底部导航栏（三键/手势条）的WindowInsets，避免底部按钮被遮挡
        setupBottomInsets()

        protocol = runCatching { Protocol.valueOf(intent.getStringExtra(EXTRA_PROTOCOL) ?: "") }.getOrNull() ?: run {
            finish()
            return
        }

        findViewById<TextView>(R.id.memberManageTips).text = buildTips()
        syncBtn = findViewById(R.id.syncUserBtn)
        clearBtn = findViewById(R.id.clearUserBtn)
        emptyTips = findViewById(R.id.memberEmptyTips)

        val recyclerView = findViewById<RecyclerView>(R.id.memberListRV)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DeviceMemberAdapter { item -> showDeleteDialog(item) }
        recyclerView.adapter = adapter

        syncBtn.setOnClickListener { syncNextPresetUser() }
        clearBtn.setOnClickListener { showClearDialog() }

        refreshEmptyState()
        // 进入页面自动获取成员列表
        getUserList()
    }

    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setupUnifiedToolbar(
            toolbar = toolbar,
            title = getString(R.string.device_member_manage),
            showBackButton = true
        )
    }

    private fun setupBottomInsets() {
        val rootLayout = findViewById<View>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // 只设置底部内边距，顶部由Toolbar处理
            view.setPadding(view.paddingLeft, 0, view.paddingRight, systemBars.bottom)
            insets
        }
    }

    /**
     * 顶部提示：Borre/Dorre 限制绑定的成员总数（maxMemberCount，默认 10）
     */
    private fun buildTips(): String {
        val advancedConfig = PPScaleHelper.getConfigDevice(deviceModel?.deviceSettingId)?.advancedConfig
        return getString(R.string.device_member_manage_tips, DeviceAdvanceHelper.getMaxMemberCount(advancedConfig))
    }

    /**
     * Borre/Dorre 的 DeviceManager 都继承自 TorreDeviceManager，用户管理接口一致
     */
    private fun torreDeviceManager(): TorreDeviceManager? = when (protocol) {
        Protocol.BORRE -> PPBlutoothPeripheralBorreInstance.instance.controller?.getTorreDeviceManager()
        Protocol.DORRE -> PPBlutoothPeripheralDorreInstance.instance.controller?.getTorreDeviceManager()
    }

    // region 用户操作

    private fun getUserList() {
        if (!startOperation(Operation.GET_LIST)) return
        Logger.d("DeviceMemberManage getUserList protocol:$protocol")
        toast(R.string.device_user_fetching)
        torreDeviceManager()?.getUserList(userInfoInterface)
    }

    /**
     * 按 userID + memberID 删除单个成员。
     * memberID 为空时 SDK 会改成删除该 userID 下的所有成员，所以列表里跳过了 memberID 为空的项
     */
    private fun deleteMember(item: DeviceMemberItem) {
        if (!startOperation(Operation.DELETE)) return
        Logger.d("DeviceMemberManage deleteUserInfo userID:${item.userId} memberID:${item.memberId}")
        val user = PPUserModel.Builder().build().apply {
            userID = item.userId
            memberID = item.memberId
        }
        torreDeviceManager()?.deleteUserInfo(user, userInfoInterface)
    }

    private fun syncUser(user: PPUserModel) {
        if (!startOperation(Operation.SYNC)) return
        Logger.d("DeviceMemberManage syncUserInfo userID:${user.userID} memberID:${user.memberID} userName:${user.userName} birthday:${user.birthday}")
        torreDeviceManager()?.syncUserInfo(user, userInfoInterface)
    }

    private fun clearUsers() {
        if (!startOperation(Operation.CLEAR)) return
        Logger.d("DeviceMemberManage clearDeviceUserInfo")
        torreDeviceManager()?.clearDeviceUserInfo(clearDataInterface)
    }

    // endregion

    // region 弹窗

    private fun showDeleteDialog(item: DeviceMemberItem) {
        if (!checkCanOperate()) return
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.device_member_delete_confirm, item.userId, item.memberId))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.confirm) { _, _ -> deleteMember(item) }
            .show()
    }

    private fun showClearDialog() {
        if (!checkCanOperate()) return
        AlertDialog.Builder(this)
            .setMessage(R.string.device_user_clear_confirm)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.confirm) { _, _ -> clearUsers() }
            .show()
    }

    /**
     * 同步用户：不弹窗，按顺序同步第一个设备上还不存在的预设测试用户
     */
    private fun syncNextPresetUser() {
        if (!checkCanOperate()) return
        val preset = PresetTestUsers.nextNotOnDeviceByMember(adapter.members)
        if (preset == null) {
            toast(R.string.device_user_sync_all_exist)
            return
        }
        syncUser(PresetTestUsers.buildUserModel(preset))
    }

    // endregion

    // region 回调

    private val userInfoInterface = object : PPUserInfoInterface {

        override fun getUserListByUserModelSuccess(userList: MutableList<PPUserModel>?) {
            //uid/memberId 是按固定长度补 0 的字符串，去掉补位；memberId 为空的项无法单独删除，不显示
            //Name 显示预设测试用户的名字，匹配不到时为空（显示“--”）
            val members = userList.orEmpty().mapNotNull { user ->
                val memberId = user.memberID.trimPadding()
                if (memberId.isEmpty()) return@mapNotNull null
                val userId = user.userID.trimPadding()
                DeviceMemberItem(userId, memberId, PresetTestUsers.findName(userId, memberId).orEmpty())
            }
            Logger.d("DeviceMemberManage getUserListByUserModelSuccess size:${members.size} $members")
            runOnUi {
                if (operation != Operation.GET_LIST) return@runOnUi
                finishOperation()
                adapter.setData(members)
                refreshEmptyState()
                toast(getString(R.string.device_user_fetch_end, members.size))
            }
        }

        /**
         * 同步开始确认失败时只回调这里；同步结果失败时会紧接着回调 [syncUserInfoEnd]，以后者为准
         */
        @Deprecated("已废弃，请使用 syncUserInfoEnd(type)")
        override fun syncUserInfoFail() {
            Logger.e("DeviceMemberManage syncUserInfoFail")
            mainHandler.postDelayed({
                if (isFinishing || isDestroyed || operation != Operation.SYNC) return@postDelayed
                finishOperation()
                toast(R.string.device_user_sync_fail)
            }, SYNC_FAIL_WAIT_END_MS)
        }

        override fun syncUserInfoEnd(type: PPSyncUserErrorType) {
            Logger.d("DeviceMemberManage syncUserInfoEnd type:$type")
            runOnUi {
                if (operation != Operation.SYNC) return@runOnUi
                finishOperation()
                val msg = when (type) {
                    PPSyncUserErrorType.PPSyncUserErrorTypeNone -> R.string.device_user_sync_success
                    PPSyncUserErrorType.PPSyncUserErrorTypeSelecting -> R.string.device_user_sync_selecting
                    PPSyncUserErrorType.PPSyncUserErrorTypeNeedSyncName -> R.string.device_user_sync_need_name
                    PPSyncUserErrorType.PPSyncUserErrorTypeStorageFull -> R.string.device_user_sync_storage_full
                    PPSyncUserErrorType.PPSyncUserErrorTypeInvalidData -> R.string.device_user_sync_invalid_data
                    else -> R.string.device_user_sync_fail
                }
                toast(msg)
                //“需要同步昵称字模”也表示用户已写入设备
                if (type == PPSyncUserErrorType.PPSyncUserErrorTypeNone ||
                    type == PPSyncUserErrorType.PPSyncUserErrorTypeNeedSyncName
                ) {
                    getUserList()
                }
            }
        }

        override fun deleteUserInfoSuccess(userModel: PPUserModel?) {
            Logger.d("DeviceMemberManage deleteUserInfoSuccess userID:${userModel?.userID} memberID:${userModel?.memberID}")
            runOnUi {
                if (operation != Operation.DELETE) return@runOnUi
                finishOperation()
                toast(R.string.device_user_delete_success)
                getUserList()
            }
        }

        override fun deleteUserInfoFail(userModel: PPUserModel?) {
            Logger.e("DeviceMemberManage deleteUserInfoFail userID:${userModel?.userID} memberID:${userModel?.memberID}")
            runOnUi {
                if (operation != Operation.DELETE) return@runOnUi
                finishOperation()
                toast(R.string.device_user_delete_fail)
            }
        }
    }

    private val clearDataInterface = object : PPClearDataInterface {
        override fun onClearSuccess() {
            Logger.d("DeviceMemberManage onClearSuccess")
            runOnUi {
                if (operation != Operation.CLEAR) return@runOnUi
                finishOperation()
                toast(R.string.device_user_clear_success)
                adapter.clear()
                refreshEmptyState()
                getUserList()
            }
        }

        override fun onClearFail() {
            Logger.e("DeviceMemberManage onClearFail")
            runOnUi {
                if (operation != Operation.CLEAR) return@runOnUi
                finishOperation()
                toast(R.string.device_user_clear_fail)
            }
        }
    }

    // endregion

    // region 操作状态

    /**
     * 开始一个操作：检查连接、确保没有正在进行的操作，并启动超时
     */
    private fun startOperation(op: Operation): Boolean {
        if (!checkCanOperate()) return false
        operation = op
        updateButtonsEnabled()
        mainHandler.removeCallbacks(timeoutRunnable)
        mainHandler.postDelayed(timeoutRunnable, OPERATION_TIMEOUT_MS)
        return true
    }

    private fun finishOperation() {
        operation = Operation.NONE
        mainHandler.removeCallbacks(timeoutRunnable)
        updateButtonsEnabled()
    }

    private fun checkCanOperate(): Boolean {
        if (operation != Operation.NONE) {
            toast(R.string.device_user_busy)
            return false
        }
        if (!isDeviceConnected(deviceModel?.deviceMac) || torreDeviceManager() == null) {
            toast(R.string.history_device_not_connected)
            return false
        }
        return true
    }

    private fun updateButtonsEnabled() {
        val enabled = operation == Operation.NONE
        syncBtn.isEnabled = enabled
        clearBtn.isEnabled = enabled
        adapter.deleteEnabled = enabled
    }

    // endregion

    private fun refreshEmptyState() {
        emptyTips.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun String?.trimPadding(): String =
        this?.trim { it.isWhitespace() || it == '\u0000' }.orEmpty()

    private fun runOnUi(action: () -> Unit) {
        runOnUiThread {
            if (isFinishing || isDestroyed) return@runOnUiThread
            action()
        }
    }

    private fun toast(resId: Int) = toast(getString(resId))

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainHandler.removeCallbacksAndMessages(null)
    }
}
