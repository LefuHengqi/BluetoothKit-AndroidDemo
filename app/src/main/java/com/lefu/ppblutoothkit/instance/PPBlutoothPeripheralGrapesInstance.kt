package com.lefu.ppblutoothkit.instance

import com.peng.ppscale.device.PeripheralGrapes.PPBlutoothPeripheralGrapesController
import com.peng.ppscale.vo.PPDeviceModel

class PPBlutoothPeripheralGrapesInstance {

    var controller: PPBlutoothPeripheralGrapesController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralGrapesInstance() }
        var deviceModel: PPDeviceModel? = null
    }

    init {
        controller = PPBlutoothPeripheralGrapesController()
        controller?.deviceModel
    }


}