package com.lefu.test260h

import android.os.Handler
import android.os.Looper
import kotlin.concurrent.fixedRateTimer

class CountDownTimer(private var totalTime: Long, private val interval: Long) {
    private val handler = Handler(Looper.getMainLooper())
    private var remainingTime = totalTime
    private var timer: java.util.Timer? = null
    private var listener: Listener? = null

    fun setTime(totalTime:Long) {
        this.totalTime = totalTime
    }

    fun start() {
        stop()
        timer = fixedRateTimer("timer", false, 0L, interval) {
            if (remainingTime <= 0) {
                handler.post { listener?.onFinish() }
                stop()
            } else {
                handler.post { listener?.onTick(remainingTime) }
                remainingTime -= interval
            }
        }
    }

    fun stop() {
        timer?.cancel()
        timer = null
        remainingTime = totalTime
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    interface Listener {
        fun onTick(remainingTime: Long)
        fun onFinish()
    }
}