package com.lefu.ppblutoothkit.device.auto

import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 自动指令执行器。
 *
 * 连接设备后，Demo 会按业务顺序自动执行一遍必要的 SDK 指令，并把每一步的结果输出到
 * 设备页面的日志面板（logTxt），这样开发者不需要自己猜调用顺序。
 *
 * 每一步都带超时保护：设备没有回复时不会卡住后面的步骤，而是记录“超时，继续下一步”。
 * 所有回调都会切回主线程执行，因此可以直接操作 TextView。
 */
class AutoCommandRunner(
    private val log: (String) -> Unit,
    private val defaultTimeoutMs: Long = 5000L
) {

    /**
     * 一步自动指令。
     *
     * @param name      显示在面板上的指令名称
     * @param timeoutMs 该步的超时时间，<=0 时使用 [defaultTimeoutMs]
     * @param action    实际下发指令的逻辑，执行完（成功/失败/拿不到回调）后必须调用一次 finish
     */
    class Step(
        val name: String,
        val timeoutMs: Long = 0L,
        val action: (finish: (Boolean, String?) -> Unit) -> Unit
    )

    private val handler = Handler(Looper.getMainLooper())

    private var steps: List<Step> = emptyList()
    private var index = 0
    private var running = false
    private var generation = 0
    private var currentTimeout: Runnable? = null
    private var onFinish: (() -> Unit)? = null

    fun start(steps: List<Step>, onFinish: (() -> Unit)? = null) {
        // 统一切到主线程，避免 BLE 回调线程和页面线程同时改状态
        handler.post {
            if (running) {
                log("[自动指令] 正在执行中，忽略重复触发")
                return@post
            }
            if (steps.isEmpty()) return@post
            running = true
            generation++
            this.steps = steps
            this.index = 0
            this.onFinish = onFinish
            log("========== 自动指令开始（共 ${steps.size} 步） ==========")
            runNext(generation)
        }
    }

    fun cancel(reason: String? = null) {
        handler.post {
            if (!running) return@post
            running = false
            generation++
            currentTimeout?.let { handler.removeCallbacks(it) }
            currentTimeout = null
            onFinish = null
            if (reason != null) log(reason)
        }
    }

    fun release() {
        // 页面销毁时必须立即停止，不能等主线程队列排空后再取消
        handler.removeCallbacksAndMessages(null)
        running = false
        generation++
        currentTimeout = null
        onFinish = null
        steps = emptyList()
    }

    private fun runNext(gen: Int) {
        if (!running || gen != generation) return
        if (index >= steps.size) {
            running = false
            log("========== 自动指令执行完成 ==========")
            val finishCallback = onFinish
            onFinish = null
            finishCallback?.invoke()
            return
        }

        val position = index + 1
        val step = steps[index]
        index++
        log("【自动指令 $position/${steps.size}】${step.name} ...")

        val settled = AtomicBoolean(false)
        val timeoutMs = if (step.timeoutMs > 0) step.timeoutMs else defaultTimeoutMs
        val timeoutRunnable = Runnable {
            completeStep(
                gen, position, settled, false,
                "【自动指令 $position/${steps.size}】${step.name} 超时（${timeoutMs}ms），继续下一步"
            )
        }
        currentTimeout = timeoutRunnable
        handler.postDelayed(timeoutRunnable, timeoutMs)

        try {
            step.action { success, message ->
                completeStep(gen, position, settled, success, message)
            }
        } catch (t: Throwable) {
            completeStep(
                gen, position, settled, false,
                "【自动指令 $position/${steps.size}】${step.name} 执行异常：${t.message}"
            )
        }
    }

    private fun completeStep(
        gen: Int,
        position: Int,
        settled: AtomicBoolean,
        success: Boolean,
        message: String?
    ) {
        if (!settled.compareAndSet(false, true)) return
        handler.post {
            if (gen != generation || !running) return@post
            currentTimeout?.let { handler.removeCallbacks(it) }
            currentTimeout = null
            val defaultMessage = "【自动指令 $position/${steps.size}】执行${if (success) "成功" else "失败"}"
            log(message ?: defaultMessage)
            runNext(gen)
        }
    }
}
