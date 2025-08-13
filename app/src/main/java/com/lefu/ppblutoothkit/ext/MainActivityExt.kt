package com.lefu.ppblutoothkit.ext

import com.lefu.ppbase.util.Logger
import com.lefu.ppblutoothkit.MainActivity
import com.lefu.ppblutoothkit.PPApplication
import com.lefu.ppblutoothkit.okhttp.DataTask
import com.lefu.ppblutoothkit.okhttp.NetUtil
import com.lefu.ppblutoothkit.okhttp.RetCallBack
import com.lefu.ppblutoothkit.vo.DemoDeviceConfigVo
import com.peng.ppscale.PPBluetoothKit
import com.peng.ppscale.util.json.GsonUtil
import okhttp3.Call


/**
 * 拉取设备配置信息，仅供Demo使用，与AppKey配套使用,
 *
 * 在你自己的App中，请使用：PPBlutoothKit.initSdk(this, appKey, Companion.appSecret, "lefu.config")
 * 使用时请务必替换成你自己的AppKey/AppSecret/lefu.config，需要增加设备配置请查看：
 * https://xinzhiyun.feishu.cn/wiki/XU4ZwABN0iFWU0kr0cpcxy9onMd?from=from_copylink
 */
fun MainActivity.initDeviceConfig() {
    try {
        // 准备网络请求参数
        val map: MutableMap<String, String> = HashMap()
        val url = NetUtil.GET_SCALE_CONFIG + PPApplication.appKey
        DataTask.get(url, map, object : RetCallBack<String>(String::class.java) {
            override fun onError(call: Call, e: Exception, id: Int) {
                Logger.e("MainActivity: 网络请求失败 - " + e.message)
            }

            override fun onResponse(response: String?, p1: Int) {
                response?.let {
                    try {
                        val configVo = GsonUtil.jsonStirngToObj<DemoDeviceConfigVo>(response, DemoDeviceConfigVo::class.java)

                        // 暂时注释掉原有的配置处理逻辑，因为需要先解析JSON
                        if (configVo.code == 200 && configVo.msg.isNullOrEmpty().not()) {
                            configVo.msg?.let { it1 ->
                                // 在主线程中执行UI相关操作
                                runOnUiThread {
                                    PPBluetoothKit.setNetConfig(this@initDeviceConfig, PPApplication.appKey, PPApplication.appSecret, it1)
                                }

                            }
                        } else {
                            Logger.w("MainActivity 配置数据无效或为空")
                        }

                    } catch (e: Exception) {
                        Logger.e("MainActivity: 解析响应数据失败 - " + e.message)
                    }


                } ?: android.util.Log.w("MainActivity", "配置响应为空")
            }
        })
    } catch (e: Exception) {
        Logger.e("MainActivity: initDeviceConfig异常 - " + e.message)
        e.printStackTrace()
    }
}