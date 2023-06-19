package com.lefu.bodyindex


import com.lefu.bodyindex.UtilTooth.keep1Point1
import com.peng.ppscale.business.device.PPUnitType
import com.peng.ppscale.data.PPBodyDetailInfoModel
import com.peng.ppscale.util.PPUtil
import com.peng.ppscale.vo.PPBluetoothScaleBaseModel
import com.peng.ppscale.vo.PPBodyFatModel

/**
 *    @author : whs
 *    e-mail : haisilen@163.com
 *    date   : 2023/3/25 14:58
 *    desc   : 身体数据指标工具类
 */
object BodyFatIndexUtils {
    
    /**
     * 获取所有的指标数据
     * @param bodyFat 测量后给出的结果
     *@param accuracyType 精度
     * @param ppUnitType 单位的枚举
     * @param bodyAgeUnitString 身体年龄单位 多语言"岁"
     * @param bodyScoreUnitString 身体得分单位 "分"
     */
    fun getBodyIndex(
        bodyFat: PPBodyFatModel, ppUnitType: PPUnitType,
        accuracyType: Int = 2,
        weightUnitStr: String,
        bodyAgeUnitString: String,
        bodyScoreUnitString:String
    ): MutableList<BodyFatItemVo?> {
        val bodyItemList: ArrayList<BodyFatItemVo?> = ArrayList()
        val bodyDetailModel = bodyFat.bodyDetailModel
        //0.体重
        val weight = BodyFatItemVo()
        val ppbodyparamWeight = bodyDetailModel.PPBodyParam_Weight
        weight.indexType = BodyFatItemType.WEIGHT.type
        weight.indexIconId = R.drawable.pic_icon_data_tizhong_n
        weight.indexGradeCircularBg = R.drawable.bg_progress_3_end_bad
        weight.valueUnit = weightUnitStr
        weight.bodyFat = bodyFat
        if (ppbodyparamWeight != null) {
            buildBodyDetailParamWeight(weight, ppbodyparamWeight, ppUnitType, accuracyType)
        }
        bodyItemList.add(weight)

        //2.脂肪率
        val fat = BodyFatItemVo()
        fat.indexType = BodyFatItemType.FAT.type
        val ppbodyparamBodyfat = bodyDetailModel.PPBodyParam_BodyFat
        if (ppbodyparamBodyfat != null) {
            buildBodyDetailParamPercent(fat, ppbodyparamBodyfat)
        }
        fat.valueUnit = "%"
        fat.indexIconId = R.drawable.pic_icon_data_zhifanglv_n
        fat.indexGradeCircularBg = R.drawable.bg_progress_4_end_bad
        fat.bodyFat = bodyFat
        bodyItemList.add(fat)

        //1 bmi
        val bmi = BodyFatItemVo()
        bmi.indexType = BodyFatItemType.BMI.type
        val ppbodyparamBodyBMI = bodyDetailModel.PPBodyParam_BMI
        if (ppbodyparamBodyBMI != null) {
            buildBodyDetailParamPercent(bmi, ppbodyparamBodyBMI)
        }
        bmi.valueUnit = ""
        bmi.indexIconId = R.drawable.pic_icon_data_bmi_n
        bmi.indexGradeCircularBg = R.drawable.bg_progress_4_end_bad
        bmi.bodyFat = bodyFat
        bodyItemList.add(bmi)

        //3.肌肉含量kg
        val muscle = BodyFatItemVo()
        muscle.indexType = BodyFatItemType.MUSCLE.type
        val ppbodyparamBodyMus = bodyDetailModel.PPBodyParam_Mus
        if (ppbodyparamBodyMus != null) {
            buildBodyDetailParamKg(muscle, ppbodyparamBodyMus, ppUnitType, accuracyType)
        }
        muscle.valueUnit = weightUnitStr
        muscle.indexIconId = R.drawable.pic_icon_data_jirouliang_n
        muscle.indexGradeCircularBg = R.drawable.bg_progress_3_end_good
        muscle.bodyFat = bodyFat
        bodyItemList.add(muscle)

        //7.BMR(基础代谢)
        val bmr = BodyFatItemVo()
        bmr.indexType = BodyFatItemType.BMR.type
        val ppbodyparamBodyBMR = bodyDetailModel.PPBodyParam_BMR
        if (ppbodyparamBodyBMR != null) {
            buildBodyDetailParamPercent(bmr, ppbodyparamBodyBMR)
        }
        bmr.valueUnit = "Kcal"
        bmr.indexIconId = R.drawable.pic_icon_data_bmr_n
        bmr.indexGradeCircularBg = R.drawable.bg_progress_2_end_good
        bmr.bodyFat = bodyFat
        bodyItemList.add(bmr)

        //28.輸出參數-全身体组成:建议卡路里摄入量 Kcal/day
        val dCI = BodyFatItemVo()
        dCI.indexType = BodyFatItemType.DCI.type
        val ppbodyparamDci = bodyDetailModel.PPBodyParam_dCI
        if (ppbodyparamDci != null) {
            buildNoStandard(dCI, ppbodyparamDci)
        }
        dCI.valueUnit = "kcal"
        dCI.indexIconId = R.drawable.pic_icon_data_kalulisheruliang_n
        dCI.bodyFat = bodyFat
        bodyItemList.add(dCI)

        //16心率
        val heartRate = BodyFatItemVo()
        heartRate.indexType = BodyFatItemType.HEART_RATE.type
        val ppbodyparamHeart = bodyDetailModel.PPBodyParam_heart
        if (ppbodyparamHeart != null) {
            buildBodyDetailParamPercent(heartRate, ppbodyparamHeart)
        }
        heartRate.valueUnit = "bpm"
        heartRate.indexIconId = R.drawable.pic_icon_data_xinlv_n
        heartRate.indexGradeCircularBg = R.drawable.bg_progress_5_end_bad
        heartRate.bodyFat = bodyFat
        bodyItemList.add(heartRate)

        //4.水分率
        val water = BodyFatItemVo()
        water.indexType = BodyFatItemType.WATER.type
        val ppbodyparamWater = bodyDetailModel.PPBodyParam_Water
        if (ppbodyparamWater != null) {
            buildBodyDetailParamPercent(water, ppbodyparamWater)
        }
        water.valueUnit = "%"
        water.indexIconId = R.drawable.pic_icon_data_shuifenlv_n
        water.indexGradeCircularBg = R.drawable.bg_progress_3_end_good
        water.bodyFat = bodyFat
        bodyItemList.add(water)

        //8.蛋白质率
        val protein = BodyFatItemVo()
        protein.indexType = BodyFatItemType.PROTEIN.type
        val ppbodyparamProteinpercentage = bodyDetailModel.PPBodyParam_proteinPercentage
        if (ppbodyparamProteinpercentage != null) {
            buildBodyDetailParamPercent(protein, ppbodyparamProteinpercentage)
        }
        protein.valueUnit = "%"
        protein.indexIconId = R.drawable.pic_icon_data_danbaizhi_n
        protein.indexGradeCircularBg = R.drawable.bg_progress_3_end_bad
        protein.bodyFat = bodyFat
        bodyItemList.add(protein)

        //24.輸出參數-全身体组成:水分量(Kg)
        val waterKg = BodyFatItemVo()
        waterKg.indexType = BodyFatItemType.WATER_KG.type
        val ppbodyparamWaterkg = bodyDetailModel.PPBodyParam_waterKg
        if (ppbodyparamWaterkg != null) {
            buildBodyDetailParamKg(waterKg, ppbodyparamWaterkg, ppUnitType, accuracyType)
        }
        waterKg.valueUnit = weightUnitStr
        waterKg.indexIconId = R.drawable.pic_icon_data_shuifenliang_n
        waterKg.bodyFat = bodyFat
        bodyItemList.add(waterKg)

        //25.輸出參數-全身体组成:蛋白质量(Kg)
        val proteinKg = BodyFatItemVo()
        proteinKg.indexType = BodyFatItemType.PROTEIN_KG.type
        val ppbodyparamProteinkg = bodyDetailModel.PPBodyParam_proteinKg
        if (ppbodyparamProteinkg != null) {
            buildBodyDetailParamKg(proteinKg, ppbodyparamProteinkg, ppUnitType, accuracyType)
        }
        proteinKg.valueUnit = weightUnitStr
        proteinKg.indexIconId = R.drawable.pic_icon_data_danbaizhiliang_n
        proteinKg.bodyFat = bodyFat
        bodyItemList.add(proteinKg)

        //23.脂肪量
        val bodyFatKg = BodyFatItemVo()
        bodyFatKg.indexType = BodyFatItemType.BODY_FAT_KG.type
        val ppbodyparamBodyfatkg = bodyDetailModel.PPBodyParam_BodyFatKg
        if (ppbodyparamBodyfatkg != null) {
            buildBodyDetailParamKg(bodyFatKg, ppbodyparamBodyfatkg, ppUnitType, accuracyType)
        }
        bodyFatKg.valueUnit = weightUnitStr
        bodyFatKg.indexIconId = R.drawable.pic_icon_data_zhifangliang_n
        bodyFatKg.indexGradeCircularBg = R.drawable.bg_progress_4_end_bad
        bodyFatKg.bodyFat = bodyFat
        bodyItemList.add(bodyFatKg)

        //13.去脂体重
        val noFatWeight = BodyFatItemVo()
        noFatWeight.indexType = BodyFatItemType.NO_FAT_WEIGHT.type
        val ppbodyparamBodylbw = bodyDetailModel.PPBodyParam_BodyLBW
        if (ppbodyparamBodylbw != null) {
            buildBodyDetailParamKg(noFatWeight, ppbodyparamBodylbw, ppUnitType, accuracyType)
        }
        noFatWeight.valueUnit = weightUnitStr
        noFatWeight.indexIconId = R.drawable.pic_icon_data_quzhitizhong_n
        noFatWeight.bodyFat = bodyFat
        bodyItemList.add(noFatWeight)

        //10.皮下脂肪率
        val subFat = BodyFatItemVo()
        subFat.indexType = BodyFatItemType.SUB_FAT.type
        val ppbodyparamBodysubcutaneousfat = bodyDetailModel.PPBodyParam_BodySubcutaneousFat
        if (ppbodyparamBodysubcutaneousfat != null) {
            buildBodyDetailParamPercent(subFat, ppbodyparamBodysubcutaneousfat)
        }
        subFat.valueUnit = "%"
        subFat.indexIconId = R.drawable.pic_icon_data_pixiazhifanglv_n
        subFat.indexGradeCircularBg = R.drawable.bg_progress_4_end_bad
        subFat.bodyFat = bodyFat
        bodyItemList.add(subFat)

        //18.骨骼肌率
        val bodySkeletal = BodyFatItemVo()
        bodySkeletal.indexType = BodyFatItemType.BODY_SKELETAL.type
        val ppbodyparamBodyskeletal = bodyDetailModel.PPBodyParam_BodySkeletal
        if (ppbodyparamBodyskeletal != null) {
            buildBodyDetailParamPercent(bodySkeletal, ppbodyparamBodyskeletal)
        }
        bodySkeletal.valueUnit = "%"
        bodySkeletal.indexIconId = R.drawable.pic_icon_data_gugejilv_n
        bodySkeletal.indexGradeCircularBg = R.drawable.bg_progress_3_end_bad
        bodySkeletal.bodyFat = bodyFat
        bodyItemList.add(bodySkeletal)

        //5.内脏脂肪等级
        val vfal = BodyFatItemVo()
        vfal.indexType = BodyFatItemType.VFAL.type
        val ppbodyparamVisfat = bodyDetailModel.PPBodyParam_VisFat
        if (ppbodyparamVisfat != null) {
            buildBodyDetailParamPercent(vfal, ppbodyparamVisfat)
        }
        vfal.valueUnit = ""
        vfal.indexIconId = R.drawable.pic_icon_data_neizangzhifangdengji_n
        vfal.indexGradeCircularBg = R.drawable.bg_progress_3_start_good
        vfal.bodyFat = bodyFat
        bodyItemList.add(vfal)

        //6.骨量
        val bone = BodyFatItemVo()
        bone.indexType = BodyFatItemType.BONE.type
        val ppbodyparamBone = bodyDetailModel.PPBodyParam_Bone
        if (ppbodyparamBone != null) {
            buildBodyDetailParamKg(bone, ppbodyparamBone, ppUnitType, accuracyType)
        }
        bone.valueUnit = weightUnitStr
        bone.indexIconId = R.drawable.pic_icon_data_guliang_n
        bone.indexGradeCircularBg = R.drawable.bg_progress_3_end_good
        bone.bodyFat = bodyFat
        bodyItemList.add(bone)

        //26.輸出參數-全身体组成:皮下脂肪量(Kg)
        val bodyFatSubCutKg = BodyFatItemVo()
        bodyFatSubCutKg.indexType = BodyFatItemType.BODY_FAT_SUBCUT_KG.type
        val ppbodyparamBodyfatsubcutkg = bodyDetailModel.PPBodyParam_bodyFatSubCutKg
        if (ppbodyparamBodyfatsubcutkg != null) {
            buildBodyDetailParamKg(
                bodyFatSubCutKg,
                ppbodyparamBodyfatsubcutkg,
                ppUnitType,
                accuracyType
            )
        }
        bodyFatSubCutKg.valueUnit = weightUnitStr
        bodyFatSubCutKg.indexIconId = R.drawable.pic_icon_data_pixiazifangliang_n
        bodyFatSubCutKg.bodyFat = bodyFat
        bodyItemList.add(bodyFatSubCutKg)

        //22.肌肉率
        val musclePercentage = BodyFatItemVo()
        musclePercentage.indexType = BodyFatItemType.MUSCLE_RATE.type
        val ppbodyparamMusrate = bodyDetailModel.PPBodyParam_MusRate
        if (ppbodyparamMusrate != null) {
            buildBodyDetailParamPercent(musclePercentage, ppbodyparamMusrate)
        }
        musclePercentage.valueUnit = "%"
        musclePercentage.indexIconId = R.drawable.pic_icon_data_jiroulv_n
        musclePercentage.indexGradeCircularBg = R.drawable.bg_progress_3_end_good
        musclePercentage.bodyFat = bodyFat
        bodyItemList.add(musclePercentage)

        //37.//左手脂肪率(%), 分辨率0.1
        val bodyFatRateLeftArm = BodyFatItemVo()
        bodyFatRateLeftArm.indexType = BodyFatItemType.BODY_FAT_RATE_LEFT_ARM.type
        val ppbodyparamBodyfatrateleftarm = bodyDetailModel.PPBodyParam_bodyFatRateLeftArm
        if (ppbodyparamBodyfatrateleftarm != null) {
            buildBodyDetailParamPercent(bodyFatRateLeftArm, ppbodyparamBodyfatrateleftarm)
        }
        bodyFatRateLeftArm.valueUnit = "%"
        bodyFatRateLeftArm.indexIconId = R.drawable.pic_icon_data_zuoshouzifanglv_n
        bodyFatRateLeftArm.bodyFat = bodyFat
        bodyItemList.add(bodyFatRateLeftArm)

        //27.輸出參數-全身体组成:身体细胞量(Kg)
        val cellMassKg = BodyFatItemVo()
        cellMassKg.indexType = BodyFatItemType.CELL_MASS_KG.type
        val ppbodyparamCellmasskg = bodyDetailModel.PPBodyParam_cellMassKg
        if (ppbodyparamCellmasskg != null) {
            buildBodyDetailParamKg(cellMassKg, ppbodyparamCellmasskg, ppUnitType, accuracyType)
        }
        cellMassKg.valueUnit = weightUnitStr
        cellMassKg.indexIconId = R.drawable.pic_icon_data_shentixibaoliang_n
        cellMassKg.bodyFat = bodyFat
        bodyItemList.add(cellMassKg)

        //39.//右手脂肪率(%), 分辨率0.1
        val bodyFatRateRightArm = BodyFatItemVo()
        bodyFatRateRightArm.indexType = BodyFatItemType.BODY_FAT_RATE_RIGHT_ARM.type
        val ppbodyparamBodyfatraterightarm = bodyDetailModel.PPBodyParam_bodyFatRateRightArm
        if (ppbodyparamBodyfatraterightarm != null) {
            buildBodyDetailParamPercent(bodyFatRateRightArm, ppbodyparamBodyfatraterightarm)
        }
        bodyFatRateRightArm.valueUnit = "%"
        bodyFatRateRightArm.indexIconId = R.drawable.pic_icon_data_youshouzifanglv_n
        bodyFatRateRightArm.bodyFat = bodyFat
        bodyItemList.add(bodyFatRateRightArm)

        //32.//輸出參數-全身体组成:细胞内水量(kg)
        val waterICWKg = BodyFatItemVo()
        waterICWKg.indexType = BodyFatItemType.WATER_ICW_KG.type
        val ppbodyparamWatericwkg = bodyDetailModel.PPBodyParam_waterICWKg
        if (ppbodyparamWatericwkg != null) {
            buildBodyDetailParamKg(waterICWKg, ppbodyparamWatericwkg, ppUnitType, accuracyType)
        }
        waterICWKg.valueUnit = weightUnitStr
        waterICWKg.indexIconId = R.drawable.pic_icon_data_xibaoneishuiliang_n
        waterICWKg.bodyFat = bodyFat
        bodyItemList.add(waterICWKg)

        //41.//躯干脂肪率(%), 分辨率0.1
        val bodyFatRateTrunk = BodyFatItemVo()
        bodyFatRateTrunk.indexType = BodyFatItemType.BODY_FAT_RATE_TRUNK.type
        val ppbodyparamBodyfatratetrunk = bodyDetailModel.PPBodyParam_bodyFatRateTrunk
        if (ppbodyparamBodyfatratetrunk != null) {
            buildBodyDetailParamPercent(bodyFatRateTrunk, ppbodyparamBodyfatratetrunk)
        }
        bodyFatRateTrunk.valueUnit = "%"
        bodyFatRateTrunk.indexIconId = R.drawable.pic_icon_data_quganzifanglv_n
        bodyFatRateTrunk.bodyFat = bodyFat
        bodyItemList.add(bodyFatRateTrunk)

        //31.輸出參數-全身体组成:细胞外水量(kg)
        val waterECWKg = BodyFatItemVo()
        waterECWKg.indexType = BodyFatItemType.WATER_ECW_KG.type
        val ppbodyparamWaterecwkg = bodyDetailModel.PPBodyParam_waterECWKg
        if (ppbodyparamWaterecwkg != null) {
            buildBodyDetailParamKg(waterECWKg, ppbodyparamWaterecwkg, ppUnitType, accuracyType)
        }
        waterECWKg.valueUnit = weightUnitStr
        waterECWKg.indexIconId = R.drawable.pic_icon_data_xibaowaishuiliang_n
        waterECWKg.bodyFat = bodyFat
        bodyItemList.add(waterECWKg)

        //38.//左脚脂肪率(%), 分辨率0.1
        val bodyFatRateLeftLeg = BodyFatItemVo()
        bodyFatRateLeftLeg.indexType = BodyFatItemType.BODY_FAT_RATE_LEFT_LEG.type
        val ppbodyparamBodyfatrateleftleg = bodyDetailModel.PPBodyParam_bodyFatRateLeftLeg
        if (ppbodyparamBodyfatrateleftleg != null) {
            buildBodyDetailParamPercent(bodyFatRateLeftLeg, ppbodyparamBodyfatrateleftleg)
        }
        bodyFatRateLeftLeg.valueUnit = "%"
        bodyFatRateLeftLeg.indexIconId = R.drawable.pic_icon_data_zuojiaozifanglv_n
        bodyFatRateLeftLeg.bodyFat = bodyFat
        bodyItemList.add(bodyFatRateLeftLeg)

        //29.輸出參數-全身体组成:无机盐量(Kg)
        val mineralKg = BodyFatItemVo()
        mineralKg.indexType = BodyFatItemType.MINERAL_KG.type
        val ppbodyparamMineralkg = bodyDetailModel.PPBodyParam_mineralKg
        if (ppbodyparamMineralkg != null) {
            buildBodyDetailParamKg(mineralKg, ppbodyparamMineralkg, ppUnitType, accuracyType)
        }
        mineralKg.valueUnit = weightUnitStr
        mineralKg.indexIconId = R.drawable.pic_icon_data_wujiyanliang_n
        mineralKg.bodyFat = bodyFat
        bodyItemList.add(mineralKg)

        //40.//右脚脂肪率(%), 分辨率0.1
        val bodyFatRateRightLeg = BodyFatItemVo()
        bodyFatRateRightLeg.indexType = BodyFatItemType.BODY_FAT_RATE_RIGHT_LEG.type
        val ppbodyparamBodyfatraterightleg = bodyDetailModel.PPBodyParam_bodyFatRateRightLeg
        if (ppbodyparamBodyfatraterightleg != null) {
            buildBodyDetailParamPercent(bodyFatRateRightLeg, ppbodyparamBodyfatraterightleg)
        }
        bodyFatRateRightLeg.valueUnit = "%"
        bodyFatRateRightLeg.indexIconId = R.drawable.pic_icon_data_youjiaozifanglv_n
        bodyFatRateRightLeg.bodyFat = bodyFat
        bodyItemList.add(bodyFatRateRightLeg)

        //42.//左手肌肉量(kg), 分辨率0.1, 范围0.0 ~ 200kg
        val muscleKgLeftArm = BodyFatItemVo()
        muscleKgLeftArm.indexType = BodyFatItemType.MUSCLE_KG_LEFT_ARM.type
        val ppbodyparamMusclekgleftarm = bodyDetailModel.PPBodyParam_muscleKgLeftArm
        if (ppbodyparamMusclekgleftarm != null) {
            buildBodyDetailParamKg(
                muscleKgLeftArm,
                ppbodyparamMusclekgleftarm,
                ppUnitType,
                accuracyType
            )
        }
        muscleKgLeftArm.valueUnit = weightUnitStr
        muscleKgLeftArm.indexIconId = R.drawable.pic_icon_data_zuoshoujirouliang_n
        muscleKgLeftArm.bodyFat = bodyFat
        bodyItemList.add(muscleKgLeftArm)


        //33.//左手脂肪量(kg), 分辨率0.1
        val bodyFatKgLeftArm = BodyFatItemVo()
        bodyFatKgLeftArm.indexType = BodyFatItemType.BODY_FAT_KG_LEFT_ARM.type
        val ppbodyparamBodyfatkgleftarm = bodyDetailModel.PPBodyParam_bodyFatKgLeftArm
        if (ppbodyparamBodyfatkgleftarm != null) {
            buildBodyDetailParamKg(
                bodyFatKgLeftArm,
                ppbodyparamBodyfatkgleftarm,
                ppUnitType,
                accuracyType
            )
        }
        bodyFatKgLeftArm.valueUnit = "%"
        bodyFatKgLeftArm.indexIconId = R.drawable.pic_icon_data_zuoshouzhifangliang_n
        bodyFatKgLeftArm.bodyFat = bodyFat
        bodyItemList.add(bodyFatKgLeftArm)

        //44.//右手肌肉量(kg), 分辨率0.1, 范围0.0 ~ 200kg
        val muscleKgRightArm = BodyFatItemVo()
        muscleKgRightArm.indexType = BodyFatItemType.MUSCLE_KG_RIGHT_ARM.type
        val ppbodyparamMusclekgrightarm = bodyDetailModel.PPBodyParam_muscleKgRightArm
        if (ppbodyparamMusclekgrightarm != null) {
            buildBodyDetailParamKg(
                muscleKgRightArm,
                ppbodyparamMusclekgrightarm,
                ppUnitType,
                accuracyType
            )
        }
        muscleKgRightArm.valueUnit = weightUnitStr
        muscleKgRightArm.indexIconId = R.drawable.pic_icon_data_youshoujirouliang_n
        muscleKgRightArm.bodyFat = bodyFat
        bodyItemList.add(muscleKgRightArm)

        //35.//右手脂肪量(kg), 分辨率0.1
        val bodyFatKgRightArm = BodyFatItemVo()
        bodyFatKgRightArm.indexType = BodyFatItemType.BODY_FAT_KG_RIGHT_ARM.type
        val ppbodyparamBodyfatkgrightarm = bodyDetailModel.PPBodyParam_bodyFatKgRightArm
        if (ppbodyparamBodyfatkgrightarm != null) {
            buildBodyDetailParamKg(
                bodyFatKgRightArm,
                ppbodyparamBodyfatkgrightarm,
                ppUnitType,
                accuracyType
            )
        }
        bodyFatKgRightArm.valueUnit = weightUnitStr
        bodyFatKgRightArm.indexIconId = R.drawable.pic_icon_data_youshouzhifangliang_n
        bodyFatKgRightArm.bodyFat = bodyFat
        bodyItemList.add(bodyFatKgRightArm)

        //46.//躯干肌肉量(kg), 分辨率0.1, 范围0.0 ~ 200kg
        val muscleKgTrunk = BodyFatItemVo()
        muscleKgTrunk.indexType = BodyFatItemType.MUSCLE_KG_TRUNK.type
        val ppbodyparamMusclekgtrunk = bodyDetailModel.PPBodyParam_muscleKgTrunk
        if (ppbodyparamMusclekgtrunk != null) {
            buildBodyDetailParamKg(
                muscleKgTrunk,
                ppbodyparamMusclekgtrunk,
                ppUnitType,
                accuracyType
            )
        }
        muscleKgTrunk.valueUnit = weightUnitStr
        muscleKgTrunk.indexIconId = R.drawable.pic_icon_data_quganjirouliang_n
        muscleKgTrunk.bodyFat = bodyFat
        bodyItemList.add(muscleKgTrunk)

        //37.//躯干脂肪量(kg), 分辨率0.1
        val bodyFatKgTrunk = BodyFatItemVo()
        bodyFatKgTrunk.indexType = BodyFatItemType.BODY_FAT_KG_TRUNK.type
        val ppbodyparamBodyfatkgtrunk = bodyDetailModel.PPBodyParam_bodyFatKgTrunk
        if (ppbodyparamBodyfatkgtrunk != null) {
            buildBodyDetailParamKg(
                bodyFatKgTrunk,
                ppbodyparamBodyfatkgtrunk,
                ppUnitType,
                accuracyType
            )
        }
        bodyFatKgTrunk.valueUnit = weightUnitStr
        bodyFatKgTrunk.indexIconId = R.drawable.pic_icon_data_quganzhifangliang_n
        bodyFatKgTrunk.bodyFat = bodyFat
        bodyItemList.add(bodyFatKgTrunk)

        //43.//左脚肌肉量(kg), 分辨率0.1, 范围0.0 ~ 200kg
        val muscleKgLeftLeg = BodyFatItemVo()
        muscleKgLeftLeg.indexType = BodyFatItemType.MUSCLE_KG_LEFT_LEG.type
        val ppbodyparamMusclekgleftleg = bodyDetailModel.PPBodyParam_muscleKgLeftLeg
        if (ppbodyparamMusclekgleftleg != null) {
            buildBodyDetailParamKg(
                muscleKgLeftLeg,
                ppbodyparamMusclekgleftleg,
                ppUnitType,
                accuracyType
            )
        }
        muscleKgLeftLeg.valueUnit = weightUnitStr
        muscleKgLeftLeg.indexIconId = R.drawable.pic_icon_data_zuojiaojirouliang_n
        muscleKgLeftLeg.bodyFat = bodyFat
        bodyItemList.add(muscleKgLeftLeg)

        //34.//左脚脂肪量(kg), 分辨率0.1
        val bodyFatKgLeftLeg = BodyFatItemVo()
        bodyFatKgLeftLeg.indexType = BodyFatItemType.BODY_FAT_KG_LEFT_LEG.type
        val ppbodyparamBodyfatkgleftleg = bodyDetailModel.PPBodyParam_bodyFatKgLeftLeg
        if (ppbodyparamBodyfatkgleftleg != null) {
            buildBodyDetailParamKg(
                bodyFatKgLeftLeg,
                ppbodyparamBodyfatkgleftleg,
                ppUnitType,
                accuracyType
            )
        }
        bodyFatKgLeftLeg.valueUnit = weightUnitStr
        bodyFatKgLeftLeg.indexIconId = R.drawable.pic_icon_data_zuojiaozhifangliang_n
        bodyFatKgLeftLeg.bodyFat = bodyFat
        bodyItemList.add(bodyFatKgLeftLeg)

        //45.//右脚肌肉量(kg), 分辨率0.1, 范围0.0 ~ 200kg
        val muscleKgRightLeg = BodyFatItemVo()
        muscleKgRightLeg.indexType = BodyFatItemType.MUSCLE_KG_RIGHT_LEG.type
        val ppbodyparamMusclekgrightleg = bodyDetailModel.PPBodyParam_muscleKgRightLeg
        if (ppbodyparamMusclekgrightleg != null) {
            buildBodyDetailParamKg(
                muscleKgRightLeg,
                ppbodyparamMusclekgrightleg,
                ppUnitType,
                accuracyType
            )
        }
        muscleKgRightLeg.valueUnit = weightUnitStr
        muscleKgRightLeg.indexIconId = R.drawable.pic_icon_data_youjiaojirouliang_n
        muscleKgRightLeg.bodyFat = bodyFat
        bodyItemList.add(muscleKgRightLeg)

        //36.//右脚脂肪量(kg), 分辨率0.1
        val bodyFatKgRightLeg = BodyFatItemVo()
        bodyFatKgRightLeg.indexType = BodyFatItemType.BODY_FAT_KG_RIGHT_LEG.type
        val ppbodyparamBodyfatkgrightleg = bodyDetailModel.PPBodyParam_bodyFatKgRightLeg
        if (ppbodyparamBodyfatkgrightleg != null) {
            buildBodyDetailParamKg(
                bodyFatKgRightLeg,
                ppbodyparamBodyfatkgrightleg,
                ppUnitType,
                accuracyType
            )
        }
        bodyFatKgRightLeg.valueUnit = weightUnitStr
        bodyFatKgRightLeg.indexIconId = R.drawable.pic_icon_data_youjiaozhifangliang_n
        bodyFatKgRightLeg.bodyFat = bodyFat
        bodyItemList.add(bodyFatKgRightLeg)

        //21.肌肉控制量
        val bodyMuscleControl = BodyFatItemVo()
        bodyMuscleControl.indexType = BodyFatItemType.MUSCLE_CONTROL.type
        val ppbodyparamMusclecontrol = bodyDetailModel.PPBodyParam_MuscleControl
        if (ppbodyparamMusclecontrol != null) {
            buildBodyDetailParamKg(
                bodyMuscleControl,
                ppbodyparamMusclecontrol,
                ppUnitType,
                accuracyType
            )
        }
        bodyMuscleControl.valueUnit = weightUnitStr
        bodyMuscleControl.indexIconId = R.drawable.pic_icon_data_jiroukongzhiliang_n
        bodyMuscleControl.bodyFat = bodyFat
        bodyItemList.add(bodyMuscleControl)

        //20.脂肪控制量
        val fatControlKg = BodyFatItemVo()
        fatControlKg.indexType = BodyFatItemType.FAT_CONTROL_KG.type
        val ppbodyparamBodycontrolliang = bodyDetailModel.PPBodyParam_BodyControlLiang
        if (ppbodyparamBodycontrolliang != null) {
            buildBodyDetailParamKg(
                fatControlKg,
                ppbodyparamBodycontrolliang,
                ppUnitType,
                accuracyType
            )
        }
        fatControlKg.valueUnit = weightUnitStr
        fatControlKg.indexIconId = R.drawable.pic_icon_data_zhifangkongzhiliang_n
        fatControlKg.bodyFat = bodyFat
        bodyItemList.add(fatControlKg)

        //15.标准体重
        val idealWeight = BodyFatItemVo()
        idealWeight.indexType = BodyFatItemType.IDEAL_WEIGHT.type
        val ppbodyparamBodystandard = bodyDetailModel.PPBodyParam_Bodystandard
        if (ppbodyparamBodystandard != null) {
            buildBodyDetailParamKg(idealWeight, ppbodyparamBodystandard, ppUnitType, accuracyType)
        }
        idealWeight.valueUnit = weightUnitStr
        idealWeight.indexIconId = R.drawable.pic_icon_data_biaozhuntizhong_n
        idealWeight.bodyFat = bodyFat
        bodyItemList.add(idealWeight)

        //19.体重控制
        val controlWeightKg = BodyFatItemVo()
        controlWeightKg.indexType = BodyFatItemType.CONTROL_WEIGHT.type
        val ppbodyparamBodycontrol = bodyDetailModel.PPBodyParam_BodyControl
        if (ppbodyparamBodycontrol != null) {
            buildBodyDetailParamKg(
                controlWeightKg,
                ppbodyparamBodycontrol,
                ppUnitType,
                accuracyType
            )
        }
        controlWeightKg.valueUnit = weightUnitStr
        controlWeightKg.indexIconId = R.drawable.pic_icon_data_kongzhitizhong_n
        controlWeightKg.bodyFat = bodyFat
        bodyItemList.add(controlWeightKg)

        //30.輸出參數-评价建议: 肥胖度(%)
        val obesity = BodyFatItemVo()
        obesity.indexType = BodyFatItemType.OBESITY.type
        val ppbodyparamObesity = bodyDetailModel.PPBodyParam_obesity
        if (ppbodyparamObesity != null) {
            buildBodyDetailParamPercent(obesity, ppbodyparamObesity)
        }
        obesity.valueUnit = "%"
        obesity.indexIconId = R.drawable.pic_icon_data_feipangdu_n
        obesity.bodyFat = bodyFat
        bodyItemList.add(obesity)

        //14.身体类型
        val bodyType = BodyFatItemVo()
        bodyType.indexType = BodyFatItemType.BODY_TYPE.type
        val ppbodyparamBodytype = bodyDetailModel.PPBodyParam_BodyType
        if (ppbodyparamBodytype != null) {
            buildBodyDetailBodyType(bodyType, ppbodyparamBodytype)
        }
        bodyType.valueUnit = ""
        bodyType.indexIconId = R.drawable.pic_icon_data_shentileixing_n
        bodyType.bodyFat = bodyFat
        bodyItemList.add(bodyType)

        //9.肥胖等级
        val obeLevel = BodyFatItemVo()
        obeLevel.indexType = BodyFatItemType.OBE_LEVEL.type
        val ppbodyparamFatgrade = bodyDetailModel.PPBodyParam_FatGrade
        if (ppbodyparamFatgrade != null) {
            buildBodyDetailParamPercent(obeLevel, ppbodyparamFatgrade)
        }
        obeLevel.valueUnit = ""
        obeLevel.indexIconId = R.drawable.pic_icon_data_feipangdengji_n
        obeLevel.indexGradeCircularBg = R.drawable.bg_progress_6_end_bad
        obeLevel.bodyFat = bodyFat
        bodyItemList.add(obeLevel)

        //17.健康评估
        val bodyHealth = BodyFatItemVo()
        bodyHealth.indexType = BodyFatItemType.BODY_HEALTH.type
        val ppbodyparamBodyhealth = bodyDetailModel.PPBodyParam_BodyHealth
        if (ppbodyparamBodyhealth != null) {
            buildBodyDetailBodyHealth(bodyHealth, ppbodyparamBodyhealth)
        }
        bodyHealth.valueUnit = ""
        bodyHealth.indexIconId = R.drawable.pic_icon_data_jiankangpinggu_n
        bodyHealth.bodyFat = bodyFat
        bodyItemList.add(bodyHealth)

        //11.身体年龄
        val bodyAge = BodyFatItemVo()
        bodyAge.indexType = BodyFatItemType.BODY_AGE.type
        val ppbodyparamPhysicalagevalue = bodyDetailModel.PPBodyParam_physicalAgeValue
        if (ppbodyparamPhysicalagevalue != null) {
            buildBodyDetailParamPercent(bodyAge, ppbodyparamPhysicalagevalue)
        }
        bodyAge.valueUnit = bodyAgeUnitString
        bodyAge.indexIconId = R.drawable.pic_icon_data_shentinianling_n
        bodyAge.indexGradeCircularBg = R.drawable.bg_progress_2_end_bad
        bodyAge.bodyFat = bodyFat
        bodyItemList.add(bodyAge)

        //12.身体得分
        val bodyGrade = BodyFatItemVo()
        bodyGrade.indexType = BodyFatItemType.BODY_GRADE.type
        val ppbodyparamBodyscore = bodyDetailModel.PPBodyParam_BodyScore
        if (ppbodyparamBodyscore != null) {
            buildBodyDetailParamPercent(bodyGrade, ppbodyparamBodyscore)
        }
        bodyGrade.valueUnit = bodyScoreUnitString
        bodyGrade.indexIconId = R.drawable.pic_icon_data_shentidefen_n
        bodyGrade.indexGradeCircularBg = R.drawable.bg_progress_4_end_good
        bodyGrade.bodyFat = bodyFat
        bodyItemList.add(bodyGrade)
        return bodyItemList.toMutableList()
    }

