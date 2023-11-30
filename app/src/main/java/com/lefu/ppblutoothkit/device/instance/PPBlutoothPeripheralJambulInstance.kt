package com.lefu.ppblutoothkit.device.instance

import com.peng.ppscale.device.PeripheralBanana.PPBlutoothPeripheralBananaController
import com.peng.ppscale.device.PeripheralJambul.PPBlutoothPeripheralJambulController
import com.peng.ppscale.vo.PPDeviceModel

class PPBlutoothPeripheralJambulInstance {

    var controller: PPBlutoothPeripheralJambulController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralJambulInstance() }
    }

    init {
        controller = PPBlutoothPeripheralJambulController()
    }



}