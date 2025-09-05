package com.lefu.ppblutoothkit.device.instance

import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.util.Logger
import com.lefu.ppbase.vo.PPUnitType
import com.peng.ppscale.PPBluetoothKit
import com.peng.ppscale.business.ble.listener.FoodScaleDataChangeListener
import com.peng.ppscale.business.ble.listener.PPBleSendResultCallBack
import com.peng.ppscale.business.ble.listener.PPBleStateInterface
import com.peng.ppscale.business.ble.listener.PPDeviceLogInterface
import com.peng.ppscale.business.ble.listener.PPDeviceSetInfoInterface
import com.peng.ppscale.business.ble.listener.PPFoodInfoInterface
import com.peng.ppscale.business.ble.listener.PPTorreDeviceModeChangeInterface
import com.peng.ppscale.business.torre.listener.OnDFUStateListener
import com.peng.ppscale.business.torre.listener.PPClearDataInterface
import com.peng.ppscale.device.PeripheralLorre.PPBluetoothPeripheralLorreController
import com.peng.ppscale.device.PeripheralMorre.PPBluetoothPeripheralMorreController
import com.peng.ppscale.vo.PPKorreFoodInfo

object PPBluetoothPeripheralMorreInstance {

    var controller: PPBluetoothPeripheralMorreController? = null

    var currentNum = 0 //记录下发用户信息的个数  若currentNum为-1, 则表示只下发指定用户数据

    init {
        if (controller == null) {
            controller = PPBluetoothPeripheralMorreController()
        }
    }

    fun startConnect(deviceModel: PPDeviceModel, onScaleDataListener: FoodScaleDataChangeListener?, bleStateInterface: PPBleStateInterface?) {
        controller?.deviceModel = deviceModel
        controller?.registDataChangeListener(onScaleDataListener)
        controller?.startConnect(deviceModel, bleStateInterface)
    }

    /**
     * 启动保活
     */
    fun startKeepAlive() {
        controller?.getTorreDeviceManager()?.startKeepAlive()
    }

    fun stopKeepAlive() {
        controller?.getTorreDeviceManager()?.stopKeepAlive()
    }

    /**
     * 开始食物管理
     */
    fun startFoodManager(sendResultCallBack: PPBleSendResultCallBack? = null) {
        controller?.getTorreDeviceManager()?.startFoodManager()
    }

    /**
     * 退出食物管理
     */
    fun endFoodManager(sendResultCallBack: PPBleSendResultCallBack? = null) {
        controller?.getTorreDeviceManager()?.endFoodManager()
    }

    /**
     * 保存当前食物和重量
     */
    fun currentFoodSave() {
        controller?.getTorreDeviceManager()?.currentFoodSave()
    }

    fun syncFoodInfo(foodInfo: PPKorreFoodInfo?, foodInfoInterface: PPFoodInfoInterface?) {
        //下发当前食物信息
        Logger.d("syncFoodInfo 下发给设备的 foodInfo:${foodInfo?.toString()}")
        if (foodInfo == null) {
            foodInfoInterface?.syncFoodInfoFail()
            return
        }
        controller?.getTorreDeviceManager()?.syncFoodInfo(foodInfo, foodInfoInterface)

    }

    fun syncFoodNutrient(foodInfo: PPKorreFoodInfo?, foodInfoInterface: PPFoodInfoInterface?) {
        //下发当前食物信息
        Logger.d("syncFoodNutrient 下发给设备的 foodInfo:${foodInfo?.toString()}")
        if (foodInfo == null) {
            foodInfoInterface?.syncFoodInfoFail()
            return
        }
        controller?.getTorreDeviceManager()?.syncFoodNutrient(foodInfo, foodInfoInterface)

    }

    fun syncFoodInfos(foodInfos: MutableList<PPKorreFoodInfo?>?, foodInfoInterface: PPFoodInfoInterface?) {
        //下发当前食物信息
        currentNum = 0
        foodInfos?.forEach {
            addPrint("syncFoodInfo 同步的食物：${it?.foodNo} foodRemoteId:${it?.foodRemoteId}")
        }
        if (foodInfos?.isNotEmpty() == true) {
            val foodInfo = foodInfos.get(currentNum)
            Logger.d("syncFoodInfo 下发给设备的 foodInfo:${foodInfo.toString()}")
            if (foodInfo == null) {
                foodInfoInterface?.syncFoodInfoFail()
                return
            }
            controller?.getTorreDeviceManager()?.syncFoodInfo(foodInfo, object : PPFoodInfoInterface {

                override fun syncFoodInfoSuccess() {
                    currentNum++
                    if (currentNum < foodInfos.size) {
                        val foodInfo = foodInfos.get(currentNum)
                        if (foodInfo == null) {
                            foodInfoInterface?.syncFoodInfoFail()
                            return
                        }
                        Logger.d("syncFoodInfo 下发给设备的 foodInfo:${foodInfo.toString()}")
                        controller?.getTorreDeviceManager()?.syncFoodInfo(foodInfo, this)
                    } else {
                        foodInfoInterface?.syncFoodInfoSuccess()
                    }
                }

                override fun syncFoodInfoFail() {
                    foodInfoInterface?.syncFoodInfoFail()
                }

            })
        }
    }

