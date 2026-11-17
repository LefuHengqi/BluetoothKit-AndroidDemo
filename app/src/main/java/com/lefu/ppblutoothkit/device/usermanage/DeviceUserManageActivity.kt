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
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralTorreInstance
import com.peng.ppscale.business.ble.PPScaleHelper
import com.peng.ppscale.business.ble.listener.PPUserInfoInterface
import com.peng.ppscale.business.torre.TorreDeviceManager
import com.peng.ppscale.business.torre.listener.PPClearDataInterface
import com.peng.ppscale.vo.PPSyncUserErrorType

/**
 * 设备用户管理（Torre）
 *
 * 把设备主页上分散的用户管理功能整合到一个页面：
 * - 进入页面自动获取用户列表 -> TorreDeviceManager.getUserList()，Torre 只返回 UserId 列表
 * - 列表项删除 -> TorreDeviceManager.deleteAllUserInfo()，按 userID 删除该账号下的所有成员
 * - 同步用户 -> TorreDeviceManager.syncUserInfo()，新增或更新一个成员（userID + memberID）
 * - 清空用户 -> TorreDeviceManager.clearDeviceUserInfo()，清空整台秤的所有用户
 *
 * 注意：
 * 1. 本页面依赖设备主页面已经建立的连接，不负责连接设备。
 * 2. SDK 中用户相关的回调接口、待删除用户都是单例上的单一字段，同时发起两个用户操作会互相覆盖，
 *    所以这里同一时间只允许一个操作；SDK 分包发送失败时没有回调，用超时兜底解除。
 */
class DeviceUserManageActivity : BaseImmersivePermissionActivity() {

    companion object {

        /** 单个操作的超时时间 */
        private const val OPERATION_TIMEOUT_MS = 10_000L

        /** 同步开始确认失败只回调 syncUserInfoFail，同步结果失败会先回调 syncUserInfoFail 再回调 syncUserInfoEnd，等一下以 syncUserInfoEnd 为准 */
        private const val SYNC_FAIL_WAIT_END_MS = 300L

        /**
         * @param deviceModel 当前已连接的设备
         */
        fun start(context: Context, deviceModel: PPDeviceModel?) {
            Companion.deviceModel = deviceModel
            val intent = Intent(context, DeviceUserManageActivity::class.java)
            context.startActivity(intent)
        }

        var deviceModel: PPDeviceModel? = null
    }

    private enum class Operation { NONE, GET_LIST, SYNC, DELETE, CLEAR }

    private var operation = Operation.NONE

    private val mainHandler = Handler(Looper.getMainLooper())
    private val timeoutRunnable = Runnable {
        Logger.e("DeviceUserManage operation timeout:$operation")
        finishOperation()
        toast(R.string.device_user_timeout)
    }

