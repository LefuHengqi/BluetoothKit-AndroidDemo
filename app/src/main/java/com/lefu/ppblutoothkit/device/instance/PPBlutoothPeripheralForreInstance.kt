package com.lefu.ppblutoothkit.device.instance

import com.peng.ppscale.device.PeripheralForre.PPBlutoothPeripheralForreController


class PPBlutoothPeripheralForreInstance {

    var controller: PPBlutoothPeripheralForreController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralForreInstance() }
    }

    init {
        controller = PPBlutoothPeripheralForreController()
    }

}