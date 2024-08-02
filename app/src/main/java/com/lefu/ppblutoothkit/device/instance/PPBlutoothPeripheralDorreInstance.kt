package com.lefu.ppblutoothkit.device.instance

import com.peng.ppscale.device.PeripheralDorre.PPBlutoothPeripheralDorreController

class PPBlutoothPeripheralDorreInstance {

    var controller: PPBlutoothPeripheralDorreController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralDorreInstance() }
    }

    init {
        controller = PPBlutoothPeripheralDorreController()
    }

}