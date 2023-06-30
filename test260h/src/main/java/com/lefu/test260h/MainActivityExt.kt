package com.lefu.test260h

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Build
import android.widget.Toast

fun MainActivity.checkPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Permissions.request(this, permissions31, object : Permissions.Apply<Boolean> {
            override fun apply(aBoolean: Boolean) {
                if (aBoolean) {
                    havePermissionCallBack(Manifest.permission.ACCESS_FINE_LOCATION)
                } else {
                    noHavePermissionCallBack(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        })
    } else {
        Permissions.request(this, permissions, object : Permissions.Apply<Boolean> {
            override fun apply(aBoolean: Boolean) {
                if (aBoolean) {
                    havePermissionCallBack(Manifest.permission.ACCESS_FINE_LOCATION)
                } else {
                    noHavePermissionCallBack(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        })
    }
}

fun MainActivity.havePermissionCallBack(permission: String?) {
    if (isOpenBle(this)) {
        if (!LocationUtil.isLocationEnabledS(this)) {
            Toast.makeText(this, "请打开定位开关", Toast.LENGTH_LONG).show()
            LocationUtil.openLocation(this)
        } else {
            startSearchBle()
        }
    }
}

fun MainActivity.noHavePermissionCallBack(permission: String?) {
    if (permission == Manifest.permission.ACCESS_FINE_LOCATION) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                || Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            ) {
                Toast.makeText(this, "请打开定位权限", Toast.LENGTH_LONG).show()
                LocationUtil.gotoPermissionSetting(this)
            } else {

            }
        } else {
            startSearchBle()
        }
    }
}

fun isOpenBle(context: Context): Boolean {
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (bluetoothAdapter == null) {
        // 设备不支持蓝牙
    } else if (!bluetoothAdapter.isEnabled()) {
        // 蓝牙未打开
        Toast.makeText(context, "请打开蓝牙开关", Toast.LENGTH_LONG).show()
        return false
    } else {
        // 蓝牙已打开
        return true
    }
    return false
}