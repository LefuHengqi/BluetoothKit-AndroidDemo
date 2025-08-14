package com.lefu.ppblutoothkit.devicelist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity
import com.lefu.ppblutoothkit.BuildConfig
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.databinding.ActivityDeviceListBinding
import com.lefu.ppblutoothkit.device.PeripheralAppleActivity
import com.lefu.ppblutoothkit.device.PeripheralBananaActivity
import com.lefu.ppblutoothkit.device.PeripheralCoconutActivity
import com.lefu.ppblutoothkit.device.PeripheralDurianActivity
import com.lefu.ppblutoothkit.device.PeripheralEggActivity
import com.lefu.ppblutoothkit.device.PeripheralFishActivity
import com.lefu.ppblutoothkit.device.PeripheralGrapesActivity
import com.lefu.ppblutoothkit.device.PeripheralHamburgerActivity
import com.lefu.ppblutoothkit.device.PeripheralIceActivity
import com.lefu.ppblutoothkit.device.PeripheralJambulActivity
import com.lefu.ppblutoothkit.device.PeripheralTorreActivity
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralAppleInstance
import com.lefu.ppblutoothkit.filter.FilterNameActivity
import com.peng.ppscale.business.ble.listener.PPBleStateInterface
import com.peng.ppscale.business.ble.listener.PPSearchDeviceInfoInterface
import com.peng.ppscale.business.state.PPBleSwitchState
import com.peng.ppscale.business.state.PPBleWorkState
import com.peng.ppscale.search.PPSearchManager
import com.lefu.ppbase.util.Logger
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.PPScaleDefine
import com.lefu.ppblutoothkit.device.PeripheralBorreActivity
import com.lefu.ppblutoothkit.device.PeripheralDorreActivity
import com.lefu.ppblutoothkit.device.PeripheralForreActivity
import com.lefu.ppblutoothkit.device.PeripheralKorreActivity
import com.lefu.ppblutoothkit.device.PeripheralLorreActivity
import com.lefu.ppblutoothkit.device.PeripheralMorreActivity

class ScanDeviceListActivity : BaseImmersivePermissionActivity() {
    var ppScale: PPSearchManager? = null
    var isOnResume = false //页面可见时再重新发起扫描
    private var adapter: DeviceListAdapter? = null

    companion object {
        var inputName = ""
        var filterSign = -80
    }

    private lateinit var binding: ActivityDeviceListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()
        
        adapter = DeviceListAdapter()
        adapter?.setOnItemClickListener { adapter, view, position ->
            onStartDeviceSetPager(position)
        }
        
