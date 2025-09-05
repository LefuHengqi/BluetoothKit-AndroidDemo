package com.lefu.ppblutoothkit.device.morre

import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.vo.PPScaleStateMeasureResultType
import com.lefu.ppbase.vo.PPScaleStateWeightType
import com.lefu.ppblutoothkit.device.PeripheralMorreActivity
import com.lefu.ppblutoothkit.util.UnitUtil
import com.peng.ppscale.business.ble.listener.FoodScaleDataChangeListener
import com.peng.ppscale.util.UnitUtils
import com.peng.ppscale.vo.LFFoodScaleGeneral

val PeripheralMorreActivity.foodScaleDataChangeListener: FoodScaleDataChangeListener
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

        }

        override fun overWeight(deviceModel: PPDeviceModel?) {
            weightTextView?.text = "Over Weight"
        }
    }
