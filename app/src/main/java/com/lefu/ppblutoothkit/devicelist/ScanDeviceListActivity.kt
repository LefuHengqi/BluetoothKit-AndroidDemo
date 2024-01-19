package com.lefu.ppblutoothkit.devicelist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.device.PeripheralAppleActivity
import com.lefu.ppblutoothkit.device.PeripheralBananaActivity
import com.lefu.ppblutoothkit.device.PeripheralCoconutActivity
import com.lefu.ppblutoothkit.device.PeripheralDutianActivity
import com.lefu.ppblutoothkit.device.PeripheralEggActivity
import com.lefu.ppblutoothkit.device.PeripheralFishActivity
import com.lefu.ppblutoothkit.device.PeripheralGrapesActivity
import com.lefu.ppblutoothkit.device.PeripheralHamburgerActivity
import com.lefu.ppblutoothkit.device.PeripheralIceActivity
import com.lefu.ppblutoothkit.device.PeripheralJambulActivity
import com.lefu.ppblutoothkit.device.PeripheralTorreActivity
import com.peng.ppscale.business.ble.listener.PPBleStateInterface
import com.peng.ppscale.business.ble.listener.PPSearchDeviceInfoInterface
import com.peng.ppscale.business.state.PPBleSwitchState
import com.peng.ppscale.business.state.PPBleWorkState
import com.peng.ppscale.search.PPSearchManager
import com.peng.ppscale.util.Logger
import com.peng.ppscale.vo.PPDeviceModel
import com.peng.ppscale.vo.PPScaleDefine
import kotlinx.android.synthetic.main.activity_splash.searchDevice

class ScanDeviceListActivity : Activity() {
    var ppScale: PPSearchManager? = null
    var isOnResume = false //页面可见时再重新发起扫描
    private var adapter: DeviceListAdapter? = null
    var deviceModels = ArrayList<PPDeviceModel>()
    private var tv_starts: TextView? = null
    var lastTimes: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)
        tv_starts = findViewById(R.id.tv_starts)
        tv_starts?.setOnClickListener(View.OnClickListener { reStartScan() })
        adapter = DeviceListAdapter(this, R.layout.activity_scan_list_item, deviceModels)
        val listView = findViewById<View>(R.id.list_View) as ListView
        listView.adapter = adapter

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                onStartDeviceSetPager(position);
//            }
//        });
        adapter!!.setOnClickInItemLisenter { position, view ->
            onStartDeviceSetPager(position)
        }
    }

    private fun reStartScan() {
        if (ppScale != null) {
            ppScale!!.stopSearch()
        }
        tv_starts!!.text = getString(R.string.start_scan)
        startScanDeviceList()
    }

    private fun onStartDeviceSetPager(position: Int) {
        val deviceModel = adapter!!.getItem(position) as PPDeviceModel?
        if (deviceModel != null) {
            if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralTorre) {
                val intent =
                    Intent(this@ScanDeviceListActivity, PeripheralTorreActivity::class.java)
                PeripheralTorreActivity.deviceModel = deviceModel
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
                    Intent(this@ScanDeviceListActivity, PeripheralDutianActivity::class.java)
                PeripheralDutianActivity.deviceModel = deviceModel
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
        if (ppScale != null) {
            ppScale!!.stopSearch()
        }
    }

    /**
     * Get around bluetooth scale devices
     */
    fun startScanDeviceList() {
        if (ppScale == null) {
            ppScale = PPSearchManager()
        }
        //You can dynamically set the scan time in ms
        ppScale!!.startSearchDeviceList(300000, searchDeviceInfoInterface, bleStateInterface)
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
            var deviceModel: PPDeviceModel? = null
            for (i in deviceModels.indices) {
                val model = deviceModels[i]
                if (model.deviceMac == ppDeviceModel.deviceMac) {
                    model.rssi = ppDeviceModel.rssi
                    deviceModel = model
                    deviceModels[i] = model
                }
            }
            if (deviceModel == null) {
                deviceModels.add(ppDeviceModel)
            }
            if (System.currentTimeMillis() - lastTimes > 500) {
                lastTimes = System.currentTimeMillis()
                adapter!!.notifyDataSetChanged()
            }
        }
    }
    var bleStateInterface: PPBleStateInterface = object : PPBleStateInterface() {
        /**
         * 蓝牙扫描和连接状态回调
         * @param ppBleWorkState 蓝牙状态标识
         * @param deviceModel 设备对象
         */
        override fun monitorBluetoothWorkState(
            ppBleWorkState: PPBleWorkState,
            deviceModel: PPDeviceModel
        ) {
            if (ppBleWorkState == PPBleWorkState.PPBleStateSearchCanceled) {
                Logger.d(getString(R.string.stop_scanning))
                tv_starts!!.text =
                    getString(R.string.bluetooth_status) + getString(R.string.stop_scanning)
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
                Logger.d(getString(R.string.scan_timeout))
                tv_starts!!.text =
                    getString(R.string.bluetooth_status) + getString(R.string.scan_timeout)
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                Logger.d(getString(R.string.scanning))
                tv_starts!!.text =
                    getString(R.string.bluetooth_status) + getString(R.string.scanning)
            } else {
            }
        }

        /**
         * 系统蓝牙状态回调
         * @param ppBleSwitchState 系统蓝牙状态标识
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