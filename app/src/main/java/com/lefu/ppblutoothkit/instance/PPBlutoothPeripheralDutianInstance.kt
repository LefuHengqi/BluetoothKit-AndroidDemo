package com.lefu.ppblutoothkit.instance

import com.peng.ppscale.device.PeripheralDurian.PPBlutoothPeripheralDutianController
import com.peng.ppscale.vo.PPDeviceModel
import com.peng.ppscale.vo.PPUserModel

class PPBlutoothPeripheralDutianInstance {

    var controller: PPBlutoothPeripheralDutianController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralCoconutInstance() }
        var deviceModel: PPDeviceModel? = null
        var userModel: PPUserModel? = null
    }

    init {
        controller = PPBlutoothPeripheralDutianController()
        controller?.deviceModel
        controller?.userModel
    }


}