package com.lefu.ppblutoothkit.device.apple

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralAppleInstance
import com.lefu.ppblutoothkit.okhttp.NetUtil
import com.lefu.ppblutoothkit.device.apple.util.WifiUtil
import com.lefu.ppbase.util.Logger
import java.lang.ref.WeakReference
import java.util.*

class BleConfigWifiActivity : AppCompatActivity() {
    var RET_CODE_SYSTEM_WIFI_SETTINGS = 8161
    private var etWifiName: EditText? = null
    var etWifiKey: EditText? = null
    var mWifiUtil: WifiUtil? = null
    private var is2_4G = false //2.4G false    非2.4G true
    private var spHintTimer: Timer? = null
    private var spHintTimerTask: TimerTask? = null
    private var tvHint: TextView? = null
    private var tvOthers: TextView? = null
    var snTv: TextView? = null
    var address: String? = null
    var ssid: String? = null
    var tvNext: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_config)
        mHandler = PreviewHandler(this)
        address = intent.getStringExtra("address")
        initView()
    }

    private fun initView() {
        val tv_loginBack = findViewById<TextView>(R.id.tv_loginBack)
        if (tv_loginBack != null) {
            tv_loginBack.visibility = View.VISIBLE
            tv_loginBack.text = ""
            tv_loginBack.setOnClickListener { finish() }
        }
        tvHint = findViewById(R.id.tvHint)
        if (tvHint != null) {
            tvHint!!.visibility = View.INVISIBLE
        }
        tvNext = findViewById(R.id.tvNext)
        etWifiName = findViewById(R.id.etWifiName)
        etWifiKey = findViewById(R.id.etWifiKey)
        snTv = findViewById(R.id.snTv)
        if (etWifiName != null) {
            etWifiName!!.setText("")
            etWifiName!!.keyListener = null
        }
        findViewById<View>(R.id.tvNext).setOnClickListener {
            if (!is2_4G) {
                startNextStep()
            }
        }
        tvOthers = findViewById(R.id.tvOthers)
        if (tvOthers != null) {
            tvOthers!!.setOnClickListener { // open system wifi settings
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivityForResult(intent, RET_CODE_SYSTEM_WIFI_SETTINGS)
            }
        }
        tvNext?.setEnabled(true)
    }

    private fun startNextStep() {
        tvNext!!.isEnabled = false
        snTv!!.text = ""
        sendModifyServerDomain()
    }

    /**
     * 绑定时请确保WIFI是2.4G，并且账号密码正确
     */
    fun startConfigWifi() {
        ssid = etWifiName?.text.toString()
        val password = etWifiKey?.text.toString()
        ssid?.let {
            PPBlutoothPeripheralAppleInstance.instance.controller?.configWifiData(it, password, configWifiInfoInterface)
        }
    }

    /**
     * 配置秤端域名
     */
    private fun sendModifyServerDomain() {
        Handler().postDelayed({
            var scaleDomain = NetUtil.getScaleDomain()
            if (!TextUtils.isEmpty(scaleDomain)) {
                PPBlutoothPeripheralAppleInstance.instance.controller?.sendModifyServerDomain(scaleDomain, configWifiInfoInterface)
            } else {
                startConfigWifi()
            }
        }, 500)
    }

    override fun onPostResume() {
        super.onPostResume()
        Logger.d("liyp_  onPostResume")
        checkWifiTimer()
    }

    private fun checkWifiTimer() {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread { checkWifiIs2_4G() }
            }
        }, 800)
    }

    override fun onPause() {
        super.onPause()
        stopSplashHint()
    }

    private fun checkWifiIs2_4G(): Boolean {
        if (mWifiUtil == null) {
            mWifiUtil = WifiUtil.getInstance(this)
        }
        Logger.d("liyp_ isWifiConnect = " + mWifiUtil!!.isWifiConnect)
        if (!mWifiUtil!!.isWifiConnect) {
            Toast.makeText(this, getString(R.string.wifi_config_disconnected), Toast.LENGTH_SHORT).show()
            return false
        }
        val wifiName = mWifiUtil!!.currentSSID
        if (wifiName != null && etWifiName != null) {
            etWifiName!!.setText(wifiName)
        }
        is2_4G = !mWifiUtil!!.is2_4GFrequency
        showSplashHint()
        verifyPwdStatus(is2_4G)
        return is2_4G
    }

    private fun showSplashHint() {
        stopTimer()
        initTimer()
        spHintTimer!!.schedule(spHintTimerTask, 100, 1000)
    }

    private fun initTimer() {
        spHintTimer = Timer()
        spHintTimerTask = object : TimerTask() {
            override fun run() {
                if (mHandler != null) {
                    val msg = mHandler!!.obtainMessage()
                    msg.what = MSG_START_HINT
                    mHandler!!.sendMessage(msg)
                }
            }
        }
    }

    private fun verifyPwdStatus(b: Boolean) {
        if (etWifiKey != null) {
            if (b) {
                etWifiKey!!.inputType = EditorInfo.TYPE_NULL
                etWifiKey!!.isEnabled = false
                etWifiKey!!.isFocusable = false
                etWifiKey!!.isCursorVisible = false
                etWifiKey!!.isFocusableInTouchMode = false
            } else {
                etWifiKey!!.isFocusableInTouchMode = true
                etWifiKey!!.isCursorVisible = true
                etWifiKey!!.isFocusable = true
                etWifiKey!!.isEnabled = true
                etWifiKey!!.inputType = EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD
                //                setKeyStatus();
            }
        }
    }

    private fun stopTimer() {
        if (spHintTimer != null) {
            spHintTimer!!.cancel()
            spHintTimer = null
        }
        if (spHintTimerTask != null) {
            spHintTimerTask!!.cancel()
            spHintTimerTask = null
        }
    }

    private fun stopSplashHint() {
        if (spHintTimer != null) {
            spHintTimer!!.cancel()
            spHintTimer = null
        }
        tvHint!!.visibility = View.INVISIBLE
        etWifiKey!!.isEnabled = true
    }

    private class PreviewHandler internal constructor(activity: BleConfigWifiActivity) : Handler() {
        private val mActivity: WeakReference<BleConfigWifiActivity>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity = mActivity.get()
            if (activity != null) {
                when (msg.what) {
                    MSG_START_HINT -> if (activity.tvHint != null) {
                        if (!activity.is2_4G) {
                            activity.tvHint!!.visibility = View.INVISIBLE
                        } else {
//                                activity.checkWifiIs2_4G();
                            activity.is2_4G = !activity.mWifiUtil!!.is2_4GFrequency
                            if (activity.tvHint!!.visibility == View.INVISIBLE) {
                                activity.tvHint!!.visibility = View.VISIBLE
                            } else {
                                activity.tvHint!!.visibility = View.INVISIBLE
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onDestroy() {
        if (spHintTimer != null) {
            spHintTimer!!.cancel()
            spHintTimer = null
        }
        if (mHandler != null) {
            mHandler!!.removeMessages(MSG_START_HINT)
            mHandler!!.removeCallbacksAndMessages(null)
            mHandler = null
        }
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RET_CODE_SYSTEM_WIFI_SETTINGS) {
            if (resultCode == RESULT_CANCELED) {
                return
            } else {
            }
        }
    }

    companion object {
        private const val MSG_START_HINT = 1
        private var mHandler: PreviewHandler? = null
    }
}