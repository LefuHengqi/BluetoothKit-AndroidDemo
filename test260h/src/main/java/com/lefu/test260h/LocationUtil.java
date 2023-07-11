package com.lefu.test260h;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by liyp on 2018/10/27.
 */

public class LocationUtil {

    private static String judgeProvider(LocationManager locationManager, Context context) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if(prodiverlist.contains(LocationManager.NETWORK_PROVIDER)){
            return LocationManager.NETWORK_PROVIDER;//网络定位
        }
//        else if(prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
//            return LocationManager.GPS_PROVIDER;//GPS定位
//        }
        else {
            Toast.makeText(context,"没有可用的位置提供器",Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public static Location beginLocatioon(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //获得位置服务
        String provider = judgeProvider(locationManager, context);
        //有位置提供器的情况
        if (provider != null) {
            //为了压制getLastKnownLocation方法的警告
            if (
//                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED &&
                     ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            return locationManager.getLastKnownLocation(provider);
        }else{
            //不存在位置提供器的情况
            Toast.makeText(context,"不存在位置提供器的情况",Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /**
     * 判断定位是否可用,GPS或者AGPS开启一个就认为是开启的
     *
     * @return {@code true}: 是
     * {@code false}: 否
     * <p>
     * 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
     * boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
     * 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
     * boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
     */
    public static boolean isLocationEnabledS(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 判断定位开关是否打开
     *
     * @param context
     * @return
     */
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    /**
     * 直接跳转至位置信息设置界面
     */
    public static void openLocation(Context context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }

    public final static String EMUI = "huawei"; //华为
    public final static String Flyme = "meizu"; //魅族
    public final static String MIUI = "xiaomi"; //小米
    public final static String Sony = "sony"; //索尼
    public final static String ColorOS = "oppo"; //OPPO
    public final static String EUI = "letv"; //乐视
    public final static String LG = "lg"; //LG
    public final static String SamSung = "samsung"; //三星
    public final static String SmartisanOS = "smartisan";//锤子
    public final static String VIVO = "vivo"; //VIVO
    public final static String Kupai = "yulong";
    public final static String ZTE = "zte";
    public final static String LENOVO = "lenovo";

    /**
     * 跳转至权限设置页面
     *
     * @param context
     * @return
     */
    public static boolean gotoPermissionSetting(Context context) {
        boolean success = true;
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String packageName = context.getPackageName();
        String romType = Build.MANUFACTURER.toLowerCase();
        switch (romType) {
            case EMUI: // 华为
                intent.putExtra("packageName", packageName);
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity"));
                break;
            case Flyme: // 魅族
                intent.setAction("com.meizu.safe.security.SHOW_APPSEC");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.putExtra("packageName", packageName);
                break;
            case MIUI: // 小米
                String rom = getMiuiVersion();
                if ("V6".equals(rom) || "V7".equals(rom) || "V5".equals(rom)) {
                    intent.setAction("miui.intent.action.APP_PERM_EDITOR");
                    intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                    intent.putExtra("extra_pkgname", packageName);
                } else if ("V8".equals(rom) || "V9".equals(rom)) {
                    intent.setAction("miui.intent.action.APP_PERM_EDITOR");
                    intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
                    intent.putExtra("extra_pkgname", packageName);
                } else {
                    intent = getAppDetailSettingIntent(context);
                }
                break;
            case Sony: // 索尼
                intent.putExtra("packageName", packageName);
                intent.setComponent(new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity"));
                break;
            case EUI: // 乐视
                intent.putExtra("packageName", packageName);
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps"));
                break;
            case LG: // LG
                intent.setAction("android.intent.action.MAIN");
                intent.putExtra("packageName", packageName);
                ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
                intent.setComponent(comp);
                break;
            case SamSung: // 三星
                intent = getAppDetailSettingIntent(context);
                break;
            case SmartisanOS: // 锤子
                intent = getAppDetailSettingIntent(context);
                break;
            case VIVO:
                intent = context.getPackageManager().getLaunchIntentForPackage("com.iqoo.secure");
                break;
            case ColorOS: // OPPO
                intent = context.getPackageManager().getLaunchIntentForPackage("com.iqoo.secure");
//                intent.putExtra("packageName", packageName);
//                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.PermissionManagerActivity"));
                break;
            default:
                intent = getAppDetailSettingIntent(context);
                break;
        }
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // 跳转失败, 前往普通设置界面
            try {
                context.startActivity(getAppDetailSettingIntent(context));
            } catch (Exception e1) {
                context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                e1.printStackTrace();
            }
        }
        return success;
    }

    private static String getMiuiVersion() {
        String propName = "ro.miui.ui.version.name";
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(
                    new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
//            LshIOUtils.close(input);
        }
//        LshLogUtils.i("MiuiVersion = " + line);
        return line;
    }

    /**
     * 获取应用详情页面intent（如果找不到要跳转的界面，也可以先把用户引导到系统设置页面）
     *
     * @return
     */
    private static Intent getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        return localIntent;
    }

    /**
     * 判断Gps是否可用
     *
     * @return {@code true}: 是
     * {@code false}: 否
     */

    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
