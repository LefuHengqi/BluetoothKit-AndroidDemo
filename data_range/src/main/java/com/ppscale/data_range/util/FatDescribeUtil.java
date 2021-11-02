package com.ppscale.data_range.util;

import android.content.Context;

import com.ppscale.data_range.R;
import com.ppscale.data_range.vo.FatDescribeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述和妙招
 */
public class FatDescribeUtil {

    public static List<FatDescribeEntity> getWeightAdviceArray() {
        List<FatDescribeEntity> describeEntities = new ArrayList<>();
        describeEntities.add(new FatDescribeEntity(R.string.weight_low, -1));
        describeEntities.add(new FatDescribeEntity(R.string.weight_standard, -1));
        describeEntities.add(new FatDescribeEntity(R.string.weight_high, -1));
        return describeEntities;
    }

    public static List<FatDescribeEntity> getHeartAdviceArray() {
        List<FatDescribeEntity> describeEntities = new ArrayList<>();
        describeEntities.add(new FatDescribeEntity(R.string.heart_leve1_evaluation, R.string.heart_leve1_suggestion));
        describeEntities.add(new FatDescribeEntity(R.string.heart_leve2_evaluation, R.string.heart_leve2_suggestion));
        describeEntities.add(new FatDescribeEntity(R.string.heart_leve3_evaluation, R.string.heart_leve3_suggestion));
        describeEntities.add(new FatDescribeEntity(R.string.heart_leve4_evaluation, R.string.heart_leve4_suggestion));
        describeEntities.add(new FatDescribeEntity(R.string.heart_leve5_evaluation, R.string.heart_leve5_suggestion));
        return describeEntities;
    }

    public static List<FatDescribeEntity> getBmiAdviceArray() {
        List<FatDescribeEntity> describeEntities = new ArrayList<>();
        describeEntities.add(new FatDescribeEntity(R.string.bmi_low_side, R.string.bmi_low_side_bestWays));
        describeEntities.add(new FatDescribeEntity(R.string.bmi_normal, R.string.bmi_normal_bestWays));
        describeEntities.add(new FatDescribeEntity(R.string.bmi_high_side, R.string.bmi_high_side_bestWays));
        describeEntities.add(new FatDescribeEntity(R.string.bmi_too_high, R.string.bmi_too_high_bestWays));
        return describeEntities;
    }

    public static List<FatDescribeEntity> getBMRAdvice() {
        List<FatDescribeEntity> describeEntities = new ArrayList<>();
        describeEntities.add(new FatDescribeEntity(R.string.bmr_low_side, R.string.bmr_low_side_bestWays));
        describeEntities.add(new FatDescribeEntity(R.string.bmr_normal, R.string.bmr_normal_bestWays));
        return describeEntities;
    }

    public static List<FatDescribeEntity> getVFALAdvice() {
        List<FatDescribeEntity> describeEntities = new ArrayList<>();
        describeEntities.add(new FatDescribeEntity(R.string.visceralFat_normal, R.string.visceralFat_normal_bestWays));
        describeEntities.add(new FatDescribeEntity(R.string.visceralFat_high_side, R.string.visceralFat_too_high_bestWays));
        describeEntities.add(new FatDescribeEntity(R.string.visceralFat_too_high, R.string.visceralFat_too_high_bestWays));
        return describeEntities;
    }

    public static List<FatDescribeEntity> getWaterAdvice() {
        List<FatDescribeEntity> describeEntities = new ArrayList<>();
        describeEntities.add(new FatDescribeEntity(R.string.water_low_side, R.string.water_low_side_bestWays));
        describeEntities.add(new FatDescribeEntity(R.string.water_normal, R.string.water_normal_bestWays));
        describeEntities.add(new FatDescribeEntity(R.string.water_normal, R.string.water_normal_bestWays));
        return describeEntities;
    }

    public static List<FatDescribeEntity> getFatAdvice() {
        List<FatDescribeEntity> describeEntities = new ArrayList<>();
        describeEntities.add(new FatDescribeEntity(R.string.fat_low_side, R.string.fat_low_side_bestWays));
        describeEntities.add(new FatDescribeEntity(R.string.fat_normal, R.string.fat_normal_bestWays));
        describeEntities.add(new FatDescribeEntity(R.string.fat_high_side, R.string.fat_high_side_bestWays));
        describeEntities.add(new FatDescribeEntity(R.string.fat_too_chaozhong, R.string.fat_too_high_bestWays));
        describeEntities.add(new FatDescribeEntity(R.string.fat_too_chaozhong, R.string.fat_too_high_bestWays));
        return describeEntities;
    }

