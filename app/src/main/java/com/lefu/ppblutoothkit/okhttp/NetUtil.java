package com.lefu.ppblutoothkit.okhttp;


//import com.lefu.ppblutoothkit.BuildConfig;

public class NetUtil {

    /**
     * http='https://healthu.lefuenergy.com',
     * ws='ws://120.79.144.170:8081',
     * scaleHttp='http://120.79.144.170:8288'
     */

    /***前面的链接地址***/
    public static String GET_URL() {
        String url = null;

        url = "https://healthu.lefuenergy.com";   // 线上正式服务器
//        }
        return url;
    }

    //清除设备数据
    public static String CLEAR_DEVICE_DATA = GET_URL() + "/lefu/wifi/app/clearDeviceData";
    //获取秤端上传的体重信息（可根据sn号和uid单独查询 或 组合查询）
    public static String GET_SCALE_WEIGHTS = GET_URL() + "/lefu/wifi/app/getScaleWeights";
    //将Wifi与用户进行绑定，需要你自己的服务器，该处只做演示
    //Binding WiFi to users requires your own server, which will only be used for demonstration purposes
    public static String SAVE_WIFI_GROUP = GET_URL() + "/lefu/wifi/app/saveWifiGroup";

    //    public static String SCALE_DOMAIN = "http://nat.lefuenergy.com:10082";
//    public static String SCALE_DOMAIN = "http://ddi-bm-staging.buzud.com/api/v1.0/scale";
//    public static String SCALE_DOMAIN = "http://ddi-bm-staging.buzud.com:80/api/v1.0/scale";
    public static String SCALE_DOMAIN = "http://nat.lefuenergy.com:10082/api/v1.0/scale";

    /**
     * 拉取设备配置信息，仅供Demo使用，与AppKey配套使用,
     * 在你自己的App中，请使用：PPBlutoothKit.initSdk(this, appKey, Companion.appSecret, "lefu.config")
     */
    public static String GET_SCALE_CONFIG = "http://nat.lefuenergy.com:10081/unique-admin/openApi/openAppKeySetting/download?appKey=";
    //下发给秤的域名，用于秤访问服务器；自己项目中，请使用你自己的
    //The domain name issued to the scale for accessing the server; In your own project, please use your own
//    public static String SCALE_DOMAIN = "http://nat.lefuenergy.com:10082";
//    public static String SCALE_DOMAIN = "http://health-api.duoduiduo.com";

    public static String getScaleDomain() {
        return SCALE_DOMAIN;
    }

    public static void setScaleDomain(String host) {
        SCALE_DOMAIN = host;
    }
}
