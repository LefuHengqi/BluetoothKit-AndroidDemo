package com.lefu.ppblutoothkit.device.instance

import com.peng.ppscale.device.PeripheralKorre.PPBlutoothPeripheralKorreController


class PPBlutoothPeripheralKorreInstance {

    var controller: PPBlutoothPeripheralKorreController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralKorreInstance() }
    }

    init {
        controller = PPBlutoothPeripheralKorreController()
    }

}