package com.lefu.ppblutoothkit

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

abstract class BasePermissionActivity : Activity() {


    /**
     *   Android 31 and below only need to apply for positioning permission
     */
    fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //The location permission is permanently denied by the user, and the user needs to go to the settings page to enable it

            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
        }
    }

    @RequiresApi(31)
    fun requestBleScalePermmision() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_SCAN)) {
                //这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限
                //TODO Here you should remind the user to go to the system settings page to enable permissions
            } else {
                //Here you should remind the user to go to the system settings page to enable permissions
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_ADVERTISE), 2
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

}