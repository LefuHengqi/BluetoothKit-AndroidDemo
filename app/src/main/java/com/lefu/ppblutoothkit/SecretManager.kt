package com.lefu.ppblutoothkit

import com.lefu.ppbase.PPBodyBaseModel
import com.lefu.ppbase.PPScaleDefine.PPDeviceCalcuteType

/**
 * 该Secret与包名挂钩，请务必将此Secret替换为你自己的Secret
 *
 */
object SecretManager {
    //bh/TwoLegs140/5.0.5
    var secretByTwoLegs140_505 = "Hl3R4f7lyP+neAWbZBmvW2di1+gZXGzuYNZ0kAwAjnElNrR/CaoAcy1mB2CfHizc"
    //bh1/TwoLegs140/5.0.D
    var secretByTwoLegs140 = "up5dkj8oEi4NUmcziVCUkaAZM4j80ffS0p8yEkDNUP+5lY4izQ7iTMV+8Qg4R/Lg"
    //bh3/TwoLegs240/5.0.C
    var secretByTwoLegs240 = "9emloLinGXEk1n8oqsccPkFjEU4mGOK6r69Lgk2XDBcnQM19AwnWOw/9lIID0ove"
    //bh2/Body270/1.7.2
    var secretByBody270_172 = "i+OS1Ir4pHlQEnN+N0NGAigejDX3ztNAhHOCNIUXA+TNUHCD5aOKhGVE4jf3uKpi"
    //bh5/Body270/1.7.4
    var secretByBody270 = "Zvpw7/fttSdetsb9CBPEdsKTIf1Dr7Avu9AnOm8o9LdH3bAxwUeOFJaW/OqmEldp"
    //bh4/TwoArms140/5.0.B
    var secretTwoArms140 = "gxYx/oJypSlWQDDe7CR5Y1rjKmN7UoL70+c6yFnMS619KFzRn+UvFtoJk08987Hp"

    var bodyBaseModel: PPBodyBaseModel? = null

    fun getSecret(calculateType: Int): String {
        when (calculateType) {
            PPDeviceCalcuteType.PPDeviceCalcuteTypeDirect.getType() -> {
                //4电极直流算法
                return ""
            }

            PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_0.getType() -> {
                //4电极双脚新算法
                return secretByTwoLegs140
            }

            PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_1.getType() -> {
                //4电极双频算法
                return secretByTwoLegs240
            }

            PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_0.getType() -> {
                //8电极算法 bhProduct = 4
                return secretByBody270
            }

            PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_1.getType() -> {
                //8电极算法 bhProduct = 3
                return secretByBody270
            }

            PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_2.getType() -> {
                //8电极算法 bhProduct = 7
                return secretByBody270
            }

            PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_3.getType() -> {
                //8电极算法 bhProduct =5 --CF577_N1
                return secretByBody270
            }

            PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_4.getType() -> {
                //8电极算法 bhProduct =6 --CF597_N
                return secretByBody270
            }

            PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_5.getType() -> {
                //8电极算法 bhProduct =6 --CF597_N
                return secretByBody270
            }

            PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8.getType() -> {
                //8电极交流算法, bhProduct=1--CF577
                if (isTwoArmsCalculate(calculateType, bodyBaseModel)) {
                    return secretTwoArms140
                } else {
                    return secretByBody270_172
                }
            }

            else -> {
                //4电极双脚阻抗
                return secretByTwoLegs140_505
            }
        }
    }

    fun isTwoArmsCalculate(deviceCalculateType: Int, bodyBaseModel: PPBodyBaseModel?): Boolean {
        if (bodyBaseModel == null) return false
        return deviceCalculateType == PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8.getType() &&
                bodyBaseModel.z20KhzRightArmEnCode > 0 && bodyBaseModel.z100KhzRightArmEnCode <= 0
    }

}