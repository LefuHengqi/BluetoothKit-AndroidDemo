package com.lefu.ppblutoothkit.device.instance

import com.peng.ppscale.device.PeripheralTorre.PPBlutoothPeripheralTorreController
import com.peng.ppscale.vo.PPDeviceModel

class PPBlutoothPeripheralTorreInstance {

    var controller: PPBlutoothPeripheralTorreController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralTorreInstance() }
    }

    init {
        controller = PPBlutoothPeripheralTorreController()
    }

}