    private lateinit var syncBtn: TextView
    private lateinit var clearBtn: TextView
    private lateinit var emptyTips: TextView
    private lateinit var adapter: DeviceUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_user_manage)

        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()

        // 初始化Toolbar
        initToolbar()

        // 处理底部导航栏（三键/手势条）的WindowInsets，避免底部按钮被遮挡
        setupBottomInsets()


        findViewById<TextView>(R.id.userManageTips).text = buildTips()
        syncBtn = findViewById(R.id.syncUserBtn)
        clearBtn = findViewById(R.id.clearUserBtn)
        emptyTips = findViewById(R.id.userEmptyTips)

        val recyclerView = findViewById<RecyclerView>(R.id.userListRV)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DeviceUserAdapter { userId -> showDeleteDialog(userId) }
        recyclerView.adapter = adapter

        syncBtn.setOnClickListener { syncNextPresetUser() }
        clearBtn.setOnClickListener { showClearDialog() }

        refreshEmptyState()
        // 进入页面自动获取用户列表
        getUserList()
    }

    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setupUnifiedToolbar(
            toolbar = toolbar,
            title = getString(R.string.device_user_manage),
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
     * 顶部提示：根据设备高级配置显示最大用户数
     * torreUserManagerMethod = 0（默认）：限制 UserId 数量，每个 UserId 限制 memberId 数量
     * torreUserManagerMethod = 1：限制绑定的成员总数
     */
    private fun buildTips(): String {
        val advancedConfig = PPScaleHelper.getConfigDevice(deviceModel?.deviceSettingId)?.advancedConfig
        val maxMemberCount = DeviceAdvanceHelper.getMaxMemberCount(advancedConfig)
        return if (DeviceAdvanceHelper.getTorreUserManagerMethod(advancedConfig) == 1) {
            getString(R.string.device_user_manage_tips_member, maxMemberCount)
        } else {
            getString(R.string.device_user_manage_tips_user, DeviceAdvanceHelper.getTorreMaxUserCount(advancedConfig), maxMemberCount)
        }
    }

    private fun torreDeviceManager(): TorreDeviceManager? =
        PPBlutoothPeripheralTorreInstance.instance.controller?.getTorreDeviceManager()

    // region 用户操作

    private fun getUserList() {
        if (!startOperation(Operation.GET_LIST)) return
        Logger.d("DeviceUserManage getUserList")
        toast(R.string.device_user_fetching)
        torreDeviceManager()?.getUserList(userInfoInterface)
    }

    /**
     * 按 userID 删除该账号下的所有成员。
     * SDK 会把传入对象的 memberID 置空，所以这里传一个新对象
     */
    private fun deleteUser(userId: String) {
        if (!startOperation(Operation.DELETE)) return
        Logger.d("DeviceUserManage deleteAllUserInfo userID:$userId")
        val user = PPUserModel.Builder().build().apply {
            userID = userId
            memberID = ""
        }
        torreDeviceManager()?.deleteAllUserInfo(user, userInfoInterface)
    }

    private fun syncUser(user: PPUserModel) {
        if (!startOperation(Operation.SYNC)) return
        Logger.d("DeviceUserManage syncUserInfo userID:${user.userID} memberID:${user.memberID} userName:${user.userName} birthday:${user.birthday}")
        torreDeviceManager()?.syncUserInfo(user, userInfoInterface)
    }

    private fun clearUsers() {
        if (!startOperation(Operation.CLEAR)) return
        Logger.d("DeviceUserManage clearDeviceUserInfo")
        torreDeviceManager()?.clearDeviceUserInfo(clearDataInterface)
    }

    // endregion

    // region 弹窗

    private fun showDeleteDialog(userId: String) {
        if (!checkCanOperate()) return
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.device_user_delete_confirm, userId))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.confirm) { _, _ -> deleteUser(userId) }
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
        val preset = PresetTestUsers.nextNotOnDeviceByUserId(adapter.userIds)
        if (preset == null) {
            toast(R.string.device_user_sync_all_exist)
            return
        }
        syncUser(PresetTestUsers.buildUserModel(preset))
    }

    // endregion

    // region 回调

    private val userInfoInterface = object : PPUserInfoInterface {

        override fun getUserListSuccess(memberIDs: List<String?>?) {
            //Torre 返回的是 64 字节补 0 的 UserId 字符串，去掉补位和空项
            val userIds = memberIDs.orEmpty()
                .mapNotNull { it?.trim { c -> c.isWhitespace() || c == '\u0000' } }
                .filter { it.isNotEmpty() }
            Logger.d("DeviceUserManage getUserListSuccess $userIds")
            runOnUi {
                if (operation != Operation.GET_LIST) return@runOnUi
                finishOperation()
                adapter.setData(userIds)
                refreshEmptyState()
                toast(getString(R.string.device_user_fetch_end, userIds.size))
            }
        }

        /**
         * 同步开始确认失败时只回调这里；同步结果失败时会紧接着回调 [syncUserInfoEnd]，以后者为准
         */
        @Deprecated("已废弃，请使用 syncUserInfoEnd(type)")
        override fun syncUserInfoFail() {
            Logger.e("DeviceUserManage syncUserInfoFail")
            mainHandler.postDelayed({
                if (isFinishing || isDestroyed || operation != Operation.SYNC) return@postDelayed
                finishOperation()
                toast(R.string.device_user_sync_fail)
            }, SYNC_FAIL_WAIT_END_MS)
        }

        override fun syncUserInfoEnd(type: PPSyncUserErrorType) {
            Logger.d("DeviceUserManage syncUserInfoEnd type:$type")
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
            Logger.d("DeviceUserManage deleteUserInfoSuccess userID:${userModel?.userID}")
            runOnUi {
                if (operation != Operation.DELETE) return@runOnUi
                finishOperation()
                toast(R.string.device_user_delete_success)
                getUserList()
            }
        }

        override fun deleteUserInfoFail(userModel: PPUserModel?) {
            Logger.e("DeviceUserManage deleteUserInfoFail userID:${userModel?.userID}")
            runOnUi {
                if (operation != Operation.DELETE) return@runOnUi
                finishOperation()
                toast(R.string.device_user_delete_fail)
            }
        }
    }

    private val clearDataInterface = object : PPClearDataInterface {
        override fun onClearSuccess() {
            Logger.d("DeviceUserManage onClearSuccess")
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
            Logger.e("DeviceUserManage onClearFail")
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
