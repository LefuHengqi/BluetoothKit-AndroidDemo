package com.lefu.ppblutoothkit.device.instance

import com.peng.ppscale.device.PeripheralGrapes.PPBlutoothPeripheralGrapesController

class PPBlutoothPeripheralGrapesInstance {

    var controller: PPBlutoothPeripheralGrapesController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralGrapesInstance() }
    }

    init {
        controller = PPBlutoothPeripheralGrapesController()
    }


}