package com.lefu.ppblutoothkit.device.instance

import com.peng.ppscale.device.PeripheralGrapes.PPBlutoothPeripheralGrapesController
import com.peng.ppscale.device.PeripheralHamburger.PPBlutoothPeripheralHamburgerController
import com.peng.ppscale.vo.PPDeviceModel

class PPBlutoothPeripheralHamburgerInstance {

    var controller: PPBlutoothPeripheralHamburgerController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralHamburgerInstance() }
        var deviceModel: PPDeviceModel? = null
    }

    init {
        controller = PPBlutoothPeripheralHamburgerController()
        controller?.deviceModel
    }

}