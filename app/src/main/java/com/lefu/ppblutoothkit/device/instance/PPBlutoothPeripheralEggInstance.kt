package com.lefu.ppblutoothkit.device.instance

import com.peng.ppscale.device.PeripheralEgg.PPBlutoothPeripheralEggController
import com.peng.ppscale.vo.PPDeviceModel

class PPBlutoothPeripheralEggInstance {

    var controller: PPBlutoothPeripheralEggController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralEggInstance() }
    }

    init {
        controller = PPBlutoothPeripheralEggController()
    }


}