package com.lefu.ppblutoothkit.device.instance

import com.peng.ppscale.device.PeripheralJambul.PPBlutoothPeripheralJambulController

class PPBlutoothPeripheralJambulInstance {

    var controller: PPBlutoothPeripheralJambulController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralJambulInstance() }
    }

    init {
        controller = PPBlutoothPeripheralJambulController()
    }



}