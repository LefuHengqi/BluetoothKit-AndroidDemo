package com.lefu.ppscale.ble

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.peng.ppscale.business.device.PPUnitType
import com.peng.ppscale.vo.PPUserGender
import android.os.Bundle
import com.lefu.ppscale.ble.R
import com.peng.ppscale.business.ble.PPScale
import android.content.Intent
import com.lefu.ppscale.ble.function.FunctionListActivity
import com.lefu.ppscale.ble.wififunction.WifiFunctionListActivity
import com.lefu.ppscale.ble.activity.BindingDeviceActivity
import com.lefu.ppscale.ble.activity.DeviceListActivity
import android.widget.EditText
import android.text.TextWatcher
import android.text.Editable
import com.peng.ppscale.util.ByteUtil
import com.peng.ppscale.util.UnitUtil
import com.peng.ppscale.vo.PPBodyFatModel
import com.peng.ppscale.vo.PPUserModel
import android.content.pm.PackageManager
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lefu.ppscale.ble.model.DataUtil
import com.lefu.ppscale.ble.userinfo.UserinfoActivity
import com.peng.ppscale.util.PPUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPower()

        onBtnClck()
    }

    private fun onBtnClck() {
        functionListBleBtn.setOnClickListener(this)
        functionListWifiBtn.setOnClickListener(this)
        bindingDeviceBtn.setOnClickListener(this)
        scaleWeightBtn.setOnClickListener(this)
        deviceManagerBtn.setOnClickListener(this)
        userInfoBtn.setOnClickListener(this)
    }

    fun requestPower() {
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) { //这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限
            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            }
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
            R.id.functionListWifiBtn -> {
                if (PPScale.isBluetoothOpened()) {
                    startActivity(Intent(this@MainActivity, WifiFunctionListActivity::class.java))
                } else {
                    PPScale.openBluetooth()
                }
            }
            R.id.functionListBleBtn -> {
                if (PPScale.isBluetoothOpened()) {
                    startActivity(Intent(this@MainActivity, FunctionListActivity::class.java))
                } else {
                    PPScale.openBluetooth()
                }
            }
        }

    }
}