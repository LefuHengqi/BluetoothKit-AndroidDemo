package com.lefu.bodyindex

import com.peng.ppscale.util.PPUtil
import com.peng.ppscale.vo.PPScaleDefine


/**
 *    @author : whs
 *    e-mail : haisilen@163.com
 *    date   : 2023/3/25 15:29
 *    desc   :身体指标工具类的扩展
 */


/**
 * 体重的标准计算
 *
 * @param gender
 * @param height
 * @param weight
 * @return 第一个是标准文本indexGradeStr
 * 第二个是颜色值 indexGradeColor16Str
 * 第三个是指标的评价indexEevaluation
 * 第四个是建议 indexSuggestion
 */
fun weightLevel(gender: Int, height: Int, weight: Double): MutableList<String> {
    val standard_weight: Float
    val critical_point1: Float
    val critical_point2: Float
    standard_weight = if (gender == 1) {
        (height - 80) * 0.7f
    } else {
        (height - 70) * 0.6f
    }
    critical_point1 = standard_weight - standard_weight * 0.1f
    critical_point2 = standard_weight + standard_weight * 0.1f
    return if (weight < critical_point1) {
        arrayListOf<String>(
            "Weight_leve1_name",
            "#F5A623",
            "Weight_leve1_evaluation",
            "Weight_leve1_suggestion",
            "1"
        )
        //偏低
    } else if (weight < critical_point2) {
        arrayListOf<String>(
            "Weight_leve2_name",
            "#14CCAD",
            "Weight_leve2_evaluation",
            "Weight_leve2_suggestion",
            "2"
        )
        //标准
    } else {
        arrayListOf<String>(
            "Weight_leve3_name",
            "#F43F31",
            "Weight_leve3_evaluation",
            "Weight_leve3_suggestion",
            "3"
        )
        //偏高
    }

}

/**
 * 心率的标准计算
 *
 * @param heartRate
 * @return
 */
fun heartRateLevel(heartRate: Int): MutableList<String> {
    val critical_point1 = 20f //20＜40≥  60≥  100≥  160≥ 180
    val critical_point2 = 40f
    val critical_point3 = 60f
    val critical_point4 = 100f
    val critical_point5 = 160f
    val critical_point6 = 180f
    return if (heartRate < critical_point2) {
        arrayListOf<String>(
            "heart_leve1_name",
            "#C2831C",
            "heart_leve1_evaluation",
            "heart_leve1_suggestion",
            "1"
        ) //过低

    } else if (heartRate < critical_point3) {
        arrayListOf<String>(
            "heart_leve4_name",
            "#F5A623",
            "heart_leve2_evaluation",
            "heart_leve2_suggestion",
            "2"
        ) //偏低
    } else if (heartRate < critical_point4) {
        //正常
        arrayListOf<String>(
            "heart_leve3_name",
            "#14CCAD",
            "heart_leve3_evaluation",
            "heart_leve3_suggestion",
            "3"
        )
    } else if (heartRate < critical_point5) {
        //偏高
        arrayListOf<String>(
            "heart_leve3_name",
            "#F43F31",
            "heart_leve4_evaluation",
            "heart_leve4_suggestion",
            "4"
        )
    } else {
        //过高
        arrayListOf<String>(
            "heart_leve3_name",
            "#C23227",
            "heart_leve5_evaluation",
            "heart_leve5_suggestion",
            "5"
        )
    }
}

/**
 * BMI相关的标准计算
 *
 * @param bmi
 * @return
 */
