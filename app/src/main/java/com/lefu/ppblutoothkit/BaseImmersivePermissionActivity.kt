package com.lefu.ppblutoothkit

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.lefu.ppbase.util.Logger
import com.lefu.ppblutoothkit.util.CommonUtils

abstract class BaseImmersivePermissionActivity : AppCompatActivity() {

    protected var toolbar: Toolbar? = null

    // 权限相关代码
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 不在这里调用 setupImmersiveMode，而是在子类的 setContentView 之后调用
    }

    /**
     * 设置沉浸式状态栏 - 需要在 setContentView 之后调用
     */
    /**
     * 设置沉浸式状态栏 - Toolbar延伸到状态栏区域
     */
    protected fun setupImmersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            
            // 设置状态栏透明，让Toolbar的背景延伸到状态栏
            window.statusBarColor = Color.TRANSPARENT
            
            // 设置布局延伸到状态栏下方
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11+ 使用新的API
                window.setDecorFitsSystemWindows(false)
                window.insetsController?.let { controller ->
                    controller.setSystemBarsAppearance(
                        0, // 清除浅色状态栏标志
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                }
            } else {
                // Android 5-10 使用旧的API
                window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
            }
        }
    }

    /**
     * 初始化统一的Toolbar - 简化版本
     */
    protected fun setupUnifiedToolbar(
        toolbar: Toolbar,
        title: String = "",
        showBackButton: Boolean = false
    ) {
        this.toolbar = toolbar
        setSupportActionBar(toolbar)
        
        supportActionBar?.apply {
            this.title = title
            setDisplayHomeAsUpEnabled(showBackButton)
            setDisplayShowHomeEnabled(showBackButton)
        }
        
        toolbar.setTitleTextColor(Color.WHITE)
        
        if (showBackButton) {
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
        
        // 设置Toolbar的内边距和高度，确保有足够空间显示标题
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // 设置内边距
            view.setPadding(
                view.paddingLeft,
                systemBars.top, // 状态栏高度
                view.paddingRight,
                view.paddingBottom
            )
            
            // 确保Toolbar有足够的高度
            val actionBarSize = theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
            val minHeight = actionBarSize.getDimensionPixelSize(0, 0)
            actionBarSize.recycle()
            
            // 设置Toolbar的总高度 = actionBarSize + 状态栏高度
            val layoutParams = view.layoutParams
            layoutParams.height = minHeight + systemBars.top
            view.layoutParams = layoutParams
            
            insets
        }
    }

    // 权限处理方法
    protected fun handleBLUETOOTHSCANPermission(appPermissionCallback: AppPermissionCallback) {
        var permissions = permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions = permissions31
        }
        if (CommonUtils.isHuaweiOS() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions = permissions31HuaWei
        }
        isLocationOrBle(appPermissionCallback, permissions)
    }

    private fun isLocationOrBle(
        appPermissionCallback: AppPermissionCallback,
        permissions: MutableList<String>
    ) {
        // 首先检查是否已经拥有所需权限
        val hasAllPermissions = XXPermissions.isGranted(this, permissions)
        
        if (hasAllPermissions) {
            //Android 12以上不需要定位服务
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && CommonUtils.isHuaweiOS().not()) {
                appPermissionCallback.onGranted(permissions, CommonUtils.isOpenBluetooth())
            } else {
                appPermissionCallback.onGranted(permissions, CommonUtils.isLocationEnabled(this) && CommonUtils.isOpenBluetooth())
            }
        } else {
            // 没有权限时，发起权限请求
            XXPermissions.with(this)
                .permission(permissions)
                .request(object : OnPermissionCallback {
                    override fun onGranted(
                        grantedPermissions: MutableList<String>,
                        allGranted: Boolean
                    ) {
                        if (allGranted) {
                            //Android 12以上不需要定位服务
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && CommonUtils.isHuaweiOS().not()) {
                                appPermissionCallback.onGranted(permissions, CommonUtils.isOpenBluetooth())
                            } else {
                                appPermissionCallback.onGranted(permissions, CommonUtils.isLocationEnabled(this@BaseImmersivePermissionActivity) && CommonUtils.isOpenBluetooth())
                            }
                        } else {
                            // 部分权限被拒绝，传递false表示权限不完整
                            appPermissionCallback.onGranted(grantedPermissions, false)
                        }
                    }
                    
                    override fun onDenied(
                        deniedPermissions: MutableList<String>,
                        doNotAskAgain: Boolean
                    ) {
                        // 权限被拒绝，传递空列表和false
                        appPermissionCallback.onGranted(mutableListOf(), false)
                        
                        if (doNotAskAgain) {
                            Toast.makeText(this@BaseImmersivePermissionActivity, "权限被永久拒绝，请到设置中手动开启", Toast.LENGTH_LONG).show()
                            // 可以引导用户到设置页面
                            XXPermissions.startPermissionActivity(this@BaseImmersivePermissionActivity, deniedPermissions)
                        } else {
                            Toast.makeText(this@BaseImmersivePermissionActivity, "需要相关权限才能正常使用蓝牙功能", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
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
            if (CommonUtils.isLocationEnabled(this) && CommonUtils.isOpenBluetooth()) {
            } else {
                if (CommonUtils.isLocationEnabled(this).not()) {
                    Toast.makeText(this, "请开启定位开关", Toast.LENGTH_LONG).show()
                }
                if (CommonUtils.isOpenBluetooth().not()) {
                    openBluetooth()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (handleCheckBLUETOOTHSCANPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                //已经可以进行蓝牙操作
            } else {
                if (CommonUtils.isLocationEnabled(this)) {
                    //已经可以进行蓝牙操作
                } else {
                    Toast.makeText(this, "请开启定位开关", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun openBluetooth() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, 1)
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

    interface AppPermissionCallback {
        fun onGranted(permissions: MutableList<String>, all: Boolean)
    }
}