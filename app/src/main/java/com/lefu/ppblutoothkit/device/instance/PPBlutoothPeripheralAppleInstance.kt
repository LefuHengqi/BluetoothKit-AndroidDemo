package com.lefu.ppblutoothkit.device.instance

import com.peng.ppscale.device.PeripheralApple.PPBlutoothPeripheralAppleController

class PPBlutoothPeripheralAppleInstance {

    var controller: PPBlutoothPeripheralAppleController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralAppleInstance() }
    }

    init {
        controller = PPBlutoothPeripheralAppleController()
    }



}