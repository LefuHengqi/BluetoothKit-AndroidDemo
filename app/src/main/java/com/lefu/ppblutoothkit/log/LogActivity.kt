package com.lefu.ppblutoothkit.log

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.appcompat.widget.Toolbar
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.util.FileUtil
import com.lefu.ppblutoothkit.util.log.LFLogFileUtils
import com.lefu.ppblutoothkit.view.MsgDialog
import com.lefu.ppbase.util.Logger
import java.io.File

class LogActivity : BaseImmersivePermissionActivity() {

    var adapter: DeviceLogListAdapter? = null

    var deviceLog = "/Log/AppLog"

    val filesList = ArrayList<File>()

    companion object {
        var logType = 0 //0App 1Device
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_log)

        // 确定日志类型和标题
        if (logType == 0) {
            deviceLog = "/Log/AppLog"
        } else if (logType == 1) {
            deviceLog = "/Log/DeviceLog"
        } else {
            deviceLog = "/Log/AppLog"
        }

        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()

        // 初始化Toolbar
        initToolbar()

        // 处理底部导航栏的WindowInsets
        setupBottomInsets()

        initView()
    }

    private fun initToolbar() {
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        val title = if (logType == 1) "DeviceLog" else "AppLog"
        toolbar?.let {
            setupUnifiedToolbar(
                toolbar = it,
                title = title,
                showBackButton = true
            )
        }
    }

    private fun setupBottomInsets() {
        val rootLayout = findViewById<View>(R.id.root_layout)
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { view, insets ->
            val systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            // 只设置底部内边距，避免影响顶部的Toolbar
            view.setPadding(
                view.paddingLeft,
                0, // 顶部不需要padding，由Toolbar处理
                view.paddingRight,
                systemBars.bottom // 底部导航栏高度
            )
            insets
        }
    }

    private fun initView() {
         initData()
     }

    private fun initData() {
        adapter = DeviceLogListAdapter()

        val deviceListRecyclerView = findViewById<RecyclerView>(R.id.deviceListRecyclerView)
        deviceListRecyclerView.layoutManager = LinearLayoutManager(this)
        deviceListRecyclerView.adapter = adapter

        adapter?.setOnItemClickListener { adapter, view, position ->
            if (position >= 0 && adapter.data.size > position) {
                val file = adapter.getItem(position) as File
                Logger.d("logFilePath: ${file.path}")
                FileUtil.sendEmail(this, file.path)
            }
        }

        adapter?.setOnItemLongClickListener { adapter, view, position ->
            if (position >= 0 && adapter.data.size > position) {
                val file = adapter.getItem(position) as File
                Logger.d("delete logFilePath: ${file.path}")
                MsgDialog.init(supportFragmentManager)
                    .setTitle(getString(R.string.tips))
                    .setMessage("确认删除？")
                    .setAnimStyle(R.style.dialog_)
                    .setCancelableAll(true)
                    .setNegativeButton(getString(R.string.cancel))
                    .setPositiveButton(getString(R.string.confirm), View.OnClickListener {
                        LFLogFileUtils.deleteFile(file)
                        reLoadData()
                    })
                    .show()
            }
            return@setOnItemLongClickListener true
        }
        reLoadData()
    }

    fun reLoadData() {
        val file = File(filesDir, deviceLog)
        filesList.clear()
        processFiles(file)
        val sortedFiles = filesList.sortedByDescending { it.lastModified() }
        adapter?.setNewData(sortedFiles)
    }


    // 递归处理文件的方法
    fun processFiles(directory: File) {
        val files = directory.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isDirectory) {
                    // 如果是文件夹，则递归处理该文件夹
                    processFiles(file)
                } else {
                    // 如果是文件，则按照之前的逻辑进行处理
                    filesList.add(file)
                }
            }
        }
    }


}