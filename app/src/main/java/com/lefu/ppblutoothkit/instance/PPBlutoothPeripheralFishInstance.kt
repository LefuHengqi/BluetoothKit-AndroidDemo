package com.lefu.ppblutoothkit.instance

import com.peng.ppscale.device.PeripheralFish.PPBlutoothPeripheralFishController
import com.peng.ppscale.vo.PPDeviceModel

class PPBlutoothPeripheralFishInstance {


    var controller: PPBlutoothPeripheralFishController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralFishInstance() }
        var deviceModel: PPDeviceModel? = null
    }

    init {
        controller = PPBlutoothPeripheralFishController()
        controller?.deviceModel
    }

}