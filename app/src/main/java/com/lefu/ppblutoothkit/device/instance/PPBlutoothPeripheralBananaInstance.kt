package com.lefu.ppblutoothkit.device.instance

import com.peng.ppscale.device.PeripheralBanana.PPBlutoothPeripheralBananaController
import com.peng.ppscale.vo.PPDeviceModel

class PPBlutoothPeripheralBananaInstance {

    var controller: PPBlutoothPeripheralBananaController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralBananaInstance() }
        var deviceModel: PPDeviceModel? = null
    }

    init {
        controller = PPBlutoothPeripheralBananaController()
        controller?.deviceModel
    }



}