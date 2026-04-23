package com.lefu.ppblutoothkit

import com.lefu.ppbase.PPScaleHelper

/**
 * 该Secret与包名挂钩，请务必将此Secret替换为你自己的Secret
 *
 */
object SecretManager {

    //TwoLegs140,TwoArms140,TwoLegs240
    val BodyCompositionDetection1 = "Jyn7dEtLTGS9J3+jmtczIergA7Wiz9iqFVNVmcxmmkNtIWiFTIrf1WWpGmkDYuVB"
    //Body270
    val BodyCompositionDetection2 = "qqlnPbHg95UEFQgmJs+bep+IQjdvY4kK9wbCGEVQr2Vv2+t9PbOP8l6NCcPSxqtk"

    fun getSecret(calculateType: Int): String {
        if (PPScaleHelper.isCalcute8(calculateType)) {
            return BodyCompositionDetection2
        } else {
            return BodyCompositionDetection1
        }
    }

}