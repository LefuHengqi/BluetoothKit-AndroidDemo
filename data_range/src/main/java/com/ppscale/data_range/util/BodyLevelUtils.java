package com.ppscale.data_range.util;

import android.content.Context;

import com.peng.ppscale.util.PPUtil;
import com.ppscale.data_range.R;
import com.ppscale.data_range.vo.FatLevelEntity;

import java.util.ArrayList;

/**
 * oAuthor:${谭良}
 * date:2018/5/4
 * time:16:48
 * projectName:NewiWellness
 */
public class BodyLevelUtils {

    public static ArrayList<FatLevelEntity> getWeightList(Context mContext, int gender, double height) {
        ArrayList<FatLevelEntity> bs = new ArrayList<>();
        double standard_weight;
        if (gender == 1) {
            standard_weight = (height - 80) * 0.7f;
        } else {
            standard_weight = (height - 70) * 0.6f;
        }
        double critical_point1 = standard_weight - standard_weight * 0.1f;
        double critical_point2 = standard_weight + standard_weight * 0.1f;
        double critical_point3 = standard_weight + standard_weight * 0.2f;
        bs.add(new FatLevelEntity(critical_point1, mContext.getString(R.string.lower), 1));
        bs.add(new FatLevelEntity(critical_point2, mContext.getString(R.string.standard), 2));
        bs.add(new FatLevelEntity(critical_point3, mContext.getString(R.string.overHeight), 3));
        return bs;
    }

    public static ArrayList<FatLevelEntity> getHeartList(Context mContext) {
        ArrayList<FatLevelEntity> bs = new ArrayList<>();
        bs.add(new FatLevelEntity(40, mContext.getString(R.string.heart_leve1_name), 4));
        bs.add(new FatLevelEntity(60, mContext.getString(R.string.heart_leve2_name), 1));
        bs.add(new FatLevelEntity(100, mContext.getString(R.string.heart_leve3_name), 2));
        bs.add(new FatLevelEntity(160, mContext.getString(R.string.heart_leve4_name), 3));
        bs.add(new FatLevelEntity(180, mContext.getString(R.string.heart_leve5_name), 4));
        return bs;
    }

    public static ArrayList<FatLevelEntity> getBMIList(Context mContext) {
        ArrayList<FatLevelEntity> bs = new ArrayList<>();
        bs.add(new FatLevelEntity(18.5, mContext.getString(R.string.slim), 1));
        bs.add(new FatLevelEntity(24.0, mContext.getString(R.string.bmi_health), 2));
        bs.add(new FatLevelEntity(30.0, mContext.getString(R.string.chubby), 3));
        bs.add(new FatLevelEntity(60, mContext.getString(R.string.fat), 4));
        return bs;
    }

    public static ArrayList<FatLevelEntity> getBMIListBaby(Context mContext, int age) {
        if (age <= 5) {
            return getBMIListLess5(mContext);
        }
        return getBMIListGT5(mContext);
    }

    public static ArrayList<FatLevelEntity> getBMIListGT5(Context mContext) {
        ArrayList<FatLevelEntity> bs = new ArrayList<>();
        bs.add(new FatLevelEntity(18.5, mContext.getString(R.string.slim), 1));
        bs.add(new FatLevelEntity(24.0, mContext.getString(R.string.health), 2));
        bs.add(new FatLevelEntity(28.0, mContext.getString(R.string.chubby), 3));
        bs.add(new FatLevelEntity(60, mContext.getString(R.string.fat), 4));
        return bs;
    }

    public static ArrayList<FatLevelEntity> getBMIListLess5(Context mContext) {
        ArrayList<FatLevelEntity> bs = new ArrayList<>();
        bs.add(new FatLevelEntity(16, mContext.getString(R.string.slim), 1));
        bs.add(new FatLevelEntity(19, mContext.getString(R.string.health), 2));
        bs.add(new FatLevelEntity(23, mContext.getString(R.string.chubby), 3));
        bs.add(new FatLevelEntity(60, mContext.getString(R.string.fat), 4));
        return bs;
    }

    public static ArrayList<FatLevelEntity> getBMRList(Context mContext, int sex, int age, double weight, double bmr) {
        ArrayList<FatLevelEntity> bs = new ArrayList<>();
        float bmr2 = StripedStand.bmrInt1(sex, age, weight);
        bs.add(new FatLevelEntity(PPUtil.keepPoint0(bmr2), mContext.getString(R.string.lower), 1));
        bs.add(new FatLevelEntity(PPUtil.keepPoint0(bmr2) * 2, mContext.getString(R.string.superior), 6));
        return bs;
    }

