package com.lefu.ppscale.ble

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.lefu.base.SettingManager
import com.lefu.ppscale.ble.activity.*
import com.lefu.ppscale.ble.calculate.Calculate8Activitiy
import com.lefu.ppscale.ble.foodscale.FoodSclaeDeviceActivity
import com.lefu.ppscale.ble.util.DataUtil
import com.lefu.ppscale.ble.userinfo.UserinfoActivity
import com.lefu.ppscale.ble.util.UnitUtil
import com.lefu.ppblutoothkit.BasePermissionActivity
import com.peng.ppscale.business.ble.PPScale
import com.peng.ppscale.business.device.DeviceManager
import com.peng.ppscale.data.PPBodyDetailModel
import com.peng.ppscale.vo.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class OldVersionManagerActivity : BasePermissionActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userModel = SettingManager.get().getDataObj(SettingManager.USER_MODEL, PPUserModel::class.java)

        if (userModel == null) {
            startActivity(Intent(this@OldVersionManagerActivity, UserinfoActivity::class.java))
        }
        onBtnClck()

        var uid: String? = SettingManager.get().getUid() ?: ""
        if (uid.isNullOrBlank()) {
            SettingManager.get().setUid(UUID.randomUUID().toString())
        }
    }

    private fun onBtnClck() {
        functionListBleBtn.setOnClickListener(this)
        bindingDeviceBtn.setOnClickListener(this)
        scaleWeightBtn.setOnClickListener(this)
        deviceManagerBtn.setOnClickListener(this)
        userInfoBtn.setOnClickListener(this)
        functionFoodScale.setOnClickListener(this)
        simulatedBodyFatCalculationBtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.userInfoBtn -> {
                startActivity(Intent(this@OldVersionManagerActivity, UserinfoActivity::class.java))
            }
            R.id.functionListBleBtn -> {
                if (PPScale.isBluetoothOpened()) {
//                    startActivity(Intent(this@MainActivity, FunctionListActivity::class.java))
                    startActivity(Intent(this@OldVersionManagerActivity, ScanDeviceListActivity::class.java))
                } else {
                    PPScale.openBluetooth()
                }
            }
            R.id.functionFoodScale -> {
                if (PPScale.isBluetoothOpened()) {
                    startActivity(Intent(this@OldVersionManagerActivity, FoodSclaeDeviceActivity::class.java))
                } else {
                    PPScale.openBluetooth()
                }
            }
        }
    }
}