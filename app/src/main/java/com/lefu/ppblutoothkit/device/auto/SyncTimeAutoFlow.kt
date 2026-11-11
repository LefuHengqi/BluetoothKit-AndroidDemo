package com.lefu.ppblutoothkit.device.auto

import android.os.Handler
import android.os.Looper
import com.lefu.bluetooth.library.Constants
import com.peng.ppscale.PPBluetoothKit
import com.peng.ppscale.business.ble.listener.PPBleSendResultCallBack
import com.peng.ppscale.vo.PPScaleSendState

/**
 * 除 Torre/Borre/Dorre 外，其他可连接设备的自动初始化流程：
 *
 * 连接成功 -> 发现服务 -> 同步时间
 *
 * 触发时机：SDK 的 PPBleWorkStateWritable 回调（V2/V3/V4 协议在服务发现、通道订阅完成后回调），
 * 或 Torre 系子协议（Forre/Korre/Lorre/Morre）的 monitorMtuChange 回调。
 */
class SyncTimeAutoFlow private constructor(
    private val log: (String) -> Unit,
    private val syncTimeAction: (finish: (Boolean, String?) -> Unit) -> Unit
) {

    private val runner = AutoCommandRunner(::postLog)
    private val mainHandler = Handler(Looper.getMainLooper())

    /** 本次连接是否已经执行过自动指令，避免重复回调把同步时间下发两次 */
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

    companion object {
        /**
         * 适用于回调类型为 [PPBleSendResultCallBack] 的同步时间接口
         * （Apple/Ice/Coconut/Egg/Fish/Durian/Forre 等）
         */
        fun ofSendResult(
            log: (String) -> Unit,
            syncTime: (PPBleSendResultCallBack) -> Unit
        ): SyncTimeAutoFlow {
            return SyncTimeAutoFlow(log) { finish ->
                syncTime(object : PPBleSendResultCallBack {
                    override fun onResult(sendState: PPScaleSendState?) {
                        val success = sendState == PPScaleSendState.PP_SEND_SUCCESS
                        finish(
                            success,
                            "[自动指令] 同步时间${if (success) "成功" else "失败（$sendState）"}"
                        )
                    }
                })
            }
        }

        /**
         * 适用于回调类型为 (Boolean) -> Unit 的同步时间接口（Korre/Lorre/Morre）
         */
        fun ofBoolean(
            log: (String) -> Unit,
            syncTime: ((Boolean) -> Unit) -> Unit
        ): SyncTimeAutoFlow {
            return SyncTimeAutoFlow(log) { finish ->
                syncTime { success ->
                    finish(
                        success,
                        "[自动指令] 同步时间${if (success) "成功" else "失败"}"
                    )
                }
            }
        }
    }

    fun onConnecting() {
        if (destroyed) return
        executed = false
        runner.cancel()
    }

    fun onConnected() {
        if (destroyed) return
        postLog("[自动流程] 连接成功，正在发现服务 ...")
    }

    fun onDisconnected() {
        if (destroyed) return
        executed = false
        runner.cancel()
    }

    fun onDestroy() {
        destroyed = true
        executed = false
        runner.release()
    }

    /**
     * 服务发现/通道就绪，可以开始下发指令了
     */
    fun onReady() {
        if (destroyed) return
        if (executed) {
            postLog("[自动流程] 已执行过自动指令，忽略重复的通道就绪回调")
            return
        }
        executed = true
        postLog("[自动流程] 发现服务完成")
        runSyncTime()
    }

    /**
     * 页面打开时设备已经处于连接状态（例如从后台返回），不会再有 Writable/MTU 回调。
     */
    fun onAlreadyConnected() {
        if (destroyed || executed) return
        executed = true
        postLog("[自动流程] 设备已连接，复用现有连接（跳过服务发现）")
        runSyncTime()
    }

    private fun runSyncTime() {
        runner.start(
            listOf(
                AutoCommandRunner.Step("同步时间", 5000) { finish -> syncTimeAction(finish) }
            )
        ) {
            postLog("[自动流程] 初始化完成，现在可以直接上秤测量了")
        }
    }
}

/**
 * 判断设备当前是否已经真正连接（不包含“连接中”状态）。
 */
fun isDeviceConnected(mac: String?): Boolean {
    if (mac.isNullOrEmpty()) return false
    return PPBluetoothKit.bluetoothClient?.getConnectStatus(mac) == Constants.STATUS_DEVICE_CONNECTED
}
