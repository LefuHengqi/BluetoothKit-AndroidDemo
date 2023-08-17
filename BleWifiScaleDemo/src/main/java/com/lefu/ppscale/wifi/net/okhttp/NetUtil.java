package com.lefu.ppscale.wifi.net.okhttp;

import com.lefu.ppscale.wifi.BuildConfig;

public class NetUtil {

    /**
     * http='https://healthu.lefuenergy.com',
     * ws='ws://120.79.144.170:8081',
     * scaleHttp='http://120.79.144.170:8288'
     */

    /***前面的链接地址***/
    public static String GET_URL() {
        String url = null;
        if (!BuildConfig.DEBUG) {
            url = "https://healthu.lefuenergy.com";   // 线上正式服务器
        } else {
            url = "https://healthu.lefuenergy.com";   // 线上正式服务器
        }
        return url;
    }

    //清除设备数据
    public static String CLEAR_DEVICE_DATA = GET_URL() + "/lefu/wifi/app/clearDeviceData";
    //获取秤端上传的体重信息（可根据sn号和uid单独查询 或 组合查询）
    public static String GET_SCALE_WEIGHTS = GET_URL() + "/lefu/wifi/app/getScaleWeights";
    //保存用户wifi组
    public static String SAVE_WIFI_GROUP = GET_URL() + "/lefu/wifi/app/saveWifiGroup";
    //下发给秤的域名，用于秤访问服务器；自己项目中，请使用你自己的
//    public static String SCALE_DOMAIN = "http://120.79.144.170:8288";
    public static String SCALE_DOMAIN = "http://health-api.duoduiduo.com";


}
