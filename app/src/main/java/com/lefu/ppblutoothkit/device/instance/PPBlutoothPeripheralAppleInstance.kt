package com.lefu.ppblutoothkit.device.instance

import com.peng.ppscale.device.PeripheralApple.PPBlutoothPeripheralAppleController
import com.peng.ppscale.vo.PPDeviceModel

class PPBlutoothPeripheralAppleInstance {

    var controller: PPBlutoothPeripheralAppleController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralAppleInstance() }
        var deviceModel: PPDeviceModel? = null
    }

    init {
        controller = PPBlutoothPeripheralAppleController()
        controller?.deviceModel
    }

}