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
import com.lefu.ppscale.ble.model.DataUtil
import com.lefu.ppscale.ble.userinfo.UserinfoActivity
import com.peng.ppscale.business.ble.PPScale
import com.peng.ppscale.data.PPBodyDetailModel
import com.peng.ppscale.vo.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : Activity(), View.OnClickListener {

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
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                //The location permission is permanently denied by the user, and the user needs to go to the settings page to enable it
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), 1
                )
            }
        }
    }

    @RequiresApi(31)
    fun requestBleScalePermmision() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                )
            ) { //这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限
                //TODO Here you should remind the user to go to the system settings page to enable permissions
            } else {
                //Here you should remind the user to go to the system settings page to enable permissions
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ), 2
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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
                PPBodyDetailModel.context = this

                val ppWeightKg = DataUtil.util().weightKg       //weight
                val impedance = DataUtil.util().impedance       //3609627

                val userModel1 = SettingManager.get()
                    .getDataObj(SettingManager.USER_MODEL, PPUserModel::class.java)

                //PPBodyBaseModel(weight=8195,
//                impedance=0,
//                ppZTwoLegs=0,
//                deviceModel=PPDeviceModel{deviceMac=CF:E7:04:10:00:13
//                , deviceName=CF577
//                , scaleType=null
//                , devicePower=64
//                , rssi=-60
//                , firmwareVersion=115.133.007.417
//                , hardwareVersion=V1.1
//                , serialNumber=CFE704100013
//                , modelNumber=null
//                , deviceType=PPDeviceTypeCF
//                , deviceProtocolType=PPDeviceProtocolTypeTorre
//                , deviceCalcuteType=PPDeviceCalcuteTypeAlternate8
//                , deviceAccuracyType=PPDeviceAccuracyTypePoint005
//                , devicePowerType=PPDevicePowerTypeCharge
//                , deviceConnectType=PPDeviceConnectTypeDirect
//                , deviceFuncType=223
//                , deviceUnitType=0
//                , deviceConnectAbled=true
//                , mtu=241
//                , illumination=-1
//                },
//                userModel=PPUserModel{userHeight=168, age=18, sex=PPUserGenderFemale, groupNum=0, isAthleteMode=false, isPregnantMode=false, userID='b49c484c-3f21-4f6b-86ac-23d22af802f3', memberID='b753771f-1cf3-4984-8a15-9c3262b3643f', userName='哈哈', deviceHeaderIndex=0, weightKg=0.0, targetWeight=61.3872, ideaWeight=58.8, userWeightArray=null, userWeightTimeArray=null},
//                isHeartRating=true,
//                isFatting=false,
//                isEnd=true, unit=Unit_KG,
//                heartRate=0,
//                isOverload=false,
//                isPlus=true,
//                dateStr='',
//                memberId='',
//                z100KhzLeftArmEnCode=1349413211,
//                z100KhzLeftLegEnCode=537517742,
//                z100KhzRightArmEnCode=1346231752,
//                z100KhzRightLegEnCode=1088034268,
//                z100KhzTrunkEnCode=1075909621,
//                z20KhzLeftArmEnCode=545129035,
//                z20KhzLeftLegEnCode=553605803,
//                z20KhzRightArmEnCode=853251,
//                z20KhzRightLegEnCode=837067472,
//                z20KhzTrunkEnCode=14200485)

                //impedance
                val userModel = PPUserModel.Builder()
                    .setSex(PPUserGender.PPUserGenderFemale) //gender
//                    .setHeight(userModel1.userHeight)//height 100-220
                    .setHeight(168)//height 100-220
                    .setAthleteMode(false)//height 100-220
                    .setAge(18)//age 10-99
                    .build()
                val deviceModel = PPDeviceModel(
                    "",
                    "CF577"
                )//Select the corresponding Bluetooth name according to your own device
                deviceModel.deviceCalcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8
                val bodyBaseModel = PPBodyBaseModel()
//                bodyBaseModel.impedance = impedance
//                bodyBaseModel.weight = UnitUtil.getWeight(ppWeightKg)
                bodyBaseModel.weight = 8195
                bodyBaseModel.deviceModel = deviceModel
                bodyBaseModel.userModel = userModel

                bodyBaseModel.z100KhzLeftArmEnCode = 1349413211;
                bodyBaseModel.z100KhzLeftLegEnCode = 537517742;
                bodyBaseModel.z100KhzRightArmEnCode = 1346231752;
                bodyBaseModel.z100KhzRightLegEnCode = 1088034268;
                bodyBaseModel.z100KhzTrunkEnCode = 1075909621;
                bodyBaseModel.z20KhzLeftArmEnCode = 545129035;
                bodyBaseModel.z20KhzLeftLegEnCode = 553605803;
                bodyBaseModel.z20KhzRightArmEnCode = 853251;
                bodyBaseModel.z20KhzRightLegEnCode = 837067472;
                bodyBaseModel.z20KhzTrunkEnCode = 14200485;

                val fatModel = PPBodyFatModel(bodyBaseModel)

                DataUtil.util().bodyDataModel = fatModel
                Log.d("liyp_", fatModel.toString())

                val intent = Intent(this@MainActivity, BodyDataDetailActivity::class.java)
                startActivity(intent)

//                val bodyIndex = BodyFatIndexUtils.getBodyIndex(
//                    fatModel, PPUnitType.Unit_KG, 2,
//                    "kg", "岁",
//                    "分"
//                )
//                Log.d("liyp_", bodyIndex.toString())
            }
        }
    }
}