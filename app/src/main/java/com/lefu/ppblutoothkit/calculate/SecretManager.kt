package com.lefu.ppblutoothkit.calculate

import com.lefu.htcalculatekit.PPDeviceCalculateType


/**
 * 该Secret与包名挂钩，请务必将此Secret替换为你自己的Secret
 *
 */
object SecretManager {

    var secretByTwoLegs140_505 = "tqgNeXljRSboMQ8iMDjyvk4kIf+G5XK9fpdWoWeO7z+IXmlk1Q9Mrv+OvPOKTq/O"
    var secretByTwoLegs140 = "qt7oIt3kG75tqev0mmtDvCUiSVCt1e8A0a8rp9pbBkUSK5gdtBaa517aGTIFTQJw"
    var secretByTwoLegs240 = "30nZPG5OfoZVpsw4lBg5fQvUMfLgqnbILlfByPgOu19p7XFX3qr47j9wY+5UtMIM"
    var secretByBody270 = "T+0Jkr5MrwwrurWQMA/VF6aJ9RBVOxJjtF5JIR3Xe59N3XM5fbKraBH/7fNOzk5s"

    fun getSecret(calculateType: Int): String {
        when (calculateType) {
            PPDeviceCalculateType.PPDeviceCalculateTypeDirect.getType() -> {
                //4电极直流算法
                return ""
            }
            PPDeviceCalculateType.PPDeviceCalculateTypeAlternate4_0.getType() -> {
                //4电极双脚新算法
                return secretByTwoLegs140
            }
            PPDeviceCalculateType.PPDeviceCalculateTypeAlternate4_1.getType() -> {
                //4电极双频算法
                return secretByTwoLegs240
            }
            PPDeviceCalculateType.PPDeviceCalculateTypeAlternate8_0.getType() -> {
                //8电极算法 bhProduct = 4
                return secretByBody270
            }
            PPDeviceCalculateType.PPDeviceCalculateTypeAlternate8_1.getType() -> {
                //8电极算法 bhProduct = 3
                return secretByBody270
            }
            PPDeviceCalculateType.PPDeviceCalculateTypeAlternate8_2.getType() -> {
                //8电极算法 bhProduct = 7
                return secretByBody270
            }
            PPDeviceCalculateType.PPDeviceCalculateTypeAlternate8_3.getType() -> {
                //8电极算法 bhProduct =5 --CF577_N1
                return secretByBody270
            }
            PPDeviceCalculateType.PPDeviceCalculateTypeAlternate8_4.getType() -> {
                //8电极算法 bhProduct =6 --CF597_N
                return secretByBody270
            }
            PPDeviceCalculateType.PPDeviceCalculateTypeAlternate8_5.getType() -> {
                //8电极算法 bhProduct =6 --CF597_N
                return secretByBody270
            }
            PPDeviceCalculateType.PPDeviceCalculateTypeAlternate8.getType() -> {
                //8电极交流算法, bhProduct=1--CF577
                return secretByBody270
            }
            else -> {
                //4电极双脚阻抗
                return secretByTwoLegs140_505
            }
        }
    }


}