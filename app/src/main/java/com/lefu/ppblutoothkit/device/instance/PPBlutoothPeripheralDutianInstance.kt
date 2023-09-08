package com.lefu.ppblutoothkit.device.instance

import com.peng.ppscale.device.PeripheralDurian.PPBlutoothPeripheralDutianController
import com.peng.ppscale.vo.PPDeviceModel
import com.peng.ppscale.vo.PPUserModel

class PPBlutoothPeripheralDutianInstance {

    var controller: PPBlutoothPeripheralDutianController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralDutianInstance() }
    }

    init {
        controller = PPBlutoothPeripheralDutianController()
    }


}