package com.ppscale.data_range.util;

import android.content.Context;

import com.peng.ppscale.util.PPUtil;
import com.peng.ppscale.vo.PPBodyType;
import com.ppscale.data_range.R;

import org.jetbrains.annotations.NotNull;


/**
 * oAuthor:${谭良}
 * date:2018/5/21
 * time:14:06
 * projectName:NewWeill
 */
public class StripedStand {

    private StripedStand() {
    }

    /**
     * 肥胖等级
     *
     * @param mContext
     * @param bmi
     * @return
     */
    public static String getObsLevelDetail(Context mContext, double bmi) {
        if (bmi >= 0 && bmi < 18.5) {
            return mContext.getString(R.string.slim);
        } else if (bmi < 24.9) {
            return mContext.getString(R.string.standard);
        } else if (bmi < 29.9) {
            return mContext.getString(R.string.overLoad);
        } else if (bmi >= 30 && bmi < 35) {
            return mContext.getString(R.string.obesity1);
        } else if (bmi < 40) {
            return mContext.getString(R.string.obesity2);
        } else if (bmi < 90) {
            return mContext.getString(R.string.obesity3);
        }
        return mContext.getString(R.string.obesity1);
    }

    /**
     * BMI相关的标准计算
     *
     * @param bmi
     * @return
     */
    public static int bmiString(double bmi) {
        double critical_point1 = 18.5;
        double critical_point2 = 24.0;
        double critical_point3 = 30.0;  //28.0
        if (bmi <= critical_point1) {
            return 0;//偏瘦
        } else if (bmi <= critical_point2) {
            return 1;//标准
        } else if (bmi <= critical_point3) {
            return 2;//偏胖
        } else {
            return 3;//胖
        }
    }

    /**
     * 水分的标准计算
     *
     * @param gender
     * @param waterPercent
     * @return
     */
    public static int water(int gender, double waterPercent) {
        double critical_point1;
        double critical_point2;
        double critical_point3 = 100;
        if (gender == 1) {  //男
            critical_point1 = 55;
            critical_point2 = 65;
        } else {  //女
            critical_point1 = 45;
            critical_point2 = 60;
        }
        if (waterPercent <= critical_point1) {
            return 0;  //不足
        } else if (waterPercent <= critical_point2) {
            return 1;  //标准
        } else {
            return 2;//优
        }
    }

    /**
     * 脂肪的标准计算
     *
     * @param gender
     * @param age
     * @param fatPercent
     * @return
     */
    public static int bftInt(int gender, int age, double fatPercent) {
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
        if (fatPercent <= critical_point1) {
            return 0;  //偏瘦
        } else if (fatPercent <= critical_point2) {
            return 1;  //健康
        } else if (fatPercent <= critical_point3) {
            return 2;  //偏胖
        } else if (fatPercent <= critical_point4) {
            return 3;  //胖
        } else {
            return 4;//超重
        }
    }

    /**
     * 肌肉相关标准计算
     *
     * @param gender
     * @param height
     * @param muscle
     * @return
     */
    public static int muscleString(int gender, double height, double muscle) {
        float critical_point1 = 1;
        float critical_point2 = 1;
        float critical_point3 = 20;
        if (gender == 1) {  //男
            if (height < 160) {
                critical_point1 = 38.5f;
                critical_point2 = 46.5f;
            } else if (height <= 170) {
                critical_point1 = 44f;
                critical_point2 = 52.4f;
            } else {
                critical_point1 = 49.4f;
                critical_point2 = 59.4f;
            }
        } else {  //女
            if (height < 150) {
                critical_point1 = 29.1f;
                critical_point2 = 34.7f;
            } else if (height >= 150 && height <= 160) {
                critical_point1 = 32.9f;
                critical_point2 = 37.5f;
            } else {
                critical_point1 = 36.5f;
                critical_point2 = 42.5f;
            }
        }

        if (muscle <= critical_point1) {
            return 0;  //不足
        } else if (muscle <= critical_point2) {
            return 1;  //标准
        } else {
            return 2;  //优
        }
    }

