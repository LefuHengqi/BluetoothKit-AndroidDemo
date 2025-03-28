package com.lefu.ppblutoothkit.device.instance

import com.peng.ppscale.device.PeripheralDurian.PPBlutoothPeripheralDurianController

class PPBlutoothPeripheralDurianInstance {

    var controller: PPBlutoothPeripheralDurianController? = null

    companion object Factory {
        val instance by lazy { PPBlutoothPeripheralDurianInstance() }
    }

    init {
        controller = PPBlutoothPeripheralDurianController()
    }


}