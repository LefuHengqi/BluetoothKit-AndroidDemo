package com.lefu.ppblutoothkit.instance

import com.peng.ppscale.device.PeripheralEgg.PPBlutoothPeripheralEggController
import com.peng.ppscale.vo.PPDeviceModel

class PPBlutoothPeripheralEggInstance {

    var controller: PPBlutoothPeripheralEggController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralEggInstance() }
        var deviceModel: PPDeviceModel? = null
    }

    init {
        controller = PPBlutoothPeripheralEggController()
        controller?.deviceModel
    }


}