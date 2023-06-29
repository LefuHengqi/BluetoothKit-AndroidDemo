package com.lefu.bodyindex

import com.peng.ppscale.util.PPUtil

/**
 *    author : whs
 *    e-mail : haisilen@163.com
 *    date   : 2021/10/28 9:10
 *    desc   :测量结果的标准范围和进度计算
 */
object BodyFatIndexStandardProgressUtils {

    /**
     * 获取体重的范围值
     * 分别是:最小值,标准最小值,标准最大值,最大值
     */
    fun getWeightRangeValue(gender: Int, height: Int): DoubleArray {
        val standardWeight: Double = if (gender == 1) {
            ((height - 80) * 0.7f).toDouble()
        } else {
            ((height - 70) * 0.6f).toDouble()
        }
        val criticalPoint1 = standardWeight - standardWeight * 0.1f
        val criticalPoint2 = standardWeight + standardWeight * 0.1f
        val criticalPoint3 = standardWeight + standardWeight * 0.2f
        return doubleArrayOf(0.0, criticalPoint1, criticalPoint2, criticalPoint3)
    }

    /**
     * 获取体水分率
     *
     */
    fun getWaterRangValue(gender: Int): DoubleArray {
        return if (gender == 1) {//男
            doubleArrayOf(0.0, 55.0, 65.0, 100.0)
        } else {
            doubleArrayOf(0.0, 45.0, 60.0, 100.0)
        }
    }

    /**
     * 获取心率的标准范围
     * 0-60 偏低
     * 60-100 正常
     * 100-180偏高
     */
    fun getHeartRangValue(): DoubleArray {
        return doubleArrayOf(0.0, 40.0, 60.0, 100.0, 160.0, 180.0)
    }

    /**
     * 获取bmi的标准范围
     * 0-18.5偏瘦
     * 18.5-24 健康
     * 24-30 偏胖
     * 30-60 胖
     */
    fun getBmiRangValue(): DoubleArray {
        return doubleArrayOf(0.0, 18.5, 24.0, 30.0, 60.0)
    }

    /**
     * 获取bmr的标准范围值
     */
    fun getBmrRangValue(sex: Int, age: Int, weight: Double): DoubleArray {
        val bmr2 = bmrInt1(sex, age, weight)
        return doubleArrayOf(0.0, bmr2.toDouble(), (bmr2 * 2).toDouble())
    }

    /**
     * bmr等级计算
     */
    fun bmrInt1(gender: Int, age: Int, weight: Double): Float {
        var critical_point1 = 1261f
        if (age <= 29) {
            if (gender == 1) {
                critical_point1 = PPUtil.keepPoint1(weight * 24.0f)
            } else {
                critical_point1 = PPUtil.keepPoint1(weight * 23.6f)
            }
        } else if (age <= 49) {
            if (gender == 1) {
                critical_point1 = PPUtil.keepPoint1(weight * 22.3f)
            } else {
                critical_point1 = PPUtil.keepPoint1(weight * 21.7f)
            }
        } else {
            if (gender == 1) {
                critical_point1 = PPUtil.keepPoint1(weight * 21.5f)
            } else {
                critical_point1 = PPUtil.keepPoint1(weight * 20.7f)
            }
        }
        return critical_point1
    }

    /**
     * 获取肌肉的标准范围值
     *
     */
    fun getMuscleRangValue(gender: Int, height: Int): DoubleArray {
        var critical_point1 = 1.0
        var critical_point2 = 1.0
        var critical_point3 = 20.0
        if (gender == 1) {  //男
            if (height < 160) {
                critical_point1 = 38.5
                critical_point2 = 46.5
                critical_point3 = 100.0
            } else if (height >= 160 && height <= 170) {
                critical_point1 = 44.0
                critical_point2 = 52.4
                critical_point3 = 100.0
            } else {
                critical_point1 = 49.4
                critical_point2 = 59.4
                critical_point3 = 100.0
            }
        } else {  //女
            if (height < 150) {
                critical_point1 = 29.1
                critical_point2 = 34.7
                critical_point3 = 100.0
            } else if (height >= 150 && height <= 160) {
                critical_point1 = 32.9
                critical_point2 = 37.5
                critical_point3 = 100.0
            } else {
                critical_point1 = 36.5
                critical_point2 = 42.5
                critical_point3 = 100.0
            }
        }
        return doubleArrayOf(0.0, critical_point1, critical_point2, critical_point3)
    }