    //BMR
    public static int bmrInt(int gender, int age, double weight, double bmr) {
        float critical_point1 = 1261;
        if (age <= 29) {
            if (gender == 1) {
                critical_point1 = PPUtil.keepPoint1(weight * 24.0f);
            } else {
                critical_point1 = PPUtil.keepPoint1(weight * 23.6f);
            }
        } else if (age <= 49) {
            if (gender == 1) {
                critical_point1 = PPUtil.keepPoint1(weight * 22.3f);
            } else {
                critical_point1 = PPUtil.keepPoint1(weight * 21.7f);
            }
        } else {
            if (gender == 1) {
                critical_point1 = PPUtil.keepPoint1(weight * 21.5f);
            } else {
                critical_point1 = PPUtil.keepPoint1(weight * 20.7f);
            }
        }
        if (bmr <= critical_point1) {
            return 0;//偏低
        } else {
            return 1;//优
        }
    }

    //BMR
    public static float bmrInt1(int gender, int age, double weight) {
        float critical_point1 = 1261;
        if (age <= 29) {
            if (gender == 1) {
                critical_point1 = PPUtil.keepPoint1(weight * 24.0f);
            } else {
                critical_point1 = PPUtil.keepPoint1(weight * 23.6f);
            }
        } else if (age <= 49) {
            if (gender == 1) {
                critical_point1 = PPUtil.keepPoint1(weight * 22.3f);
            } else {
                critical_point1 = PPUtil.keepPoint1(weight * 21.7f);
            }
        } else {
            if (gender == 1) {
                critical_point1 = PPUtil.keepPoint1(weight * 21.5f);
            } else {
                critical_point1 = PPUtil.keepPoint1(weight * 20.7f);
            }
        }
        return critical_point1;
    }

    /**
     * 内脏脂肪标准的计算
     *
     * @param visceralfat
     * @return
     */
    public static int visceralFatInt(double visceralfat) {
        double critical_point1 = 9.0;
        double critical_point2 = 14.0;
        double critical_point3 = 16;
        if (visceralfat <= critical_point1) {
            return 0;//标准
        } else if (visceralfat <= critical_point2) {
            return 1;//警惕
        } else {
            return 2;//危险
        }
    }

    //骨量
    public static int boneInt(int gender, double weight, double bone) {
        double critical_point1 = 2.4;
        double critical_point2 = 2.6;
        if (gender == 1) {  //男
            if (weight < 60) {
                critical_point1 = 2.4;
                critical_point2 = 2.6;
            } else if (weight >= 60 && weight <= 75) {
                critical_point1 = 2.8;
                critical_point2 = 3.0;
            } else if (weight > 75) {
                critical_point1 = 3.1;
                critical_point2 = 3.3;
            }
        } else {
            // 女
            if (weight < 45) {
                critical_point1 = 1.7;
                critical_point2 = 1.9;
            } else if (weight <= 60) {
                critical_point1 = 2.1;
                critical_point2 = 2.3;
            } else {
                critical_point1 = 2.4;
                critical_point2 = 2.6;
            }
        }
        if (bone < critical_point1) {
            return 0;//不足
        } else if (bone <= critical_point2) {
            return 1;//标准
        } else {
            return 2;//优
        }
    }

    /**
     * 蛋白质
     *
     * @param protein
     * @return
     */
    public static int proteinInt(double protein) {
        if (protein < 16) {
            return 0;//过低
        } else if (protein <= 20) {
            return 1;//标准
        } else {
            return 2;//过高
        }
    }

    public static int obsInt02(double bmi) {
        if (bmi >= 0 && bmi < 18.5) {
            return 1;  //偏瘦
        } else if (bmi < 24.9) {
            return 2;  //标准
        } else if (bmi < 29.9) {
            return 3;  //偏胖
        } else {
            return 4;  //胖
        }
    }

    /**
     * 肥胖等级
     *
     * @param bmi
     * @return
     */
    public static int obsInt(double bmi) {
        if (bmi >= 0 && bmi < 18.5) {
            return 0;  //偏瘦
        } else if (bmi < 24.9) {
            return 1;  //标准
        } else if (bmi < 29.9) {
            return 2;  //超重
        } else if (bmi >= 30 && bmi < 35) {
            return 3;  //肥胖1级
        } else if (bmi < 40) {
            return 4;  //肥胖2级
        } else if (bmi < 90) {
            return 5;  //肥胖3级
        }
        return 1;
    }

