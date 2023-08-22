package com.lefu.ppblutoothkit.instance

import com.peng.ppscale.device.PeripheralCoconut.PPBlutoothPeripheralCoconutController
import com.peng.ppscale.vo.PPDeviceModel

class PPBlutoothPeripheralCoconutInstance {

    var controller: PPBlutoothPeripheralCoconutController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralCoconutInstance() }
        var deviceModel: PPDeviceModel? = null
    }

    init {
        controller = PPBlutoothPeripheralCoconutController()
        controller?.deviceModel
    }


}