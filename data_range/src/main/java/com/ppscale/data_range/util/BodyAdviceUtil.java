
package com.ppscale.data_range.util;

import android.content.Context;

import com.ppscale.data_range.R;
import com.ppscale.data_range.vo.FatDescribeEntity;

public class BodyAdviceUtil {

    public static FatDescribeEntity getWeightAdvice(int index) {
        return FatDescribeUtil.getFatAdvice().get(index);
    }

    public static FatDescribeEntity getHeartAdvice(int index) {
        return FatDescribeUtil.getHeartAdviceArray().get(index);
    }

    public static FatDescribeEntity getBmiAdvice(int index) {
        return FatDescribeUtil.getBmiAdviceArray().get(index);
    }

    public static FatDescribeEntity getBMRAdvice(int index) {
        return FatDescribeUtil.getBMRAdvice().get(index);
    }

    public static FatDescribeEntity getVFALAdvice(int index) {
        return FatDescribeUtil.getVFALAdvice().get(index);
    }

    public static FatDescribeEntity getWaterAdvice(int index) {
        return FatDescribeUtil.getWaterAdvice().get(index);
    }

    public static FatDescribeEntity getFatAdvice(int index) {
        return FatDescribeUtil.getFatAdvice().get(index);
    }

    public static FatDescribeEntity getMuscleAdvice(int index) {
        return FatDescribeUtil.getMuscleAdvice().get(index);
    }

    public static FatDescribeEntity getBoneAdvice(int index) {
        return FatDescribeUtil.getBoneAdvice().get(index);
    }

    public static FatDescribeEntity getProteinAdvice(int index) {
        return FatDescribeUtil.getProteinAdvice().get(index);
    }

    public static FatDescribeEntity getObsAdvice(int index) {
        return FatDescribeUtil.getObsAdvice().get(index);
    }

    public static FatDescribeEntity getSubFatAdvice(int index) {
        return FatDescribeUtil.getSubFatAdvice().get(index);

    }

    public static FatDescribeEntity getBodyAgeAdvice(int index) {
        return FatDescribeUtil.getBodyAgeAdvice().get(index);
    }

    public static FatDescribeEntity getBodyScoreAdvice(int index) {
        return FatDescribeUtil.getBodyScoreAdvice().get(index);
    }
}
