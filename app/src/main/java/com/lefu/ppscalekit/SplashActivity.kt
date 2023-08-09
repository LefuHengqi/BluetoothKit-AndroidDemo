package com.lefu.ppscalekit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.lefu.ppscale.ble.MainActivity
import com.lefu.ppscale.ble.R
import com.lefu.ppscale.ble.activity.ScanDeviceListActivity
import com.lefu.ppscale.ble.calculate.CalculateManagerActivity
import com.peng.ppscale.business.ble.PPScale

class SplashActivity : BasePermissionActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        findViewById<Button>(R.id.oldVersionSDK).setOnClickListener(this)
        findViewById<Button>(R.id.searchDevice).setOnClickListener(this)
        findViewById<Button>(R.id.caculateBodyFat).setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.oldVersionSDK -> {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
            R.id.searchDevice -> {
                if (PPScale.isBluetoothOpened()) {
                    startActivity(Intent(this@SplashActivity, ScanDeviceListActivity::class.java))
                } else {
                    PPScale.openBluetooth()
                }
            }
            R.id.caculateBodyFat -> {
                startActivity(Intent(this@SplashActivity, CalculateManagerActivity::class.java))
            }
        }
    }


}