fun bmiLevel(bmi: Double): MutableList<String> {
    val critical_point1 = 18.5
    val critical_point2 = 24.0
    val critical_point3 = 30.0 //28.0
    return if (bmi <= critical_point1) {
        //偏瘦
        arrayListOf<String>(
            "BMI_leve1_name",
            "#F5A623",
            "BMI_leve1_evaluation",
            "BMI_leve1_suggestion",
            "1"
        )
    } else if (bmi > critical_point1 && bmi <= critical_point2) {
        arrayListOf<String>(
            "BMI_leve2_name",
            "#14CCAD",
            "BMI_leve1_evaluation",
            "BMI_leve1_suggestion",
            "2"
        )
    } else if (bmi > critical_point2 && bmi <= critical_point3) {
        //轻度脂肪堆积
        arrayListOf<String>(
            "BMI_leve3_name",
            "#F43F31",
            "BMI_leve3_evaluation",
            "BMI_leve3_suggestion",
            "3"
        )
    } else {
        //严重脂肪堆积
        arrayListOf<String>(
            "BMI_leve4_name",
            "#C23227",
            "BMI_leve4_evaluation",
            "BMI_leve4_suggestion",
            "4"
        )
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
fun bftLevel(gender: Int, age: Int, fatPercent: Double): MutableList<String> {
    var critical_point1 = 0.0
    var critical_point2 = 0.0
    var critical_point3 = 0.0
    var critical_point4 = 0.0
    if (gender == 1) { // 男
        if (age < 40) {
            critical_point1 = 10.0
            critical_point2 = 21.0
            critical_point3 = 26.0
            critical_point4 = 45.0
        } else if (age in 40..59) {
            critical_point1 = 11.0
            critical_point2 = 22.0
            critical_point3 = 27.0
            critical_point4 = 45.0
        } else if (age > 60) {
            critical_point1 = 13.0
            critical_point2 = 24.0
            critical_point3 = 29.0
            critical_point4 = 45.0
        }
    } else { // 女
        if (age < 40) {
            critical_point1 = 20.0
            critical_point2 = 34.0
            critical_point3 = 39.0
            critical_point4 = 45.0
        } else if (age in 40..59) {
            critical_point1 = 21.0
            critical_point2 = 35.0
            critical_point3 = 40.0
            critical_point4 = 45.0
        } else {
            critical_point1 = 22.0
            critical_point2 = 36.0
            critical_point3 = 41.0
            critical_point4 = 45.0
        }
    }
    return if (fatPercent <= critical_point1) {
        //偏瘦
        arrayListOf<String>(
            "Bodyfat_leve1_name",
            "#F5A623",
            "Bodyfat_leve1_evaluation",
            "Bodyfat_leve1_suggestion",
            "1"
        )
    } else if (fatPercent <= critical_point2) {
        //健康
        arrayListOf<String>(
            "Bodyfat_leve2_name",
            "#14CCAD",
            "Bodyfat_leve2_evaluation",
            "Bodyfat_leve2_suggestion",
            "2"
        )
    } else if (fatPercent <= critical_point3) {
        //偏胖
        arrayListOf<String>(
            "Bodyfat_leve3_name",
            "#F43F31",
            "Bodyfat_leve3_evaluation",
            "Bodyfat_leve3_suggestion",
            "3"
        )
    } else if (fatPercent <= critical_point4) {
        //胖
        arrayListOf<String>(
            "Bodyfat_leve4_name",
            "#C23227",
            "Bodyfat_leve4_evaluation",
            "Bodyfat_leve4_suggestion",
            "4"
        )
    } else {
        //胖
        arrayListOf<String>(
            "Bodyfat_leve4_name",
            "#C23227",
            "Bodyfat_leve4_evaluation",
            "Bodyfat_leve4_suggestion",
            "4"
        )
    }
}

/**
 * 脂肪量的标准计算
 *
 * @param gender
 * @param age
 * @param fatPercent
 * @return
 */
fun bftKgLevel(gender: Int, age: Int, fatPercent: Double): MutableList<String> {
    var critical_point1 = 0.0
    var critical_point2 = 0.0
    var critical_point3 = 0.0
    var critical_point4 = 0.0
    if (gender == 1) { // 男
        if (age < 40) {
            critical_point1 = 10.0
            critical_point2 = 21.0
            critical_point3 = 26.0
            critical_point4 = 45.0
        } else if (age in 40..59) {
            critical_point1 = 11.0
            critical_point2 = 22.0
            critical_point3 = 27.0
            critical_point4 = 45.0
        } else if (age > 60) {
            critical_point1 = 13.0
            critical_point2 = 24.0
            critical_point3 = 29.0
            critical_point4 = 45.0
        }
    } else { // 女
        if (age < 40) {
            critical_point1 = 20.0
            critical_point2 = 34.0
            critical_point3 = 39.0
            critical_point4 = 45.0
        } else if (age in 40..59) {
            critical_point1 = 21.0
            critical_point2 = 35.0
            critical_point3 = 40.0
            critical_point4 = 45.0
        } else {
            critical_point1 = 22.0
            critical_point2 = 36.0
            critical_point3 = 41.0
            critical_point4 = 45.0
        }
    }
    return if (fatPercent <= critical_point1) {
        //偏瘦
        arrayListOf<String>(
            "BodyFatKg_leve1_name",
            "#F5A623",
            "BodyFatKg_leve1_evaluation",
            "BodyFatKg_leve1_suggestion",
            "1"
        )
    } else if (fatPercent <= critical_point2) {
        //正常
        arrayListOf<String>(
            "BodyFatKg_leve2_name",
            "#14CCAD",
            "BodyFatKg_leve2_evaluation",
            "BodyFatKg_leve2_suggestion",
            "2"
        )
    } else if (fatPercent <= critical_point3) {
        //偏高
        arrayListOf<String>(
            "BodyFatKg_leve3_name",
            "#F43F31",
            "BodyFatKg_leve3_evaluation",
            "BodyFatKg_leve3_suggestion",
            "3"
        )
    } else if (fatPercent <= critical_point4) {
        //严重偏高
        arrayListOf<String>(
            "BodyFatKg_leve4_name",
            "#C23227",
            "BodyFatKg_leve4_evaluation",
            "BodyFatKg_leve4_suggestion",
            "4"
        )
    } else {
        //胖
        arrayListOf<String>(
            "BodyFatKg_leve4_name",
            "#C23227",
            "BodyFatKg_leve4_evaluation",
            "BodyFatKg_leve4_suggestion",
            "4"
        )
    }
}


private const val KILOGRAMS_PER_STONE = 6.35029 // 1 stone = 6.35029 kg

/**
 * kg转st
 */
fun kilogramsToStones(kilograms: Double): Double {
    return kilograms / KILOGRAMS_PER_STONE
}

fun getWeightUnit(weightUnit:Int): String {
    return if (weightUnit == 0) {
        "kg"
    } else if (weightUnit == 1) {
        "lb"
    } else if (weightUnit == 2) {
        "st"
    } else {
        "kg"
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
fun muscleLevel(
    gender: Int,
    height: Double,
    muscle: Double
): MutableList<String> {
    var critical_point1 = 1f
    var critical_point2 = 1f
    val critical_point3 = 20f
    if (gender == 1) {  //男
        if (height < 160) {
            critical_point1 = 38.5f
            critical_point2 = 46.5f
        } else if (height <= 170) {
            critical_point1 = 44f
            critical_point2 = 52.4f
        } else {
            critical_point1 = 49.4f
            critical_point2 = 59.4f
        }
    } else {  //女
        if (height < 150) {
            critical_point1 = 29.1f
            critical_point2 = 34.7f
        } else if (height <= 160) {
            critical_point1 = 32.9f
            critical_point2 = 37.5f
        } else {
            critical_point1 = 36.5f
            critical_point2 = 42.5f
        }
    }
    return if (muscle <= critical_point1) {
        //不足
        arrayListOf<String>(
            "mus_leve1_name",
            "#F5A623",
            "mus_leve1_evaluation",
            "mus_leve1_suggestion",
            "1"
        )
    } else if (muscle <= critical_point2) {
        //标准
        arrayListOf<String>(
            "mus_leve2_name",
            "#14CCAD",
            "mus_leve2_evaluation",
            "mus_leve2_suggestion",
            "2"
        )
    } else {
        //优
        arrayListOf<String>(
            "mus_leve3_name",
            "#0F9982",
            "mus_leve2_evaluation",
            "mus_leve2_suggestion",
            "3"
        )
    }
}

/**
 * 肌肉率相关标准计算
 *
 * @param gender
 * @param height
 * @param muscle
 * @return
 */
fun muscleRateLevel(
    gender: Int,
    height: Double,
    muscle: Double
): MutableList<String> {
    var critical_point1 = 1f
    var critical_point2 = 1f
    val critical_point3 = 20f
    if (gender == 1) {  //男
        if (height < 160) {
            critical_point1 = 38.5f
            critical_point2 = 46.5f
        } else if (height <= 170) {
            critical_point1 = 44f
            critical_point2 = 52.4f
        } else {
            critical_point1 = 49.4f
            critical_point2 = 59.4f
        }
    } else {  //女
        if (height < 150) {
            critical_point1 = 29.1f
            critical_point2 = 34.7f
        } else if (height <= 160) {
            critical_point1 = 32.9f
            critical_point2 = 37.5f
        } else {
            critical_point1 = 36.5f
            critical_point2 = 42.5f
        }
    }
    return if (muscle <= critical_point1) {
        //不足
        arrayListOf<String>(
            "MusRate_leve1_name",
            "#F5A623",
            "MusRate_leve1_evaluation",
            "MusRate_leve1_suggestion",
            "1"
        )
    } else if (muscle <= critical_point2) {
        //标准
        arrayListOf<String>(
            "MusRate_leve2_name",
            "#14CCAD",
            "MusRate_leve2_evaluation",
            "MusRate_leve2_suggestion",
            "2"
        )
    } else {
        //优秀
        arrayListOf<String>(
            "MusRate_leve3_name",
            "#0F9982",
            "MusRate_leve3_evaluation",
            "MusRate_leve3_suggestion",
            "3"
        )
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
fun bmrLevel(gender: Int, age: Int, weight: Double, bmr: Float): MutableList<String> {
    val critical_point1: Float
    critical_point1 = if (age <= 29) {
        if (gender == 1) {
            PPUtil.keepPoint1(weight * 24.0f)
        } else {
            PPUtil.keepPoint1(weight * 23.6f)
        }
    } else if (age in 30..49) {
        if (gender == 1) {
            PPUtil.keepPoint1(weight * 22.3f)
        } else {
            PPUtil.keepPoint1(weight * 21.7f)
        }
    } else {
        if (gender == 1) {
            PPUtil.keepPoint1(weight * 21.5f)
        } else {
            PPUtil.keepPoint1(weight * 20.7f)
        }
    }
    return if (bmr <= critical_point1) {
        //偏低
        arrayListOf<String>(
            "BMR_leve1_name",
            "#F5A623",
            "BMR_leve1_evaluation",
            "BMR_leve1_suggestion",
            "1"
        )
    } else {
        //优
        arrayListOf<String>(
            "BMR_leve2_name",
            "#14CCAD",
            "BMR_leve2_evaluation",
            "BMR_leve2_suggestion",
            "2"
        )
    }
}

/**
 * Protein
 *
 * @param 蛋白质
 * @return
 */
fun getProtein(bmi: Double): MutableList<String> {
    return if (bmi < 16) {//过低
        arrayListOf<String>(
            "proteinPercentage_leve1_name",
            "#F5A623",
            "proteinPercentage_leve1_evaluation",
            "proteinPercentage_leve1_suggestion",
            "1"
        )
    } else if (bmi <= 20) {//标准
        arrayListOf<String>(
            "proteinPercentage_leve2_name",
            "#14CCAD",
            "proteinPercentage_leve2_evaluation",
            "proteinPercentage_leve2_suggestion",
            "2"
        )
    } else {
        arrayListOf<String>(//过高
            "proteinPercentage_leve3_name",
            "#0F9982",
            "proteinPercentage_leve3_evaluation",
            "proteinPercentage_leve3_suggestion",
            "3"
        )
    }
}

/**
 * 肥胖等级
 *
 * @param mContext
 * @param bmi
 * @return
 */
fun getObsLevelDetail(bmi: Double): MutableList<String> {
    if (bmi >= 0 && bmi < 18.5) {
        return arrayListOf<String>(
            "FatGrade_leve1_name",
            "#F5A623",
            "FatGrade_leve1_evaluation",
            "FatGrade_leve1_suggestion",
            "1"
        )
    } else if (bmi < 24.9) {
        return arrayListOf<String>(
            "FatGrade_leve2_name",
            "#14CCAD",
            "FatGrade_leve2_evaluation",
            "FatGrade_leve2_suggestion",
            "2"
        )
    } else if (bmi < 29.9) {
        return arrayListOf<String>(
            "FatGrade_leve3_name",
            "#F43F31",
            "FatGrade_leve3_evaluation",
            "FatGrade_leve3_suggestion",
            "3"
        )
    } else if (bmi >= 30 && bmi < 35) {
        return arrayListOf<String>(
            "FatGrade_leve4_name",
            "#C23227",
            "FatGrade_leve4_evaluation",
            "FatGrade_leve4_suggestion",
            "4"
        )
    } else if (bmi < 40) {
        return arrayListOf<String>(
            "FatGrade_leve5_name",
            "#8F251D",
            "FatGrade_leve5_evaluation",
            "FatGrade_leve5_suggestion",
            "5"
        )
    } else if (bmi < 90) {
        return arrayListOf<String>(
            "FatGrade_leve6_name",
            "#5C1813",
            "FatGrade_leve6_evaluation",
            "FatGrade_leve6_suggestion",
            "6"
        )
    }
    return arrayListOf<String>(
        "FatGrade_leve4_name",
        "#C23227",
        "FatGrade_leve4_evaluation",
        "FatGrade_leve4_suggestion",
        "4"
    )
}

/**
 * 水分的标准计算
 *
 * @param gender
 * @param waterPercent
 * @return
 */
fun waterLevel(gender: Int, waterPercent: Double): MutableList<String> {
    val critical_point1: Double
    val critical_point2: Double
    val critical_point3 = 100.0
    if (gender == 1) {  //男
        critical_point1 = 55.0
        critical_point2 = 65.0
    } else {  //女
        critical_point1 = 45.0
        critical_point2 = 60.0
    }
    return if (waterPercent <= critical_point1) {
        //不足
        arrayListOf<String>(
            "water_leve1_name",
            "#F5A623",
            "water_leve1_evaluation",
            "water_leve1_suggestion",
            "1"
        )
    } else if (waterPercent <= critical_point2) {
        //标准
        arrayListOf<String>(
            "water_leve2_name",
            "#14CCAD",
            "water_leve2_evaluation",
            "water_leve2_suggestion",
            "2"
        )
    } else {
        //优秀
        arrayListOf<String>(
            "water_leve3_name",
            "#0F9982",
            "water_leve2_evaluation",
            "water_leve2_suggestion",
            "3"
        )
    }
}

/**
 * 内脏脂肪标准的计算
 *
 * @param visceralfat
 * @return
 */
fun visceralLevel(visceralfat: Double): MutableList<String> {
    val critical_point1 = 9.0
    val critical_point2 = 14.0
    return if (visceralfat <= critical_point1) {
        //标准
        arrayListOf<String>(
            "visfat_leve1_name",
            "#14CCAD",
            "visfat_leve1_evaluation",
            "visfat_leve1_suggestion",
            "1"
        )
    } else if (visceralfat <= critical_point2) {
        //警惕
        arrayListOf<String>(
            "visfat_leve2_name",
            "#F5A623",
            "visfat_leve2_evaluation",
            "visfat_leve2_suggestion",
            "2"
        )
    } else {
        //危险
        arrayListOf<String>(
            "visfat_leve3_name",
            "#F43F31",
            "visfat_leve3_evaluation",
            "visfat_leve3_suggestion",
            "3"
        )
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
fun boneLevel(gender: Int, weight: Double, bone: Double): MutableList<String> {
    var critical_point1 = 2.4
    var critical_point2 = 2.6
    if (gender == 1) {
        // 男
        if (weight < 60) {
            critical_point1 = 2.4
            critical_point2 = 2.6
        } else if (weight >= 60 && weight <= 75) {
            critical_point1 = 2.8
            critical_point2 = 3.0
        } else if (weight > 75) {
            critical_point1 = 3.1
            critical_point2 = 3.3
        }
    } else {
        // 女
        if (weight < 45) {
            critical_point1 = 1.7
            critical_point2 = 1.9
        } else if (weight >= 45 && weight <= 60) {
            critical_point1 = 2.1
            critical_point2 = 2.3
        } else if (weight > 60) {
            critical_point1 = 2.4
            critical_point2 = 2.6
        }
    } //屏幕宽度
    return if (bone < critical_point1) {
        //不足
        arrayListOf<String>(
            "bone_leve1_name",
            "#F5A623",
            "bone_leve1_evaluation",
            "bone_leve1_suggestion",
            "1"
        )
    } else if (bone < critical_point2) {
        //标准
        arrayListOf<String>(
            "bone_leve2_name",
            "#14CCAD",
            "bone_leve2_evaluation",
            "bone_leve2_suggestion",
            "2"
        )
    } else {
        //优秀
        arrayListOf<String>(
            "bone_leve3_name",
            "#0F9982",
            "bone_leve3_evaluation",
            "bone_leve3_suggestion",
            "3"
        )
    }
}

/**
 * 皮下脂肪相关标准的计算
 */
fun subFatLevel(sex: Int, subFat: Double): MutableList<String> {
    val critical_point1 = 8.6f
    val critical_point2 = 16.7f
    val critical_point3 = 20.7f
    val w_point1 = 18.5f
    val w_point2 = 26.7f
    val w_point3 = 30.8f
    return if (sex == 1) {
        if (subFat < critical_point1) {
            //偏低
            arrayListOf<String>(
                "BodySubcutaneousFat_leve1_name",
                "#F5A623",
                "BodySubcutaneousFat_leve1_evaluation",
                "BodySubcutaneousFat_leve1_suggestion",
                "1"
            )
        } else if (subFat < critical_point2) {
            //标准
            arrayListOf<String>(
                "BodySubcutaneousFat_leve2_name",
                "#14CCAD",
                "BodySubcutaneousFat_leve2_evaluation",
                "BodySubcutaneousFat_leve2_suggestion",
                "2"
            )
        } else if (subFat < critical_point3) {
            //偏高
            arrayListOf<String>(
                "BodySubcutaneousFat_leve3_name",
                "#F43F31",
                "BodySubcutaneousFat_leve3_evaluation",
                "BodySubcutaneousFat_leve3_suggestion",
                "3"
            )
        } else {
            //严重偏高
            arrayListOf<String>(
                "BodySubcutaneousFat_leve4_name",
                "#C23227",
                "BodySubcutaneousFat_leve4_evaluation",
                "BodySubcutaneousFat_leve4_suggestion",
                "4"
            )
        }
    } else {
        if (subFat < w_point1) {
            //偏低
            arrayListOf<String>(
                "BodySubcutaneousFat_leve1_name",
                "#F5A623",
                "BodySubcutaneousFat_leve1_evaluation",
                "BodySubcutaneousFat_leve1_suggestion",
                "1"
            )
        } else if (subFat < w_point2) {
            //标准
            arrayListOf<String>(
                "BodySubcutaneousFat_leve2_name",
                "#14CCAD",
                "BodySubcutaneousFat_leve2_evaluation",
                "BodySubcutaneousFat_leve2_suggestion",
                "2"
            )
        } else if (subFat < w_point3) {
            //偏高
            arrayListOf<String>(
                "BodySubcutaneousFat_leve3_name",
                "#F43F31",
                "BodySubcutaneousFat_leve3_evaluation",
                "BodySubcutaneousFat_leve3_suggestion",
                "3"
            )
        } else {
            //严重偏高
            arrayListOf<String>(
                "BodySubcutaneousFat_leve4_name",
                "#C23227",
                "BodySubcutaneousFat_leve4_evaluation",
                "BodySubcutaneousFat_leve4_suggestion",
                "4"
            )
        }
    }
}

/**
 * 身体年龄相关标准的计算
 */
fun bodyAgeLevel(age: Int, bodyAge: Int): MutableList<String> {
    val critical_point1 = age.toFloat()
    return if (bodyAge < critical_point1) {
        //优秀
        arrayListOf<String>(
            "physicalAgeValue_leve1_name",
            "#14CCAD",
            "physicalAgeValue_leve1_evaluation",
            "physicalAgeValue_leve1_suggestion",
            "1"
        )
    } else {
        //偏高
        arrayListOf<String>(
            "physicalAgeValue_leve2_name",
            "#F43F31",
            "physicalAgeValue_leve2_evaluation",
            "physicalAgeValue_leve2_suggestion",
            "2"
        )
    }
}

/**
 * 健康等级
 *
 * @param assessment
 * @return
 */
fun bodyHealthAssessment(assessment: Int): MutableList<String> {
    return if (assessment == 0) {//健康存在隐患
        arrayListOf<String>(
            "BodyHealth_leve1_name",
            "",
            "BodyHealth_leve1_evaluation",
            "BodyHealth_leve1_suggestion",
            "1"
        )
    } else if (assessment == 1) {//亚健康
        arrayListOf<String>(
            "BodyHealth_leve2_name",
            "",
            "BodyHealth_leve2_evaluation",
            "BodyHealth_leve2_suggestion",
            "2"
        )
    } else if (assessment == 2) {//一般
        arrayListOf<String>(
            "BodyHealth_leve3_name",
            "",
            "BodyHealth_leve3_evaluation",
            "BodyHealth_leve3_suggestion",
            "3"
        )
    } else if (assessment == 3) {//良好
        arrayListOf<String>(
            "BodyHealth_leve4_name",
            "",
            "BodyHealth_leve4_evaluation",
            "BodyHealth_leve4_suggestion",
            "4"
        )
    } else {//严重脂肪堆积
        arrayListOf<String>(
            "BodyHealth_leve5_name",
            "",
            "BodyHealth_leve5_evaluation",
            "BodyHealth_leve5_suggestion",
            "5"
        )
    }
}

/**
 * 骨骼肌率的等级
 *
 * @param gender 1是男 其他是女
 * @return
 */
fun bodySkeletalLevel(gender: Int, bodySkeletal: Double): MutableList<String> {
    return if (gender == 1) {
        if (bodySkeletal < 40) {//偏低
            arrayListOf<String>(
                "BodySkeletal_leve1_name",
                "#F5A623",
                "BodySkeletal_leve1_evaluation",
                "BodySkeletal_leve1_suggestion",
                "1"
            )
        } else if (bodySkeletal >= 40 && bodySkeletal <= 60) {//正常
            arrayListOf<String>(
                "BodySkeletal_leve2_name",
                "#14CCAD",
                "BodySkeletal_leve2_evaluation",
                "BodySkeletal_leve2_suggestion",
                "2"
            )
        } else {//偏高
            arrayListOf<String>(
                "BodySkeletal_leve3_name",
                "#F43F31",
                "BodySkeletal_leve3_evaluation",
                "BodySkeletal_leve3_suggestion",
                "3"
            )
        }
    } else {
        if (bodySkeletal < 49) {//偏低
            arrayListOf<String>(
                "BodySkeletal_leve1_name",
                "#F5A623",
                "BodySkeletal_leve1_evaluation",
                "BodySkeletal_leve1_suggestion",
                "1"
            )
        } else if (bodySkeletal >= 49 && bodySkeletal <= 69) {//正常
            arrayListOf<String>(
                "BodySkeletal_leve2_name",
                "#14CCAD",
                "BodySkeletal_leve2_evaluation",
                "BodySkeletal_leve2_suggestion",
                "2"
            )
        } else {//偏高
            arrayListOf<String>(
                "BodySkeletal_leve3_name",
                "#F43F31",
                "BodySkeletal_leve3_evaluation",
                "BodySkeletal_leve3_suggestion",
                "3"
            )
        }
    }
}

/**
 * 身体得分相关标准的计算
 */
fun bodyScoreLevel(bodyScore: Double): MutableList<String> {
    val critical_point1 = 70f
    val critical_point2 = 80f
    val critical_point3 = 90f
    return if (bodyScore < critical_point1) {
        //不好
        arrayListOf<String>(
            "BodyScore_leve1_name",
            "#C2831C",
            "BodyScore_leve1_evaluation",
            "BodyScore_leve1_suggestion",
            "1"
        )
    } else if (bodyScore < critical_point2) {
        //一般
        arrayListOf<String>(
            "BodyScore_leve2_name",
            "#F5A623",
            "BodyScore_leve2_evaluation",
            "BodyScore_leve2_suggestion",
            "2"
        )
    } else if (bodyScore < critical_point3) {
        //良好
        arrayListOf<String>(
            "BodyScore_leve3_name",
            "#14CCAD",
            "BodyScore_leve3_evaluation",
            "BodyScore_leve3_suggestion",
            "4"
        )
    } else {
        //非常好
        arrayListOf<String>(
            "BodyScore_leve4_name",
            "#0F9982",
            "BodyScore_leve4_evaluation",
            "BodyScore_leve4_suggestion",
            "5"
        )
    }
}

/**
 * * BH_BODY_TYPE_THIN = 1,
 * * BH_BODY_TYPE_THIN_MUSCLE = 2,
 * * BH_BODY_TYPE_MUSCULAR = 3,
 * * BH_BODY_TYPE_OBESE_FAT = 4,
 * * BH_BODY_TYPE_FAT_MUSCLE = 5,
 * * BH_BODY_TYPE_MUSCLE_FAT = 6,
 * * BH_BODY_TYPE_LACK_EXERCISE = 7,
 * * BH_BODY_TYPE_STANDARD = 8,
 * * BH_BODY_TYPE_STANDARD_MUSCLE = 9
 */
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
 */
fun getBodyfatType(type: Int): MutableList<String> {
    if (type == 0) {             //偏瘦型
        return arrayListOf<String>(
            "BodyType_leve1_name",
            "",
            "BodyType_leve1_evaluation",
            "BodyType_leve1_suggestion"
        )
    } else if (type == 1) {      //偏瘦肌肉型
        return arrayListOf<String>(
            "BodyType_leve2_name",
            "",
            "BodyType_leve2_evaluation",
            "BodyType_leve2_suggestion"
        )
    } else if (type == 2) {         //2 肌肉发达型
        return arrayListOf<String>(
            "BodyType_leve3_name",
            "",
            "BodyType_leve3_evaluation",
            "BodyType_leve3_suggestion"
        )
    } else if (type == 3) {   //3 缺乏运动型
        return arrayListOf<String>(
            "BodyType_leve4_name",
            "",
            "BodyType_leve4_evaluation",
            "BodyType_leve4_suggestion"
        )
    } else if (type == 4) {         //4 标准型
        return arrayListOf<String>(
            "BodyType_leve5_name",
            "",
            "BodyType_leve5_evaluation",
            "BodyType_leve5_suggestion"
        )
    } else if (type == 5) {   //5 标准肌肉型
        return arrayListOf<String>(
            "BodyType_leve6_name",
            "",
            "BodyType_leve6_evaluation",
            "BodyType_leve6_suggestion"
        )
    } else if (type == 6) {          //6 浮肿肥胖型
        return arrayListOf<String>(
            "BodyType_leve7_name",
            "",
            "BodyType_leve7_evaluation",
            "BodyType_leve7_suggestion"
        )
    } else if (type == 7) {       //7 偏胖肌肉型
        return arrayListOf<String>(
            "BodyType_leve8_name",
            "",
            "BodyType_leve8_evaluation",
            "BodyType_leve8_suggestion"
        )
    } else if (type == 8) {        //8 肌肉型偏胖
        return arrayListOf<String>(
            "BodyType_leve9_name",
            "",
            "BodyType_leve9_evaluation",
            "BodyType_leve9_suggestion"
        )
    }
    return arrayListOf<String>(
        "BodyType_leve5_name",
        "",
        "BodyType_leve5_evaluation",
        "BodyType_leve5_suggestion"
    )
}
private fun isTwoPointScale(accuracyType: Int): Boolean {
    return accuracyType == PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint005.getType()
}
