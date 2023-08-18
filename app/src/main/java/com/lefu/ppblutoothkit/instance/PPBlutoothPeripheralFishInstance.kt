package com.lefu.ppblutoothkit.instance

import com.peng.ppscale.device.PeripheralFish.PPBluttoothPeripheralFishController
import com.peng.ppscale.vo.PPDeviceModel

class PPBlutoothPeripheralFishInstance {


    var controller: PPBluttoothPeripheralFishController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralFishInstance() }
        var deviceModel: PPDeviceModel? = null
    }

    init {
        controller = PPBluttoothPeripheralFishController()
        controller?.deviceModel
    }

}