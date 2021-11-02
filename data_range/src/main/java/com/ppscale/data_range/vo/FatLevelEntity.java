package com.ppscale.data_range.vo;

import android.graphics.Color;

import java.io.Serializable;


/**
 * oAuthor:${谭良}
 * date:2018/5/15
 * time:15:13
 * projectName:NewiWellness
 */
public class FatLevelEntity implements Serializable {

    public final static int Color1 = 1;//偏瘦
    public final static int Color2 = 2;//正常
    public final static int Color3 = 3;//偏胖
    public final static int Color4 = 4;//肥胖
    public final static int Color5 = 5;//极胖
    public final static int Color6 = 6;//优
    public final static int Color7 = 7;//肥胖

    public double wight;
    public int colorType = Color1;
    public String levelName;
    public int bmiColor;

    public FatLevelEntity(double wight, String levelName, int colorType) {
        this.wight = wight;
        this.levelName = levelName;
        this.colorType = colorType;
        this.bmiColor = getBmiColor(colorType);
    }

    @Override
    public String toString() {
        return "BMIEntity{" +
                "wight=" + wight +
                ", bmiType=" + colorType +
                ", bmiName='" + levelName + '\'' +
                ", bmiColor=" + bmiColor +
                '}';
    }

    private int getBmiColor(int bmiType) {
        switch (bmiType) {
            case Color1:
                return Color.parseColor("#4592f8");//蓝色
            case Color2:
                return Color.parseColor("#48da7f");//浅绿色
            case Color3:
                return Color.parseColor("#fece2f");//黄色
            case Color4:
                return Color.parseColor("#f55453");//浅红色
            case Color5:
                return Color.parseColor("#ec3b30");//中级红
            case Color6:
                return Color.parseColor("#1aa646");//深绿色
            case Color7:
                return Color.parseColor("#ff0000");//纯红色
            default:
                return Color.parseColor("#4592f8");//蓝色
        }
    }
}