    /**
     * 内脏脂肪的标准范围值
     * 0-9正常
     * 9-14 警惕
     * 14-16危险
     */
    fun getVFALRangValue(): DoubleArray {
        return doubleArrayOf(0.0, 9.0, 14.0, 16.0)
    }

    /**
     * 获取骨量的标准范围值和极限值
     */
    fun getBoneRangValue(gender: Int, weight: Double): DoubleArray {
        var critical_point1 = 2.4
        var critical_point2 = 2.6
        var critical_point3 = 3.2
        if (gender == 1) {  //男
            if (weight < 60) {
                critical_point1 = 2.4
                critical_point2 = 2.6
                critical_point3 = 3.2
            } else if (weight >= 60 && weight <= 75) {
                critical_point1 = 2.8
                critical_point2 = 3.0
                critical_point3 = 3.2
            } else if (weight > 75) {
                critical_point1 = 3.1
                critical_point2 = 3.3
                critical_point3 = 3.4
            }
        } else {  //女
            //女
            if (weight < 45) {
                critical_point1 = 1.7
                critical_point2 = 1.9
                critical_point3 = 2.5
            } else if (weight >= 45 && weight <= 60) {
                critical_point1 = 2.1
                critical_point2 = 2.3
                critical_point3 = 2.5
            } else if (weight > 60) {
                critical_point1 = 2.4
                critical_point2 = 2.6
                critical_point3 = 3.0
            }
        }
        return doubleArrayOf(0.0, critical_point1, critical_point2, critical_point3)
    }

    /**
     * 获取蛋白质的标准百分比值
     * 是百分比,不是重量
     */
    fun getProteinRangValue(): DoubleArray {
        return doubleArrayOf(0.0, 16.0, 20.0, 60.0)
    }

    /**
     * 肥胖度的标准范围值
     * 用bmi的值去计算
     *
     */
    fun getObsRangValue(): DoubleArray {
        return doubleArrayOf(0.0, 18.5, 24.9, 29.9, 35.0, 40.0, 90.0)
    }

    /**
     * 脂肪率的标准范围值
     */
    fun getFatRangValue(gender: Int, age: Int): DoubleArray {
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
        return doubleArrayOf(
            0.0,
            critical_point1,
            critical_point2,
            critical_point3,
            critical_point4
        )
    }

    /**
     * 获取皮下脂肪率的百分比标准值
     */
    fun getSubFatRangValue(sex: Int): DoubleArray {
        return if (sex == 1) {
            doubleArrayOf(0.0, 8.6, 16.7, 20.7, 50.0)
        } else {
            doubleArrayOf(0.0, 18.5, 26.7, 30.8, 60.0)
        }
    }

    /**
     * 获取身体年龄的标准
     */
    fun getAgeRangValue(age: Int): DoubleArray {
        return doubleArrayOf(0.0, age.toDouble(), (age + 10).toDouble())
    }

    /**
     * 获取身体得分的标准
     */
    fun getBodyScoreRangValue(): DoubleArray {
        return doubleArrayOf(0.0, 70.0, 80.0, 90.0, 100.0)
    }

    /**
     * 获取健康评估的标准
     */
    fun getBodyHealthRangValue(): DoubleArray {
        return doubleArrayOf(0.0, 0.8, 1.6, 2.4, 3.2, 4.0)
    }

    /**
     * 获取骨骼肌率的百分比标准值
     */
    fun getBodySkeletalRangValue(sex: Int): DoubleArray {
        return if (sex == 1) {
            doubleArrayOf(0.0, 40.0, 60.0, 80.0)
        } else {
            doubleArrayOf(0.0, 49.0, 69.0, 89.0)
        }
    }


