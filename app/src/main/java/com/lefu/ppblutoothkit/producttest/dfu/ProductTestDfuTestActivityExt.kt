package com.lefu.ppblutoothkit.producttest.dfu

import android.widget.Toast
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.util.FileUtil
import com.peng.ppscale.PPBlutoothKit.TAG
import com.peng.ppscale.business.ble.listener.PPDeviceLogInterface
import com.peng.ppscale.business.torre.listener.OnDFUStateListener
import com.peng.ppscale.util.Logger
import kotlinx.android.synthetic.main.product_test_dfu_test_activity.mDfuTestCurrentNumTv
import kotlinx.android.synthetic.main.product_test_dfu_test_activity.mTestStateTv
import java.io.File


var currentNum = 0
val fileList: MutableList<File?>? = ArrayList()

fun ProductTestDfuTestActivity.startDfu(dfuFilePath: String) {
    currentNum++
    mDfuTestCurrentNumTv?.text = currentNum.toString()
    addPrint("startDfu currentNum:$currentNum dfuFilePath:$dfuFilePath")
    if (controller?.getTorreDeviceManager()?.isDFU?.not() ?: false) {
        addPrint("Start DFU upgrade")
        val isFullyDFUState = true //是否全量升级
        controller?.getTorreDeviceManager()?.startDFU(isFullyDFUState, dfuFilePath, onDFUStateListener)
    } else {
        //正在升级，请不要频繁调用//Upgrading in progress, please do not call frequently
        addPrint("Upgrading in progress, please do not call frequently")
    }
}

val ProductTestDfuTestActivity.onDFUStateListener: OnDFUStateListener
    get() = object : OnDFUStateListener {

        override fun onDfuStart() {
            addPrint("onDfuStart")
//            PPBlutoothKit.setDebug(false)
        }

        override fun onDfuFail(errorType: String?) {
            addPrint("onDfuFail $errorType")
//            PPBlutoothKit.setDebug(true)
            currentNum = 0

        }

        override fun onInfoOout(outInfo: String?) {
            addPrint("sdk i: $outInfo")
        }

        override fun onStartSendDfuData() {
            addPrint("onStartSendDfuData")
        }

        override fun onDfuProgress(progress: Int) {
            addPrint("onDfuProgress progress:$progress")
        }

        override fun onDfuSucess() {
            addPrint("onDfuSucess currentNum:$currentNum")
//            PPBlutoothKit.setDebug(true)
            if (currentNum <= totalNum) {
                mTestStateTv?.text = getString(R.string.scanning)
                reSearchAndConnectDevice()
            } else {
                onAllDfuSuccess()
            }
        }
    }

/**
 * 搜索连接
 */
fun ProductTestDfuTestActivity.reSearchAndConnectDevice() {
    addPrint("reSearchAndConnectDevice")
    ProductTestDfuTestActivity.deviceModel?.let {
        controller?.startSearch(it.deviceMac, bleStateInterface)
    }
}

fun ProductTestDfuTestActivity.onAllDfuSuccess() {
    addPrint("all num dfu success totalNum:$totalNum")
}


val ProductTestDfuTestActivity.deviceLogInterface: PPDeviceLogInterface
    get() = object : PPDeviceLogInterface {
        override fun syncLogStart() {
            addPrint("syncLogStart")
        }

        override fun syncLoging(progress: Int) {
            addPrint("syncLoging progress:$progress")
        }

        override fun syncLogEnd(logFilePath: String?) {

            addPrint("syncLogEnd ")
            addPrint("logFilePath: $logFilePath")

            Toast.makeText(this@deviceLogInterface, "Sync Log Success", Toast.LENGTH_SHORT).show()
            FileUtil.sendEmail(this@deviceLogInterface, logFilePath)
        }
    }


fun ProductTestDfuTestActivity.startUploadLog() {
    Logger.d("$TAG 上传日志")
    var logDirectory: File? = File(filesDir, "/Log/AppLog")
    logDirectory?.let {
        fileList?.clear()
        val files = processFiles(it)
        if (files.isNullOrEmpty().not()) {
            files?.let {
                val file = it.get(files.size - 1)
                var fileName = file!!.name
                Logger.d("$TAG 上传日志 path:${file.absolutePath}")
                val lastIndex = fileName.lastIndexOf(".")
                if (lastIndex != -1) {
                    val substring = fileName.substring(0, lastIndex)
                    fileName = "${substring}_${System.currentTimeMillis()}.txt"
                    Logger.d("$TAG 上传日志 传给服务器的名字 fileName：$fileName")
                }

                FileUtil.sendEmail(this@startUploadLog, file.absolutePath)
            }
        } else {
            Logger.e("$TAG 日志导出 日志列表为空 logDirectory path:${logDirectory.absolutePath}")
        }

    }
}

// 递归处理文件的方法
fun processFiles(directory: File): MutableList<File?>? {
    val files = directory.listFiles()
    if (files != null) {
        for (file in files) {
            if (file.isDirectory) {
                // 如果是文件夹，则递归处理该文件夹
                processFiles(file)
            } else {
                // 如果是文件，则按照之前的逻辑进行处理
                fileList?.add(file)
            }
        }
    }
    return fileList
}


