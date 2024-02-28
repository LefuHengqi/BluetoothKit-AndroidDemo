package com.lefu.ppblutoothkit

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.lefu.ppblutoothkit.calculate.CalculateManagerActivity
import com.lefu.ppblutoothkit.devicelist.ScanDeviceListActivity
import com.lefu.ppblutoothkit.okhttp.DataTask
import com.lefu.ppblutoothkit.okhttp.NetUtil
import com.lefu.ppblutoothkit.okhttp.RetCallBack
import com.lefu.ppblutoothkit.vo.DemoDeviceConfigVo
import com.peng.ppscale.PPBlutoothKit
import okhttp3.Call

class MainActivity : BasePermissionActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initToolbar()
        initDeviceConfig()
        findViewById<Button>(R.id.searchDevice).setOnClickListener(this)
        findViewById<Button>(R.id.caculateBodyFat).setOnClickListener(this)

        requestLocationPermission()

    }

    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "${getString(R.string.app_name)}V${BuildConfig.VERSION_NAME}"
        toolbar.setTitleTextColor(Color.WHITE)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.searchDevice -> {
                if (PPBlutoothKit.isBluetoothOpened()) {
                    startActivity(Intent(this@MainActivity, ScanDeviceListActivity::class.java))
                } else {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
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


    /**
     * 拉取设备配置信息，仅供Demo使用，与AppKey配套使用,
     * 在你自己的App中，请使用：PPBlutoothKit.initSdk(this, appKey, Companion.appSecret, "lefu.config")
     *
     */
    private fun initDeviceConfig() {
        val map: MutableMap<String, String> = HashMap()
        DataTask.get(NetUtil.GET_SCALE_CONFIG + PPApplication.appKey, map, object : RetCallBack<DemoDeviceConfigVo>(DemoDeviceConfigVo::class.java) {
            override fun onError(call: Call, e: Exception, id: Int) {}
            override fun onResponse(configVo: DemoDeviceConfigVo?, p1: Int) {

                configVo?.let {
                    if (configVo.code == 200 && configVo.msg.isNullOrEmpty().not()) {
                        configVo.msg?.let { it1 -> PPBlutoothKit.setNetConfig(this@MainActivity, PPApplication.appKey, PPApplication.appSecret, it1) }
                    }
                }

            }
        })

    }


}