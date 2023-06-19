//package com.lefu.bodyindex
//
//
//import com.peng.ppscale.vo.PPBodyBaseMode
//import com.peng.ppscale.vo.PPBodyFatModel
//
///**
// *    @author : whs
// *    e-mail : haisilen@163.com
// *    date   : 2023/3/25 14:58
// *    desc   : 身体数据指标工具类
// */
//object BodyFatIndexUtils {
//
//    enum class BodyFatIndexStandardValue(val typeIndex: Int) {
//        indexGradeStr(0),
//        indexGradeColor16Str(1),
//        indexEevaluation(2),
//        indexSuggestion(3),
//        indexLevel(4)
//    }
//
//    /**
//     * 获取所有的指标数据
//     */
//    fun getBodyIndex(bodyFat: PPBodyFatModel): MutableList<BodyFatItemVo?> {
//        val bodyItemList: ArrayList<BodyFatItemVo?> = ArrayList()
//        //0.体重
//        val weight = BodyFatItemVo()
//        weight.indexType = BodyFatItemType.WEIGHT.type
//        weight.indexName = SettingManagerMMKV.getValueFromJson("Weight_name")
//        val weightLevel = weightLevel(bodyFat.sex, bodyFat.height, bodyFat.weightKg)
//        weight.indexGradeStr = weightLevel[BodyFatIndexStandardValue.indexGradeStr.typeIndex]
//        weight.indexGradeColor16Str =
//            weightLevel[BodyFatIndexStandardValue.indexGradeColor16Str.typeIndex]
//        weight.indexEevaluation = weightLevel[BodyFatIndexStandardValue.indexEevaluation.typeIndex]
//        weight.indexSuggestion = weightLevel[BodyFatIndexStandardValue.indexSuggestion.typeIndex]
//        weight.indexIntroduction = SettingManagerMMKV.getValueFromJson("Weight_introduction")
//        weight.value = getWeightValue(bodyFat.weightKg, bodyFat.accuracyType)
//        weight.valueUnit = getWeightUnit()
//        weight.indexIconId = R.drawable.pic_icon_data_tizhong_n
//        val bodyItemProgress = BodyFatIndexStandardProgressUtils.getBodyItemProgress(
//            weightLevel[BodyFatIndexStandardValue.indexLevel.typeIndex].toInt(),
//            bodyFat.weightKg,
//            BodyFatIndexStandardProgressUtils.getWeightRangeValue(bodyFat.sex, bodyFat.height)
//        )
//        weight.progress = bodyItemProgress
//        weight.indexGradeCircularBg = R.drawable.bg_progress_3_end_bad
//        weight.bodyFat = bodyFat
//        bodyItemList.add(weight)
//
//        //2.脂肪率
//        val fat = BodyFatItemVo()
//        fat.indexType = BodyFatItemType.FAT.type
//        fat.indexName = SettingManagerMMKV.getValueFromJson("Bodyfat_name")
//        val bftLevel = bftLevel(bodyFat.sex, bodyFat.age, bodyFat.fat)
//        fat.indexGradeStr = bftLevel[BodyFatIndexStandardValue.indexGradeStr.typeIndex]
//        fat.indexGradeColor16Str =
//            bftLevel[BodyFatIndexStandardValue.indexGradeColor16Str.typeIndex]
//        fat.indexEevaluation = bftLevel[BodyFatIndexStandardValue.indexEevaluation.typeIndex]
//        fat.indexSuggestion = bftLevel[BodyFatIndexStandardValue.indexSuggestion.typeIndex]
//        fat.indexIntroduction = SettingManagerMMKV.getValueFromJson("Bodyfat_introduction")
//        fat.value = "${keep1Point1(bodyFat.fat)}"
//        fat.valueUnit = "%"
//        fat.indexIconId = R.drawable.pic_icon_data_zhifanglv_n
//        val fatProgress = BodyFatIndexStandardProgressUtils.getBodyItemProgress(
//            bftLevel[BodyFatIndexStandardValue.indexLevel.typeIndex].toInt(),
//            bodyFat.fat,
//            BodyFatIndexStandardProgressUtils.getFatRangValue(bodyFat.sex, bodyFat.age)
//        )
//        fat.progress = fatProgress
//        fat.indexGradeCircularBg = R.drawable.bg_progress_4_end_bad
//        fat.bodyFat = bodyFat
////        fat.indexGradeColorId = getTextColorByDetail(fat.indexGradeStr, context)
//        bodyItemList.add(fat)
//
//
//        //1 bmi
//        val bmi = BodyFatItemVo()
//        bmi.indexType = BodyFatItemType.BMI.type
//        bmi.indexName = SettingManagerMMKV.getValueFromJson("BMI_name")
//        val bmiLevel = bmiLevel(bodyFat.bmi)
//        bmi.indexGradeStr = bmiLevel[BodyFatIndexStandardValue.indexGradeStr.typeIndex]
//        bmi.indexGradeColor16Str =
//            bmiLevel[BodyFatIndexStandardValue.indexGradeColor16Str.typeIndex]
//        bmi.indexEevaluation = bmiLevel[BodyFatIndexStandardValue.indexEevaluation.typeIndex]
//        bmi.indexSuggestion = bmiLevel[BodyFatIndexStandardValue.indexSuggestion.typeIndex]
//        bmi.indexIntroduction = SettingManagerMMKV.getValueFromJson("BMI_introduction")
//        bmi.value = "${UtilTooth.keep1Point1(bodyFat.bmi)}"
//        bmi.valueUnit = ""
//        bmi.indexIconId = R.drawable.pic_icon_data_bmi_n
//        val bmiProgress = BodyFatIndexStandardProgressUtils.getBodyItemProgress(
//            bmiLevel[BodyFatIndexStandardValue.indexLevel.typeIndex].toInt(),
//            bodyFat.bmi,
//            BodyFatIndexStandardProgressUtils.getBmiRangValue()
//        )
//        bmi.progress = bmiProgress
//        bmi.indexGradeCircularBg = R.drawable.bg_progress_4_end_bad
//        bmi.bodyFat = bodyFat
////        bmi.indexGradeColorId = getTextColorByDetail(bmi.indexGradeStr, context)
//        bodyItemList.add(bmi)
//
//        //3.肌肉含量kg
//        val muscle = BodyFatItemVo()
//        muscle.indexType = BodyFatItemType.MUSCLE.type
//        muscle.indexName = SettingManagerMMKV.getValueFromJson("mus_name")
//        val muscleLevel = muscleLevel(bodyFat.sex, bodyFat.height.toDouble(), bodyFat.muscleKg)
//        muscle.indexGradeStr = muscleLevel[BodyFatIndexStandardValue.indexGradeStr.typeIndex]
//        muscle.indexGradeColor16Str =
//            muscleLevel[BodyFatIndexStandardValue.indexGradeColor16Str.typeIndex]
//        muscle.indexEevaluation = muscleLevel[BodyFatIndexStandardValue.indexEevaluation.typeIndex]
//        muscle.indexSuggestion = muscleLevel[BodyFatIndexStandardValue.indexSuggestion.typeIndex]
//        muscle.indexIntroduction = SettingManagerMMKV.getValueFromJson("mus_introduction")
//        muscle.value = getWeightValue(bodyFat.muscleKg, bodyFat.accuracyType)
//        muscle.valueUnit = getWeightUnit()
//        muscle.indexIconId = R.drawable.pic_icon_data_jirouliang_n
//        val muscleProgress = BodyFatIndexStandardProgressUtils.getBodyItemProgress(
//            muscleLevel[BodyFatIndexStandardValue.indexLevel.typeIndex].toInt(),
//            bodyFat.muscleKg,
//            BodyFatIndexStandardProgressUtils.getMuscleRangValue(bodyFat.sex, bodyFat.height)
//        )
//        muscle.progress = muscleProgress
//        muscle.indexGradeCircularBg = R.drawable.bg_progress_3_end_good
//        muscle.bodyFat = bodyFat
//        bodyItemList.add(muscle)
//
//        //7.BMR(基础代谢)
//        val bmr = BodyFatItemVo()
//        bmr.indexType = BodyFatItemType.BMR.type
//        bmr.indexName = SettingManagerMMKV.getValueFromJson("BMR_name")
//        val bmrLevel = bmrLevel(bodyFat.sex, bodyFat.age, bodyFat.weightKg, bodyFat.bmr.toFloat())
//        bmr.indexGradeStr = bmrLevel[BodyFatIndexStandardValue.indexGradeStr.typeIndex]
//        bmr.indexGradeColor16Str =
//            bmrLevel[BodyFatIndexStandardValue.indexGradeColor16Str.typeIndex]
//        bmr.indexEevaluation = bmrLevel[BodyFatIndexStandardValue.indexEevaluation.typeIndex]
//        bmr.indexSuggestion = bmrLevel[BodyFatIndexStandardValue.indexSuggestion.typeIndex]
//        bmr.indexIntroduction = SettingManagerMMKV.getValueFromJson("BMR_introduction")
//        bmr.value = "${bodyFat.bmr}"
//        bmr.valueUnit = "Kcal"
//        bmr.indexIconId = R.drawable.pic_icon_data_bmr_n
//        val bmrProgress = BodyFatIndexStandardProgressUtils.getBodyItemProgress(
//            bmrLevel[BodyFatIndexStandardValue.indexLevel.typeIndex].toInt(),
//            bodyFat.bmr.toDouble(),
//            BodyFatIndexStandardProgressUtils.getBmrRangValue(
//                bodyFat.sex,
//                bodyFat.age,
//                bodyFat.weightKg
//            )
//        )
//        bmr.progress = bmrProgress
//        bmr.indexGradeCircularBg = R.drawable.bg_progress_2_end_good
//        bmr.bodyFat = bodyFat
////        bmr.indexGradeColorId = getTextColorByDetail(bmr.indexGradeStr, context)
//        bodyItemList.add(bmr)
//
//        //28.輸出參數-全身体组成:建议卡路里摄入量 Kcal/day
//        val dCI = BodyFatItemVo()
//        dCI.indexType = BodyFatItemType.DCI.type
//        dCI.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_dCI")
//        dCI.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_dCI_introduction")
//        dCI.value = "${bodyFat.dci}"
//        dCI.valueUnit = "kcal"
//        dCI.indexIconId = R.drawable.pic_icon_data_kalulisheruliang_n
//        dCI.bodyFat = bodyFat
//        bodyItemList.add(dCI)
//
//        //16心率
//        val heartRate = BodyFatItemVo()
//        heartRate.indexType = BodyFatItemType.HEART_RATE.type
//        heartRate.indexName = SettingManagerMMKV.getValueFromJson("heart_name")
//        val heartRateLevel = heartRateLevel(bodyFat.heartRate)
//        heartRate.indexGradeStr = heartRateLevel[BodyFatIndexStandardValue.indexGradeStr.typeIndex]
//        heartRate.indexGradeColor16Str =
//            heartRateLevel[BodyFatIndexStandardValue.indexGradeColor16Str.typeIndex]
//        heartRate.indexEevaluation =
//            heartRateLevel[BodyFatIndexStandardValue.indexEevaluation.typeIndex]
//        heartRate.indexSuggestion =
//            heartRateLevel[BodyFatIndexStandardValue.indexSuggestion.typeIndex]
//        heartRate.indexIntroduction = SettingManagerMMKV.getValueFromJson("heart_introduction")
//        heartRate.value = "${bodyFat.heartRate}"
//        heartRate.valueUnit = "bpm"
//        heartRate.indexIconId = R.drawable.pic_icon_data_xinlv_n
//        val heartRateProgress = BodyFatIndexStandardProgressUtils.getBodyItemProgress(
//            heartRateLevel[BodyFatIndexStandardValue.indexLevel.typeIndex].toInt(),
//            bodyFat.heartRate.toDouble(),
//            BodyFatIndexStandardProgressUtils.getHeartRangValue()
//        )
//        heartRate.progress = heartRateProgress
//        heartRate.indexGradeCircularBg = R.drawable.bg_progress_5_end_bad
//        heartRate.bodyFat = bodyFat
//        bodyItemList.add(heartRate)
//
//        //4.水分率
//        val water = BodyFatItemVo()
//        water.indexType = BodyFatItemType.WATER.type
//        water.indexName = SettingManagerMMKV.getValueFromJson("water_name")
//        val waterLevel = waterLevel(bodyFat.sex, bodyFat.waterPercentage)
//        water.indexGradeStr = waterLevel[BodyFatIndexStandardValue.indexGradeStr.typeIndex]
//        water.indexGradeColor16Str =
//            waterLevel[BodyFatIndexStandardValue.indexGradeColor16Str.typeIndex]
//        water.indexEevaluation = waterLevel[BodyFatIndexStandardValue.indexEevaluation.typeIndex]
//        water.indexSuggestion = waterLevel[BodyFatIndexStandardValue.indexSuggestion.typeIndex]
//        water.indexIntroduction = SettingManagerMMKV.getValueFromJson("water_introduction")
//        water.value = "${bodyFat.waterPercentage}"
//        water.valueUnit = "%"
//        water.indexIconId = R.drawable.pic_icon_data_shuifenlv_n
//        val waterProgress = BodyFatIndexStandardProgressUtils.getBodyItemProgress(
//            waterLevel[BodyFatIndexStandardValue.indexLevel.typeIndex].toInt(),
//            bodyFat.waterPercentage,
//            BodyFatIndexStandardProgressUtils.getWaterRangValue(bodyFat.sex)
//        )
//        water.progress = waterProgress
//        water.indexGradeCircularBg = R.drawable.bg_progress_3_end_good
//        water.bodyFat = bodyFat
////        water.indexGradeColorId = getTextColorByDetail(water.indexGradeStr, context)
//        bodyItemList.add(water)
//
//        //8.蛋白质率
//        val protein = BodyFatItemVo()
//        protein.indexType = BodyFatItemType.PROTEIN.type
//        protein.indexName = SettingManagerMMKV.getValueFromJson("proteinPercentage_name")
//        val proteinLevel = getProtein(bodyFat.proteinPercentage)
//        protein.indexGradeStr = proteinLevel[BodyFatIndexStandardValue.indexGradeStr.typeIndex]
//        protein.indexGradeColor16Str =
//            proteinLevel[BodyFatIndexStandardValue.indexGradeColor16Str.typeIndex]
//        protein.indexEevaluation =
//            proteinLevel[BodyFatIndexStandardValue.indexEevaluation.typeIndex]
//        protein.indexSuggestion = proteinLevel[BodyFatIndexStandardValue.indexSuggestion.typeIndex]
//        protein.indexIntroduction =
//            SettingManagerMMKV.getValueFromJson("proteinPercentage_introduction")
//        protein.value = "${keep1Point1(bodyFat.proteinPercentage)}"
//        protein.valueUnit = "%"
//        protein.indexIconId = R.drawable.pic_icon_data_danbaizhi_n
//        val proteinProgress = BodyFatIndexStandardProgressUtils.getBodyItemProgress(
//            proteinLevel[BodyFatIndexStandardValue.indexLevel.typeIndex].toInt(),
//            bodyFat.proteinPercentage,
//            BodyFatIndexStandardProgressUtils.getProteinRangValue()
//        )
//        protein.progress = proteinProgress
//        protein.indexGradeCircularBg = R.drawable.bg_progress_3_end_bad
//        protein.bodyFat = bodyFat
////        protein.indexGradeColorId = getTextColorByDetail(protein.indexGradeStr, context)
//        bodyItemList.add(protein)
//
//        //24.輸出參數-全身体组成:水分量(Kg)
//        val waterKg = BodyFatItemVo()
//        waterKg.indexType = BodyFatItemType.WATER_KG.type
//        waterKg.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_waterKg")
//        waterKg.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_waterKg_introduction")
//        waterKg.value = getWeightValue(bodyFat.waterKg, bodyFat.accuracyType)
//        waterKg.valueUnit = getWeightUnit()
//        waterKg.indexIconId = R.drawable.pic_icon_data_shuifenliang_n
//        waterKg.bodyFat = bodyFat
//        bodyItemList.add(waterKg)
//
//        //25.輸出參數-全身体组成:蛋白质量(Kg)
//        val proteinKg = BodyFatItemVo()
//        proteinKg.indexType = BodyFatItemType.PROTEIN_KG.type
//        proteinKg.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_proteinKg")
//        proteinKg.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_proteinKg_introduction")
//        proteinKg.value = getWeightValue(bodyFat.proteinKg, bodyFat.accuracyType)
//        proteinKg.valueUnit = getWeightUnit()
//        proteinKg.indexIconId = R.drawable.pic_icon_data_danbaizhiliang_n
//        proteinKg.bodyFat = bodyFat
//        bodyItemList.add(proteinKg)
//
//        //23.脂肪量
//        val bodyFatKg = BodyFatItemVo()
//        bodyFatKg.indexType = BodyFatItemType.BODY_FAT_KG.type
//        bodyFatKg.indexName = SettingManagerMMKV.getValueFromJson("BodyFatKg_name")
//        val bodyFatKgLevel = bftKgLevel(bodyFat.sex, bodyFat.age, bodyFat.fat) //和脂肪率是一样的
//        bodyFatKg.indexGradeStr = bodyFatKgLevel[BodyFatIndexStandardValue.indexGradeStr.typeIndex]
//        bodyFatKg.indexGradeColor16Str =
//            bodyFatKgLevel[BodyFatIndexStandardValue.indexGradeColor16Str.typeIndex]
//        bodyFatKg.indexEevaluation =
//            bodyFatKgLevel[BodyFatIndexStandardValue.indexEevaluation.typeIndex]
//        bodyFatKg.indexSuggestion =
//            bodyFatKgLevel[BodyFatIndexStandardValue.indexSuggestion.typeIndex]
//        bodyFatKg.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyFatKg_introduction")
//        bodyFatKg.value = "${getWeightValue(bodyFat.bodyFatKg, bodyFat.accuracyType)}"
//        bodyFatKg.valueUnit = getWeightUnit()
//        bodyFatKg.indexIconId = R.drawable.pic_icon_data_zhifangliang_n
//        bodyFatKg.progress = fatProgress
//        bodyFatKg.indexGradeCircularBg = R.drawable.bg_progress_4_end_bad
//        bodyFatKg.bodyFat = bodyFat
//        bodyItemList.add(bodyFatKg)
//
//        //13.去脂体重
//        val noFatWeight = BodyFatItemVo()
//        noFatWeight.indexType = BodyFatItemType.NO_FAT_WEIGHT.type
//        noFatWeight.indexName = SettingManagerMMKV.getValueFromJson("BodyLBW_name")
//        noFatWeight.value = getWeightValue(bodyFat.loseFatWeightKg, bodyFat.accuracyType)
//        noFatWeight.valueUnit = getWeightUnit()
//        noFatWeight.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyLBW_introduction")
//        noFatWeight.indexIconId = R.drawable.pic_icon_data_quzhitizhong_n
//        noFatWeight.bodyFat = bodyFat
//        bodyItemList.add(noFatWeight)
//
//        //10.皮下脂肪率
//        val subFat = BodyFatItemVo()
//        subFat.indexType = BodyFatItemType.SUB_FAT.type
//        subFat.indexName = SettingManagerMMKV.getValueFromJson("BodySubcutaneousFat_name")
//        val subFatLevel = subFatLevel(bodyFat.sex, bodyFat.vfPercentage)
//        subFat.indexGradeStr = subFatLevel[BodyFatIndexStandardValue.indexGradeStr.typeIndex]
//        subFat.indexGradeColor16Str =
//            subFatLevel[BodyFatIndexStandardValue.indexGradeColor16Str.typeIndex]
//        subFat.indexEevaluation = subFatLevel[BodyFatIndexStandardValue.indexEevaluation.typeIndex]
//        subFat.indexSuggestion = subFatLevel[BodyFatIndexStandardValue.indexSuggestion.typeIndex]
//        subFat.indexIntroduction =
//            SettingManagerMMKV.getValueFromJson("BodySubcutaneousFat_introduction")
//        subFat.value = "${keep1Point1(bodyFat.vfPercentage)}"
//        subFat.valueUnit = "%"
//        subFat.indexIconId = R.drawable.pic_icon_data_pixiazhifanglv_n
//        val subFatProgress = BodyFatIndexStandardProgressUtils.getBodyItemProgress(
//            subFatLevel[BodyFatIndexStandardValue.indexLevel.typeIndex].toInt(),
//            bodyFat.vfPercentage,
//            BodyFatIndexStandardProgressUtils.getSubFatRangValue(bodyFat.sex)
//        )
//        subFat.progress = subFatProgress
//        subFat.indexGradeCircularBg = R.drawable.bg_progress_4_end_bad
//        subFat.bodyFat = bodyFat
//        bodyItemList.add(subFat)
//
//        //18.骨骼肌率
//        val bodySkeletal = BodyFatItemVo()
//        bodySkeletal.indexType = BodyFatItemType.BODY_SKELETAL.type
//        bodySkeletal.indexName = SettingManagerMMKV.getValueFromJson("BodySkeletal_name")
//        val bodySkeletalLevel = bodySkeletalLevel(bodyFat.sex, bodyFat.bonePercentage)
//        bodySkeletal.indexGradeStr =
//            bodySkeletalLevel[BodyFatIndexStandardValue.indexGradeStr.typeIndex]
//        bodySkeletal.indexGradeColor16Str =
//            bodySkeletalLevel[BodyFatIndexStandardValue.indexGradeColor16Str.typeIndex]
//        bodySkeletal.indexEevaluation =
//            bodySkeletalLevel[BodyFatIndexStandardValue.indexEevaluation.typeIndex]
//        bodySkeletal.indexSuggestion =
//            bodySkeletalLevel[BodyFatIndexStandardValue.indexSuggestion.typeIndex]
//        bodySkeletal.indexIntroduction =
//            SettingManagerMMKV.getValueFromJson("BodySkeletal_introduction")
//        bodySkeletal.value = "${keep1Point1(bodyFat.bonePercentage)}"
//        bodySkeletal.valueUnit = "%"
//        bodySkeletal.indexIconId = R.drawable.pic_icon_data_gugejilv_n
//        val bodySkeletalProgress = BodyFatIndexStandardProgressUtils.getBodyItemProgress(
//            bodySkeletalLevel[BodyFatIndexStandardValue.indexLevel.typeIndex].toInt(),
//            bodyFat.bonePercentage,
//            BodyFatIndexStandardProgressUtils.getBodySkeletalRangValue(bodyFat.sex)
//        )
//        bodySkeletal.progress = bodySkeletalProgress
//        bodySkeletal.indexGradeCircularBg = R.drawable.bg_progress_3_end_bad
//        bodySkeletal.bodyFat = bodyFat
//        bodyItemList.add(bodySkeletal)
//
//        //5.内脏脂肪等级
//        val vfal = BodyFatItemVo()
//        vfal.indexType = BodyFatItemType.VFAL.type
//        vfal.indexName = SettingManagerMMKV.getValueFromJson("visfat_name")
//        val visceralLevel = visceralLevel(bodyFat.visceralFat)
//        vfal.indexGradeStr = visceralLevel[BodyFatIndexStandardValue.indexGradeStr.typeIndex]
//        vfal.indexGradeColor16Str =
//            visceralLevel[BodyFatIndexStandardValue.indexGradeColor16Str.typeIndex]
//        vfal.indexEevaluation = visceralLevel[BodyFatIndexStandardValue.indexEevaluation.typeIndex]
//        vfal.indexSuggestion = visceralLevel[BodyFatIndexStandardValue.indexSuggestion.typeIndex]
//        vfal.indexIntroduction = SettingManagerMMKV.getValueFromJson("visfat_introduction")
//        vfal.value = "${bodyFat.visceralFat.toInt()}"
//        vfal.valueUnit = ""
//        vfal.indexIconId = R.drawable.pic_icon_data_neizangzhifangdengji_n
//        val vfalProgress = BodyFatIndexStandardProgressUtils.getBodyItemProgress(
//            visceralLevel[BodyFatIndexStandardValue.indexLevel.typeIndex].toInt(),
//            bodyFat.visceralFat,
//            BodyFatIndexStandardProgressUtils.getVFALRangValue()
//        )
//        vfal.progress = vfalProgress
//        vfal.indexGradeCircularBg = R.drawable.bg_progress_3_start_good
//        vfal.bodyFat = bodyFat
//        bodyItemList.add(vfal)
//
//        //6.骨量
//        val bone = BodyFatItemVo()
//        bone.indexType = BodyFatItemType.BONE.type
//        bone.indexName = SettingManagerMMKV.getValueFromJson("bone_name")
//        val boneLevel = boneLevel(bodyFat.sex, bodyFat.weightKg, bodyFat.boneKg)
//        bone.indexGradeStr = boneLevel[BodyFatIndexStandardValue.indexGradeStr.typeIndex]
//        bone.indexGradeColor16Str =
//            boneLevel[BodyFatIndexStandardValue.indexGradeColor16Str.typeIndex]
//        bone.indexEevaluation = boneLevel[BodyFatIndexStandardValue.indexEevaluation.typeIndex]
//        bone.indexSuggestion = boneLevel[BodyFatIndexStandardValue.indexSuggestion.typeIndex]
//        bone.indexIntroduction = SettingManagerMMKV.getValueFromJson("bone_introduction")
//        bone.value = getWeightValue(bodyFat.boneKg, bodyFat.accuracyType)
//        bone.valueUnit = getWeightUnit()
//        bone.indexIconId = R.drawable.pic_icon_data_guliang_n
//        val boneProgress = BodyFatIndexStandardProgressUtils.getBodyItemProgress(
//            boneLevel[BodyFatIndexStandardValue.indexLevel.typeIndex].toInt(),
//            bodyFat.boneKg,
//            BodyFatIndexStandardProgressUtils.getBoneRangValue(bodyFat.sex, bodyFat.weightKg)
//        )
//        bone.progress = boneProgress
//        bone.indexGradeCircularBg = R.drawable.bg_progress_3_end_good
//        bone.bodyFat = bodyFat
////        bone.indexGradeColorId = getTextColorByDetail(bone.indexGradeStr, context)
//        bodyItemList.add(bone)
//
//        //26.輸出參數-全身体组成:皮下脂肪量(Kg)
//        val bodyFatSubCutKg = BodyFatItemVo()
//        bodyFatSubCutKg.indexType = BodyFatItemType.BODY_FAT_SUBCUT_KG.type
//        bodyFatSubCutKg.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatSubCutKg")
//        bodyFatSubCutKg.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatSubCutKg_introduction")
//        bodyFatSubCutKg.value = getWeightValue(bodyFat.bodyFatSubCutKg, bodyFat.accuracyType)
//        bodyFatSubCutKg.valueUnit = getWeightUnit()
//        bodyFatSubCutKg.indexIconId = R.drawable.pic_icon_data_pixiazifangliang_n
//        bodyFatSubCutKg.bodyFat = bodyFat
//        bodyItemList.add(bodyFatSubCutKg)
//
//        //22.肌肉率
//        val musclePercentage = BodyFatItemVo()
//        musclePercentage.indexType = BodyFatItemType.MUSCLE_RATE.type
//        musclePercentage.indexName = SettingManagerMMKV.getValueFromJson("MusRate_name")
//        val musclePercentageLevel =
//            muscleRateLevel(bodyFat.sex, bodyFat.height.toDouble(), bodyFat.muscleKg)
//        musclePercentage.indexGradeStr =
//            musclePercentageLevel[BodyFatIndexStandardValue.indexGradeStr.typeIndex]
//        musclePercentage.indexGradeColor16Str =
//            musclePercentageLevel[BodyFatIndexStandardValue.indexGradeColor16Str.typeIndex]
//        musclePercentage.indexEevaluation =
//            musclePercentageLevel[BodyFatIndexStandardValue.indexEevaluation.typeIndex]
//        musclePercentage.indexSuggestion =
//            musclePercentageLevel[BodyFatIndexStandardValue.indexSuggestion.typeIndex]
//        musclePercentage.indexIntroduction =
//            SettingManagerMMKV.getValueFromJson("MusRate_introduction")
//        musclePercentage.value = "${keep1Point1(bodyFat.musclePercentage)}"
//        musclePercentage.valueUnit = "%"
//        musclePercentage.indexIconId = R.drawable.pic_icon_data_jiroulv_n
//        musclePercentage.progress = muscleProgress
//        musclePercentage.indexGradeCircularBg = R.drawable.bg_progress_3_end_good
//        musclePercentage.bodyFat = bodyFat
//        bodyItemList.add(musclePercentage)
//
//        //37.//左手脂肪率(%), 分辨率0.1
//        val bodyFatRateLeftArm = BodyFatItemVo()
//        bodyFatRateLeftArm.indexType = BodyFatItemType.BODY_FAT_RATE_LEFT_ARM.type
//        bodyFatRateLeftArm.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatRateLeftArm")
//        bodyFatRateLeftArm.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatRateLeftArm_introduction")
//        bodyFatRateLeftArm.value = "${UtilTooth.keep1Point1(bodyFat.bodyFatRateLeftArm)}"
//        bodyFatRateLeftArm.valueUnit = "%"
//        bodyFatRateLeftArm.indexIconId = R.drawable.pic_icon_data_zuoshouzifanglv_n
//        bodyFatRateLeftArm.bodyFat = bodyFat
//        bodyItemList.add(bodyFatRateLeftArm)
//
//        //27.輸出參數-全身体组成:身体细胞量(Kg)
//        val cellMassKg = BodyFatItemVo()
//        cellMassKg.indexType = BodyFatItemType.CELL_MASS_KG.type
//        cellMassKg.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_cellMassKg")
//        cellMassKg.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_cellMassKg_introduction")
//        cellMassKg.value = getWeightValue(bodyFat.cellMassKg, bodyFat.accuracyType)
//        cellMassKg.valueUnit = getWeightUnit()
//        cellMassKg.indexIconId = R.drawable.pic_icon_data_shentixibaoliang_n
//        cellMassKg.bodyFat = bodyFat
//        bodyItemList.add(cellMassKg)
//
//        //39.//右手脂肪率(%), 分辨率0.1
//        val bodyFatRateRightArm = BodyFatItemVo()
//        bodyFatRateRightArm.indexType = BodyFatItemType.BODY_FAT_RATE_RIGHT_ARM.type
//        bodyFatRateRightArm.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatRateRightArm")
//        bodyFatRateRightArm.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatRateRightArm_introduction")
//        bodyFatRateRightArm.value = "${UtilTooth.keep1Point1(bodyFat.bodyFatRateRightArm)}"
//        bodyFatRateRightArm.valueUnit = "%"
//        bodyFatRateRightArm.indexIconId = R.drawable.pic_icon_data_youshouzifanglv_n
//        bodyFatRateRightArm.bodyFat = bodyFat
//        bodyItemList.add(bodyFatRateRightArm)
//
//        //32.//輸出參數-全身体组成:细胞内水量(kg)
//        val waterICWKg = BodyFatItemVo()
//        waterICWKg.indexType = BodyFatItemType.WATER_ICW_KG.type
//        waterICWKg.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_waterICWKg")
//        waterICWKg.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_waterICWKg_introduction")
//        waterICWKg.value = getWeightValue(bodyFat.waterICWKg,bodyFat.accuracyType)
//        waterICWKg.valueUnit = getWeightUnit()
//        waterICWKg.indexIconId = R.drawable.pic_icon_data_xibaoneishuiliang_n
//        waterICWKg.bodyFat = bodyFat
//        bodyItemList.add(waterICWKg)
//
//        //41.//躯干脂肪率(%), 分辨率0.1
//        val bodyFatRateTrunk = BodyFatItemVo()
//        bodyFatRateTrunk.indexType = BodyFatItemType.BODY_FAT_RATE_TRUNK.type
//        bodyFatRateTrunk.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatRateTrunk")
//        bodyFatRateTrunk.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatRateTrunk_introduction")
//        bodyFatRateTrunk.value = "${UtilTooth.keep1Point1(bodyFat.bodyFatRateTrunk)}"
//        bodyFatRateTrunk.valueUnit = "%"
//        bodyFatRateTrunk.indexIconId = R.drawable.pic_icon_data_quganzifanglv_n
//        bodyFatRateTrunk.bodyFat = bodyFat
//        bodyItemList.add(bodyFatRateTrunk)
//
//        //31.輸出參數-全身体组成:细胞外水量(kg)
//        val waterECWKg = BodyFatItemVo()
//        waterECWKg.indexType = BodyFatItemType.WATER_ECW_KG.type
//        waterECWKg.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_waterECWKg")
//        waterECWKg.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_waterECWKg_introduction")
//        waterECWKg.value = getWeightValue(bodyFat.waterECWKg,bodyFat.accuracyType)
//        waterECWKg.valueUnit = getWeightUnit()
//        waterECWKg.indexIconId = R.drawable.pic_icon_data_xibaowaishuiliang_n
//        waterECWKg.bodyFat = bodyFat
//        bodyItemList.add(waterECWKg)
//
//        //38.//左脚脂肪率(%), 分辨率0.1
//        val bodyFatRateLeftLeg = BodyFatItemVo()
//        bodyFatRateLeftLeg.indexType = BodyFatItemType.BODY_FAT_RATE_LEFT_LEG.type
//        bodyFatRateLeftLeg.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatRateLeftLeg")
//        bodyFatRateLeftLeg.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatRateLeftLeg_introduction")
//        bodyFatRateLeftLeg.value = "${UtilTooth.keep1Point1(bodyFat.bodyFatRateLeftLeg)}"
//        bodyFatRateLeftLeg.valueUnit = "%"
//        bodyFatRateLeftLeg.indexIconId = R.drawable.pic_icon_data_zuojiaozifanglv_n
//        bodyFatRateLeftLeg.bodyFat = bodyFat
//        bodyItemList.add(bodyFatRateLeftLeg)
//
//        //29.輸出參數-全身体组成:无机盐量(Kg)
//        val mineralKg = BodyFatItemVo()
//        mineralKg.indexType = BodyFatItemType.MINERAL_KG.type
//        mineralKg.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_mineralKg")
//        mineralKg.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_mineralKg_introduction")
//        mineralKg.value = getWeightValue(bodyFat.mineralKg, bodyFat.accuracyType)
//        mineralKg.valueUnit = getWeightUnit()
//        mineralKg.indexIconId = R.drawable.pic_icon_data_wujiyanliang_n
//        mineralKg.bodyFat = bodyFat
//        bodyItemList.add(mineralKg)
//
//        //40.//右脚脂肪率(%), 分辨率0.1
//        val bodyFatRateRightLeg = BodyFatItemVo()
//        bodyFatRateRightLeg.indexType = BodyFatItemType.BODY_FAT_RATE_RIGHT_LEG.type
//        bodyFatRateRightLeg.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatRateRightLeg")
//        bodyFatRateRightLeg.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatRateRightLeg_introduction")
//        bodyFatRateRightLeg.value = "${UtilTooth.keep1Point1(bodyFat.bodyFatRateRightLeg)}"
//        bodyFatRateRightLeg.valueUnit = "%"
//        bodyFatRateRightLeg.indexIconId = R.drawable.pic_icon_data_youjiaozifanglv_n
//        bodyFatRateRightLeg.bodyFat = bodyFat
//        bodyItemList.add(bodyFatRateRightLeg)
//
//        //42.//左手肌肉量(kg), 分辨率0.1, 范围0.0 ~ 200kg
//        val muscleKgLeftArm = BodyFatItemVo()
//        muscleKgLeftArm.indexType = BodyFatItemType.MUSCLE_KG_LEFT_ARM.type
//        muscleKgLeftArm.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_muscleKgLeftArm")
//        muscleKgLeftArm.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_muscleKgLeftArm_introduction")
//        muscleKgLeftArm.value = getWeightValue(bodyFat.muscleKgLeftArm,bodyFat.accuracyType)
//        muscleKgLeftArm.valueUnit = getWeightUnit()
//        muscleKgLeftArm.indexIconId = R.drawable.pic_icon_data_zuoshoujirouliang_n
//        muscleKgLeftArm.bodyFat = bodyFat
//        bodyItemList.add(muscleKgLeftArm)
//
//
//        //33.//左手脂肪量(kg), 分辨率0.1
//        val bodyFatKgLeftArm = BodyFatItemVo()
//        bodyFatKgLeftArm.indexType = BodyFatItemType.BODY_FAT_KG_LEFT_ARM.type
//        bodyFatKgLeftArm.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatKgLeftArm")
//        bodyFatKgLeftArm.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatKgLeftArm_introduction")
//        bodyFatKgLeftArm.value = getWeightValue(bodyFat.bodyFatKgLeftArm,bodyFat.accuracyType)
//        bodyFatKgLeftArm.valueUnit = "%"
//        bodyFatKgLeftArm.indexIconId = R.drawable.pic_icon_data_zuoshouzhifangliang_n
//        bodyFatKgLeftArm.bodyFat = bodyFat
//        bodyItemList.add(bodyFatKgLeftArm)
//
//        //44.//右手肌肉量(kg), 分辨率0.1, 范围0.0 ~ 200kg
//        val muscleKgRightArm = BodyFatItemVo()
//        muscleKgRightArm.indexType = BodyFatItemType.MUSCLE_KG_RIGHT_ARM.type
//        muscleKgRightArm.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_muscleKgRightArm")
//        muscleKgRightArm.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_muscleKgRightArm_introduction")
//        muscleKgRightArm.value = getWeightValue(bodyFat.muscleKgRightArm,bodyFat.accuracyType)
//        muscleKgRightArm.valueUnit = getWeightUnit()
//        muscleKgRightArm.indexIconId = R.drawable.pic_icon_data_youshoujirouliang_n
//        muscleKgRightArm.bodyFat = bodyFat
//        bodyItemList.add(muscleKgRightArm)
//
//        //35.//右手脂肪量(kg), 分辨率0.1
//        val bodyFatKgRightArm = BodyFatItemVo()
//        bodyFatKgRightArm.indexType = BodyFatItemType.BODY_FAT_KG_RIGHT_ARM.type
//        bodyFatKgRightArm.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatKgRightArm")
//        bodyFatKgRightArm.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatKgRightArm_introduction")
//        bodyFatKgRightArm.value = getWeightValue(bodyFat.bodyFatKgRightArm,bodyFat.accuracyType)
//        bodyFatKgRightArm.valueUnit = getWeightUnit()
//        bodyFatKgRightArm.indexIconId = R.drawable.pic_icon_data_youshouzhifangliang_n
//        bodyFatKgRightArm.bodyFat = bodyFat
//        bodyItemList.add(bodyFatKgRightArm)
//
//        //46.//躯干肌肉量(kg), 分辨率0.1, 范围0.0 ~ 200kg
//        val muscleKgTrunk = BodyFatItemVo()
//        muscleKgTrunk.indexType = BodyFatItemType.MUSCLE_KG_TRUNK.type
//        muscleKgTrunk.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_muscleKgTrunk")
//        muscleKgTrunk.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_muscleKgTrunk_introduction")
//        muscleKgTrunk.value = getWeightValue(bodyFat.muscleKgTrunk,bodyFat.accuracyType)
//        muscleKgTrunk.valueUnit = getWeightUnit()
//        muscleKgTrunk.indexIconId = R.drawable.pic_icon_data_quganjirouliang_n
//        muscleKgTrunk.bodyFat = bodyFat
//        bodyItemList.add(muscleKgTrunk)
//
//        //37.//躯干脂肪量(kg), 分辨率0.1
//        val bodyFatKgTrunk = BodyFatItemVo()
//        bodyFatKgTrunk.indexType = BodyFatItemType.BODY_FAT_KG_TRUNK.type
//        bodyFatKgTrunk.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatKgTrunk")
//        bodyFatKgTrunk.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatKgTrunk_introduction")
//        bodyFatKgTrunk.value = getWeightValue(bodyFat.bodyFatKgTrunk,bodyFat.accuracyType)
//        bodyFatKgTrunk.valueUnit = getWeightUnit()
//        bodyFatKgTrunk.indexIconId = R.drawable.pic_icon_data_quganzhifangliang_n
//        bodyFatKgTrunk.bodyFat = bodyFat
//        bodyItemList.add(bodyFatKgTrunk)
//
//        //43.//左脚肌肉量(kg), 分辨率0.1, 范围0.0 ~ 200kg
//        val muscleKgLeftLeg = BodyFatItemVo()
//        muscleKgLeftLeg.indexType = BodyFatItemType.MUSCLE_KG_LEFT_LEG.type
//        muscleKgLeftLeg.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_muscleKgLeftLeg")
//        muscleKgLeftLeg.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_muscleKgLeftLeg_introduction")
//        muscleKgLeftLeg.value = getWeightValue(bodyFat.muscleKgLeftLeg,bodyFat.accuracyType)
//        muscleKgLeftLeg.valueUnit = getWeightUnit()
//        muscleKgLeftLeg.indexIconId = R.drawable.pic_icon_data_zuojiaojirouliang_n
//        muscleKgLeftLeg.bodyFat = bodyFat
//        bodyItemList.add(muscleKgLeftLeg)
//
//        //34.//左脚脂肪量(kg), 分辨率0.1
//        val bodyFatKgLeftLeg = BodyFatItemVo()
//        bodyFatKgLeftLeg.indexType = BodyFatItemType.BODY_FAT_KG_LEFT_LEG.type
//        bodyFatKgLeftLeg.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatKgLeftLeg")
//        bodyFatKgLeftLeg.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatKgLeftLeg_introduction")
//        bodyFatKgLeftLeg.value = getWeightValue(bodyFat.bodyFatKgLeftLeg,bodyFat.accuracyType)
//        bodyFatKgLeftLeg.valueUnit = getWeightUnit()
//        bodyFatKgLeftLeg.indexIconId = R.drawable.pic_icon_data_zuojiaozhifangliang_n
//        bodyFatKgLeftLeg.bodyFat = bodyFat
//        bodyItemList.add(bodyFatKgLeftLeg)
//
//        //45.//右脚肌肉量(kg), 分辨率0.1, 范围0.0 ~ 200kg
//        val muscleKgRightLeg = BodyFatItemVo()
//        muscleKgRightLeg.indexType = BodyFatItemType.MUSCLE_KG_RIGHT_LEG.type
//        muscleKgRightLeg.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_muscleKgRightLeg")
//        muscleKgRightLeg.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_muscleKgRightLeg_introduction")
//        muscleKgRightLeg.value = getWeightValue(bodyFat.muscleKgRightLeg,bodyFat.accuracyType)
//        muscleKgRightLeg.valueUnit = getWeightUnit()
//        muscleKgRightLeg.indexIconId = R.drawable.pic_icon_data_youjiaojirouliang_n
//        muscleKgRightLeg.bodyFat = bodyFat
//        bodyItemList.add(muscleKgRightLeg)
//
//        //36.//右脚脂肪量(kg), 分辨率0.1
//        val bodyFatKgRightLeg = BodyFatItemVo()
//        bodyFatKgRightLeg.indexType = BodyFatItemType.BODY_FAT_KG_RIGHT_LEG.type
//        bodyFatKgRightLeg.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatKgRightLeg")
//        bodyFatKgRightLeg.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_bodyFatKgRightLeg_introduction")
//        bodyFatKgRightLeg.value = getWeightValue(bodyFat.bodyFatKgRightLeg,bodyFat.accuracyType)
//        bodyFatKgRightLeg.valueUnit = getWeightUnit()
//        bodyFatKgRightLeg.indexIconId = R.drawable.pic_icon_data_youjiaozhifangliang_n
//        bodyFatKgRightLeg.bodyFat = bodyFat
//        bodyItemList.add(bodyFatKgRightLeg)
//
//        //21.肌肉控制量
//        val bodyMuscleControl = BodyFatItemVo()
//        bodyMuscleControl.indexType = BodyFatItemType.MUSCLE_CONTROL.type
//        bodyMuscleControl.indexName = SettingManagerMMKV.getValueFromJson("MuscleControl_name")
//        bodyMuscleControl.indexIntroduction =
//            SettingManagerMMKV.getValueFromJson("MuscleControl_introduction")
//        bodyMuscleControl.value = Math.abs(getWeightValue(bodyFat.bodyMuscleControl, bodyFat.accuracyType).toDouble()).toString()
//        bodyMuscleControl.valueUnit = getWeightUnit()
//        bodyMuscleControl.indexIconId = R.drawable.pic_icon_data_jiroukongzhiliang_n
//        bodyMuscleControl.bodyFat = bodyFat
//        bodyItemList.add(bodyMuscleControl)
//
//        //20.脂肪控制量
//        val fatControlKg = BodyFatItemVo()
//        fatControlKg.indexType = BodyFatItemType.FAT_CONTROL_KG.type
//        fatControlKg.indexName = SettingManagerMMKV.getValueFromJson("BodyControlLiang_name")
//        fatControlKg.indexIntroduction =
//            SettingManagerMMKV.getValueFromJson("BodyControlLiang_introduction")
//        fatControlKg.value = Math.abs(getWeightValue(bodyFat.fatControlKg, bodyFat.accuracyType).toDouble()).toString()
//        fatControlKg.valueUnit = getWeightUnit()
//        fatControlKg.indexIconId = R.drawable.pic_icon_data_zhifangkongzhiliang_n
//        fatControlKg.bodyFat = bodyFat
//        bodyItemList.add(fatControlKg)
//
//        //15.标准体重
//        val idealWeight = BodyFatItemVo()
//        idealWeight.indexType = BodyFatItemType.IDEAL_WEIGHT.type
//        idealWeight.indexName = SettingManagerMMKV.getValueFromJson("Bodystandard_name")
//        idealWeight.value = getWeightValue(bodyFat.bodyStandardWeightKg, bodyFat.accuracyType)
//        idealWeight.valueUnit = getWeightUnit()
//        idealWeight.indexIntroduction =
//            SettingManagerMMKV.getValueFromJson("Bodystandard_introduction")
//        idealWeight.indexIconId = R.drawable.pic_icon_data_biaozhuntizhong_n
//        idealWeight.bodyFat = bodyFat
//        bodyItemList.add(idealWeight)
//
//        //19.体重控制
//        val controlWeightKg = BodyFatItemVo()
//        controlWeightKg.indexType = BodyFatItemType.CONTROL_WEIGHT.type
//        controlWeightKg.indexName = SettingManagerMMKV.getValueFromJson("BodyControl_name")
//        controlWeightKg.indexIntroduction =
//            SettingManagerMMKV.getValueFromJson("BodyControl_introduction")
//        //取绝对值
//        controlWeightKg.value = Math.abs(getWeightValue(bodyFat.controlWeightKg, bodyFat.accuracyType).toDouble()).toString()
//        controlWeightKg.valueUnit = getWeightUnit()
//        controlWeightKg.indexIconId = R.drawable.pic_icon_data_kongzhitizhong_n
//        controlWeightKg.bodyFat = bodyFat
//        bodyItemList.add(controlWeightKg)
//
//        //30.輸出參數-评价建议: 肥胖度(%)
//        val obesity = BodyFatItemVo()
//        obesity.indexType = BodyFatItemType.OBESITY.type
//        obesity.indexName = SettingManagerMMKV.getValueFromJson("BodyParam_obesity")
//        obesity.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyParam_obesity_introduction")
//        obesity.value = "${UtilTooth.keep1Point1(bodyFat.obesity)}"
//        obesity.valueUnit = "%"
//        obesity.indexIconId = R.drawable.pic_icon_data_feipangdu_n
//        obesity.bodyFat = bodyFat
//        bodyItemList.add(obesity)
//
//        //14.身体类型
//        val bodyType = BodyFatItemVo()
//        bodyType.indexType = BodyFatItemType.BODY_TYPE.type
//        bodyType.indexName = SettingManagerMMKV.getValueFromJson("BodyType_name")
//        val bodyfatType = getBodyfatType(bodyFat.bodyType)
//        bodyType.indexEevaluation =
//            bodyfatType[BodyFatIndexStandardValue.indexEevaluation.typeIndex]
//        bodyType.indexSuggestion = bodyfatType[BodyFatIndexStandardValue.indexSuggestion.typeIndex]
//        bodyType.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyType_introduction")
//        bodyType.value = bodyfatType[BodyFatIndexStandardValue.indexGradeStr.typeIndex]
//        bodyType.valueUnit = ""
//        bodyType.indexIconId = R.drawable.pic_icon_data_shentileixing_n
//        bodyType.bodyFat = bodyFat
//        bodyItemList.add(bodyType)
//
//        //9.肥胖等级
//        val obeLevel = BodyFatItemVo()
//        obeLevel.indexType = BodyFatItemType.OBE_LEVEL.type
//        obeLevel.indexName = SettingManagerMMKV.getValueFromJson("FatGrade_name")
//        val obsLevelDetail = getObsLevelDetail(bodyFat.bmi)
//        obeLevel.indexGradeStr = obsLevelDetail[BodyFatIndexStandardValue.indexGradeStr.typeIndex]
//        obeLevel.indexGradeColor16Str =
//            obsLevelDetail[BodyFatIndexStandardValue.indexGradeColor16Str.typeIndex]
//        obeLevel.indexEevaluation =
//            obsLevelDetail[BodyFatIndexStandardValue.indexEevaluation.typeIndex]
//        obeLevel.indexSuggestion =
//            obsLevelDetail[BodyFatIndexStandardValue.indexSuggestion.typeIndex]
//        obeLevel.indexIntroduction = SettingManagerMMKV.getValueFromJson("FatGrade_introduction")
//        obeLevel.value = "${keep1Point1(bodyFat.bmi)}"
//        obeLevel.valueUnit = ""
//        obeLevel.indexIconId = R.drawable.pic_icon_data_feipangdengji_n
//        val obeLevelProgress = BodyFatIndexStandardProgressUtils.getBodyItemProgress(
//            obsLevelDetail[BodyFatIndexStandardValue.indexLevel.typeIndex].toInt(),
//            bodyFat.bmi,
//            BodyFatIndexStandardProgressUtils.getObsRangValue()
//        )
//        obeLevel.progress = obeLevelProgress
//        obeLevel.indexGradeCircularBg = R.drawable.bg_progress_6_end_bad
//        obeLevel.bodyFat = bodyFat
//        bodyItemList.add(obeLevel)
//
//        //17.健康评估
//        val bodyHealth = BodyFatItemVo()
//        bodyHealth.indexType = BodyFatItemType.BODY_HEALTH.type
//        bodyHealth.indexName = SettingManagerMMKV.getValueFromJson("BodyHealth_name")
//        val bodyHealthAssessment = bodyHealthAssessment(bodyFat.bodyHealth)
//        bodyHealth.indexGradeStr = ""
//        bodyHealth.indexGradeColor16Str =
//            bodyHealthAssessment[BodyFatIndexStandardValue.indexGradeColor16Str.typeIndex]
//        bodyHealth.indexEevaluation =
//            bodyHealthAssessment[BodyFatIndexStandardValue.indexEevaluation.typeIndex]
//        bodyHealth.indexSuggestion =
//            bodyHealthAssessment[BodyFatIndexStandardValue.indexSuggestion.typeIndex]
//        bodyHealth.indexIntroduction = ""
//        bodyHealth.value = bodyHealthAssessment[BodyFatIndexStandardValue.indexGradeStr.typeIndex]
//        bodyHealth.valueUnit = ""
//        bodyHealth.indexIconId = R.drawable.pic_icon_data_jiankangpinggu_n
//        bodyHealth.bodyFat = bodyFat
//        bodyItemList.add(bodyHealth)
//
//        //11.身体年龄
//        val bodyAge = BodyFatItemVo()
//        bodyAge.indexType = BodyFatItemType.BODY_AGE.type
//        bodyAge.indexName = SettingManagerMMKV.getValueFromJson("physicalAgeValue_name")
//        val bodyAgeLevel = bodyAgeLevel(bodyFat.age, bodyFat.bodyAge)
//        bodyAge.indexGradeStr = bodyAgeLevel[BodyFatIndexStandardValue.indexGradeStr.typeIndex]
//        bodyAge.indexGradeColor16Str =
//            bodyAgeLevel[BodyFatIndexStandardValue.indexGradeColor16Str.typeIndex]
//        bodyAge.indexEevaluation =
//            bodyAgeLevel[BodyFatIndexStandardValue.indexEevaluation.typeIndex]
//        bodyAge.indexSuggestion = bodyAgeLevel[BodyFatIndexStandardValue.indexSuggestion.typeIndex]
//        bodyAge.indexIntroduction =
//            SettingManagerMMKV.getValueFromJson("physicalAgeValue_introduction")
//        bodyAge.value = "${bodyFat.bodyAge}"
//        bodyAge.valueUnit = SettingManagerMMKV.getValueFromJson("SUI")
//        bodyAge.indexIconId = R.drawable.pic_icon_data_shentinianling_n
//        val bodyAgeProgress = BodyFatIndexStandardProgressUtils.getBodyItemProgress(
//            bodyAgeLevel[BodyFatIndexStandardValue.indexLevel.typeIndex].toInt(),
//            bodyFat.bodyAge.toDouble(),
//            BodyFatIndexStandardProgressUtils.getAgeRangValue(bodyFat.age)
//        )
//        bodyAge.progress = bodyAgeProgress
//        bodyAge.indexGradeCircularBg = R.drawable.bg_progress_2_end_bad
//        bodyAge.bodyFat = bodyFat
//        bodyItemList.add(bodyAge)
//
//        //12.身体得分
//        val bodyGrade = BodyFatItemVo()
//        bodyGrade.indexType = BodyFatItemType.BODY_GRADE.type
//        bodyGrade.indexName = SettingManagerMMKV.getValueFromJson("BodyScore_name")
//        val bodyScoreLevel = bodyScoreLevel(bodyFat.bodyScore.toDouble())
//        bodyGrade.indexGradeStr = bodyScoreLevel[BodyFatIndexStandardValue.indexGradeStr.typeIndex]
//        bodyGrade.indexGradeColor16Str =
//            bodyScoreLevel[BodyFatIndexStandardValue.indexGradeColor16Str.typeIndex]
//        bodyGrade.indexEevaluation =
//            bodyScoreLevel[BodyFatIndexStandardValue.indexEevaluation.typeIndex]
//        bodyGrade.indexSuggestion =
//            bodyScoreLevel[BodyFatIndexStandardValue.indexSuggestion.typeIndex]
//        bodyGrade.indexIntroduction = SettingManagerMMKV.getValueFromJson("BodyScore_introduction")
//        bodyGrade.value = "${bodyFat.bodyScore}"
//        bodyGrade.valueUnit = SettingManagerMMKV.getValueFromJson("FEN")
//        bodyGrade.indexIconId = R.drawable.pic_icon_data_shentidefen_n
//        val bodyGradeProgress = BodyFatIndexStandardProgressUtils.getBodyItemProgress(
//            bodyScoreLevel[BodyFatIndexStandardValue.indexLevel.typeIndex].toInt(),
//            bodyFat.bodyScore.toDouble(),
//            BodyFatIndexStandardProgressUtils.getBodyScoreRangValue()
//        )
//        bodyGrade.progress = bodyGradeProgress
//        bodyGrade.indexGradeCircularBg = R.drawable.bg_progress_4_end_good
//        bodyGrade.bodyFat = bodyFat
//        bodyItemList.add(bodyGrade)
//        return bodyItemList.toMutableList()
//    }
//
//    /**
//     * 获取首页空数据的时候
//     */
//    fun getBodyFatHomeNullData(): MutableList<BodyFatItemVo?> {
//        val bodyItemList: ArrayList<BodyFatItemVo?> = ArrayList()
//        //0.体重
//        val weight = BodyFatItemVo()
//        weight.indexType = BodyFatItemType.WEIGHT.type
//        weight.indexName = SettingManagerMMKV.getValueFromJson("Weight_name")
//        weight.indexIntroduction = SettingManagerMMKV.getValueFromJson("Weight_introduction")
//        weight.value = "0"
//        weight.valueUnit = getWeightUnit()
//        weight.indexIconId = R.drawable.icon_data_tizhong_n
//        weight.bodyFat = BodyFat()
//        bodyItemList.add(weight)
//
//        //2.脂肪率
//        val fat = BodyFatItemVo()
//        fat.indexType = BodyFatItemType.FAT.type
//        fat.indexName = SettingManagerMMKV.getValueFromJson("Bodyfat_name")
//        fat.indexIntroduction = SettingManagerMMKV.getValueFromJson("Bodyfat_introduction")
//        fat.value = "0"
//        fat.valueUnit = "%"
//        fat.indexIconId = R.drawable.icon_data_zhifanglv_n
//        fat.bodyFat = BodyFat()
//        bodyItemList.add(fat)
//        return bodyItemList
//    }
//
//}