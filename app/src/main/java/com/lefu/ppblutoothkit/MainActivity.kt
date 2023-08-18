package com.lefu.ppblutoothkit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.lefu.ppscale.ble.OldVersionManagerActivity
import com.lefu.ppscale.ble.R
import com.lefu.ppscale.ble.activity.ScanDeviceListActivity
import com.lefu.ppscale.ble.calculate.CalculateManagerActivity
import com.peng.ppscale.business.ble.PPScale

class MainActivity : BasePermissionActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        findViewById<Button>(R.id.oldVersionSDK).setOnClickListener(this)
        findViewById<Button>(R.id.searchDevice).setOnClickListener(this)
        findViewById<Button>(R.id.caculateBodyFat).setOnClickListener(this)

        requestLocationPermission()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.oldVersionSDK -> {
                startActivity(Intent(this@MainActivity, OldVersionManagerActivity::class.java))
            }
            R.id.searchDevice -> {
                if (PPScale.isBluetoothOpened()) {
                    startActivity(Intent(this@MainActivity, ScanDeviceListActivity::class.java))
                } else {
                    PPScale.openBluetooth()
                }
            }
            R.id.caculateBodyFat -> {
                startActivity(Intent(this@MainActivity, CalculateManagerActivity::class.java))
            }
        }
    }


}