    /**
     * 没有标准的数据构建
     */
    private fun buildNoStandard(
        dCI: BodyFatItemVo,
        ppbodyparamDci: PPBodyDetailInfoModel
    ) {
        dCI.indexName = ppbodyparamDci.bodyParamNameString
        dCI.indexIntroduction = ppbodyparamDci.introductionString
        dCI.value = "${keep1Point1(ppbodyparamDci.currentValue)}"
    }

    /**
     * 构建body详情信息
     * kg为主的单位除了体重
     */
    private fun buildBodyDetailParamKg(
        bodyFatItemVo: BodyFatItemVo,
        ppbodyparam: PPBodyDetailInfoModel,
        ppUnitType: PPUnitType,
        accuracyType: Int
    ) {
        bodyFatItemVo.indexName = ppbodyparam.bodyParamNameString
        bodyFatItemVo.indexGradeStr = ppbodyparam.standardTitle
        bodyFatItemVo.indexGradeColor16Str = ppbodyparam.standColor
        bodyFatItemVo.indexEevaluation = ppbodyparam.standeEvaluation
        bodyFatItemVo.indexSuggestion = ppbodyparam.standSuggestion
        bodyFatItemVo.indexIntroduction = ppbodyparam.introductionString
        val weightValueD =
            PPUtil.getWeightValueD(ppUnitType, ppbodyparam.currentValue, accuracyType)
        if (weightValueD.contains(":")) {
            bodyFatItemVo.value = "$weightValueD"
        } else {
            bodyFatItemVo.value = "${UtilTooth.keep1Point1(Math.abs(weightValueD.toDouble()))}"
        }
        val bodyItemProgress = BodyFatIndexStandardProgressUtils.getBodyItemProgress(
            ppbodyparam.currentStandard,
            ppbodyparam.currentValue,
            ppbodyparam.standardArray
        )
        bodyFatItemVo.progress = bodyItemProgress
    }

