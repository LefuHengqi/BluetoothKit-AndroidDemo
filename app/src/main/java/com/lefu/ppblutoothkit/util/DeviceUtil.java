package com.lefu.ppblutoothkit.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class DeviceUtil {

    public final static String UNIT_KG_STR = "kg";
    public final static String UNIT_LB_STR = "lbs";
    public final static String UNIT_ST_STR = "st";

    public static float convertOneDecimales(int newScale, float value) {
        try {
            String data = String.valueOf(value);
            BigDecimal wlb = new BigDecimal(data);
            BigDecimal ilb = wlb.setScale(newScale, RoundingMode.DOWN);
            return ilb.floatValue();
        } catch (Exception e) {
            e.printStackTrace();
//            LogUtil.d(e.getMessage());
        }
        return 0.0f;
    }


    public static String convertKGToOtherOneDecimaleT9148Str(float weight, String unit) {
        int rawWeight = Math.round(weight * 100);
        int tenKg = (rawWeight + 5) / 10;
        int tenlbs = tenKg * 22046 / 10000;
        float lbs = tenlbs / 10.0f;
        String result = "--";
        switch (unit) {
            case UNIT_KG_STR:
                result = String.format(Locale.ENGLISH, "%.2f", weight);
                break;
            case UNIT_LB_STR:
                result = String.format(Locale.ENGLISH, "%.1f", lbs);
                break;
            case UNIT_ST_STR:
                int m = (int) Math.floor(lbs / 14);
                float n = lbs - 14 * m;
                float h = convertOneDecimalesUp(n);
                if (n == 14) {
                    m += 1;
                    n = 0;
                }
                result = h < 10 ? m + ":0" + h : m + ":" + h;
                break;
            default:
                break;
        }
        return "" + result;

    }

    public static float convertOneDecimalesUp(float value) {
        try {
            String data = String.valueOf(value);
            BigDecimal wlb = new BigDecimal(data);
            BigDecimal ilb = wlb.setScale(1, RoundingMode.HALF_UP);
            return ilb.floatValue();
        } catch (Exception e) {
//            LogUtil.d(e.getMessage());
        }
        return 0.0f;
    }


}
