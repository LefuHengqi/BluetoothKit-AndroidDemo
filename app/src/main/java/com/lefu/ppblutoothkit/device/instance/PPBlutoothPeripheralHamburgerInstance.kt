package com.lefu.ppblutoothkit.device.instance

import com.peng.ppscale.device.PeripheralHamburger.PPBlutoothPeripheralHamburgerController
import com.peng.ppscale.vo.PPDeviceModel

class PPBlutoothPeripheralHamburgerInstance {

    var controller: PPBlutoothPeripheralHamburgerController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralHamburgerInstance() }
    }

    init {
        controller = PPBlutoothPeripheralHamburgerController()
    }

}