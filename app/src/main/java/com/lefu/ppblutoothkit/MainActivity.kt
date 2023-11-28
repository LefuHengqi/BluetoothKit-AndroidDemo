package com.lefu.ppblutoothkit

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.lefu.ppblutoothkit.calculate.CalculateManagerActivity
import com.lefu.ppblutoothkit.devicelist.ScanDeviceListActivity
import com.peng.ppscale.PPBlutoothKit

class MainActivity : BasePermissionActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        findViewById<Button>(R.id.searchDevice).setOnClickListener(this)
        findViewById<Button>(R.id.caculateBodyFat).setOnClickListener(this)

        requestLocationPermission()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.searchDevice -> {
                if (PPBlutoothKit.isBluetoothOpened()) {
                    startActivity(Intent(this@MainActivity, ScanDeviceListActivity::class.java))
                } else {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }
                    val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(intent, 0x001)
//                    PPBlutoothKit.openBluetooth()
                }
            }
            R.id.caculateBodyFat -> {
                startActivity(Intent(this@MainActivity, CalculateManagerActivity::class.java))
            }
        }
    }


}