    public static ArrayList<FatLevelEntity> getWaterList(Context mContext, int gender) {
        ArrayList<FatLevelEntity> bs = new ArrayList<>();
        if (gender == 1) {  //男
            bs.add(new FatLevelEntity(55, mContext.getString(R.string.lower), 1));
            bs.add(new FatLevelEntity(65, mContext.getString(R.string.standard), 2));
        } else {  //女
            bs.add(new FatLevelEntity(45, mContext.getString(R.string.lower), 1));
            bs.add(new FatLevelEntity(60, mContext.getString(R.string.standard), 2));
        }
        bs.add(new FatLevelEntity(100, mContext.getString(R.string.superior), 6));
        return bs;
    }

    public static ArrayList<FatLevelEntity> getFatList(Context mContext, int gender, int age) {
        ArrayList<FatLevelEntity> bs = new ArrayList<>();
        double critical_point1 = 0;
        double critical_point2 = 0;
        double critical_point3 = 0;
        double critical_point4 = 0;
        if (gender == 1) {  //男
            if (age < 40) {
                critical_point1 = 10;
                critical_point2 = 21;
                critical_point3 = 26;
                critical_point4 = 45;
            } else if (age >= 40 && age < 60) {
                critical_point1 = 11;
                critical_point2 = 22;
                critical_point3 = 27;
                critical_point4 = 45;
            } else if (age >= 60) {
                critical_point1 = 13;
                critical_point2 = 24;
                critical_point3 = 29;
                critical_point4 = 45;
            }
        } else {  //女
            if (age < 40) {
                critical_point1 = 20;
                critical_point2 = 34;
                critical_point3 = 39;
                critical_point4 = 45;
            } else if (age >= 40 && age < 60) {
                critical_point1 = 21;
                critical_point2 = 35;
                critical_point3 = 40;
                critical_point4 = 45;
            } else if (age >= 60) {
                critical_point1 = 22;
                critical_point2 = 36;
                critical_point3 = 41;
                critical_point4 = 45;
            }
        }
        bs.add(new FatLevelEntity(critical_point1, mContext.getString(R.string.slim), 1));
        bs.add(new FatLevelEntity(critical_point2, mContext.getString(R.string.health), 2));
        bs.add(new FatLevelEntity(critical_point3, mContext.getString(R.string.chubby), 3));
        bs.add(new FatLevelEntity(critical_point4, mContext.getString(R.string.fat), 4));
        return bs;
    }

    public static ArrayList<FatLevelEntity> getMuscleList(Context mContext, int gender, double height) {
        ArrayList<FatLevelEntity> bs = new ArrayList<>();
        float critical_point1 = 1;
        float critical_point2 = 1;
        float critical_point3 = 20;
        if (gender == 1) {  //男
            if (height < 160) {
                critical_point1 = 38.5f;
                critical_point2 = 46.5f;
                critical_point3 = 100;
            } else if (height >= 160 && height <= 170) {
                critical_point1 = 44f;
                critical_point2 = 52.4f;
                critical_point3 = 100;
            } else {
                critical_point1 = 49.4f;
                critical_point2 = 59.4f;
                critical_point3 = 100;
            }
        } else {  //女
            if (height < 150) {
                critical_point1 = 29.1f;
                critical_point2 = 34.7f;
                critical_point3 = 100;
            } else if (height >= 150 && height <= 160) {
                critical_point1 = 32.9f;
                critical_point2 = 37.5f;
                critical_point3 = 100;
            } else {
                critical_point1 = 36.5f;
                critical_point2 = 42.5f;
                critical_point3 = 100;
            }
        }
        bs.add(new FatLevelEntity(PPUtil.formatDouble1(critical_point1), mContext.getString(R.string.insufficient), 1));
        bs.add(new FatLevelEntity(PPUtil.formatDouble1(critical_point2), mContext.getString(R.string.standard), 2));
        bs.add(new FatLevelEntity(PPUtil.formatDouble1(critical_point3), mContext.getString(R.string.superior), 6));
        return bs;
    }

    public static ArrayList<FatLevelEntity> getVFALList(Context mContext) {
        ArrayList<FatLevelEntity> bs = new ArrayList<>();
        bs.add(new FatLevelEntity(9.0, mContext.getString(R.string.standard), 2));
        bs.add(new FatLevelEntity(14.0, mContext.getString(R.string.alter), 3));
        bs.add(new FatLevelEntity(16, mContext.getString(R.string.danger), 4));
        return bs;
    }

