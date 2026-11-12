package com.lefu.ppblutoothkit.calculate

import android.content.Context
import com.lefu.ppbase.PPScaleDefine
import com.lefu.ppbase.PPScaleHelper
import com.lefu.ppbase.util.PPUtil
import com.lefu.ppbase.vo.PPUnitType
import com.lefu.ppblutoothkit.R
import com.lefu.ppcalculate.PPBodyFatModel
import com.lefu.ppcalculate.data.PPBodyDetailInfoModel
import com.lefu.ppcalculate.vo.BodyFatErrorType
import com.lefu.ppcalculate.vo.PPBodyDetailModel
import com.lefu.toolsutils.language.JsonLanguageDefaultValueUtils

data class MeasureResultItem(
    val name: String,
    val value: String,
    val unit: String,
    val status: String,
    val isTextValue: Boolean = false
)

/**
 * 把 [PPBodyFatModel] 转换成「测量结果」页的数据项。
 *
 * - 8 电极：PPBodyDetailModel 排序列表，过滤掉没有名称的脚长后共 57 项
 * - 4 电极：对齐参考页的 25 项（含心率，不含脚长、标准体重）
 * - 计算失败：只显示体重、BMI
 */
object MeasureResultBuilder {

    private const val NO_VALUE = "--"

    private val TEXT_KEYS = setOf("ppBodyType", "PPBodyFatGrade", "ppBodyHealth")
    private val INT_KEYS = setOf("ppHeartRate", "ppVisceralFat", "ppBodyAge", "ppBodyScore")

    /**
     * 参考页的分组顺序（页面不展示分组标题，只用它做稳定排序）。
     */
    private val GROUP_ORDER: Map<String, Int> = buildMap {
        listOf("ppWeightKg", "ppHeartRate", "ppBMI", "ppFootLen").forEach { put(it, 1) }
        listOf(
            "ppFat", "ppBodyfatKg", "ppBodyFatSubCutPercentage", "ppBodyFatSubCutKg",
            "ppVisceralFat", "PPBodyFatGrade", "ppObesity"
        ).forEach { put(it, 2) }
        listOf(
            "ppMusclePercentage", "ppMuscleKg", "ppProteinPercentage", "ppProteinKg",
            "ppBodySkeletal", "ppBodySkeletalKg", "ppBoneKg", "ppSmi"
        ).forEach { put(it, 3) }
        listOf(
            "ppWaterPercentage", "ppWaterKg", "ppWaterICWKg", "ppWaterECWKg",
            "ppCellMassKg", "ppMineralKg"
        ).forEach { put(it, 4) }
        listOf(
            "ppBodyFatRateLeftArm", "ppBodyFatKgLeftArm", "ppMuscleRateLeftArm", "ppMuscleKgLeftArm"
        ).forEach { put(it, 5) }
        listOf(
            "ppBodyFatRateRightArm", "ppBodyFatKgRightArm", "ppMuscleRateRightArm", "ppMuscleKgRightArm"
        ).forEach { put(it, 6) }
        listOf(
            "ppBodyFatRateLeftLeg", "ppBodyFatKgLeftLeg", "ppMuscleRateLeftLeg", "ppMuscleKgLeftLeg"
        ).forEach { put(it, 7) }
        listOf(
            "ppBodyFatRateRightLeg", "ppBodyFatKgRightLeg", "ppMuscleRateRightLeg", "ppMuscleKgRightLeg"
        ).forEach { put(it, 8) }
        listOf(
            "ppBodyFatRateTrunk", "ppBodyFatKgTrunk", "ppMuscleRateTrunk", "ppMuscleKgTrunk"
        ).forEach { put(it, 9) }
        listOf(
            "ppFatControlKg", "ppBodyMuscleControl", "ppControlWeightKg",
            "ppBodyStandardWeightKg", "ppIdealWeightKg", "ppLoseFatWeightKg", "ppDCI", "ppBMR"
        ).forEach { put(it, 10) }
        listOf(
            "ppBodyType", "ppBodyAge", "ppBodyHealth", "ppBodyScore", "ppWHR"
        ).forEach { put(it, 11) }
    }

    fun build(context: Context, fatModel: PPBodyFatModel): List<MeasureResultItem> {
        val detailModel = PPBodyDetailModel(fatModel)
        val calcuteType = fatModel.ppBodyBaseModel?.deviceModel?.deviceCalcuteType?.getType()

        val infos = when {
            fatModel.errorType != BodyFatErrorType.PP_ERROR_TYPE_NONE ->
                listOfNotNull(detailModel.PPBodyParam_Weight, detailModel.PPBodyParam_BMI)

            PPScaleHelper.isCalcute8(calcuteType) ->
                detailModel.ppBodyDetailInfoModelToJsonVo?.lefuBodyData
                    ?.filter { it.bodyParamNameString.isNotBlank() }
                    .orEmpty()

            PPScaleHelper.isInScale(calcuteType) ->
                detailModel.ppBodyDetailInfoModelToJsonVo?.lefuBodyData.orEmpty()

            calcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeUnknow.getType() ||
                    calcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeNeedNot.getType() ->
                listOfNotNull(detailModel.PPBodyParam_Weight, detailModel.PPBodyParam_BMI)

            else -> fourElectrodeItems(detailModel)
        }

        return infos
            .sortedBy { GROUP_ORDER[it.bodyParamKey] ?: Int.MAX_VALUE }
            .map { toItem(context, fatModel, it) }
    }

