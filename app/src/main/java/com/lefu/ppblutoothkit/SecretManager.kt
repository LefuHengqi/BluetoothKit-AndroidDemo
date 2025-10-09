package com.lefu.ppblutoothkit

import com.lefu.ppbase.PPScaleHelper

/**
 * 该Secret与包名挂钩，请务必将此Secret替换为你自己的Secret
 *
 */
object SecretManager {

    val SECRET_TYPE_8 = "qqlnPbHg95UEFQgmJs+bep+IQjdvY4kK9wbCGEVQr2Vv2+t9PbOP8l6NCcPSxqtk"
    val SECRET_TYPE_4 = "Jyn7dEtLTGS9J3+jmtczIergA7Wiz9iqFVNVmcxmmkNtIWiFTIrf1WWpGmkDYuVB"

    fun getSecret(calculateType: Int): String {
        if (PPScaleHelper.isCalcute8(calculateType)) {
            return SECRET_TYPE_8
        } else {
            return SECRET_TYPE_4
        }
    }

}