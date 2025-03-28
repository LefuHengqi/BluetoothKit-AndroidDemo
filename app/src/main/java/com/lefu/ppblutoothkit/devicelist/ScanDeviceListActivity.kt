package com.lefu.ppblutoothkit.devicelist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lefu.ppblutoothkit.BuildConfig
import com.lefu.ppblutoothkit.R
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
import kotlinx.android.synthetic.main.activity_device_list.startFilterName
import kotlinx.android.synthetic.main.activity_device_list.startRefresh

class ScanDeviceListActivity : Activity() {
    var ppScale: PPSearchManager? = null
    var isOnResume = false //页面可见时再重新发起扫描
    private var adapter: DeviceListAdapter? = null
    private var tv_starts: TextView? = null

    companion object {
        var inputName = ""
        var filterSign = -80
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)
        initView()
        tv_starts = findViewById(R.id.tv_starts)
        tv_starts?.setOnClickListener(View.OnClickListener { reStartScan() })
        adapter = DeviceListAdapter()
        val deviceListRecyclerView = findViewById<RecyclerView>(R.id.deviceListRecyclerView)
        deviceListRecyclerView.layoutManager = LinearLayoutManager(this)
        deviceListRecyclerView.adapter = adapter

        adapter?.setOnItemClickListener { adapter, view, position ->
            if (position >= 0 && adapter.data.size > position) {
                onStartDeviceSetPager(position)
            }
        }
    }

    private fun initView() {
        val toolbar: TextView? = findViewById(R.id.titleTv)
        toolbar?.text = "${getString(R.string.app_name)}V${BuildConfig.VERSION_NAME}"

        startRefresh?.setOnClickListener(View.OnClickListener { reStartScan() })

        startFilterName?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, FilterNameActivity::class.java)
            startActivityForResult(intent, 0x01)
        })
    }

    private fun reStartScan() {
        ppScale?.stopSearch()
        tv_starts?.text = getString(R.string.start_scan)
        adapter?.setNewData(null)
        startScanDeviceList()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        reStartScan()
        adapter?.setNewData(null)
    }

    private fun onStartDeviceSetPager(position: Int) {
        val deviceVo = adapter?.getItem(position)
        val deviceModel = deviceVo?.deviceModel
        deviceModel?.let {
            if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralTorre) {
                val intent =
                    Intent(this@ScanDeviceListActivity, PeripheralTorreActivity::class.java)
                PeripheralTorreActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralDorre) {
                val intent =
                    Intent(this@ScanDeviceListActivity, PeripheralDorreActivity::class.java)
                PeripheralDorreActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralBorre) {
                val intent =
                    Intent(this@ScanDeviceListActivity, PeripheralBorreActivity::class.java)
                PeripheralBorreActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralForre) {
                val intent =
                    Intent(this@ScanDeviceListActivity, PeripheralForreActivity::class.java)
                PeripheralForreActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralApple) {
                val intent =
                    Intent(this@ScanDeviceListActivity, PeripheralAppleActivity::class.java)
                PeripheralAppleActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralBanana) {
                val intent =
                    Intent(this@ScanDeviceListActivity, PeripheralBananaActivity::class.java)
                PeripheralBananaActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralCoconut) {
                val intent =
                    Intent(this@ScanDeviceListActivity, PeripheralCoconutActivity::class.java)
                PeripheralCoconutActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralDurian) {
                val intent =
                    Intent(this@ScanDeviceListActivity, PeripheralDurianActivity::class.java)
                PeripheralDurianActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralEgg) {
                val intent = Intent(this@ScanDeviceListActivity, PeripheralEggActivity::class.java)
                PeripheralEggActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralFish) {
                val intent = Intent(this@ScanDeviceListActivity, PeripheralFishActivity::class.java)
                PeripheralFishActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralGrapes) {
                val intent =
                    Intent(this@ScanDeviceListActivity, PeripheralGrapesActivity::class.java)
                PeripheralGrapesActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralHamburger) {
                val intent =
                    Intent(this@ScanDeviceListActivity, PeripheralHamburgerActivity::class.java)
                PeripheralHamburgerActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralIce) {
                val intent = Intent(this@ScanDeviceListActivity, PeripheralIceActivity::class.java)
                PeripheralIceActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralJambul) {
                val intent =
                    Intent(this@ScanDeviceListActivity, PeripheralJambulActivity::class.java)
                PeripheralJambulActivity.deviceModel = deviceModel
                startActivity(intent)
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
         *
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
            if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralApple) {
                PPBlutoothPeripheralAppleInstance.instance.controller?.deviceModel = deviceModel
//                PPBlutoothPeripheralAppleInstance.instance.controller?.registDataChangeListener(dataChangeListener)
//                PPBlutoothPeripheralAppleInstance.instance.controller?.onSearchResponse(hex)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralBanana) {
                val intent =
                    Intent(this@ScanDeviceListActivity, PeripheralBananaActivity::class.java)
                PeripheralBananaActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralCoconut) {
                val intent =
                    Intent(this@ScanDeviceListActivity, PeripheralCoconutActivity::class.java)
                PeripheralCoconutActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralDurian) {
                val intent =
                    Intent(this@ScanDeviceListActivity, PeripheralDurianActivity::class.java)
                PeripheralDurianActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralEgg) {
                val intent = Intent(this@ScanDeviceListActivity, PeripheralEggActivity::class.java)
                PeripheralEggActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralFish) {
                val intent = Intent(this@ScanDeviceListActivity, PeripheralFishActivity::class.java)
                PeripheralFishActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralGrapes) {
                val intent =
                    Intent(this@ScanDeviceListActivity, PeripheralGrapesActivity::class.java)
                PeripheralGrapesActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralHamburger) {
                val intent =
                    Intent(this@ScanDeviceListActivity, PeripheralHamburgerActivity::class.java)
                PeripheralHamburgerActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralIce) {
                val intent = Intent(this@ScanDeviceListActivity, PeripheralIceActivity::class.java)
                PeripheralIceActivity.deviceModel = deviceModel
                startActivity(intent)
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralJambul) {
                val intent =
                    Intent(this@ScanDeviceListActivity, PeripheralJambulActivity::class.java)
                PeripheralJambulActivity.deviceModel = deviceModel
                startActivity(intent)
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
            if (ppBleWorkState == PPBleWorkState.PPBleStateSearchCanceled) {
                Logger.d(getString(R.string.stop_scanning))
                tv_starts?.text =
                    getString(R.string.bluetooth_status) + getString(R.string.stop_scanning)
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
                Logger.d(getString(R.string.scan_timeout))
                tv_starts?.text =
                    getString(R.string.bluetooth_status) + getString(R.string.scan_timeout)
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                Logger.d(getString(R.string.scanning))
                tv_starts?.text =
                    getString(R.string.bluetooth_status) + getString(R.string.scanning)
            } else {
            }
        }

        /**
         * 系统蓝牙状态回调/System Bluetooth status callback
         * @param ppBleSwitchState 系统蓝牙状态标识/System Bluetooth status indicator
         */
        override fun monitorBluetoothSwitchState(ppBleSwitchState: PPBleSwitchState) {
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                Logger.e(getString(R.string.system_bluetooth_disconnect))
                Toast.makeText(
                    this@ScanDeviceListActivity,
                    getString(R.string.system_bluetooth_disconnect),
                    Toast.LENGTH_SHORT
                ).show()
                tv_starts!!.text =
                    getString(R.string.bluetooth_status) + getString(R.string.system_bluetooth_disconnect)
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                delayScan()
                Logger.d(getString(R.string.system_blutooth_on))
                Toast.makeText(
                    this@ScanDeviceListActivity,
                    getString(R.string.system_blutooth_on),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}