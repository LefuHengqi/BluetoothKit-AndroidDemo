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
        
        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()
        
        // 初始化Toolbar
        initToolbar()
        
        initView()
    }
    
    private fun initToolbar() {
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        toolbar?.let {
            setupUnifiedToolbar(
                toolbar = it,
                title = "日志",
                showBackButton = true
            )
        }
    }

    private fun initView() {
        if (logType == 0) {
            deviceLog = "/Log/AppLog"
            findViewById<TextView>(R.id.title).setText("AppLog")
        } else if (logType == 1) {
            deviceLog = "/Log/DeviceLog"
            findViewById<TextView>(R.id.title).setText("DeviceLog")
        } else {
            deviceLog = "/Log/AppLog"
            findViewById<TextView>(R.id.title).setText("AppLog")
        }
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