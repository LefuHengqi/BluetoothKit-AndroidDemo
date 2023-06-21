package com.lefu.bodyindex

import com.peng.ppscale.data.PPBodyDetailInfoModel
import com.peng.ppscale.data.PPBodyDetailModel
import com.peng.ppscale.vo.PPBluetoothScaleBaseModel
import com.peng.ppscale.vo.PPBodyBaseModel
import com.peng.ppscale.vo.PPBodyFatModel

/**
 *    @author : whs
 *    e-mail : haisilen@163.com
 *    date   : 2023/3/18 11:17
 *    desc   : 数据展示时的bodyFat
 *    同时继承body,这样数据可以完整
 */
data class BodyFatItemVo(
    var value: String? = "",//指标的值
    var indexName: String? = "",//指标名称
    var indexIconId: Int = -1,//指标的icon
    var indexIconUrl: String? = "",//指标的网络链接
    var valueUnit: String? = "kg",//值的单位名称
    var indexGradeCircularBg: Int = -1, //圆环的背景图
    var indexGradeStr: String = "", //指标的等级,标准等
    var progress: Int = 0, //进度
    var indexGradeColor16Str: String = "", //指标的等级的颜色
    var indexType: Int = 0,//默认体重
    var indexIntroduction:String = "",//指标的介绍说明
    var indexEevaluation:String = "",//指标的评价说明
    var indexSuggestion:String = "",//指标的建议
    var viewType: Int = -1, //viewType的类型,用来多类型item使用
    var bodyFat: PPBodyDetailInfoModel? = null
) : java.io.Serializable

/**
 * 指标类型 体重 体脂率等
 */
enum class BodyFatItemType(val type:Int){
    WEIGHT(0),
    HEART_RATE(16),
    BMI(1),
    FAT(2),
    MUSCLE(3),
    WATER(4),
    VFAL(5),//内脏脂肪等级
    BONE(6),
    BMR(7),
    PROTEIN(8),//蛋白质含量
    OBE_LEVEL(9),//肥胖等级
    SUB_FAT(10),//皮下脂肪率
    BODY_AGE(11),
    BODY_GRADE(12),//身体得分
    NO_FAT_WEIGHT(13),//去脂体重
    BODY_TYPE(14),//身体类型
    IDEAL_WEIGHT(15),//标准体重
    BODY_HEALTH(17),//健康评估
    BODY_SKELETAL(18),//骨骼肌率
    CONTROL_WEIGHT(19),//体重控制
    FAT_CONTROL_KG(20),//脂肪控制量
    MUSCLE_CONTROL(21),//肌肉控制量
    MUSCLE_RATE(22),//肌肉率
    BODY_FAT_KG(23),//脂肪量
    WATER_KG(24),//水分量
    PROTEIN_KG(25),//蛋白质量(Kg)
    BODY_FAT_SUBCUT_KG(26),//皮下脂肪量(Kg)
    CELL_MASS_KG(27),//身体细胞量(Kg)
    DCI(47),//建议卡路里摄入量Kcal/day
    MINERAL_KG(28),//无机盐量(Kg)
    OBESITY(29),//肥胖度(%)
    WATER_ECW_KG(30),//细胞外水量(kg)
    WATER_ICW_KG(31),//细胞内水量(kg)
    BODY_FAT_KG_LEFT_ARM(32),//左手脂肪量(%), 分辨率0.1
    BODY_FAT_KG_LEFT_LEG(33),//左左脚脂肪量(%), 分辨率0.1
    BODY_FAT_KG_RIGHT_ARM(34),//右手脂肪量(%), 分辨率0.1
    BODY_FAT_KG_RIGHT_LEG(35),//右脚脂肪量(%), 分辨率0.1
    BODY_FAT_KG_TRUNK(36),//躯干脂肪量(%), 分辨率0.1
    BODY_FAT_RATE_LEFT_ARM(37),//左手脂肪率(%), 分辨率0.1
    BODY_FAT_RATE_LEFT_LEG(38),//左脚脂肪率(%), 分辨率0.1
    BODY_FAT_RATE_RIGHT_ARM(39),//右手脂肪率(%), 分辨率0.1
    BODY_FAT_RATE_RIGHT_LEG(40),//右脚脂肪率(%), 分辨率0.1
    BODY_FAT_RATE_TRUNK(41),//躯干脂肪率(%), 分辨率0.1
    MUSCLE_KG_LEFT_ARM(42),//左手肌肉量(kg), 分辨率0.1, 范围0.0 ~ 200kg
    MUSCLE_KG_LEFT_LEG(43),//左脚肌肉量(kg), 分辨率0.1, 范围0.0 ~ 200kg
    MUSCLE_KG_RIGHT_ARM(44),//右手肌肉量(kg), 分辨率0.1, 范围0.0 ~ 200kg
    MUSCLE_KG_RIGHT_LEG(45),//右脚肌肉量(kg), 分辨率0.1, 范围0.0 ~ 200kg
    MUSCLE_KG_TRUNK(46),//躯干肌肉量(kg), 分辨率0.1, 范围0.0 ~ 200kg
}