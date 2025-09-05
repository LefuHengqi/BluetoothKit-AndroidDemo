package com.lefu.ppblutoothkit.device.lorre

import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.vo.PPScaleStateMeasureResultType
import com.lefu.ppbase.vo.PPScaleStateWeightType
import com.lefu.ppblutoothkit.device.PeripheralLorreActivity
import com.lefu.ppblutoothkit.device.foodscale.vo.toPPKorreFoodInfo
import com.lefu.ppblutoothkit.device.korre.getFoodIdByFoodNumber
import com.lefu.ppblutoothkit.util.UnitUtil
import com.peng.ppscale.business.ble.listener.FoodScaleDataChangeListener
import com.peng.ppscale.util.UnitUtils
import com.peng.ppscale.vo.LFFoodScaleGeneral

val PeripheralLorreActivity.foodScaleDataChangeListener: FoodScaleDataChangeListener
    get() = object : FoodScaleDataChangeListener() {

        override fun processData(lfFoodScaleGeneral: LFFoodScaleGeneral, deviceModel: PPDeviceModel?) {
//            val lfWeightKg: Double = lfFoodScaleGeneral.getLfWeightKg() * if (lfFoodScaleGeneral.getThanZero() == 1) 1.0 else -1.0
            val voiceValue = UnitUtils.getValue(lfFoodScaleGeneral, deviceModel)
            val voiceUnit = UnitUtil.unitText(this@foodScaleDataChangeListener, lfFoodScaleGeneral.getUnit())
            if (lfFoodScaleGeneral.scaleState?.weightType == PPScaleStateWeightType.PP_SCALE_STATE_WEIGHT_OVERWEIGHT) {
                overWeight(deviceModel)
                return
            }
            if (lfFoodScaleGeneral.scaleState?.measureResultType == PPScaleStateMeasureResultType.PP_SCALE_STATE_MEASURE_RESULT_FINISH) {
                weightTextView?.text = "lock:${voiceValue}${voiceUnit}"
            } else {
                weightTextView?.text = "process:${voiceValue}${voiceUnit}"
            }

            if (lfFoodScaleGeneral.foodNumber == 0) {
                foodNoTv?.text = "foodNo: No Food"
                foodRemoteIdTv?.text = ""
            } else {
                foodRemoteIdTv?.text = "foodRemoteId:${getFoodIdByFoodNumber(lfFoodScaleGeneral.foodNumber)}"
                foodNoTv?.text = "foodNo:${lfFoodScaleGeneral.foodNumber}"
            }

        }

        override fun overWeight(deviceModel: PPDeviceModel?) {
            weightTextView?.text = "Over Weight"
        }
    }

fun PeripheralLorreActivity.getFoodIdByFoodNumber(foodNumber: Int): String {

    if (foodNumberList.isNullOrEmpty()) {
        if (foodList.isNullOrEmpty()) {

            // 创建食物数据源（基于用户提供的数据）
            val foodDataList = createFoodDataList()

            foodList = mutableListOf()

            foodDataList?.forEachIndexed({ index, defaultFood ->
                val foodInfo = defaultFood.toPPKorreFoodInfo(index)
                foodList?.add(foodInfo)
            })
        }

        foodList?.forEachIndexed { index, food ->
            if ((index + 1) == foodNumber) {
                return food?.foodRemoteId ?: "" //食物编号
            }
        }
    } else {
        return foodNumberList?.find { it.foodNo == foodNumber }?.foodRemoteId ?: "" //食物编号
    }
    return ""

}