    public static List<FatDescribeEntity> getMuscleAdvice() {
        List<FatDescribeEntity> describeEntities = new ArrayList<>();
        describeEntities.add(new FatDescribeEntity(R.string.muscle_low_side, R.string.muscle_low_side_bestWays));
        describeEntities.add(new FatDescribeEntity(R.string.muscle_normal, R.string.muscle_normal_bestWays));
        describeEntities.add(new FatDescribeEntity(R.string.muscle_you, R.string.muscle_you_bestWays));
        return describeEntities;
    }

    public static List<FatDescribeEntity> getBoneAdvice() {
        List<FatDescribeEntity> describeEntities = new ArrayList<>();
        describeEntities.add(new FatDescribeEntity(R.string.boneAge_low_side, R.string.boneAge_low_side_bestWays));
        describeEntities.add(new FatDescribeEntity(R.string.boneAge_normal, R.string.boneAge_normal_bestWays));
        describeEntities.add(new FatDescribeEntity(R.string.boneAge_optimal, R.string.boneAge_optimal_bestWays));
        return describeEntities;
    }

    public static List<FatDescribeEntity> getProteinAdvice() {
        List<FatDescribeEntity> describeEntities = new ArrayList<>();
        describeEntities.add(new FatDescribeEntity(R.string.protein_tooLow, R.string.protein_tooLow_crop));
        describeEntities.add(new FatDescribeEntity(R.string.protein_normal, R.string.protein_normal_crop));
        describeEntities.add(new FatDescribeEntity(R.string.protein_tooHigh, R.string.protein_tooHigh_crop));
        return describeEntities;
    }

    public static List<FatDescribeEntity> getObsAdvice() {
        List<FatDescribeEntity> describeEntities = new ArrayList<>();
        describeEntities.add(new FatDescribeEntity(R.string.obs1_describe, R.string.obs1_crop));
        describeEntities.add(new FatDescribeEntity(R.string.obs2_describe, R.string.obs2_crop));
        describeEntities.add(new FatDescribeEntity(R.string.obs3_describe, R.string.obs3_crop));
        describeEntities.add(new FatDescribeEntity(R.string.obs4_describe, R.string.obs4_crop));
        describeEntities.add(new FatDescribeEntity(R.string.obs5_describe, R.string.obs5_crop));
        describeEntities.add(new FatDescribeEntity(R.string.obs6_describe, R.string.obs6_crop));
        return describeEntities;
    }

    public static List<FatDescribeEntity> getSubFatAdvice() {
        List<FatDescribeEntity> describeEntities = new ArrayList<>();
        describeEntities.add(new FatDescribeEntity(R.string.subFat_Low_describe, R.string.subFat_Low_crop));
        describeEntities.add(new FatDescribeEntity(R.string.subFat_standard_describe, R.string.subFat_standard_crop));
        describeEntities.add(new FatDescribeEntity(R.string.subFat_higher_describe, R.string.subFat_higher_crop));
        describeEntities.add(new FatDescribeEntity(R.string.subFat_overHigh_describe, R.string.subFat_overHigh_crop));
        describeEntities.add(new FatDescribeEntity(R.string.obs5_describe, R.string.obs5_crop));
        describeEntities.add(new FatDescribeEntity(R.string.obs6_describe, R.string.obs6_crop));
        return describeEntities;
    }

    public static List<FatDescribeEntity> getBodyAgeAdvice() {
        List<FatDescribeEntity> describeEntities = new ArrayList<>();
        describeEntities.add(new FatDescribeEntity(R.string.bodyAge_superior_describe, R.string.bodyAge_superior_coup));
        describeEntities.add(new FatDescribeEntity(R.string.bodyAge_overHeight_describe, R.string.bodyAge_overHeight_coup));
        return describeEntities;
    }

    public static List<FatDescribeEntity> getBodyScoreAdvice() {
        List<FatDescribeEntity> describeEntities = new ArrayList<>();
        describeEntities.add(new FatDescribeEntity(R.string.bodyGrade_notGood_describe, R.string.bodyGrade_notGood_coup));
        describeEntities.add(new FatDescribeEntity(R.string.bodyGrade_general_describe, R.string.bodyGrade_general_coup));
        describeEntities.add(new FatDescribeEntity(R.string.bodyGrade_well_describe, R.string.bodyGrade_well_coup));
        describeEntities.add(new FatDescribeEntity(R.string.bodyGrade_veryWell_describe, R.string.bodyGrade_veryWell_coup));
        return describeEntities;
    }


}
