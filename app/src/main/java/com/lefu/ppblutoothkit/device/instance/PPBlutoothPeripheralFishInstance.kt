package com.lefu.ppblutoothkit.device.instance

import com.peng.ppscale.device.PeripheralFish.PPBlutoothPeripheralFishController

class PPBlutoothPeripheralFishInstance {
    
    var controller: PPBlutoothPeripheralFishController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralFishInstance() }
    }

    init {
        controller = PPBlutoothPeripheralFishController()
    }

}