    /**
     * 皮下脂肪相关标准的计算
     */
    public static int subFatInt(int sex, double bmi) {
        double critical_point1 = 8.6;
        double critical_point2 = 16.7;
        double critical_point3 = 20.7;
        double w_point1 = 18.5;
        double w_point2 = 26.7;
        double w_point3 = 30.8;
        if (sex == 1) {
            if (bmi < critical_point1) {
                return 0;//偏瘦
            } else if (bmi < critical_point2) {
                return 1;//标准
            } else if (bmi < critical_point3) {
                return 2;//偏高
            } else {
                return 3;//严重偏高
            }
        } else {
            if (bmi < w_point1) {
                return 0;//偏瘦
            } else if (bmi < w_point2) {
                return 1;//标准
            } else if (bmi < w_point3) {
                return 2;//偏高
            } else {
                return 3;//严重偏高
            }
        }
    }

    /**
     * 身体年龄相关标准的计算
     */
    public static int bodyAgeInt(int age, double bodyAge) {
        if (bodyAge < age) {
            return 0;//忧
        } else {
            return 1;//偏高
        }
    }

    /**
     * 身体年龄相关标准的计算
     */
    public static int bodyScoreInt(double bodyScore) {
        double critical_point1 = 70;
        double critical_point2 = 80;
        double critical_point3 = 90;
        if (bodyScore < critical_point1) {
            return 0;//不好
        } else if (bodyScore < critical_point2) {
            return 1;//一般
        } else if (bodyScore < critical_point3) {
            return 2;//良好
        } else {
            return 3;//非常好
        }
    }

    public static int getBmrValue(int height, double weight, int sex, int age) {
        double bmrValue = 0;
        if (age < 15) {
            if (sex == 1) {
                switch (age) {
                    case 1:
                        bmrValue = 900;
                        break;
                    case 2:
                        bmrValue = 1100;
                        break;
                    case 3:
                        bmrValue = 1250;
                        break;
                    case 4:
                        bmrValue = 1300;
                        break;
                    case 5:
                    case 6:
                        bmrValue = 1400;
                        break;
                    case 7:
                        bmrValue = 1500;
                        break;
                    case 8:
                        bmrValue = 1650;
                        break;
                    case 9:
                        bmrValue = 1750;
                        break;
                    case 10:
                        bmrValue = 1800;
                        break;
                    case 11:
                    case 12:
                    case 13:
                        bmrValue = 2050;
                        break;
                    case 14:
                        bmrValue = 2500;
                        break;
                }
            } else {
                switch (age) {
                    case 1:
                        bmrValue = 800;
                        break;
                    case 2:
                        bmrValue = 1000;
                        break;
                    case 3:
                        bmrValue = 1200;
                        break;
                    case 4:
                        bmrValue = 1250;
                        break;
                    case 5:
                        bmrValue = 1300;
                        break;
                    case 6:
                        bmrValue = 1250;
                        break;
                    case 7:
                        bmrValue = 1350;
                        break;
                    case 8:
                        bmrValue = 1450;
                        break;
                    case 9:
                        bmrValue = 1550;
                        break;
                    case 10:
                        bmrValue = 1650;
                        break;
                    case 11:
                    case 12:
                    case 13:
                        bmrValue = 1800;
                        break;
                    case 14:
                        bmrValue = 2000;
                        break;
                }
            }
        } else {
            if (sex == 1) {
                bmrValue = 10 * weight + 6.25 * height - 5 * age + 5;
                bmrValue = bmrValue * 1.2;
            } else {
                bmrValue = 10 * weight + 6.25 * height - 5 * age - 161;
                bmrValue = bmrValue * 1.3;
            }
        }
        return (int) (bmrValue);
    }

    /**
     * 体重的标准计算
     *
     * @param gender
     * @param height
     * @param weight
     * @return
     */
    public static String weightLevel(Context mContext, int gender, int height, double weight) {
        double standard_weight;
        double critical_point1;
        double critical_point2;
        if (gender == 1) {
            standard_weight = (height - 80) * 0.7;
        } else {
            standard_weight = (height - 70) * 0.6;
        }
        critical_point1 = standard_weight - standard_weight * 0.1f;
        critical_point2 = standard_weight + standard_weight * 0.1f;
        if (weight < critical_point1) {
            return mContext.getString(R.string.lower);//偏低
        } else if (weight < critical_point2) {
            return mContext.getString(R.string.standard);//标准
        } else {
            return mContext.getString(R.string.overHeight);//偏高
        }
    }

