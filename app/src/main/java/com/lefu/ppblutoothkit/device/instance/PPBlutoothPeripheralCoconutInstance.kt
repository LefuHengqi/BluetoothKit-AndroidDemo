package com.lefu.ppblutoothkit.device.instance

import com.peng.ppscale.device.PeripheralCoconut.PPBlutoothPeripheralCoconutController

class PPBlutoothPeripheralCoconutInstance {

    var controller: PPBlutoothPeripheralCoconutController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralCoconutInstance() }
    }

    init {
        controller = PPBlutoothPeripheralCoconutController()
    }


}