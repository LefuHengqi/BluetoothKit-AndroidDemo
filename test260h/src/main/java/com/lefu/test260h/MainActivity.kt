package com.lefu.test260h

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lefu.test260h.setting.SettingActivity
import com.lefu.test260h.setting.SettingManager
import com.lefu.test260h.util.BleSearchBroadcastHelper
import com.lefu.test260h.util.ByteUtil
import com.lefu.test260h.vo.Devcie
import kotlin.experimental.and

class MainActivity : FragmentActivity() {
    var permissions31 = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE
    )
    var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    val deviceAdapter = DeviceAdapter()

    val countDownTimer = CountDownTimer(10000, 1000)

    val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    var deviceName: String = "260H"
    var rssi = 60

    private var startSearchBtn: Button? = null
    private var stopSearchBtn: Button? = null

    private var scanStateTv: TextView? = null
    private var settingTv: TextView? = null
    var startTag = 0//0是启动App，1点击启动扫描

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SettingManager.getInstance(this)
        initView()
        startTag = 0

        checkBlePermission()
        initBle()
    }

    private fun initBle() {

    }

    private fun initView() {
        startSearchBtn = findViewById(R.id.startSearchBtn)
        stopSearchBtn = findViewById(R.id.stopSearchBtn)
        scanStateTv = findViewById(R.id.scanStateTv)
        settingTv = findViewById(R.id.settingTv)

        findViewById<TextView>(R.id.titleTv).text = "蓝牙检测工具V${BuildConfig.VERSION_NAME}"

        startSearchBtn?.setOnClickListener {
            startTag = 1
            checkBlePermission()
        }
        stopSearchBtn?.setOnClickListener {
            stopSearchBle()
        }
        val deviceListRecyclerView = findViewById<RecyclerView>(R.id.deviceListRecyclerView)
        deviceListRecyclerView.adapter = deviceAdapter
        deviceListRecyclerView.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))

        countDownTimer.setListener(object : CountDownTimer.Listener {

            override fun onTick(remainingTime: Long) {
                Log.d("liyp_", " remainingTime: " + remainingTime)
                scanStateTv?.text = "正在扫描...${remainingTime / 1000}s"
            }

            override fun onFinish() {
                Log.d("liyp_", " 倒计时结束")
                stopSearchBle()
            }
        })

        settingTv?.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

    }

    @SuppressLint("MissingPermission", "NewApi")
    fun startSearchBle() {
        if (startTag == 1) {
            deviceAdapter.setNewData(null)

            deviceAdapter.count = SettingManager.get().count
            deviceName = SettingManager.get().deviceName ?: ""
            countDownTimer.setTime(SettingManager.get().time)
            rssi = SettingManager.get().rssi
            stopSearchBle()

            val scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
//            val scanFilter: ScanFilter = ScanFilter.Builder().setDeviceName(deviceName).build()
//            val scanFilters: MutableList<ScanFilter> = ArrayList()
//            scanFilters.add(scanFilter)
            scanStateTv?.text = "正在扫描..."
            mBluetoothAdapter.bluetoothLeScanner.startScan(null, scanSettings, scanCallBack)
        }
    }

    @SuppressLint("MissingPermission", "NewApi")
    fun stopSearchBle() {
        countDownTimer.stop()
        scanStateTv?.text = "已停止"
        mBluetoothAdapter.bluetoothLeScanner.stopScan(scanCallBack)
    }

    override fun onPause() {
        super.onPause()
        stopSearchBle()
    }

    var scanCallBack: ScanCallback = @SuppressLint("MissingPermission", "NewApi")
    object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val name = result.device.name
            if (name.isNullOrEmpty().not() && (deviceName.isEmpty() || result.device.name.contains(deviceName))) {
                val devcieItem = deviceAdapter.data.find { it.mac.equals(result.device.address) }
                if (deviceAdapter.data.isNullOrEmpty()) {
                    countDownTimer.start()
                }
                Log.d("liyp_", "name:" + name + " mac:" + result.device.address)
                if (devcieItem == null) {//不包含
                    if (result.rssi > -rssi) {
                        val devcie = Devcie()
                        devcie.mac = result.device.address
                        devcie.name = result.device.name
                        devcie.rssi = result.rssi
                        deviceAdapter.addData(devcie)
                        val data = deviceAdapter.data
                        val newData = data.sortedByDescending { it.rssi }
                        if (newData.size > 3) {
                            deviceAdapter.replaceData(newData.subList(0, 3))
                        } else {
                            deviceAdapter.replaceData(newData)
                        }
                    }
                } else {
                    val index = deviceAdapter.data.indexOf(devcieItem)
                    devcieItem.rssi = result.rssi
                    val lock = isLock(result.scanRecord?.bytes)
                    if (lock) {
                        devcieItem.countLock = devcieItem.countLock?.plus(1)
                    } else {
                        devcieItem.countProcess = devcieItem.countProcess?.plus(1)
                    }
                    deviceAdapter.setData(index, devcieItem)
                }
            }
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
        }

        override fun onScanFailed(errorCode: Int) {


        }
    }

    fun isLock(bytes: ByteArray?): Boolean {
        if (bytes?.isNotEmpty() ?: false) {
            val broadcastData = ByteUtil.byteToString(bytes)
            Log.d("liyp_", " broadcastData: " + broadcastData)
            val dataNormal = BleSearchBroadcastHelper.analysiBroadcastDataNormal(bytes)
            val data = ByteUtil.byteToString(dataNormal.beacondata)
            Log.d("liyp_", " data: " + data)

            val beacondata = dataNormal.beacondata
            if (beacondata.size >= 17) {
                val byte = beacondata[beacondata.size - 2]
                val byteValueA0: Byte = 0xA0.toByte()
                if (byte == byteValueA0) {
                    return true
                }
            }
        }
        return false
    }


}