    public static int weightLevel(int gender, int height, double weight) {
        double standard_weight;
        double critical_point1;
        double critical_point2;
        if (gender == 1) {
            standard_weight = (height - 80) * 0.7f;
        } else {
            standard_weight = (height - 70) * 0.6f;
        }
        critical_point1 = standard_weight - standard_weight * 0.1f;
        critical_point2 = standard_weight + standard_weight * 0.1f;
        if (weight < critical_point1) {
            return 0;//偏低
        } else if (weight < critical_point2) {
            return 1;//标准
        } else {
            return 2;//偏高
        }
    }

    /**
     * 心率的标准计算
     *
     * @param heartRate
     * @return
     */
    public static String heartRateLevel(Context mContext, int heartRate) {
        float critical_point1 = 20;//20＜40≥  60≥  100≥  160≥ 180
        float critical_point2 = 40;
        float critical_point3 = 60;
        float critical_point4 = 100;
        float critical_point5 = 160;
        float critical_point6 = 180;
        if (heartRate < critical_point2) {
            return mContext.getString(R.string.heart_leve1_name);//过低
        } else if (heartRate < critical_point3) {
            return mContext.getString(R.string.heart_leve2_name);//偏低
        } else if (heartRate < critical_point4) {
            return mContext.getString(R.string.heart_leve3_name);//正常
        } else if (heartRate < critical_point5) {
            return mContext.getString(R.string.heart_leve4_name);//偏高
        } else {
            return mContext.getString(R.string.heart_leve5_name);//过高
        }
    }

    /**
     * 心率的标准计算
     *
     * @param heartRate
     * @return
     */
    public static int heartRateLevelInt(int heartRate) {
        float critical_point1 = 20;//20＜40≥  60≥  100≥  160≥ 180
        float critical_point2 = 40;
        float critical_point3 = 60;
        float critical_point4 = 100;
        float critical_point5 = 160;
        float critical_point6 = 180;
        if (heartRate < critical_point2) {
            return 0;//过低
        } else if (heartRate < critical_point3) {
            return 1;//偏低
        } else if (heartRate < critical_point4) {
            return 2;//正常
        } else if (heartRate < critical_point5) {
            return 3;//偏高
        } else {
            return 4;//过高
        }
    }

    public static String bmiLevelBaby(Context context, double bmi, int age) {
        if (age <= 5) {
            return bmiLevelBabyLess5(context, bmi);
        }
        return bmiLevelBabyGT5(context, bmi);
    }

    /**
     * BMI相关的标准计算
     *
     * @param bmi
     * @return
     */
    public static String bmiLevel(Context mContext, double bmi) {
        double critical_point1 = 18.5;
        double critical_point2 = 24.0;
        double critical_point3 = 30.0;  //28.0
        if (bmi <= critical_point1) {
            return mContext.getString(R.string.slim);//偏瘦
        } else if (bmi > critical_point1 && bmi <= critical_point2) {
            return mContext.getString(R.string.health);//健康
        } else if (bmi > critical_point2 && bmi <= critical_point3) {
            return mContext.getString(R.string.overWeight);//轻度脂肪堆积
        } else {
            return mContext.getString(R.string.overOverWeight);//严重脂肪堆积
        }
    }

    public static String bmiLevelBabyLess5(Context mContext, double bmi) {
        double critical_point1 = 16;
        double critical_point2 = 19;
        double critical_point3 = 23;  //28.0
        if (bmi <= critical_point1) {
            return mContext.getString(R.string.slim);//偏瘦
        } else if (bmi > critical_point1 && bmi <= critical_point2) {
            return mContext.getString(R.string.health);//健康
        } else if (bmi > critical_point2 && bmi <= critical_point3) {
            return mContext.getString(R.string.overWeight);//轻度脂肪堆积
        } else {
            return mContext.getString(R.string.overOverWeight);//严重脂肪堆积
        }
    }

