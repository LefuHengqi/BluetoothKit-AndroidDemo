package com.lefu.ppblutoothkit

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.lefu.ppbase.util.PPUtil
import com.lefu.ppbase.vo.PPUserGender
import com.lefu.ppblutoothkit.calculate.CalculateManagerActivity
import com.lefu.ppblutoothkit.devicelist.ScanDeviceListActivity
import com.lefu.ppblutoothkit.ext.initDeviceConfig
import com.lefu.ppblutoothkit.log.LogActivity


class MainActivity : BaseImmersivePermissionActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("MainActivity", "onCreate方法开始执行")
        System.out.println("MainActivity: onCreate方法开始执行")
        setContentView(R.layout.activity_main)

        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()

        initToolbar()
        initDeviceConfig()
        findViewById<Button>(R.id.searchDevice).setOnClickListener(this)
        findViewById<Button>(R.id.calculateBodyFat).setOnClickListener(this)

        handleBLUETOOTHSCANPermission(object : AppPermissionCallback {

            override fun onGranted(permissions: MutableList<String>, granted: Boolean) {
                if (granted) {

                } else {
                    handlingPermission()
                }
            }

        })

    }

    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setupUnifiedToolbar(
            toolbar = toolbar,
            title = "${getString(R.string.app_name)}V${BuildConfig.VERSION_NAME}",
            showBackButton = false
        )

        // 设置菜单
        toolbar.inflateMenu(R.menu.main_toolbar_menu)

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_item_export_app_log -> {
                    LogActivity.logType = 0
                    val intent = Intent(this, LogActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.menu_item_export_device_log -> {
                    LogActivity.logType = 1
                    val intent = Intent(this, LogActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_export_app_log -> {
                LogActivity.logType = 0
                val intent = Intent(this, LogActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.menu_item_export_device_log -> {
                LogActivity.logType = 1
                val intent = Intent(this, LogActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.searchDevice -> {

                handleBLUETOOTHSCANPermission(object : AppPermissionCallback {

                    override fun onGranted(permissions: MutableList<String>, granted: Boolean) {
                        if (granted) {
                            startActivity(Intent(this@MainActivity, ScanDeviceListActivity::class.java))
                        } else {
                            handlingPermission()
                        }
                    }

                })
            }

            R.id.calculateBodyFat -> {
                startActivity(Intent(this@MainActivity, CalculateManagerActivity::class.java))
            }
        }
    }


}