    public static ArrayList<FatLevelEntity> getBoneList(Context mContext, double weight, int gender) {
        ArrayList<FatLevelEntity> bs = new ArrayList<>();
        float critical_point1 = 2.4f;
        float critical_point2 = 2.6f;
        float critical_point3 = 3.2f;
        if (gender == 1) {  //男
            if (weight < 60) {
                critical_point1 = 2.4f;
                critical_point2 = 2.6f;
                critical_point3 = 3.2f;
            } else if (weight >= 60 && weight <= 75) {
                critical_point1 = 2.8f;
                critical_point2 = 3.0f;
                critical_point3 = 3.2f;
            } else if (weight > 75) {
                critical_point1 = 3.1f;
                critical_point2 = 3.3f;
                critical_point3 = 3.4f;
            }
        } else {  //女
            if (weight < 45) {
                critical_point1 = 1.7f;
                critical_point2 = 1.9f;
                critical_point3 = 2.5f;
            } else if (weight >= 45 && weight <= 60) {
                critical_point1 = 2.1f;
                critical_point2 = 2.3f;
                critical_point3 = 2.5f;
            } else if (weight > 60) {
                critical_point1 = 2.4f;
                critical_point2 = 2.6f;
                critical_point3 = 3.0f;
            }
        }
        bs.add(new FatLevelEntity(PPUtil.formatDouble1(critical_point1), mContext.getString(R.string.insufficient), 1));
        bs.add(new FatLevelEntity(PPUtil.formatDouble1(critical_point2), mContext.getString(R.string.standard), 2));
        bs.add(new FatLevelEntity(PPUtil.formatDouble1(critical_point3), mContext.getString(R.string.superior), 6));
        return bs;
    }

    public static ArrayList<FatLevelEntity> getProteinList(Context mContext) {
        ArrayList<FatLevelEntity> bs = new ArrayList<>();
        bs.add(new FatLevelEntity(16, mContext.getString(R.string.lower), 1));
        bs.add(new FatLevelEntity(20, mContext.getString(R.string.standard), 2));
        bs.add(new FatLevelEntity(60, mContext.getString(R.string.overHeight), 3));
        return bs;
    }

    public static ArrayList<FatLevelEntity> getObsLevelList(Context mContext) {
        ArrayList<FatLevelEntity> bs = new ArrayList<>();
        bs.add(new FatLevelEntity(18.5, mContext.getString(R.string.slim), 1));
        bs.add(new FatLevelEntity(24.9, mContext.getString(R.string.standard), 2));
        bs.add(new FatLevelEntity(29.9, mContext.getString(R.string.overLoad), 3));
        bs.add(new FatLevelEntity(35, mContext.getString(R.string.obesity1), 4));
        bs.add(new FatLevelEntity(40, mContext.getString(R.string.obesity2), 5));
        bs.add(new FatLevelEntity(90, mContext.getString(R.string.obesity3), 7));
        return bs;
    }

    public static ArrayList<FatLevelEntity> getSubFatList(Context mContext, int sex) {
        ArrayList<FatLevelEntity> bs = new ArrayList<>();
        if (sex == 1) {
            bs.add(new FatLevelEntity(8.6f, mContext.getString(R.string.lower), 1));
            bs.add(new FatLevelEntity(16.7f, mContext.getString(R.string.standard), 2));
            bs.add(new FatLevelEntity(20.7f, mContext.getString(R.string.overHeight), 3));
            bs.add(new FatLevelEntity(50.0f, mContext.getString(R.string.overOverHeight), 4));
        } else {
            bs.add(new FatLevelEntity(18.5f, mContext.getString(R.string.lower), 1));
            bs.add(new FatLevelEntity(26.7f, mContext.getString(R.string.standard), 2));
            bs.add(new FatLevelEntity(30.8f, mContext.getString(R.string.overHeight), 3));
            bs.add(new FatLevelEntity(60.0f, mContext.getString(R.string.overOverHeight), 4));
        }
        return bs;
    }

    public static ArrayList<FatLevelEntity> getBodyAgeList(Context mContext, int age) {
        ArrayList<FatLevelEntity> bs = new ArrayList<>();
        bs.add(new FatLevelEntity(age, mContext.getString(R.string.bodyAge_title_superior), 2));
        bs.add(new FatLevelEntity(age * 2, mContext.getString(R.string.bodyAge_title_overHeight), 3));
        return bs;
    }

    public static ArrayList<FatLevelEntity> getBodyScoreList(Context mContext) {
        ArrayList<FatLevelEntity> bs = new ArrayList<>();
        bs.add(new FatLevelEntity(70f, mContext.getString(R.string.notGood), 4));
        bs.add(new FatLevelEntity(80f, mContext.getString(R.string.general), 3));
        bs.add(new FatLevelEntity(90f, mContext.getString(R.string.well), 2));
        bs.add(new FatLevelEntity(100f, mContext.getString(R.string.veryWell), 6));
        return bs;
    }


}
