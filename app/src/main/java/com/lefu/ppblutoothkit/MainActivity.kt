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
import com.lefu.ppblutoothkit.log.LogActivity
import com.lefu.ppblutoothkit.okhttp.DataTask
import com.lefu.ppblutoothkit.okhttp.NetUtil
import com.lefu.ppblutoothkit.okhttp.RetCallBack
import com.lefu.ppblutoothkit.producttest.ProductTestManagerActivity
import com.lefu.ppblutoothkit.vo.DemoDeviceConfigVo
import com.peng.ppscale.PPBlutoothKit
import com.peng.ppscale.PPBlutoothKit.TAG
import com.peng.ppscale.util.Logger
import okhttp3.Call
import java.io.File

class MainActivity : BasePermissionActivity(), View.OnClickListener {

    val fileList: MutableList<File> = ArrayList() //存包序号，防止重复接收数据

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initToolbar()
        initDeviceConfig()
        findViewById<Button>(R.id.searchDevice).setOnClickListener(this)
        findViewById<Button>(R.id.caculateBodyFat).setOnClickListener(this)
        findViewById<Button>(R.id.productionTestTools).setOnClickListener(this)

        requestLocationPermission()

    }


    private fun initToolbar() {
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        toolbar?.title = "${getString(R.string.app_name)}V${BuildConfig.VERSION_NAME}"
        toolbar?.setTitleTextColor(Color.WHITE)
        toolbar?.inflateMenu(R.menu.main_toolbar_menu)
        toolbar?.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.main_menu_item_export_app_log -> {
                    //                    startUploadLog()
                    LogActivity.logType = 0
                    val intent = Intent(this, LogActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.main_menu_item_clear_app_log -> {
                    LogActivity.logType = 1//deviceLog
                    val intent = Intent(this, LogActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> super.onOptionsItemSelected(it)
            }
        }
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

                val a= 6425
                val b = 6130

                val c =  a/100f - b/100f

                Logger.d("$TAG kgtost2Point2d: $c")

                startActivity(Intent(this@MainActivity, CalculateManagerActivity::class.java))
            }

            R.id.productionTestTools -> {
                if (PPBlutoothKit.isBluetoothOpened()) {
                    startActivity(Intent(this@MainActivity, ProductTestManagerActivity::class.java))
                } else {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return
                    }
                    val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(intent, 0x002)
//                    PPBlutoothKit.openBluetooth()
                }

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

    // 递归处理文件的方法
    fun processFiles(directory: File): MutableList<File> {
        val files = directory.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isDirectory) {
                    // 如果是文件夹，则递归处理该文件夹
                    processFiles(file)
                } else {
                    // 如果是文件，则按照之前的逻辑进行处理
//                    val createdTime = file.lastModified()
//                    val date = Date(createdTime)
//                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//                    val dateString = dateFormat.format(date)
                    Logger.d("log share logPath:${file.absolutePath}")
                    fileList.add(file)
                }
            }
        }
        return fileList
    }


}