    /**
     * 计算公式
     * 1,先计算有都是格:分母
     * 2,计算落在哪一格上
     * 3,计算出该数值在该格上的比例
     */
    fun getBodyItemProgress(level: Int, currentValue: Float, rangValue: List<Float>): Int {
        var progress = 0
        if (rangValue.isEmpty()) {
            return 0
        }
        if (currentValue - rangValue[0] < 0) {
            return 4
        }
        when (rangValue.size - 1) {
            2 -> {//进度条分的块数-格
                when (level) {
                    0 -> {
                        progress = ((currentValue / rangValue[1]) * 100 / 2).toInt()
                    }
                    1 -> {
                        progress =
                            ((((currentValue - rangValue[1]) / (rangValue[2] - rangValue[1])) + 1) * 100 / 2).toInt()
                    }
                }
            }
            3 -> {
                when (level) {
                    0 -> {
                        progress = ((currentValue / rangValue[1]) * 100 / 3).toInt()
                    }
                    1 -> {
                        progress =
                            ((((currentValue - rangValue[1]) / (rangValue[2] - rangValue[1])) + 1) * 100 / 3).toInt()
                    }
                    2 -> {
                        progress =
                            ((((currentValue - rangValue[2]) / (rangValue[3] - rangValue[2])) + 2) * 100 / 3).toInt()
                    }
                }
            }
            4 -> {
                when (level) {
                    0 -> {
                        progress = ((currentValue / rangValue[1]) * 100 / 4).toInt()
                    }
                    1 -> {
                        progress =
                            ((((currentValue - rangValue[1]) / (rangValue[2] - rangValue[1])) + 1) * 100 / 4).toInt()
                    }
                    2 -> {
                        progress =
                            ((((currentValue - rangValue[2]) / (rangValue[3] - rangValue[2])) + 2) * 100 / 4).toInt()
                    }
                    3 -> {
                        progress =
                            ((((currentValue - rangValue[3]) / (rangValue[4] - rangValue[3])) + 3) * 100 / 4).toInt()
                    }
                }
            }
            5 -> {
                when (level) {
                    0 -> {
                        progress = ((currentValue / rangValue[1]) * 100 / 5).toInt()
                    }
                    1 -> {
                        progress =
                            ((((currentValue - rangValue[1]) / (rangValue[2] - rangValue[1])) + 1) * 100 / 5).toInt()
                    }
                    2 -> {
                        progress =
                            ((((currentValue - rangValue[2]) / (rangValue[3] - rangValue[2])) + 2) * 100 / 5).toInt()
                    }
                    3 -> {
                        progress =
                            ((((currentValue - rangValue[3]) / (rangValue[4] - rangValue[3])) + 3) * 100 / 5).toInt()
                    }
                    4 -> {
                        progress =
                            ((((currentValue - rangValue[4]) / (rangValue[5] - rangValue[4])) + 4) * 100 / 5).toInt()
                    }
                }
            }
            6 -> {
                when (level) {
                    0 -> {
                        progress = ((currentValue / rangValue[1]) * 100 / 6).toInt()
                    }
                    1 -> {
                        progress =
                            ((((currentValue - rangValue[1]) / (rangValue[2] - rangValue[1])) + 1) * 100 / 6).toInt()
                    }
                    2 -> {
                        progress =
                            ((((currentValue - rangValue[2]) / (rangValue[3] - rangValue[2])) + 2) * 100 / 6).toInt()
                    }
                    3 -> {
                        progress =
                            ((((currentValue - rangValue[3]) / (rangValue[4] - rangValue[3])) + 3) * 100 / 6).toInt()
                    }
                    4 -> {
                        progress =
                            ((((currentValue - rangValue[4]) / (rangValue[5] - rangValue[4])) + 4) * 100 / 6).toInt()
                    }
                    5 -> {
                        progress =
                            ((((currentValue - rangValue[5]) / (rangValue[6] - rangValue[5])) + 5) * 100 / 6).toInt()
                    }
                }
            }
        }
        return progress
    }


}