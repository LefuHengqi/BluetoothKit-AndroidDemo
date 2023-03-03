package com.ppscale.data_range.vo;

import java.io.Serializable;
import java.util.List;

/**
 * oAuthor:${谭良}
 * date:2018/5/21
 * time:14:06
 * projectName:NewWeill
 */
public class BodyItem implements Serializable {
    //id
    private int id;
    //flag
    private String code;
    //类型名称 Type name
    private String name;
    //数值 numerical value
    private String dataVal;
    //具体数值,带单位转换 Specific value with unit conversion
    private float value;
    //当前范围名称 Current scope name
    private String level;
    //当前范围 Current scope
    private int levelIndex;
    //单位 unit
    private String unit;
    //描述 describe
    private String describe;
    //标准描述 Standard description
    public String side;
    //妙招 Clever move
    public String advice;
    //范围列表 Scope list
    private List<FatLevelEntity> levelEntitys;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataVal() {
        return dataVal;
    }

    public void setDataVal(String dataVal) {
        this.dataVal = dataVal;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    public void setLevelIndex(int levelIndex) {
        this.levelIndex = levelIndex;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public List<FatLevelEntity> getLevelEntitys() {
        return levelEntitys;
    }

    public void setLevelEntitys(List<FatLevelEntity> levelEntitys) {
        this.levelEntitys = levelEntitys;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    @Override
    public String toString() {
        return "name='" + name + ":\n" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", dataVal='" + dataVal + '\'' +
                ", value=" + value +
                ", level='" + level + '\'' +
                ", levelIndex=" + levelIndex +
                ", unit='" + unit + '\'' +
                ", describe='" + describe + '\'' +
                ", side='" + side + '\'' +
                ", advice='" + advice + '\'' +
                ", levelEntitys=" + levelEntitys;
    }
}