    /**
     * 构建body详情信息
     * weight为主的单位
     */
    private fun buildBodyDetailParamWeight(
        bodyFatItemVo: BodyFatItemVo,
        ppbodyparam: PPBodyDetailInfoModel,
        ppUnitType: PPUnitType,
        accuracyType: Int
    ) {
        bodyFatItemVo.indexName = ppbodyparam.bodyParamNameString
        bodyFatItemVo.indexGradeStr = ppbodyparam.standardTitle
        bodyFatItemVo.indexGradeColor16Str = ppbodyparam.standColor
        bodyFatItemVo.indexEevaluation = ppbodyparam.standeEvaluation
        bodyFatItemVo.indexSuggestion = ppbodyparam.standSuggestion
        bodyFatItemVo.indexIntroduction = ppbodyparam.introductionString
        bodyFatItemVo.value =
            PPUtil.getWeightValueD(ppUnitType, ppbodyparam.currentValue, accuracyType)
        val bodyItemProgress = BodyFatIndexStandardProgressUtils.getBodyItemProgress(
            ppbodyparam.currentStandard,
            ppbodyparam.currentValue,
            ppbodyparam.standardArray
        )
        bodyFatItemVo.progress = bodyItemProgress
    }

    /**
     * 构建body详情信息
     * %为主的单位
     */
    private fun buildBodyDetailParamPercent(
        bodyFatItemVo: BodyFatItemVo,
        ppbodyparam: PPBodyDetailInfoModel
    ) {
        bodyFatItemVo.indexName = ppbodyparam.bodyParamNameString
        bodyFatItemVo.indexGradeStr = ppbodyparam.standardTitle
        bodyFatItemVo.indexGradeColor16Str = ppbodyparam.standColor
        bodyFatItemVo.indexEevaluation = ppbodyparam.standeEvaluation
        bodyFatItemVo.indexSuggestion = ppbodyparam.standSuggestion
        bodyFatItemVo.indexIntroduction = ppbodyparam.introductionString
        bodyFatItemVo.value = "${UtilTooth.keep1Point1(ppbodyparam.currentValue)}"
        val bodyItemProgress = BodyFatIndexStandardProgressUtils.getBodyItemProgress(
            ppbodyparam.currentStandard,
            ppbodyparam.currentValue,
            ppbodyparam.standardArray
        )
        bodyFatItemVo.progress = bodyItemProgress
    }


