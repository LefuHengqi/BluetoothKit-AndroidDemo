package com.lefu.ppblutoothkit.producttest

import java.io.Serializable

/**
 * 保存测试结果对象
 */
class TestResultVo : Serializable {
    var startTime = 0L//启动时间
    var endTime = 0L//结束时间
    var packagesSize = 0L//每包大小
    var deviceModel = ""//设备Model
    var deviceMac = ""
    var phoneModel = ""//手机Model
    var firmwareVersion = ""//固件版本
    var testNum = 0 //实际测试次数
    var planNum = 0//计划测试次数
    var successNum = 0//成功次数

    fun initObj() {
        startTime = 0L//启动时间
        endTime = 0L//结束时间
        packagesSize = 0L//每包大小
        deviceModel = ""//设备Model
        deviceMac = ""
        phoneModel = ""//手机Model
        firmwareVersion = ""//固件版本
        testNum = 0 //实际测试次数
        planNum = 0//计划测试次数
        successNum = 0//成功次数
    }

    override fun toString(): String {
        return "TestResultVo(startTime=$startTime, endTime=$endTime, packagesSize=$packagesSize, deviceModel='$deviceModel', deviceMac='$deviceMac', phoneModel='$phoneModel', firmwareVersion='$firmwareVersion', testNum=$testNum, planNum=$planNum, successNum=$successNum)"
    }


}