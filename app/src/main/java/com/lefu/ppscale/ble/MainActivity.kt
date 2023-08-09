package com.lefu.ppscale.ble

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lefu.base.SettingManager
import com.lefu.ppscale.ble.activity.*
import com.lefu.ppscale.ble.calculate.Calculate8Activitiy
import com.lefu.ppscale.ble.foodscale.FoodSclaeDeviceActivity
import com.lefu.ppscale.ble.util.DataUtil
import com.lefu.ppscale.ble.userinfo.UserinfoActivity
import com.lefu.ppscale.ble.util.UnitUtil
import com.lefu.ppscalekit.BasePermissionActivity
import com.peng.ppscale.business.ble.PPScale
import com.peng.ppscale.business.device.DeviceManager
import com.peng.ppscale.data.PPBodyDetailModel
import com.peng.ppscale.vo.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BasePermissionActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userModel = SettingManager.get().getDataObj(SettingManager.USER_MODEL, PPUserModel::class.java)

        if (userModel == null) {
            startActivity(Intent(this@MainActivity, UserinfoActivity::class.java))
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
                startActivity(Intent(this@MainActivity, UserinfoActivity::class.java))
            }
            R.id.scaleWeightBtn -> {
                if (PPScale.isBluetoothOpened()) {
                    val intent = Intent(this@MainActivity, BindingDeviceActivity::class.java)
                    intent.putExtra(BindingDeviceActivity.SEARCH_TYPE, 1)
                    startActivity(intent)
                } else {
                    PPScale.openBluetooth()
                }
            }
            R.id.deviceManagerBtn -> {
                startActivity(Intent(this@MainActivity, DeviceListActivity::class.java))
            }
            R.id.bindingDeviceBtn -> {
                if (PPScale.isBluetoothOpened()) {
                    val intent = Intent(this@MainActivity, BindingDeviceActivity::class.java)
                    intent.putExtra(BindingDeviceActivity.SEARCH_TYPE, 0)
                    startActivity(intent)
                } else {
                    PPScale.openBluetooth()
                }
            }
            R.id.functionListBleBtn -> {
                if (PPScale.isBluetoothOpened()) {
//                    startActivity(Intent(this@MainActivity, FunctionListActivity::class.java))
                    startActivity(Intent(this@MainActivity, ScanDeviceListActivity::class.java))
                } else {
                    PPScale.openBluetooth()
                }
            }
            R.id.functionFoodScale -> {
                if (PPScale.isBluetoothOpened()) {
                    startActivity(Intent(this@MainActivity, FoodSclaeDeviceActivity::class.java))
                } else {
                    PPScale.openBluetooth()
                }
            }
            R.id.simulatedBodyFatCalculationBtn -> {
                PPBodyDetailModel.context = this

                val ppWeightKg = DataUtil.util().weightKg       //weight
                val impedance = DataUtil.util().impedance       //3609627

                val userModel = SettingManager.get()
                    .getDataObj(SettingManager.USER_MODEL, PPUserModel::class.java)

                val deviceModel = PPDeviceModel("", DeviceManager.CF568)    //Select the corresponding Bluetooth name according to your own device
                deviceModel.deviceCalcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate
                val bodyBaseModel = PPBodyBaseModel()
                bodyBaseModel.impedance = impedance
                bodyBaseModel.weight = UnitUtil.getWeight(ppWeightKg)
                bodyBaseModel.deviceModel = deviceModel
                bodyBaseModel.userModel = userModel

                val fatModel = PPBodyFatModel(bodyBaseModel)

                DataUtil.util().bodyDataModel = fatModel
                Log.d("liyp_", fatModel.toString())

                val intent = Intent(this@MainActivity, Calculate8Activitiy::class.java)
//                val intent = Intent(this@MainActivity, BodyDataDetailActivity::class.java)
                startActivity(intent)
            }
        }
    }
}