    fun getFoodList(foodInfoInterface: PPFoodInfoInterface?) {
        controller?.getTorreDeviceManager()?.getFoodList(foodInfoInterface)
    }

    fun deleteFood(number: Int, foodInfoInterface: PPFoodInfoInterface?) {
        controller?.getTorreDeviceManager()?.deleteFood(number, foodInfoInterface)
    }

    fun deleteAllFood(foodInfoInterface: PPFoodInfoInterface?) {
        controller?.getTorreDeviceManager()?.deleteAllFood(foodInfoInterface)
    }

    /**
     * 同步日志
     *
     * @param logFilePath 指定日志文件存储的路径，必传 例如：val fileFath = context.filesDir.absolutePath + "/Log/DeviceLog"
     * @param
     */
    fun syncLog(logFilePath: String, deviceLogInterface: PPDeviceLogInterface) {
        addPrint("syncLog : $logFilePath")
        controller?.getTorreDeviceManager()?.syncLog(logFilePath, deviceLogInterface)
    }

    /**
     * 同步时间
     */
    fun syncTime(callback: (success: Boolean) -> Unit) {
        addPrint("syncTime")
        controller?.getTorreDeviceManager()?.syncTimeMorre(callback)
    }

    fun syncUnit(unitType: PPUnitType, resultCallBack: PPBleSendResultCallBack) {
        addPrint("syncUnit unitType:${unitType}")
        controller?.getTorreDeviceManager()?.syncUnitMorre(unitType, resultCallBack)
    }

    fun toZero() {
        addPrint("toZero")
        controller?.getTorreDeviceManager()?.toZero()
    }

    fun clearAllDevice(ppClearDataInterface: PPClearDataInterface) {
        controller?.getTorreDeviceManager()?.clearAllDeviceInfo(ppClearDataInterface)
    }

    //清除clearAllDevice 回调
    fun clearAllDeviceCallBack() {
        addPrint("clearAllDeviceCallBack")
        controller?.getTorreDeviceManager()?.clearAllDeviceInfoCallback()
    }

    /**
     * 读取设备信息
     */
    fun readDeviceInfo(modeChangeInterface: PPTorreDeviceModeChangeInterface) {
        addPrint("readDeviceInfo")
        controller?.getTorreDeviceManager()?.readDeviceInfoFromCharacter(modeChangeInterface)
    }

    /**
     * 读取设备电量
     */
    fun readDeviceBattery(modeChangeInterface: PPTorreDeviceModeChangeInterface) {
        addPrint("readDeviceBattery")
        controller?.getTorreDeviceManager()?.readDeviceBattery(modeChangeInterface)
    }

    /**
     * 恢复出厂设备
     */
    fun resetDevice(deviceSetInterface: PPDeviceSetInfoInterface) {
        addPrint("resetDevice")
        controller?.getTorreDeviceManager()?.resetDevice(deviceSetInterface)
    }

    fun addPrint(msg: String) {
        if (msg.isNotEmpty()) {
            Logger.d("${PPBluetoothPeripheralLorreInstance::class.java.simpleName}:msg:Lorre $msg")
        }
    }

    fun disConnect() {
        controller?.disConnect()
    }

    fun stopSearch() {
        controller?.stopSeach()
    }

    fun getConnectState(): Boolean {
        return controller?.connectState() ?: false
    }

    fun stopDFU() {
        PPBluetoothKit.setDebug(true)
        controller?.getTorreDeviceManager()?.stopDFU()
    }

    fun startDFU(dfuFilePath: String?, version: String, firmwareVersion: String, dfuStateListener: OnDFUStateListener?) {
        val contains = version.contains("999")
        if (contains) {
            controller?.getTorreDeviceManager()?.startDFU(dfuFilePath, dfuStateListener)
        } else {
            controller?.getTorreDeviceManager()?.startSmartDFU(dfuFilePath, firmwareVersion, dfuStateListener)
        }
    }
}

