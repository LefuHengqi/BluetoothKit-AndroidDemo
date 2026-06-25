package com.lefu.bluetooth.library.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dingjikerbo on 2015/12/16.
 */
public class BluetoothLog {

    private static final String LOG_TAG = "miio-bluetooth";

    public static boolean isDebug = false;

    public static void setDebug(boolean isDebug) {
        BluetoothLog.isDebug = isDebug;
    }

    public static void i(String msg) {
        if (isDebug) {
            Log.i(LOG_TAG, hideMacAddressInString(msg));
        }
    }

    public static void e(String msg) {
        Log.e(LOG_TAG, hideMacAddressInString(msg));
    }

    public static void v(String msg) {
        if (isDebug) {
            Log.v(LOG_TAG, hideMacAddressInString(msg));
        }
    }

    public static void d(String msg) {
        if (isDebug) {
            Log.d(LOG_TAG, hideMacAddressInString(msg));
        }
    }

    public static void w(String msg) {
        if (isDebug) {
            Log.w(LOG_TAG, hideMacAddressInString(msg));
        }
    }

    public static void e(Throwable e) {
        e(getThrowableString(e));
    }

    public static void w(Throwable e) {
        w(getThrowableString(e));
    }

    private static String getThrowableString(Throwable e) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);

        while (e != null) {
            e.printStackTrace(printWriter);
            e = e.getCause();
        }

        String text = writer.toString();

        printWriter.close();

        return text;
    }

    public static String hideMacAddressInString(String logMessage) {
        try {
            String regex = "([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(logMessage);

            StringBuffer buffer = new StringBuffer();
            while (matcher.find()) {
                String macAddress = matcher.group();
                String hiddenMacAddress = macAddress.substring(0, 2) + ":**:**:**:**:" + macAddress.substring(macAddress.length() - 2);
                matcher.appendReplacement(buffer, hiddenMacAddress);
            }
            matcher.appendTail(buffer);
            return buffer.toString();
        } catch (Exception e) {
            return logMessage;
        }
    }
}
