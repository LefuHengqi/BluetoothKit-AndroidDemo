package com.lefu.ppblutoothkit.device.instance

import com.peng.ppscale.device.PeripheralBanana.PPBlutoothPeripheralBananaController

class PPBlutoothPeripheralBananaInstance {

    var controller: PPBlutoothPeripheralBananaController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralBananaInstance() }
    }

    init {
        controller = PPBlutoothPeripheralBananaController()
    }



}