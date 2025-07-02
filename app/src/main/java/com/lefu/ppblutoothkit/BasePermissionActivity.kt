package com.lefu.ppblutoothkit

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.lefu.ppbase.util.Logger
import com.lefu.ppblutoothkit.util.CommonUtils

abstract class BasePermissionActivity : Activity() {


    var permissions = mutableListOf<String>(Manifest.permission.ACCESS_FINE_LOCATION)

    @RequiresApi(Build.VERSION_CODES.S)
    var permissions31 = mutableListOf<String>(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_ADVERTISE
    )

    @RequiresApi(Build.VERSION_CODES.S)
    var permissions31HuaWei = mutableListOf<String>(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_ADVERTISE
    )

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (handleCheckBLUETOOTHSCANPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                //已经可以进行蓝牙操作
            } else {
                if (CommonUtils.isLocationEnabled(this)) {
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
     * 蓝牙相关权限检测
     */
    protected fun handleBLUETOOTHSCANPermission(appPermissionCallback: AppPermissionCallback) {
        var permissions = permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions = permissions31
        }
        if (CommonUtils.isHuaweiOS() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions = permissions31HuaWei
        }
        if (XXPermissions.isGranted(this, permissions)) {
            isLocationOrBle(appPermissionCallback, permissions)
        } else {
            appPermissionCallback.onGranted(permissions, false)
        }
    }

    private fun handleCheckBLUETOOTHSCANPermission(): Boolean {
        var permissions = permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions = permissions31
        }
        if (CommonUtils.isHuaweiOS() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions = permissions31HuaWei
        }
        return XXPermissions.isGranted(this, permissions)
    }

    fun isLocationOrBle(
        appPermissionCallback: AppPermissionCallback,
        permissions: MutableList<String>
    ) {
        //Android 12以上不需要定位服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && CommonUtils.isHuaweiOS().not()) {
            appPermissionCallback.onGranted(permissions, CommonUtils.isOpenBluetooth())
        } else {
            if (CommonUtils.isLocationEnabled(this)) {
                appPermissionCallback.onGranted(permissions, CommonUtils.isOpenBluetooth())
            } else {
                appPermissionCallback.onGranted(permissions, false)
            }
        }

    }

    protected fun handlingPermission() {
        var permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) permissions31 else permissions
        if (CommonUtils.isHuaweiOS() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions = permissions31HuaWei
        }
        XXPermissions.with(this)
            .permission(permissions)
            .request(object : OnPermissionCallback {
                override fun onGranted(
                    permissions: MutableList<String>,
                    allGranted: Boolean
                ) {
                    if (allGranted) {
                        requestLocationOrBle()
                    }
                }
            })
    }

    protected fun requestLocationOrBle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && CommonUtils.isHuaweiOS().not()) {
            if (CommonUtils.isOpenBluetooth()) {
            } else {
                openBluetooth()
            }
        } else {
            if (CommonUtils.isLocationEnabled(this)) {
                if (CommonUtils.isOpenBluetooth()) {
                } else {
                    openBluetooth()
                }
            } else {
                CommonUtils.openLocation(this)
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun openBluetooth() {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                try {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(enableBtIntent, 0x0001)
                } catch (e: Exception) {
                    CommonUtils.openOpenBluetooth()
                }
            } else {
                // 蓝牙已经打开
                Logger.d("蓝牙已经打开")
            }
        } else {
            // 设备不支持蓝牙
            Logger.d("设备不支持蓝牙")
        }
    }


    interface AppPermissionCallback {
        /**
         * 请求回调
         */
        fun onGranted(permissions: MutableList<String>, all: Boolean)
    }

}