    public static String bmiLevelBabyGT5(Context mContext, double bmi) {
        double critical_point1 = 18.5;
        double critical_point2 = 24.0;
        double critical_point3 = 28.0;  //28.0
        if (bmi <= critical_point1) {
            return mContext.getString(R.string.slim);//偏瘦
        } else if (bmi > critical_point1 && bmi <= critical_point2) {
            return mContext.getString(R.string.health);//健康
        } else if (bmi > critical_point2 && bmi <= critical_point3) {
            return mContext.getString(R.string.overWeight);//轻度脂肪堆积
        } else {
            return mContext.getString(R.string.overOverWeight);//严重脂肪堆积
        }
    }

    /**
     * BMR相关的标准计算
     *
     * @param gender
     * @param age
     * @param weight
     * @param bmr
     * @return
     */
    public static String bmrLevel(Context mContext, int gender, int age, double weight, float bmr) {
        float critical_point1;
        if (age <= 29) {
            if (gender == 1) {
                critical_point1 = PPUtil.keepPoint1(weight * 24.0f);
            } else {
                critical_point1 = PPUtil.keepPoint1(weight * 23.6f);
            }
        } else if (age >= 30 && age <= 49) {
            if (gender == 1) {
                critical_point1 = PPUtil.keepPoint1(weight * 22.3f);
            } else {
                critical_point1 = PPUtil.keepPoint1(weight * 21.7f);
            }
        } else {
            if (gender == 1) {
                critical_point1 = PPUtil.keepPoint1(weight * 21.5f);
            } else {
                critical_point1 = PPUtil.keepPoint1(weight * 20.7f);
            }
        }
        if (bmr <= critical_point1) {
            return mContext.getString(R.string.lower);//偏低
        } else {
            return mContext.getString(R.string.superior);//优
        }
    }