    /**
     * 构建body详情信息
     * %为主的单位
     */
    private fun buildBodyDetailBodyType(
        bodyFatItemVo: BodyFatItemVo,
        ppbodyparam: PPBodyDetailInfoModel
    ) {
        bodyFatItemVo.indexName = ppbodyparam.bodyParamNameString
        bodyFatItemVo.indexGradeStr = ppbodyparam.standardTitle
        bodyFatItemVo.indexEevaluation = ppbodyparam.standeEvaluation
        bodyFatItemVo.indexSuggestion = ppbodyparam.standSuggestion
        bodyFatItemVo.indexIntroduction = ppbodyparam.introductionString
        bodyFatItemVo.value = "${ppbodyparam.standardTitle}"
    }

    /**
     * 构建body详情信息
     * %为主的单位
     */
    private fun buildBodyDetailBodyHealth(
        bodyFatItemVo: BodyFatItemVo,
        ppbodyparam: PPBodyDetailInfoModel
    ) {
        bodyFatItemVo.indexName = ppbodyparam.bodyParamNameString
        bodyFatItemVo.indexGradeStr = ""
        bodyFatItemVo.indexEevaluation = ppbodyparam.standeEvaluation
        bodyFatItemVo.indexSuggestion = ppbodyparam.standSuggestion
        bodyFatItemVo.indexIntroduction = ppbodyparam.introductionString
        bodyFatItemVo.value = "${ppbodyparam.standardTitle}"
    }

    /**
     * 获取首页空数据的时候
     */
    fun getBodyFatHomeNullData(weightUnitStr: String): MutableList<BodyFatItemVo?> {
        val bodyItemList: ArrayList<BodyFatItemVo?> = ArrayList()
        //0.体重
        val weight = BodyFatItemVo()
        weight.indexType = BodyFatItemType.WEIGHT.type
        weight.indexName = "Weight_name"
        weight.indexIntroduction = "Weight_introduction"
        weight.value = "0"
        weight.valueUnit = weightUnitStr
        weight.indexIconId = R.drawable.pic_icon_data_tizhong_n
        weight.bodyFat = PPBodyFatModel(PPBluetoothScaleBaseModel())
        bodyItemList.add(weight)

        //2.脂肪率
        val fat = BodyFatItemVo()
        fat.indexType = BodyFatItemType.FAT.type
        fat.indexName = "Bodyfat_name"
        fat.indexIntroduction = "Bodyfat_introduction"
        fat.value = "0"
        fat.valueUnit = "%"
        fat.indexIconId = R.drawable.pic_icon_data_zhifanglv_n
        fat.bodyFat = PPBodyFatModel(PPBluetoothScaleBaseModel())
        bodyItemList.add(fat)
        return bodyItemList
    }

}