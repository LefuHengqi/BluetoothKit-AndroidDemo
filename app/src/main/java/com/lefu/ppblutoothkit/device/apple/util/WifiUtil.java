package com.lefu.ppblutoothkit.device.apple.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;

import com.peng.ppscale.util.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WifiUtil {

    private static final String TAG = "WifiUtil";
    public static final String LF_SCALE_ = "LF_Scale_";

    private static volatile WifiUtil instance = null;

    private WifiManager mWifiManager;

    private Context mContext;

    private WifiUtil(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static WifiUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (WifiUtil.class) {
                if (instance == null) {
                    instance = new WifiUtil(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    /**
     * 判断是否2.4Gwifi
     *
     * @param frequency
     * @return
     */
    public boolean is24GHzWifi(int frequency) {
        return frequency > 2400 && frequency < 2500;
    }

    /**
     * 判断是否5Gwifi
     *
     * @param frequency
     * @return
     */
    public boolean is5GHzWifi(int frequency) {
        return frequency > 4900 && frequency < 5900;
    }


    public boolean is2_4GFrequency() {
        int frequency = 0;
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        String tempSsidString = wifiInfo.getSSID();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            frequency = wifiInfo.getFrequency();
        } else {
            if (tempSsidString != null && tempSsidString.length() > 2) {
                String wifiSsid = tempSsidString.substring(1, tempSsidString.length() - 1);
                List<ScanResult> scanResults = mWifiManager.getScanResults();
                for (ScanResult scanResult : scanResults) {
                    if (scanResult.SSID.equals(wifiSsid)) {
                        frequency = scanResult.frequency;
                        break;
                    }
                }
            }
        }
        /*if (frequency > 2500){
            return false;
        }*/
        Logger.d("liyp_ frequency = " + frequency);
        return is24GHzWifi(frequency);
    }

    public boolean is_TargetScale() {
        String wifiName = getCurrentSSID();
        if (!TextUtils.isEmpty(wifiName) && wifiName.startsWith(LF_SCALE_)) {
            return true;
        }
        return false;
    }

    public String connectSSID2SNCode() {
        String wifiName = getCurrentSSID();
        Logger.d("wifiName = " + wifiName);
        String[] all = wifiName.split("_");//queryWifiInfo
        if (all.length == 3) {
            return all[2];
        } else {
            return "";
        }
    }

    public boolean isWifiConnect() {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifiInfo.isConnected();
    }

    public String getCurrentSSID() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        String srcSsid = wifiInfo.getSSID();
        if (srcSsid != null && !srcSsid.isEmpty()) {
            srcSsid = srcSsid.substring(1, srcSsid.length() - 1);
        } else {
            ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            String wifiName = networkInfo.getExtraInfo();
            if (wifiName != null && !wifiName.isEmpty()) {
                wifiName = wifiName.replace("\"", "");
            }
            srcSsid = wifiName;
        }
        if (srcSsid != null) {
            Logger.d("wifi name = " + srcSsid);
        }
        return srcSsid;
    }

    public String getLocalIPAddress() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return intToIp(wifiInfo.getIpAddress());
    }

    public String getServerIPAddress() {
        DhcpInfo mDhcpInfo = mWifiManager.getDhcpInfo();
        return intToIp(mDhcpInfo.gateway);
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
                + ((i >> 24) & 0xFF);
    }

    public static String verifyNetData(String srcBuf) {
        if (srcBuf == null) {
            Logger.e("verifyNetData(): srcBuf == null");
            return null;
        }
        String[] allBuf = srcBuf.split(">>");
        if (allBuf.length == 3 && allBuf[0].equals(allBuf[2])) {
            return allBuf[1];
        } else {
            return null;
        }
    }

    /*** 日期long格式化为yyyy/MM/dd HH:mm ***/
    public static String formatDate2(long date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date(date));
    }

}
