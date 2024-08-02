package com.lefu.ppblutoothkit

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

abstract class BasePermissionActivity : Activity() {


    var strings = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    var strings31BlePermission = arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE)

    /**
     *   Android 31 and below only need to apply for positioning permission
     */
    fun requestLocationPermission() {
        if (isHasBluetoothPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                //已经可以进行蓝牙操作
            } else {
                if (isLocationEnabled()) {
                    //已经可以进行蓝牙操作
                } else {
                    Toast.makeText(this@BasePermissionActivity, "请开启定位开关", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this, strings31BlePermission, 1)
            } else {
                ActivityCompat.requestPermissions(this, strings, 1)
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isHasBluetoothPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
             //已经可以进行蓝牙操作
            } else {
                if (isLocationEnabled()) {
                    //已经可以进行蓝牙操作
                } else {
                    Toast.makeText(this@BasePermissionActivity, "请开启定位开关", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this@BasePermissionActivity, "权限禁止后无法使用蓝牙，随后请到设备设置权限页开启", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * 判断是否已经赋予权限
     *
     * @return
     */
    fun isHasBluetoothPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            for (permission in strings31BlePermission) {
                if (!(ContextCompat.checkSelfPermission(this, permission) === PackageManager.PERMISSION_GRANTED)) {
                    return false
                }
            }
        } else {
            for (permission in strings) {
                if (!(ContextCompat.checkSelfPermission(this, permission) === PackageManager.PERMISSION_GRANTED)) {
                    return false
                }
            }
        }
        return true
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

}