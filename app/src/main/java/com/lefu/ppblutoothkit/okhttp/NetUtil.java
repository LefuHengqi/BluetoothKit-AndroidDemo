package com.lefu.ppblutoothkit.okhttp;

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
    //将Wifi与用户进行绑定，需要你自己的服务器，该处只做演示
    //Binding WiFi to users requires your own server, which will only be used for demonstration purposes
    public static String SAVE_WIFI_GROUP = GET_URL() + "/lefu/wifi/app/saveWifiGroup";
    //下发给秤的域名，用于秤访问服务器；自己项目中，请使用你自己的
    //The domain name issued to the scale for accessing the server; In your own project, please use your own
    public static String SCALE_DOMAIN = "http://nat.lefuenergy.com:10082";


}
