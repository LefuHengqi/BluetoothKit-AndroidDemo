package com.lefu.test260h

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lefu.test260h.vo.Devcie

class MainActivity : FragmentActivity() {
    var permissions31 = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE
    )
    var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    val deviceAdapter = DeviceAdapter()

    val countDownTimer = CountDownTimer(10000, 1000)

    val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    var deviceName = "260H"

    private var startSearchBtn: Button? = null
    private var inputTimeEt: EditText? = null
    private var inputNumberEt: EditText? = null
    private var inputBleNameEt: EditText? = null
    private var scanStateTv: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
        initBle()
        initView()
    }

    private fun initBle() {

    }

    private fun initView() {

        startSearchBtn = findViewById(R.id.startSearchBtn)
        inputTimeEt = findViewById<EditText>(R.id.inputTimeEt)
        inputNumberEt = findViewById<EditText>(R.id.inputNumberEt)
        inputBleNameEt = findViewById<EditText>(R.id.inputBleNameEt)
        scanStateTv = findViewById<TextView>(R.id.scanStateTv)

        startSearchBtn?.setOnClickListener {
            checkPermission()
        }
        val deviceListRecyclerView = findViewById<RecyclerView>(R.id.deviceListRecyclerView)
        deviceListRecyclerView.adapter = deviceAdapter
        deviceListRecyclerView.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))

        countDownTimer.setListener(object : CountDownTimer.Listener {

            override fun onTick(remainingTime: Long) {
                Log.d("liyp_", " remainingTime: " + remainingTime)
            }

            override fun onFinish() {
                Log.d("liyp_", " 倒计时结束")
                stopSearchBle()
            }
        })
    }

    @SuppressLint("MissingPermission", "NewApi")
    fun startSearchBle() {
        deviceAdapter.setNewData(null)

        initParms()

//        mBluetoothAdapter.bluetoothLeScanner.startScan(scanCallBack)
        val scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        val scanFilter: ScanFilter = ScanFilter.Builder().setDeviceName(deviceName).build()
        val scanFilters: MutableList<ScanFilter> = ArrayList()
        scanFilters.add(scanFilter)
        scanStateTv?.text = "正在扫描..."
        mBluetoothAdapter.bluetoothLeScanner.startScan(scanFilters, scanSettings, scanCallBack)
    }

    private fun initParms() {
        var time: String? = inputTimeEt?.text?.toString()
        var count: String? = inputNumberEt?.text?.toString()
        deviceName = inputBleNameEt?.text?.toString() ?: "260H"

        time?.let {
            if (it.isEmpty()) {
                countDownTimer.setTime(10000L)
            } else {
                countDownTimer.setTime(it.toLong() * 1000)
            }
        }
        deviceAdapter.count = count?.toInt() ?: 10
    }

    @SuppressLint("MissingPermission", "NewApi")
    fun stopSearchBle() {
        countDownTimer.stop()
        scanStateTv?.text = "已停止"
        mBluetoothAdapter.bluetoothLeScanner.stopScan(scanCallBack)
    }

    var scanCallBack: ScanCallback = @SuppressLint("MissingPermission", "NewApi")
    object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
//            super.onScanResult(callbackType, result);
//            notifyDeviceFounded(com.inuker.bluetooth.library.search.SearchResult(result.device, result.rssi, result.scanRecord!!.bytes))
            val name = result.device.name
            if (name.isNullOrEmpty().not() && result.device.name.equals(deviceName)) {
                val devcieItem = deviceAdapter.data.find { it.mac.equals(result.device.address) }
                if (deviceAdapter.data.isNullOrEmpty()) {
                    countDownTimer.start()
                }
                Log.d("liyp_", "name:" + name + " mac:" + result.device.address)
                if (devcieItem == null) {//不包含
                    val devcie = Devcie()
                    devcie.mac = result.device.address
                    devcie.name = result.device.name
                    devcie.rssi = result.rssi
                    deviceAdapter.addData(devcie)
                } else {
                    val index = deviceAdapter.data.indexOf(devcieItem)
                    devcieItem.rssi = result.rssi
                    devcieItem.count = devcieItem.count?.plus(1)
                    deviceAdapter.setData(index, devcieItem)
                }

                val data = deviceAdapter.data

                val newData = data.sortedByDescending { it.rssi }

                deviceAdapter.replaceData(newData)
            }
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
        }

        override fun onScanFailed(errorCode: Int) {


        }
    }


}

