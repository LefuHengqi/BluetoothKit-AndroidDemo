package com.lefu.ppscale.ble

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lefu.base.SettingManager
import com.lefu.ppscale.ble.activity.BindingDeviceActivity
import com.lefu.ppscale.ble.activity.DeviceListActivity
import com.lefu.ppscale.ble.activity.FoodSclaeDeviceActivity
import com.lefu.ppscale.ble.activity.ScanDeviceListActivity
import com.lefu.ppscale.ble.userinfo.UserinfoActivity
import com.peng.ppscale.business.ble.PPScale
import com.peng.ppscale.business.device.DeviceManager
import com.peng.ppscale.business.device.PPUnitType
import com.peng.ppscale.vo.PPBodyFatModel
import com.peng.ppscale.vo.PPDeviceModel
import com.peng.ppscale.vo.PPUserGender
import com.peng.ppscale.vo.PPUserModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userModel =
                SettingManager.get().getDataObj(SettingManager.USER_MODEL, PPUserModel::class.java)

        if (userModel == null) {
            startActivity(Intent(this@MainActivity, UserinfoActivity::class.java))
        }
        requestLocationPermission()

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

    /**
     *   Android 31 and below only need to apply for positioning permission
     */
    fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //The location permission is permanently denied by the user, and the user needs to go to the settings page to enable it
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }
    }

    @RequiresApi(31)
    fun requestBleScalePermmision() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_SCAN)) { //这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限
                //TODO Here you should remind the user to go to the system settings page to enable permissions
            } else {
                //Here you should remind the user to go to the system settings page to enable permissions
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT), 2)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            //Here you should remind the user to go to the system settings page to enable permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestBleScalePermmision()
            } else {
                //Android 31 and below only need to apply for positioning permission
            }
} else if (requestCode == 2) {

        }
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
                val ppWeightKg = 69.7       //weight
                val impedance = 6186090     //impedance
                val userModel = PPUserModel.Builder()
                        .setSex(PPUserGender.PPUserGenderFemale) //gender
                        .setHeight(165)//height 100-220
                        .setAge(30)//age 10-99
                        .build()
                val deviceModel = PPDeviceModel("", DeviceManager.HEARTRATE_SCALE)//Select the corresponding Bluetooth name according to your own device

                val ppBodyFatModel = PPBodyFatModel(ppWeightKg, impedance, userModel, deviceModel, PPUnitType.Unit_KG)

                Log.d("liyp_", ppBodyFatModel.toString())

            }
        }

    }
}