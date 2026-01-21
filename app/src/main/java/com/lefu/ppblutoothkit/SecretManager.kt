package com.lefu.ppblutoothkit

import com.lefu.ppbase.PPScaleHelper

/**
 * 该Secret与包名挂钩，请务必将此Secret替换为你自己的Secret
 *
 */
object SecretManager {

    //TwoLegs140,TwoArms140,TwoLegs240
    val BodyCompositionDetection1 = "0Msv6DxhEMCBX+gZxdPxekPXZ2fETyeeNYJmdjAM2P/DDeq4jWfjP13x3HLi05jO"
    //Body270
    val BodyCompositionDetection2 = "/Vz74YxCHZ0RXZnwZxUjL43zojyXO+d40a+22J8Of5OwQKswLy1ttTH75aIOipeY"

    fun getSecret(calculateType: Int): String {
        if (PPScaleHelper.isCalcute8(calculateType)) {
            return BodyCompositionDetection2
        } else {
            return BodyCompositionDetection1
        }
    }

}