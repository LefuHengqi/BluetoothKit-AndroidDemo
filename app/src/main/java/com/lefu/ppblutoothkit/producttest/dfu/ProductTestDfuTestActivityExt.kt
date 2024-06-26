package com.lefu.ppblutoothkit.producttest.dfu

import android.widget.Toast
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.util.FileUtil
import com.peng.ppscale.PPBlutoothKit.TAG
import com.peng.ppscale.business.ble.listener.PPDeviceLogInterface
import com.peng.ppscale.business.torre.listener.OnDFUStateListener
import com.peng.ppscale.util.FileUtilCallBack
import com.peng.ppscale.util.FileUtilsKotlin.createFileAndWrite
import com.peng.ppscale.util.Logger
import kotlinx.android.synthetic.main.product_test_dfu_test_activity.mDfuTestCurrentNumTv
import kotlinx.android.synthetic.main.product_test_dfu_test_activity.mTestStateTv
import kotlinx.android.synthetic.main.product_test_dfu_test_activity.startTestBtn
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

var currentNum = 0
val fileList: MutableList<File?>? = ArrayList()

fun ProductTestDfuTestActivity.startDfu(dfuFilePath: String) {
    currentNum++
    testResultVo.testNum = currentNum
    mDfuTestCurrentNumTv?.text = currentNum.toString()
    addPrint("startDfu currentNum:$currentNum dfuFilePath:$dfuFilePath")
    if (controller?.getTorreDeviceManager()?.isDFU?.not() ?: false) {
        addPrint("Start DFU upgrade")
        controller?.getTorreDeviceManager()?.startDFU(dfuFilePath, onDFUStateListener)
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
            if (errorType == "-1") {
                if (isTesting) {
                    addPrint("onDfuFail reStartDfu")
                    dfuFilePath?.let { startDfu(it) }
                }
            }
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
            addPrint("onDfuSucess currentNum:$currentNum totalNum:$totalNum")
//            PPBlutoothKit.setDebug(true)
            testResultVo.successNum = currentNum
            if (currentNum < totalNum) {
            } else {
                onAllDfuSuccess()
            }
        }
    }

/**
 * 搜索连接
 */
fun ProductTestDfuTestActivity.reSearchAndConnectDevice() {
    testResultVo.successNum = currentNum
    if (currentNum < totalNum) {
        mTestStateTv?.text = getString(R.string.scanning)
        addPrint("reSearchAndConnectDevice")
        ProductTestDfuTestActivity.deviceModel?.let {
            controller?.startSearch(it.deviceMac, bleStateInterface)
        }
    } else {
        onAllDfuSuccess()
    }
}

fun ProductTestDfuTestActivity.onAllDfuSuccess() {
    testResultVo.endTime = System.currentTimeMillis()
    addPrint("Test Success totalNum:$totalNum")
    isTesting = false
    startTestBtn?.text = "开始测试"
    mTestStateTv?.text = "测试完成"
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


/**
 * 分享报告
 */
fun ProductTestDfuTestActivity.exportTestReport() {
    if (testResultVo.endTime > 0) {
        Logger.d("exportTestReport: ${testResultVo.toString()}")
        val testFileFath = filesDir.absolutePath + "/Log/TestLog"
        Logger.d("exportTestReport: testFileFath:$testFileFath")
        val simpleDateFormat = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
        val date = Date()
        date.time = testResultVo.startTime
        val startTime = simpleDateFormat.format(date)

        val allTime = testResultVo.endTime - testResultVo.startTime
        val minutes = allTime / 1000.0f / 60 / 60

        val failNum = testResultVo.testNum - testResultVo.successNum

        val secendTime = allTime / 1000

        val d = testResultVo.packagesSize / 1024.0f * testResultVo.successNum
        val speedKs = d / secendTime


        val buffer = StringBuffer()
        buffer.append("起始时间：$startTime \n")
        buffer.append("总耗时：${String.format("%.1f", minutes)}分钟\n")
        buffer.append("升级速率：${String.format("%.1f", speedKs)} KB/s\n")
        buffer.append("设备蓝牙名称：${testResultVo.deviceModel}\n")
        buffer.append("设备蓝牙Mac：${testResultVo.deviceMac}\n")
        buffer.append("固件版本:${testResultVo.firmwareVersion}\n")
        buffer.append("固件大小:${testResultVo.packagesSize}\n")
        buffer.append("测试结果：\n")
        buffer.append("计划测试次数：${testResultVo.planNum}次，实际测试次数：${testResultVo.testNum}次\n")
        buffer.append("成功：${testResultVo.successNum}次  失败：$failNum 次\n")
        buffer.append("OTA成功率：${String.format("%.1f", testResultVo.successNum.toDouble() / testResultVo.testNum * 100)}%")

        createFileAndWrite(testFileFath, "Report_" + testResultVo.deviceModel, testResultVo.deviceMac, buffer.toString(), object : FileUtilCallBack {
            override fun callBack(logFilePath: String?) {
                Logger.d("exportTestReport success: logFilePath:$logFilePath")
                FileUtil.sendEmail(this@exportTestReport, logFilePath)
            }
        })
    } else {
        Logger.e("exportTestReport fail: ${testResultVo.toString()}")
        showToast("测量未结束")
        addPrint("测量未结束,请启动测量")
    }

}


