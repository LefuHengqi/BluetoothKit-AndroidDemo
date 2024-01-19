package com.lefu.ppblutoothkit.producttest.dfu

import com.lefu.ppblutoothkit.R
import com.peng.ppscale.business.torre.listener.OnDFUStateListener
import kotlinx.android.synthetic.main.product_test_dfu_test_activity.mDfuTestCurrentNumTv
import kotlinx.android.synthetic.main.product_test_dfu_test_activity.mTestStateTv


var currentNum = 0

fun ProductTestDfuTestActivity.startDfu(dfuFilePath: String) {
    currentNum++
    mDfuTestCurrentNumTv?.text = currentNum.toString()
    addPrint("startDfu currentNum:$currentNum dfuFilePath:$dfuFilePath")
    if (controller?.getTorreDeviceManager()?.isDFU?.not() ?: false) {
        addPrint("Start DFU upgrade")
        val isFullyDFUState = true //是否全量升级
        controller?.getTorreDeviceManager()
            ?.startDFU(isFullyDFUState, dfuFilePath, onDFUStateListener)
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