    /**
     * 4 电极 25 项，与参考页口径一致（含心率，不含脚长、标准体重）。
     */
    private fun fourElectrodeItems(model: PPBodyDetailModel): List<PPBodyDetailInfoModel> {
        return listOfNotNull(
            model.PPBodyParam_Weight,
            model.PPBodyParam_heart,
            model.PPBodyParam_BMI,
            model.PPBodyParam_BodyFat,
            model.PPBodyParam_BodyFatKg,
            model.PPBodyParam_BodySubcutaneousFat,
            model.PPBodyParam_VisFat,
            model.PPBodyParam_FatGrade,
            model.PPBodyParam_MusRate,
            model.PPBodyParam_Mus,
            model.PPBodyParam_proteinPercentage,
            model.PPBodyParam_BodySkeletal,
            model.PPBodyParam_SkeletalMuscleKg,
            model.PPBodyParam_Bone,
            model.PPBodyParam_Water,
            model.PPBodyParam_BodyControlLiang,
            model.PPBodyParam_MuscleControl,
            model.PPBodyParam_BodyControl,
            model.PPBodyParam_BodyIdealWeight,
            model.PPBodyParam_BodyLBW,
            model.PPBodyParam_BMR,
            model.PPBodyParam_BodyType,
            model.PPBodyParam_physicalAgeValue,
            model.PPBodyParam_BodyHealth,
            model.PPBodyParam_BodyScore
        )
    }

    private fun toItem(
        context: Context,
        fatModel: PPBodyFatModel,
        info: PPBodyDetailInfoModel
    ): MeasureResultItem {
        val name = translate(info.bodyParamNameString)
        val textKey = textValueKey(fatModel, info)
        val hasTextValue = textKey.isNotBlank()

        val value = if (hasTextValue) translate(textKey) else formatValue(fatModel, info)
        val unit = if (hasTextValue) "" else unitOf(context, info)
        val status = if (hasTextValue || info.currentValue <= 0f) "" else translate(info.standardTitle)

        return MeasureResultItem(
            name = name,
            value = value,
            unit = unit,
            status = status,
            isTextValue = hasTextValue
        )
    }

    /**
     * 身体类型 / 肥胖等级 / 健康评估在 UI 上展示的是文本，而不是数值。
     */
    private fun textValueKey(fatModel: PPBodyFatModel, info: PPBodyDetailInfoModel): String {
        if (info.bodyParamKey !in TEXT_KEYS) return ""
        val valueExists = when (info.bodyParamKey) {
            "ppBodyType" -> fatModel.ppBodyType != null
            "PPBodyFatGrade" -> fatModel.ppFatGrade != null
            "ppBodyHealth" -> fatModel.ppBodyHealth != null
            else -> false
        }
        if (!valueExists) return ""
        return info.currentValueStr.ifBlank { info.standardTitle }
    }

    private fun formatValue(
        fatModel: PPBodyFatModel,
        info: PPBodyDetailInfoModel
    ): String {
        if (info.currentValue <= 0f) return NO_VALUE
        return when (info.bodyParamKey) {
            //体重按秤的精度显示，0.05 精度的秤显示两位小数
            "ppWeightKg" -> {
                val accuracyType = fatModel.ppBodyBaseModel?.deviceModel?.deviceAccuracyType?.getType() ?: 0
                PPUtil.getWeightValueD(
                    PPUnitType.Unit_KG,
                    info.currentValue.toDouble(),
                    accuracyType,
                    true
                )
            }

            "ppWHR" -> PPUtil.keepPoint2S(info.currentValue.toDouble())
            in INT_KEYS -> info.currentValue.toInt().toString()
            else -> PPUtil.keepPoint1S(info.currentValue.toDouble())
        }
    }

    private fun unitOf(context: Context, info: PPBodyDetailInfoModel): String {
        return when (info.bodyParamKey) {
            "ppBodyAge" -> context.getString(R.string.unit_age)
            "ppBodyScore" -> context.getString(R.string.unit_score)
            else -> info.unit
        }
    }

    private fun translate(key: String): String {
        if (key.isBlank()) return ""
        return JsonLanguageDefaultValueUtils.getValueFromJson(key)
    }
}