    /**
     * 脂肪的标准计算
     *
     * @param gender
     * @param age
     * @param fatPercent
     * @return
     */
    public static String bftLevel(Context mContext, int gender, int age, double fatPercent) {
        double critical_point1 = 0;
        double critical_point2 = 0;
        double critical_point3 = 0;
        double critical_point4 = 0;
        if (gender == 1) {// 男
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
        } else {// 女
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
        if (fatPercent <= critical_point1) {
            return mContext.getString(R.string.slim);//偏瘦
        } else if (fatPercent <= critical_point2) {
            return mContext.getString(R.string.health);//健康
        } else if (fatPercent <= critical_point3) {
            return mContext.getString(R.string.overWeight);//偏胖
        } else if (fatPercent <= critical_point4) {
            return mContext.getString(R.string.overOverWeight);//胖
        } else {
            return mContext.getString(R.string.overOverWeight);//胖
        }
    }

    /**
     * 脂肪的标准计算
     *
     * @param gender
     * @param age
     * @param fatPercent
     * @return
     */
    public static String bftLevelOld(Context mContext, int gender, int age, double fatPercent) {
        double critical_point1 = 0;
        double critical_point2 = 0;
        double critical_point3 = 0;
        double critical_point4 = 0;
        double critical_point5 = 100;
        if (gender == 1) {  //男
            if (age < 40) {
                critical_point1 = 10;
                critical_point2 = 16;
                critical_point3 = 21;
                critical_point4 = 26;
            } else if (age >= 40 && age < 60) {
                critical_point1 = 11;
                critical_point2 = 17;
                critical_point3 = 22;
                critical_point4 = 27;
            } else if (age >= 60) {
                critical_point1 = 13;
                critical_point2 = 19;
                critical_point3 = 24;
                critical_point4 = 29;
            }
        } else {  //女
            if (age < 40) {
                critical_point1 = 20;
                critical_point2 = 27;
                critical_point3 = 34;
                critical_point4 = 39;
            } else if (age >= 40 && age < 60) {
                critical_point1 = 21;
                critical_point2 = 28;
                critical_point3 = 35;
                critical_point4 = 40;
            } else if (age >= 60) {
                critical_point1 = 22;
                critical_point2 = 29;
                critical_point3 = 36;
                critical_point4 = 41;
            }
        }
        if (fatPercent <= critical_point1) {
            return mContext.getString(R.string.slim); //偏瘦
        } else if (fatPercent > critical_point1 && fatPercent <= critical_point2) {
            return mContext.getString(R.string.health); //健康
        } else if (fatPercent > critical_point2 && fatPercent <= critical_point3) {
            return mContext.getString(R.string.overWeight); //偏胖
        } else if (fatPercent > critical_point3 && fatPercent <= critical_point4) {
            return mContext.getString(R.string.overOverWeight); //胖
        } else {
            return mContext.getString(R.string.overLoad); //超重
        }
    }

    /**
     * 水分的标准计算
     *
     * @param gender
     * @param waterPercent
     * @return
     */
    public static String waterLevel(Context mContext, int gender, double waterPercent) {
        double critical_point1;
        double critical_point2;
        double critical_point3 = 100;
        if (gender == 1) {  //男
            critical_point1 = 55;
            critical_point2 = 65;
        } else {  //女
            critical_point1 = 45;
            critical_point2 = 60;
        }
        if (waterPercent <= critical_point1) {
            return mContext.getString(R.string.lower); //不足
        } else if (waterPercent <= critical_point2) {
            return mContext.getString(R.string.standard); //标准
        } else {
            return mContext.getString(R.string.superior); //优秀
        }
    }

    /**
     * 骨量
     *
     * @param gender
     * @param weight
     * @param bone
     * @return
     */
    public static String boneLevel(Context mContext, int gender, double weight, double bone) {
        double critical_point1 = 2.4;
        double critical_point2 = 2.6;
        if (gender == 1) {
            // 男
            if (weight < 60) {
                critical_point1 = 2.4;
                critical_point2 = 2.6;
            } else if (weight >= 60 && weight <= 75) {
                critical_point1 = 2.8;
                critical_point2 = 3.0;
            } else if (weight > 75) {
                critical_point1 = 3.1;
                critical_point2 = 3.3;
            }
        } else {
            // 女
            if (weight < 45) {
                critical_point1 = 1.7;
                critical_point2 = 1.9;
            } else if (weight >= 45 && weight <= 60) {
                critical_point1 = 2.1;
                critical_point2 = 2.3;
            } else if (weight > 60) {
                critical_point1 = 2.4;
                critical_point2 = 2.6;
            }
        }  //屏幕宽度
        if (bone < critical_point1) {
            return mContext.getString(R.string.insufficient);//不足
        } else if (bone < critical_point2) {
            return mContext.getString(R.string.standard);//标准
        } else {
            return mContext.getString(R.string.superior);//优秀
        }
    }

    /**
     * 肌肉相关标准计算
     *
     * @param gender
     * @param height
     * @param muscle
     * @return
     */
    public static String muscleLevel(Context mContext, int gender, double height, double muscle) {
        float critical_point1 = 1;
        float critical_point2 = 1;
        float critical_point3 = 20;
        if (gender == 1) {  //男
            if (height < 160) {
                critical_point1 = 38.5f;
                critical_point2 = 46.5f;
            } else if (height <= 170) {
                critical_point1 = 44f;
                critical_point2 = 52.4f;
            } else {
                critical_point1 = 49.4f;
                critical_point2 = 59.4f;
            }
        } else {  //女
            if (height < 150) {
                critical_point1 = 29.1f;
                critical_point2 = 34.7f;
            } else if (height <= 160) {
                critical_point1 = 32.9f;
                critical_point2 = 37.5f;
            } else {
                critical_point1 = 36.5f;
                critical_point2 = 42.5f;
            }
        }
        if (muscle <= critical_point1) {
            return mContext.getString(R.string.insufficient);  //不足
        } else if (muscle <= critical_point2) {
            return mContext.getString(R.string.standard);  //标准
        } else {
            return mContext.getString(R.string.superior);  //优
        }
    }

    /**
     * 内脏脂肪标准的计算
     *
     * @param visceralfat
     * @return
     */
    public static String visceralLevel(Context mContext, double visceralfat) {
        double critical_point1 = 9.0;
        double critical_point2 = 14.0;
        if (visceralfat <= critical_point1) {
            return mContext.getString(R.string.standard);//标准
        } else if (visceralfat <= critical_point2) {
            return mContext.getString(R.string.alter);//警惕
        } else {
            return mContext.getString(R.string.danger);//危险
        }
    }

    /**
     * 皮下脂肪相关标准的计算
     */
    public static String subFatLevel(Context mContext, int sex, double subFat) {
        float critical_point1 = 8.6f;
        float critical_point2 = 16.7f;
        float critical_point3 = 20.7f;
        float w_point1 = 18.5f;
        float w_point2 = 26.7f;
        float w_point3 = 30.8f;
        if (sex == 1) {
            if (subFat < critical_point1) {
                return mContext.getString(R.string.lower);//偏低
            } else if (subFat < critical_point2) {
                return mContext.getString(R.string.standard);//标准
            } else if (subFat < critical_point3) {
                return mContext.getString(R.string.overHeight);//偏高
            } else {
                return mContext.getString(R.string.overOverHeight);//严重偏高
            }
        } else {
            if (subFat < w_point1) {
                return mContext.getString(R.string.lower);//偏低
            } else if (subFat < w_point2) {
                return mContext.getString(R.string.standard);//标准
            } else if (subFat < w_point3) {
                return mContext.getString(R.string.overHeight);//偏高
            } else {
                return mContext.getString(R.string.overOverHeight);//严重偏高
            }
        }
    }

    /**
     * 身体年龄相关标准的计算
     */
    public static String bodyAgeLevel(Context mContext, int age, double bodyAge) {
        float critical_point1 = age;
        if (bodyAge < critical_point1) {
            return mContext.getString(R.string.bodyAge_title_superior);//优秀
        } else {
            return mContext.getString(R.string.bodyAge_title_overHeight);//偏高
        }
    }

    /**
     * 身体年龄相关标准的计算
     */
    public static String bodyScoreLevel(Context mContext, double bodyScore) {
        float critical_point1 = 70f;
        float critical_point2 = 80f;
        float critical_point3 = 90f;
        if (bodyScore < critical_point1) {
            return mContext.getString(R.string.notGood);//不好
        } else if (bodyScore < critical_point2) {
            return mContext.getString(R.string.general);//一般
        } else if (bodyScore < critical_point3) {
            return mContext.getString(R.string.well);//良好
        } else {
            return mContext.getString(R.string.veryWell);//非常好
        }
    }

    /**
     * 获取身体类型
     * 0 偏瘦型
     * 1 偏瘦肌肉型
     * 2 肌肉发达型
     * 3 缺乏运动型
     * 4 标准型
     * 5 标准肌肉型
     * 6 浮肿肥胖型
     * 7 偏胖肌肉型
     * 8 肌肉型偏胖
     *
     * @param type
     * @return
     */
    public static String getBodyfatType(Context mContext, PPBodyType type) {
        switch (type) {
            case LF_BODY_TYPE_THIN:             //0 偏瘦型
                return mContext.getString(R.string.bodySlim);
            case LF_BODY_TYPE_THIN_MUSCLE:      //1 偏瘦肌肉型
                return mContext.getString(R.string.bodySlimMuscle);
            case LF_BODY_TYPE_MUSCULAR:         //2 肌肉发达型
                return mContext.getString(R.string.bodyMoreMuscle);
            case LF_BODY_TYPE_LACK_EXERCISE:   //3 缺乏运动型
                return mContext.getString(R.string.bodyLittleExercise);
            case LF_BODY_TYPE_STANDARD:         //4 标准型
                return mContext.getString(R.string.bodyStandard);
            case LF_BODY_TYPE_STANDARD_MUSCLE:   //5 标准肌肉型
                return mContext.getString(R.string.bodyStandardMuscle);
            case LF_BODY_TYPE_OBESE_FAT:          //6 浮肿肥胖型
                return mContext.getString(R.string.bodyMoreObsity);
            case LF_BODY_TYPE_FAT_MUSCLE:       //7 偏胖肌肉型
                return mContext.getString(R.string.bodyLittleMuscle);
            case LF_BODY_TYPE_MUSCLE_FAT:        //8 肌肉型偏胖
                return mContext.getString(R.string.bodyFatMuscle);
        }
        return mContext.getString(R.string.bodyUndefine);
    }

    /**
     * Protein
     *
     * @param
     * @return
     */
    public static String getProtein(Context mContext, double bmi) {
        if (bmi < 16) {
            return mContext.getString(R.string.lower);
        } else if (bmi <= 20) {
            return mContext.getString(R.string.standard);
        } else {
            return mContext.getString(R.string.overHeight);
        }
    }

}
