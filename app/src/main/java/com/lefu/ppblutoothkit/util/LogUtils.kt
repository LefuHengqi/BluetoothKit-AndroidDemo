package com.lefu.ppblutoothkit.util

import android.os.Build
import com.lefu.ppblutoothkit.util.log.LogConstant
import com.lefu.ppblutoothkit.util.log.MyQueueLinkedUtils

/**
 *    @author : whs
 *    e-mail : haisilen@163.com
 *    date   : 2023/6/5 11:21
 *    desc   : 日志相关总操作类
 */
object LogUtils {

    // log输出开关，发布的时候设成false
    public var DEBUG = true

    // app名字,用来标识上传日志所属哪个app
    var APP_NAME = "Unique_Health"


    // 用户的邮箱或昵称
    var USER_LOGO = ""

    // 手机型号
    var PHONE_MODEL = ""

    enum class UploadLogType(val logType: String) {
        AppLog("AppLog"),
        DeviceLog("DeviceLog")
    }

    /**
     * 调试信息的输出
     */
    fun d(
        msg: String = LogConstant.LFLOG_TAG,
        vararg messages: Any,
        type: String = LogConstant.LFLOG_TYPE_COMMON
    ) {
        if (!DEBUG) {
            return
        }
        writeBluetoothLog("tag=${msg}--  type:${type} --msg:${getString(messages)}")
    }

    /**
     * 警告信息的输出
     */
    fun w(
        msg: String = LogConstant.LFLOG_TAG,
        vararg messages: Any,
        type: String = LogConstant.LFLOG_TYPE_COMMON
    ) {
        if (!DEBUG) {
            return
        }
        writeBluetoothLog("tag=${msg}--  type:${type} --msg:${getString(messages)}")
    }

    /**
     * 错误信息的输出
     */
    fun e(
        tag: String = LogConstant.LFLOG_TAG,
        vararg messages: Any,
        type: String = LogConstant.LFLOG_TYPE_COMMON
    ) {
        if (!DEBUG) {
            return
        }
        writeBluetoothLog("tag=${tag}--  type:${type} --msg:${getString(messages)}")
    }

    /**
     * 设置是否输出日志
     */
    fun setLogEnable(isEnable: Boolean) {
        this.DEBUG = isEnable
    }

    /**
     * 初始化基本信息
     */
    fun init(appName: String = "PPBluetoothKit") {
        this.APP_NAME = appName
        this.PHONE_MODEL = Build.MODEL
    }

    /**
     * 用户标识
     */
    fun setUserLogo(userLogo: String) {
        this.USER_LOGO = userLogo
    }


    fun getString(messages: Array<out Any>): String {
        val sb = StringBuilder()
        for (obj in messages) {
            if (obj is Array<*>) {
                for (obj1 in obj) {
                    sb.append(obj1.toString())
                    sb.append(' ')
                }
            } else {
                sb.append(obj.toString())
                sb.append(' ')
            }
        }
        return sb.toString()
    }

    /**
     * 写入普通日志到指定文件中
     */
    fun writeNormalLog(vararg messages: Any, type: String = LogConstant.LFLOG_TYPE_COMMON) {
        //用队列来存储日志
//        MyQueueLinkedUtils.offer(MyQueueLinkedUtils.LogVo(messages, type))
        //需求更改为全部写在一个文件下
        MyQueueLinkedUtils.offer(MyQueueLinkedUtils.LogVo(messages, LogConstant.LFLOG_TYPE_COMMON))
    }

    /**
     * 写入蓝牙日志到指定文件中
     */
    fun writeBluetoothLog(vararg messages: Any) {
        writeNormalLog(messages, type = LogConstant.LFLOG_TYPE_BLUETOOTH)
    }

    /**
     * 写入网络日志到指定文件中
     */
    fun writeNetworkLog(vararg messages: Any) {
        writeNormalLog(messages, type = LogConstant.LFLOG_TYPE_NETWORK)
    }

}