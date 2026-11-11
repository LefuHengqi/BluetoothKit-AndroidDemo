package com.lefu.ppblutoothkit.device.auto

import android.os.Handler
import android.os.Looper
import com.lefu.ppbase.vo.PPUserModel
import com.peng.ppscale.business.ble.PPScaleHelper
import com.peng.ppscale.business.ble.listener.PPBleSendResultCallBack
import com.peng.ppscale.business.ble.listener.PPDataChangeListener
import com.peng.ppscale.business.ble.listener.PPUserInfoInterface
import com.peng.ppscale.business.torre.TorreDeviceManager
import com.peng.ppscale.vo.PPScaleSendState
import com.peng.ppscale.vo.PPSyncUserErrorType

/**
 * Torre / Borre / Dorre 设备的自动初始化流程：
 *
 * 连接成功 -> 发现服务 -> 更新 MTU -> 同步时间 -> 同步成员 -> 选择成员 -> 开始测量（仅 Torre）-> 启动保活
 *
 * 触发时机：SDK 的 monitorMtuChange 回调。对 Torre 系协议来说，这个回调代表服务发现、
 * 通道订阅和 MTU 协商都已经完成，此时才可以开始下发业务指令。
 */
class TorreFamilyAutoFlow(
    private val log: (String) -> Unit,
    private val userModelProvider: () -> PPUserModel?,
    private val managerProvider: () -> TorreDeviceManager?,
    private val deviceSettingIdProvider: () -> Int?,
    private val startMeasure: Boolean,
    private val dataChangeListenerProvider: () -> PPDataChangeListener?
) {

    private val runner = AutoCommandRunner(::postLog)
    private val mainHandler = Handler(Looper.getMainLooper())

    /** 本次连接是否已经执行过自动指令，避免重复的 MTU 回调把流程跑两遍 */
    @Volatile
    private var executed = false

    /** 页面已销毁，忽略销毁后仍可能到达的 BLE 回调 */
    @Volatile
    private var destroyed = false

    private fun postLog(message: String) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            log(message)
        } else {
            mainHandler.post { log(message) }
        }
    }

    fun onConnecting() {
        if (destroyed) return
        executed = false
        runner.cancel()
    }

    fun onConnected() {
        if (destroyed) return
        postLog("[自动流程] 连接成功，正在发现服务并协商 MTU ...")
    }

    fun onDisconnected() {
        if (destroyed) return
        executed = false
        runner.cancel()
        managerProvider()?.stopKeepAlive()
    }

    fun onDestroy() {
        destroyed = true
        executed = false
        runner.release()
        managerProvider()?.stopKeepAlive()
    }

    fun onMtuChanged(mtu: Int?) {
        if (destroyed) return
        if (executed) {
            postLog("[自动流程] 已执行过自动指令，忽略重复的 MTU 回调")
            return
        }
        executed = true
        postLog("[自动流程] 发现服务完成")
        postLog("[自动流程] 更新 MTU：${mtu ?: "未知（使用默认值）"}")
        runSteps()
    }

    /**
     * 页面打开时设备已经处于连接状态（例如从后台返回）。
     * 此时不会再次触发服务发现/MTU 回调，直接执行剩下的业务指令。
     */
    fun onAlreadyConnected() {
        if (destroyed || executed) return
        executed = true
        postLog("[自动流程] 设备已连接，复用现有连接（跳过服务发现/MTU 协商）")
        runSteps()
    }

    private fun runSteps() {
        val manager = managerProvider()
        if (manager == null) {
            postLog("[自动流程] 设备管理器未就绪，跳过自动指令")
            return
        }

        val userModel = userModelProvider()
        // 部分设备同步成员时要求带上生日，和手动点击“同步用户信息”按钮时的处理保持一致
        if (userModel != null &&
            deviceSettingIdProvider()?.let { PPScaleHelper.isSupportBirthday(it) } == true
        ) {
            userModel.birthday = "2000-01-01"
        }

        // Torre 需要主动开始测量；Borre/Dorre 上秤后设备会自动测量，这里只注册数据监听
        dataChangeListenerProvider()?.let { manager.registDataChangeListener(it) }

        val steps = buildSteps(manager, userModel)
        postLog("[自动流程] 将按顺序执行：${steps.joinToString(" -> ") { it.name }}")
        runner.start(steps) {
            postLog("[自动流程] 设备已就绪，现在可以直接上秤测量了")
        }
    }

    private fun buildSteps(
        manager: TorreDeviceManager,
        userModel: PPUserModel?
    ): List<AutoCommandRunner.Step> {
        val steps = mutableListOf<AutoCommandRunner.Step>()

        // 1. 同步时间
        steps += AutoCommandRunner.Step("同步时间", 5000) { finish ->
            manager.syncTime(object : PPBleSendResultCallBack {
                override fun onResult(sendState: PPScaleSendState?) {
                    val success = sendState == PPScaleSendState.PP_SEND_SUCCESS
                    finish(
                        success,
                        "[自动指令] 同步时间${if (success) "成功" else "失败（$sendState）"}"
                    )
                }
            })
        }

        // 2. 同步成员（下发用户信息）
        steps += AutoCommandRunner.Step("同步成员", 8000) { finish ->
            if (userModel == null) {
                finish(false, "[自动指令] 未配置用户信息，跳过同步成员")
            } else {
                manager.syncUserInfo(userModel, object : PPUserInfoInterface {
                    override fun syncUserInfoEnd(type: PPSyncUserErrorType) {
                        val success = type == PPSyncUserErrorType.PPSyncUserErrorTypeNone
                        finish(
                            success,
                            "[自动指令] 同步成员${if (success) "成功" else "失败（${type.name}）"}"
                        )
                    }
                })
            }
        }

        // 3. 选择成员（下发当前称重用户）
        steps += AutoCommandRunner.Step("选择成员", 8000) { finish ->
            if (userModel == null) {
                finish(false, "[自动指令] 未配置用户信息，跳过选择成员")
            } else {
                manager.confirmCurrentUser(userModel, object : PPUserInfoInterface {
                    override fun confirmCurrentUserInfoSuccess() {
                        finish(true, "[自动指令] 选择成员成功")
                    }

                    override fun confirmCurrentUserInfoFail() {
                        finish(false, "[自动指令] 选择成员失败")
                    }
                })
            }
        }

        // 4. 开始测量（仅 Torre 需要）
        if (startMeasure) {
            steps += AutoCommandRunner.Step("开始测量", 3000) { finish ->
                manager.startMeasure(object : PPBleSendResultCallBack {
                    override fun onResult(sendState: PPScaleSendState?) {
                        val success = sendState == PPScaleSendState.PP_SEND_SUCCESS
                        finish(
                            success,
                            "[自动指令] 开始测量${if (success) "成功" else "失败（$sendState）"}"
                        )
                    }
                })
            }
        }

        // 5. 定时保活，SDK 内部每 10 秒下发一次保活指令
        steps += AutoCommandRunner.Step("启动保活", 2000) { finish ->
            manager.startKeepAlive()
            finish(true, "[自动指令] 已启动定时保活（每 10 秒下发一次保活指令）")
        }

        return steps
    }
}
