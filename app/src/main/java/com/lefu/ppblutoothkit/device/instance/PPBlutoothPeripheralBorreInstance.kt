package com.lefu.ppblutoothkit.device.instance

import com.peng.ppscale.device.PeripheralBorre.PPBlutoothPeripheralBorreController


class PPBlutoothPeripheralBorreInstance {

    var controller: PPBlutoothPeripheralBorreController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralBorreInstance() }
    }

    init {
        controller = PPBlutoothPeripheralBorreController()
    }

}