        initView()
        initBle()
    }
    
    private fun initView() {
        // 初始化Toolbar
        initToolbar()
        
        // 修复：startRefresh是ImageView，应该使用setOnClickListener而不是setOnRefreshListener
        binding.startRefresh.setOnClickListener {
            reStartScan() // 重新开始扫描
        }
        
        binding.startFilterName.setOnClickListener {
            val intent = Intent(this, FilterNameActivity::class.java)
            startActivityForResult(intent, 0x01)
        }
        
        binding.deviceListRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.deviceListRecyclerView.adapter = adapter
    }
    
    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setupUnifiedToolbar(
            toolbar = toolbar,
            title = "设备扫描",
            showBackButton = true
        )
    }
    
    private fun initBle() {
        // 初始化蓝牙相关设置
    }
    
    private fun reStartScan() {
        ppScale?.stopSearch()
        binding.tvStarts.text = getString(R.string.start_scan)
        adapter?.setNewData(null)
        startScanDeviceList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        adapter?.setNewData(null)
    }

    private fun onStartDeviceSetPager(position: Int) {
        val deviceVo = adapter?.getItem(position)
        val deviceModel = deviceVo?.deviceModel
        deviceModel?.let {
            when (deviceModel.getDevicePeripheralType()) {
                PPScaleDefine.PPDevicePeripheralType.PeripheralTorre -> {
                    val intent = Intent(this@ScanDeviceListActivity, PeripheralTorreActivity::class.java)
                    PeripheralTorreActivity.deviceModel = deviceModel
                    startActivity(intent)
                }
                PPScaleDefine.PPDevicePeripheralType.PeripheralDorre -> {
                    val intent = Intent(this@ScanDeviceListActivity, PeripheralDorreActivity::class.java)
                    PeripheralDorreActivity.deviceModel = deviceModel
                    startActivity(intent)
                }
                PPScaleDefine.PPDevicePeripheralType.PeripheralBorre -> {
                    val intent = Intent(this@ScanDeviceListActivity, PeripheralBorreActivity::class.java)
                    PeripheralBorreActivity.deviceModel = deviceModel
                    startActivity(intent)
                }
                PPScaleDefine.PPDevicePeripheralType.PeripheralForre -> {
                    val intent = Intent(this@ScanDeviceListActivity, PeripheralForreActivity::class.java)
                    PeripheralForreActivity.deviceModel = deviceModel
                    startActivity(intent)
                }
                PPScaleDefine.PPDevicePeripheralType.PeripheralApple -> {
                    val intent = Intent(this@ScanDeviceListActivity, PeripheralAppleActivity::class.java)
                    PeripheralAppleActivity.deviceModel = deviceModel
                    startActivity(intent)
                }
                PPScaleDefine.PPDevicePeripheralType.PeripheralBanana -> {
                    val intent = Intent(this@ScanDeviceListActivity, PeripheralBananaActivity::class.java)
                    PeripheralBananaActivity.deviceModel = deviceModel
                    startActivity(intent)
                }
                PPScaleDefine.PPDevicePeripheralType.PeripheralCoconut -> {
                    val intent = Intent(this@ScanDeviceListActivity, PeripheralCoconutActivity::class.java)
                    PeripheralCoconutActivity.deviceModel = deviceModel
                    startActivity(intent)
                }
                PPScaleDefine.PPDevicePeripheralType.PeripheralDurian -> {
                    val intent = Intent(this@ScanDeviceListActivity, PeripheralDurianActivity::class.java)
                    PeripheralDurianActivity.deviceModel = deviceModel
                    startActivity(intent)
                }
                PPScaleDefine.PPDevicePeripheralType.PeripheralEgg -> {
                    val intent = Intent(this@ScanDeviceListActivity, PeripheralEggActivity::class.java)
                    PeripheralEggActivity.deviceModel = deviceModel
                    startActivity(intent)
                }
                PPScaleDefine.PPDevicePeripheralType.PeripheralFish -> {
                    val intent = Intent(this@ScanDeviceListActivity, PeripheralFishActivity::class.java)
                    PeripheralFishActivity.deviceModel = deviceModel
                    startActivity(intent)
                }
                PPScaleDefine.PPDevicePeripheralType.PeripheralGrapes -> {
                    val intent = Intent(this@ScanDeviceListActivity, PeripheralGrapesActivity::class.java)
                    PeripheralGrapesActivity.deviceModel = deviceModel
                    startActivity(intent)
                }
                PPScaleDefine.PPDevicePeripheralType.PeripheralHamburger -> {
                    val intent = Intent(this@ScanDeviceListActivity, PeripheralHamburgerActivity::class.java)
                    PeripheralHamburgerActivity.deviceModel = deviceModel
                    startActivity(intent)
                }
                PPScaleDefine.PPDevicePeripheralType.PeripheralIce -> {
                    val intent = Intent(this@ScanDeviceListActivity, PeripheralIceActivity::class.java)
                    PeripheralIceActivity.deviceModel = deviceModel
                    startActivity(intent)
                }
                PPScaleDefine.PPDevicePeripheralType.PeripheralJambul -> {
                    val intent = Intent(this@ScanDeviceListActivity, PeripheralJambulActivity::class.java)
                    PeripheralJambulActivity.deviceModel = deviceModel
                    startActivity(intent)
                }
                PPScaleDefine.PPDevicePeripheralType.PeripheralLorre -> {
                    val intent = Intent(this@ScanDeviceListActivity, PeripheralLorreActivity::class.java)
                    PeripheralLorreActivity.deviceModel = deviceModel
                    startActivity(intent)
                }
                PPScaleDefine.PPDevicePeripheralType.PeripheralKorre -> {
                    val intent = Intent(this@ScanDeviceListActivity, PeripheralKorreActivity::class.java)
                    PeripheralKorreActivity.deviceModel = deviceModel
                    startActivity(intent)
                }
                PPScaleDefine.PPDevicePeripheralType.PeripheralMorre -> {
                    val intent = Intent(this@ScanDeviceListActivity, PeripheralMorreActivity::class.java)
                    PeripheralMorreActivity.deviceModel = deviceModel
                    startActivity(intent)
                }
                else -> {
                    // 处理其他类型
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        isOnResume = false
        ppScale?.stopSearch()
    }

    /**
     * Get around bluetooth scale devices
     */
    fun startScanDeviceList() {
        ppScale = PPSearchManager.getInstance()
        //You can dynamically set the scan time in ms
        ppScale?.startSearchDeviceList(300000, searchDeviceInfoInterface, bleStateInterface)
    }

    /**
     * 重新扫描
     */
    fun delayScan() {
        Handler(mainLooper).postDelayed({
            if (isOnResume) {
                startScanDeviceList()
            }
        }, 1000)
    }

    override fun onResume() {
        super.onResume()
        isOnResume = true
        delayScan()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (ppScale != null) {
            ppScale!!.stopSearch()
        }
    }

    var searchDeviceInfoInterface = PPSearchDeviceInfoInterface { ppDeviceModel, data ->
        /**
         * @param ppDeviceModel 设备对象
         * @param data  广播数据
         */
        if (ppDeviceModel != null) {
            adapter?.let {
                if (inputName.isEmpty() || ppDeviceModel.deviceName.contains(inputName, true)) {
                    if (ppDeviceModel.rssi < filterSign) {
                        return@PPSearchDeviceInfoInterface
                    }
                    if (it.data.isEmpty().not()) {
                        for (i in it.data.indices) {
                            val deviceVo = it.data.get(i)
                            if (deviceVo != null) {
                                deviceVo.deviceModel?.let { device ->
                                    if (device.deviceMac == ppDeviceModel.deviceMac) {
                                        device.rssi = ppDeviceModel.rssi
                                        adapter?.notifyItemChanged(i)
                                        return@PPSearchDeviceInfoInterface
                                    }
                                }
                            }
                        }
                    }
                    val deviceVo = DeviceVo()
                    deviceVo.deviceModel = ppDeviceModel
                    it.addData(deviceVo)
                }
            }
        }
    }

    private fun analyticalData(deviceModel: PPDeviceModel?, data: String?) {
        deviceModel?.let {
            when (deviceModel.getDevicePeripheralType()) {
                PPScaleDefine.PPDevicePeripheralType.PeripheralApple -> {
                    PPBlutoothPeripheralAppleInstance.instance.controller?.deviceModel = deviceModel
                }
                else -> {
                    // 处理其他设备类型
                }
            }
        }
    }

    var bleStateInterface: PPBleStateInterface = object : PPBleStateInterface() {
        /**
         * 蓝牙扫描和连接状态回调
         * Bluetooth scanning and connection status callback
         * @param ppBleWorkState 蓝牙状态标识/Bluetooth status indicator
         * @param deviceModel 设备对象/Device Object
         */
        override fun monitorBluetoothWorkState(
            ppBleWorkState: PPBleWorkState,
            deviceModel: PPDeviceModel?
        ) {
            when (ppBleWorkState) {
                PPBleWorkState.PPBleStateSearchCanceled -> {
                    Logger.d(getString(R.string.stop_scanning))
                    binding.tvStarts.text = getString(R.string.bluetooth_status) + getString(R.string.stop_scanning)
                }
                PPBleWorkState.PPBleWorkSearchTimeOut -> {
                    Logger.d(getString(R.string.scan_timeout))
                    binding.tvStarts.text = getString(R.string.bluetooth_status) + getString(R.string.scan_timeout)
                }
                PPBleWorkState.PPBleWorkStateSearching -> {
                    Logger.d(getString(R.string.scanning))
                    binding.tvStarts.text = getString(R.string.bluetooth_status) + getString(R.string.scanning)
                }
                else -> {
                    // 处理其他状态
                }
            }
        }

        /**
         * 系统蓝牙状态回调/System Bluetooth status callback
         * @param ppBleSwitchState 系统蓝牙状态标识/System Bluetooth status indicator
         */
        override fun monitorBluetoothSwitchState(ppBleSwitchState: PPBleSwitchState) {
            when (ppBleSwitchState) {
                PPBleSwitchState.PPBleSwitchStateOff -> {
                    Logger.e(getString(R.string.system_bluetooth_disconnect))
                    Toast.makeText(
                        this@ScanDeviceListActivity,
                        getString(R.string.system_bluetooth_disconnect),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.tvStarts.text = getString(R.string.bluetooth_status) + getString(R.string.system_bluetooth_disconnect)
                }
                PPBleSwitchState.PPBleSwitchStateOn -> {
                    delayScan()
                    Logger.d(getString(R.string.system_blutooth_on))
                    Toast.makeText(
                        this@ScanDeviceListActivity,
                        getString(R.string.system_blutooth_on),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    // 处理其他状态
                }
            }
        }
    }
}