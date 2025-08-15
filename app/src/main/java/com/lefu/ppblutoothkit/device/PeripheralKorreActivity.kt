package com.lefu.ppblutoothkit.device

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.vo.PPUnitType
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.device.foodscale.vo.XinmiaoFoodScaleDefault
import com.lefu.ppblutoothkit.device.foodscale.vo.toPPKorreFoodInfo
import com.lefu.ppblutoothkit.device.instance.PPBluetoothPeripheralKorreInstance
import com.lefu.ppblutoothkit.device.korre.foodScaleDataChangeListener
import com.lefu.ppblutoothkit.util.FileUtil
import com.peng.ppscale.business.ble.listener.PPBleSendResultCallBack
import com.peng.ppscale.business.ble.listener.PPBleStateInterface
import com.peng.ppscale.business.ble.listener.PPDeviceLogInterface
import com.peng.ppscale.business.ble.listener.PPFoodInfoInterface
import com.peng.ppscale.business.ble.listener.PPTorreDeviceModeChangeInterface
import com.peng.ppscale.business.state.PPBleSwitchState
import com.peng.ppscale.business.state.PPBleWorkState
import com.peng.ppscale.util.json.GsonUtil
import com.peng.ppscale.vo.PPKorreFoodInfo
import com.peng.ppscale.vo.PPScaleSendState

/**
 * 一定要先连接设备，确保设备在已连接状态下使用
 * 对应的协议: 3.x
 * 连接类型:连接
 * 设备类型 厨房秤
 */
class PeripheralKorreActivity : BaseImmersivePermissionActivity() {

    var weightTextView: TextView? = null
    var logTxt: TextView? = null
    var device_set_connect_state: TextView? = null
    var foodNoTv: TextView? = null
    var foodRemoteIdTv: TextView? = null

    //从秤端获取的列表
    var foodNumberList: MutableList<PPKorreFoodInfo>? = null

    //准备下发给秤的列表
    var foodList: MutableList<PPKorreFoodInfo?>? = null

    companion object {
        var deviceModel: PPDeviceModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.peripheral_korre_layout)

        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()

        // 初始化Toolbar
        initToolbar()

        // 注册新的返回键处理机制
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPressed()
            }

        })

        weightTextView = findViewById<TextView>(R.id.weightTextView)
        foodNoTv = findViewById<TextView>(R.id.foodNoTv)
        foodRemoteIdTv = findViewById<TextView>(R.id.foodRemoteIdTv)
        logTxt = findViewById<TextView>(R.id.logTxt)
        device_set_connect_state = findViewById<TextView>(R.id.device_set_connect_state)
        val nestedScrollViewLog = findViewById<NestedScrollView>(R.id.nestedScrollViewLog)

        logTxt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                nestedScrollViewLog.fullScroll(View.FOCUS_DOWN)
            }
        })
        addPrint("startConnect")
        initClick()
        deviceModel?.let { it1 -> PPBluetoothPeripheralKorreInstance.startConnect(it1, foodScaleDataChangeListener, bleStateInterface) }
    }

    private fun initToolbar() {
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        toolbar?.let {
            setupUnifiedToolbar(
                toolbar = it,
                title = "Korre:${deviceModel?.deviceName}",
                showBackButton = true
            )
        }
    }

    fun initClick() {
        findViewById<Button>(R.id.startConnectDevice).setOnClickListener {
            addPrint("startConnect")
            deviceModel?.let { it1 -> PPBluetoothPeripheralKorreInstance.startConnect(it1, foodScaleDataChangeListener, bleStateInterface) }
        }
        findViewById<Button>(R.id.syncUnit).setOnClickListener {
            addPrint("syncUnit")
            PPBluetoothPeripheralKorreInstance.syncUnit(PPUnitType.Unit_LB, object : PPBleSendResultCallBack {
                override fun onResult(sendState: PPScaleSendState?) {
                    if (sendState == PPScaleSendState.PP_SEND_SUCCESS) {
                        addPrint("syncUnit send success")
                    } else {
                        addPrint("syncUnit send fail")
                    }
                }
            })
        }

        findViewById<Button>(R.id.device_set_sync_log).setOnClickListener {
            addPrint("syncLog")
            //logFilePath 指定文件存储路径，必传例如：val fileFath = context.filesDir.absolutePath + "/Log/DeviceLog"
            val fileFath = filesDir.absolutePath + "/Log/DeviceLog"
            PPBluetoothPeripheralKorreInstance.syncLog(fileFath, deviceLogInterface)
        }

        findViewById<Button>(R.id.readDeviceBattery).setOnClickListener {
            addPrint("readDeviceBattery")
            PPBluetoothPeripheralKorreInstance.readDeviceBattery(object : PPTorreDeviceModeChangeInterface {
                override fun readDevicePower(power: Int) {
                    addPrint("readDevicePower power:$power")
                }
            })
        }
        findViewById<Button>(R.id.readDeviceInfo).setOnClickListener {
            addPrint("readDeviceInfo")
            PPBluetoothPeripheralKorreInstance.readDeviceInfo(object : PPTorreDeviceModeChangeInterface {
                override fun onReadDeviceInfo(deviceModel: PPDeviceModel?) {
                    deviceModel?.let {
                        addPrint(it.toString())
                    }
                }
            })
        }
        findViewById<Button>(R.id.toZeroKitchenScale).setOnClickListener {
            addPrint("To Zero")
            PPBluetoothPeripheralKorreInstance.toZero()
        }
        findViewById<Button>(R.id.startFoodManager).setOnClickListener {
            addPrint("startFoodManager")
            PPBluetoothPeripheralKorreInstance.startFoodManager(object : PPBleSendResultCallBack {
                override fun onResult(sendState: PPScaleSendState?) {
                    if (sendState == PPScaleSendState.PP_SEND_SUCCESS) {
                        addPrint("startFoodManager send success")
                    } else {
                        addPrint("startFoodManager send fail")
                    }
                }
            })
        }
        findViewById<Button>(R.id.exitFoodManager).setOnClickListener {
            addPrint("exitFoodManager")
            syncDefaultFoodEnd()
        }
        findViewById<Button>(R.id.delFood).setOnClickListener {
            addPrint("delFood")
            PPBluetoothPeripheralKorreInstance.deleteAllFood(object : PPFoodInfoInterface {

                override fun deleteFoodInfoSuccess() {
                    addPrint("deleteFoodInfoSuccess")
                    foodNoTv?.text = ""
                    foodRemoteIdTv?.text = ""
                }

                override fun deleteFoodInfoFail() {
                    addPrint("deleteFoodInfoFail")
                }

            })
        }
        findViewById<Button>(R.id.fetchFoodIdList).setOnClickListener {
            addPrint("getFoodIdList")
            PPBluetoothPeripheralKorreInstance.getFoodList(object : PPFoodInfoInterface {

                override fun getFoodNumberList(infos: MutableList<PPKorreFoodInfo>?) {

                    foodNumberList = infos

                    if (infos.isNullOrEmpty()) {
                        addPrint("getFoodNumberList the food list is null or empty")
                        foodNoTv?.text = ""
                        foodRemoteIdTv?.text = ""
                    } else {
                        infos.forEach { it ->
                            addPrint("getFoodNumberList foodRemoteId:${it.foodRemoteId}")
                        }

                        addPrint("getFoodNumberList the food list size is ${infos.size}")
                    }
                }


            })
        }
        findViewById<Button>(R.id.syncFood).setOnClickListener {
            addPrint("syncFood")

            // 创建食物数据源（基于用户提供的数据）
            val foodDataList = createFoodDataList()

            foodList = mutableListOf()

            foodDataList?.forEachIndexed({ index, defaultFood ->
                val foodInfo = defaultFood.toPPKorreFoodInfo(index)
                foodList?.add(foodInfo)
            })

            if (foodList.isNullOrEmpty().not()) {
                PPBluetoothPeripheralKorreInstance.startFoodManager(object : PPBleSendResultCallBack {
                    override fun onResult(sendState: PPScaleSendState?) {
                        addPrint("syncFoodInfos ing")
                        // 同步食物信息到设备
                        PPBluetoothPeripheralKorreInstance.syncFoodInfos(foodList, object : PPFoodInfoInterface {
                            override fun syncFoodInfoSuccess() {
                                addPrint("syncFoodInfos success")
                                syncDefaultFoodEnd()
                            }

                            override fun syncFoodInfoFail() {
                                addPrint("syncFoodInfos fail")
                                syncDefaultFoodEnd()
                            }
                        })
                    }
                })

            } else {
                addPrint("syncFoodInfos the foodList is null or empty")
            }

        }
    }

    fun syncDefaultFoodEnd() {
        PPBluetoothPeripheralKorreInstance.endFoodManager(object : PPBleSendResultCallBack {
            override fun onResult(sendState: PPScaleSendState?) {
                if (sendState == PPScaleSendState.PP_SEND_SUCCESS) {
                    addPrint("exitFoodManager send success")
                } else {
                    addPrint("exitFoodManager send fail")
                }
            }
        })
    }

    fun addPrint(msg: String) {
        runOnUiThread {
            logTxt?.append("$msg\n")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PPBluetoothPeripheralKorreInstance.disConnect()
    }

    private val bleStateInterface = object : PPBleStateInterface() {
        override fun monitorBluetoothWorkState(ppBleWorkState: PPBleWorkState?, deviceModel: PPDeviceModel?) {
            if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
                addPrint(getString(R.string.device_connected))
                device_set_connect_state?.text = getString(R.string.device_connected)
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateCanBeConnected) {
                addPrint(getString(R.string.can_be_connected))
                device_set_connect_state?.text = getString(R.string.can_be_connected)
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
                addPrint(getString(R.string.device_connecting))
                device_set_connect_state?.text = getString(R.string.device_connecting)
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
                addPrint(getString(R.string.device_disconnected))
                device_set_connect_state?.text = getString(R.string.device_disconnected)
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateWritable) {
                addPrint(getString(R.string.writable))
            }
        }

        override fun monitorBluetoothSwitchState(ppBleSwitchState: PPBleSwitchState?) {
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                addPrint(getString(R.string.system_bluetooth_disconnect))
                Toast.makeText(this@PeripheralKorreActivity, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show()
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                addPrint(getString(R.string.system_blutooth_on))
                Toast.makeText(this@PeripheralKorreActivity, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val deviceLogInterface = object : PPDeviceLogInterface {
        override fun syncLoging(progress: Int) {
            addPrint("syncLoging progress:$progress")
        }

        override fun syncLogEnd(logFilePath: String?) {
            addPrint("syncLogEnd ")
            addPrint("logFilePath: $logFilePath")
            Toast.makeText(this@PeripheralKorreActivity, "Sync Log Success", Toast.LENGTH_SHORT).show()
            FileUtil.sendEmail(this@PeripheralKorreActivity, logFilePath)
        }

        override fun syncLogStart() {
            addPrint("syncLogStart")
        }
    }

    fun createFoodDataList(): List<XinmiaoFoodScaleDefault> {

        // 创建食物数据列表
        val dataJson = """
            [{"brandName":"善食源","claims":[],"foodId":"CFJL01Yfg8","foodName":"鸡胸肉","fullNutrients":[{"nutrientId":208,"nutrientName":"Energy","nutrientUnit":"kcal","nutrientValue":127.0},{"nutrientId":203,"nutrientName":"Protein","nutrientUnit":"g","nutrientValue":25.8},{"nutrientId":204,"nutrientName":"Total lipid (fat)","nutrientUnit":"g","nutrientValue":2.0},{"nutrientId":205,"nutrientName":"Carbohydrate, by difference","nutrientUnit":"g","nutrientValue":1.5},{"nutrientId":291,"nutrientName":"Fiber, total dietary","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":320,"nutrientName":"Vitamin A, RAE","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":401,"nutrientName":"Vitamin C, total ascorbic acid","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":323,"nutrientName":"Vitamin E (alpha-tocopherol)","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":328,"nutrientName":"Vitamin D (D2 + D3)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":322,"nutrientName":"Carotene, alpha","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":404,"nutrientName":"Thiamin","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":405,"nutrientName":"Riboflavin","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":406,"nutrientName":"Niacin","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":601,"nutrientName":"Cholesterol","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":304,"nutrientName":"Magnesium, Mg","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":301,"nutrientName":"Calcium, Ca","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":303,"nutrientName":"Iron, Fe","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":312,"nutrientName":"Copper, Cu","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":315,"nutrientName":"Manganese, Mn","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":306,"nutrientName":"Potassium, K","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":305,"nutrientName":"Phosphorus, P","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":307,"nutrientName":"Sodium, Na","nutrientUnit":"mg","nutrientValue":214.0},{"nutrientId":317,"nutrientName":"Selenium, Se","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":606,"nutrientName":"Fatty acids, total saturated","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":605,"nutrientName":"Fatty acids, total trans","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":645,"nutrientName":"Fatty acids, total monounsaturated","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":693,"nutrientName":"Fatty acids, total trans-monoenoic","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":221,"nutrientName":"Alcohol, ethyl","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":431,"nutrientName":"Folic acid","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":410,"nutrientName":"Pantothenic acid","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":421,"nutrientName":"Choline, total","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":269,"nutrientName":"Sugars, total","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":430,"nutrientName":"Vitamin K (phylloquinone)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":415,"nutrientName":"Vitamin B-6","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":418,"nutrientName":"Vitamin B-12","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":313,"nutrientName":"Fluoride, F","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":309,"nutrientName":"Zinc, Zn","nutrientUnit":"mg","nutrientValue":0.0}],"imageIndex":6,"isRawFood":0,"lfCalories":127.0,"lfCholesterol":0.0,"lfP":0.0,"lfPotassium":0.0,"lfProtein":25.8,"lfSodium":214.0,"lfTotalCarbohydrate":1.5,"lfTotalFat":2.0,"locales":"zh_CN","measures":[{"measure":"份","qty":1,"servingWeight":150.0},{"measure":"克","qty":100,"servingWeight":100.0},{"measure":"包","qty":1,"servingWeight":110.0}],"servingQty":100,"servingUnit":"g","servingWeightGrams":100.0},{"claims":[],"foodId":"PwIil5Uzbz","foodName":"鸡蛋","fullNutrients":[{"nutrientId":208,"nutrientName":"Energy","nutrientUnit":"kcal","nutrientValue":139.0},{"nutrientId":203,"nutrientName":"Protein","nutrientUnit":"g","nutrientValue":13.1},{"nutrientId":204,"nutrientName":"Total lipid (fat)","nutrientUnit":"g","nutrientValue":8.6},{"nutrientId":205,"nutrientName":"Carbohydrate, by difference","nutrientUnit":"g","nutrientValue":2.4},{"nutrientId":291,"nutrientName":"Fiber, total dietary","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":320,"nutrientName":"Vitamin A, RAE","nutrientUnit":"μg","nutrientValue":255.0},{"nutrientId":401,"nutrientName":"Vitamin C, total ascorbic acid","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":323,"nutrientName":"Vitamin E (alpha-tocopherol)","nutrientUnit":"mg","nutrientValue":1.1},{"nutrientId":328,"nutrientName":"Vitamin D (D2 + D3)","nutrientUnit":"μg","nutrientValue":2.0},{"nutrientId":322,"nutrientName":"Carotene, alpha","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":404,"nutrientName":"Thiamin","nutrientUnit":"mg","nutrientValue":0.1},{"nutrientId":405,"nutrientName":"Riboflavin","nutrientUnit":"mg","nutrientValue":0.2},{"nutrientId":406,"nutrientName":"Niacin","nutrientUnit":"mg","nutrientValue":0.2},{"nutrientId":601,"nutrientName":"Cholesterol","nutrientUnit":"mg","nutrientValue":648.0},{"nutrientId":304,"nutrientName":"Magnesium, Mg","nutrientUnit":"mg","nutrientValue":10.0},{"nutrientId":301,"nutrientName":"Calcium, Ca","nutrientUnit":"mg","nutrientValue":56.0},{"nutrientId":303,"nutrientName":"Iron, Fe","nutrientUnit":"mg","nutrientValue":1.6},{"nutrientId":312,"nutrientName":"Copper, Cu","nutrientUnit":"mg","nutrientValue":0.2},{"nutrientId":315,"nutrientName":"Manganese, Mn","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":306,"nutrientName":"Potassium, K","nutrientUnit":"mg","nutrientValue":154.0},{"nutrientId":305,"nutrientName":"Phosphorus, P","nutrientUnit":"mg","nutrientValue":130.0},{"nutrientId":307,"nutrientName":"Sodium, Na","nutrientUnit":"mg","nutrientValue":13.2},{"nutrientId":317,"nutrientName":"Selenium, Se","nutrientUnit":"μg","nutrientValue":14.0},{"nutrientId":606,"nutrientName":"Fatty acids, total saturated","nutrientUnit":"g","nutrientValue":4.6},{"nutrientId":605,"nutrientName":"Fatty acids, total trans","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":645,"nutrientName":"Fatty acids, total monounsaturated","nutrientUnit":"g","nutrientValue":1.9},{"nutrientId":693,"nutrientName":"Fatty acids, total trans-monoenoic","nutrientUnit":"g","nutrientValue":0.5},{"nutrientId":221,"nutrientName":"Alcohol, ethyl","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":431,"nutrientName":"Folic acid","nutrientUnit":"μg","nutrientValue":113.3},{"nutrientId":410,"nutrientName":"Pantothenic acid","nutrientUnit":"mg","nutrientValue":1.5},{"nutrientId":421,"nutrientName":"Choline, total","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":269,"nutrientName":"Sugars, total","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":430,"nutrientName":"Vitamin K (phylloquinone)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":415,"nutrientName":"Vitamin B-6","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":418,"nutrientName":"Vitamin B-12","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":313,"nutrientName":"Fluoride, F","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":309,"nutrientName":"Zinc, Zn","nutrientUnit":"mg","nutrientValue":0.9}],"highres":"https://s.boohee.cn/house/new_food/big/b139c4ffb2e7443ea437a7349bf56206.jpg","imageIndex":42,"isRawFood":0,"lfCalories":139.0,"lfCholesterol":648.0,"lfDietaryFiber":0.0,"lfP":130.0,"lfPotassium":154.0,"lfProtein":13.1,"lfSaturatedFat":4.6,"lfSodium":13.2,"lfTotalCarbohydrate":2.4,"lfTotalFat":8.6,"locales":"zh_CN","measures":[{"measure":"份","qty":1,"servingWeight":150.0},{"measure":"克","qty":100,"servingWeight":100.0},{"measure":"只(大)","qty":1,"servingWeight":70.0},{"measure":"只（中）","qty":1,"servingWeight":60.0}],"servingQty":100,"servingUnit":"g","servingWeightGrams":100.0,"thumb":"https://s.boohee.cn/house/new_food/mid/b139c4ffb2e7443ea437a7349bf56206.jpg"},{"claims":[],"foodId":"gKB0R1mw31","foodName":"米饭","fullNutrients":[{"nutrientId":208,"nutrientName":"Energy","nutrientUnit":"kcal","nutrientValue":116.0},{"nutrientId":203,"nutrientName":"Protein","nutrientUnit":"g","nutrientValue":2.6},{"nutrientId":204,"nutrientName":"Total lipid (fat)","nutrientUnit":"g","nutrientValue":0.3},{"nutrientId":205,"nutrientName":"Carbohydrate, by difference","nutrientUnit":"g","nutrientValue":54.2},{"nutrientId":291,"nutrientName":"Fiber, total dietary","nutrientUnit":"g","nutrientValue":0.3},{"nutrientId":320,"nutrientName":"Vitamin A, RAE","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":401,"nutrientName":"Vitamin C, total ascorbic acid","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":323,"nutrientName":"Vitamin E (alpha-tocopherol)","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":328,"nutrientName":"Vitamin D (D2 + D3)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":322,"nutrientName":"Carotene, alpha","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":404,"nutrientName":"Thiamin","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":405,"nutrientName":"Riboflavin","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":406,"nutrientName":"Niacin","nutrientUnit":"mg","nutrientValue":1.9},{"nutrientId":601,"nutrientName":"Cholesterol","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":304,"nutrientName":"Magnesium, Mg","nutrientUnit":"mg","nutrientValue":15.0},{"nutrientId":301,"nutrientName":"Calcium, Ca","nutrientUnit":"mg","nutrientValue":7.0},{"nutrientId":303,"nutrientName":"Iron, Fe","nutrientUnit":"mg","nutrientValue":1.3},{"nutrientId":312,"nutrientName":"Copper, Cu","nutrientUnit":"mg","nutrientValue":0.1},{"nutrientId":315,"nutrientName":"Manganese, Mn","nutrientUnit":"mg","nutrientValue":0.6},{"nutrientId":306,"nutrientName":"Potassium, K","nutrientUnit":"mg","nutrientValue":30.0},{"nutrientId":305,"nutrientName":"Phosphorus, P","nutrientUnit":"mg","nutrientValue":62.0},{"nutrientId":307,"nutrientName":"Sodium, Na","nutrientUnit":"mg","nutrientValue":666.0},{"nutrientId":317,"nutrientName":"Selenium, Se","nutrientUnit":"μg","nutrientValue":0.4},{"nutrientId":606,"nutrientName":"Fatty acids, total saturated","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":605,"nutrientName":"Fatty acids, total trans","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":645,"nutrientName":"Fatty acids, total monounsaturated","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":693,"nutrientName":"Fatty acids, total trans-monoenoic","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":221,"nutrientName":"Alcohol, ethyl","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":431,"nutrientName":"Folic acid","nutrientUnit":"μg","nutrientValue":3.4},{"nutrientId":410,"nutrientName":"Pantothenic acid","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":421,"nutrientName":"Choline, total","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":269,"nutrientName":"Sugars, total","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":430,"nutrientName":"Vitamin K (phylloquinone)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":415,"nutrientName":"Vitamin B-6","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":418,"nutrientName":"Vitamin B-12","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":313,"nutrientName":"Fluoride, F","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":309,"nutrientName":"Zinc, Zn","nutrientUnit":"mg","nutrientValue":0.9}],"highres":"https://s.boohee.cn/house/upload_food/2020/5/27/big_photo_url_big_photo_url_f81e8de29f76e6d0c39d4142fe45764f.jpg","imageIndex":0,"isRawFood":0,"lfCalories":116.0,"lfCholesterol":0.0,"lfDietaryFiber":0.3,"lfP":62.0,"lfPotassium":30.0,"lfProtein":2.6,"lfSaturatedFat":0.0,"lfSodium":666.0,"lfTotalCarbohydrate":54.2,"lfTotalFat":0.3,"locales":"zh_CN","measures":[{"measure":"两 食堂的","qty":1,"servingWeight":110.0},{"measure":"份","qty":1,"servingWeight":150.0},{"measure":"克","qty":100,"servingWeight":100.0},{"measure":"勺","qty":1,"servingWeight":40.0},{"measure":"盒（快餐饭盒）","qty":1,"servingWeight":280.0},{"measure":"碗","qty":1,"servingWeight":180.0},{"measure":"碗(小乐扣)","qty":1,"servingWeight":100.0},{"measure":"碗(小碗)","qty":1,"servingWeight":150.0},{"measure":"碗（小碗）","qty":1,"servingWeight":150.0}],"servingQty":100,"servingUnit":"g","servingWeightGrams":100.0,"thumb":"https://s.boohee.cn/house/upload_food/2020/5/27/mid_photo_url_big_photo_url_f81e8de29f76e6d0c39d4142fe45764f.jpg"},{"claims":[],"foodId":"TlDq3lNgDu","foodName":"燕麦片","fullNutrients":[{"nutrientId":208,"nutrientName":"Energy","nutrientUnit":"kcal","nutrientValue":338.0},{"nutrientId":203,"nutrientName":"Protein","nutrientUnit":"g","nutrientValue":10.1},{"nutrientId":204,"nutrientName":"Total lipid (fat)","nutrientUnit":"g","nutrientValue":0.2},{"nutrientId":205,"nutrientName":"Carbohydrate, by difference","nutrientUnit":"g","nutrientValue":77.4},{"nutrientId":291,"nutrientName":"Fiber, total dietary","nutrientUnit":"g","nutrientValue":6.0},{"nutrientId":320,"nutrientName":"Vitamin A, RAE","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":401,"nutrientName":"Vitamin C, total ascorbic acid","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":323,"nutrientName":"Vitamin E (alpha-tocopherol)","nutrientUnit":"mg","nutrientValue":0.9},{"nutrientId":328,"nutrientName":"Vitamin D (D2 + D3)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":322,"nutrientName":"Carotene, alpha","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":404,"nutrientName":"Thiamin","nutrientUnit":"mg","nutrientValue":0.5},{"nutrientId":405,"nutrientName":"Riboflavin","nutrientUnit":"mg","nutrientValue":0.1},{"nutrientId":406,"nutrientName":"Niacin","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":601,"nutrientName":"Cholesterol","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":304,"nutrientName":"Magnesium, Mg","nutrientUnit":"mg","nutrientValue":116.0},{"nutrientId":301,"nutrientName":"Calcium, Ca","nutrientUnit":"mg","nutrientValue":58.0},{"nutrientId":303,"nutrientName":"Iron, Fe","nutrientUnit":"mg","nutrientValue":2.9},{"nutrientId":312,"nutrientName":"Copper, Cu","nutrientUnit":"mg","nutrientValue":0.2},{"nutrientId":315,"nutrientName":"Manganese, Mn","nutrientUnit":"mg","nutrientValue":3.9},{"nutrientId":306,"nutrientName":"Potassium, K","nutrientUnit":"mg","nutrientValue":356.0},{"nutrientId":305,"nutrientName":"Phosphorus, P","nutrientUnit":"mg","nutrientValue":342.0},{"nutrientId":307,"nutrientName":"Sodium, Na","nutrientUnit":"mg","nutrientValue":2.1},{"nutrientId":317,"nutrientName":"Selenium, Se","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":606,"nutrientName":"Fatty acids, total saturated","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":605,"nutrientName":"Fatty acids, total trans","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":645,"nutrientName":"Fatty acids, total monounsaturated","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":693,"nutrientName":"Fatty acids, total trans-monoenoic","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":221,"nutrientName":"Alcohol, ethyl","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":431,"nutrientName":"Folic acid","nutrientUnit":"μg","nutrientValue":30.1},{"nutrientId":410,"nutrientName":"Pantothenic acid","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":421,"nutrientName":"Choline, total","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":269,"nutrientName":"Sugars, total","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":430,"nutrientName":"Vitamin K (phylloquinone)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":415,"nutrientName":"Vitamin B-6","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":418,"nutrientName":"Vitamin B-12","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":313,"nutrientName":"Fluoride, F","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":309,"nutrientName":"Zinc, Zn","nutrientUnit":"mg","nutrientValue":1.8}],"highres":"https://s.boohee.cn/house/upload_food/2018/3/12/big_photo_url_18d27b42ca4915348d31135a538c58e9.jpg","imageIndex":48,"isRawFood":0,"lfCalories":338.0,"lfCholesterol":0.0,"lfDietaryFiber":6.0,"lfP":342.0,"lfPotassium":356.0,"lfProtein":10.1,"lfSaturatedFat":0.0,"lfSodium":2.1,"lfTotalCarbohydrate":77.4,"lfTotalFat":0.2,"locales":"zh_CN","measures":[{"measure":"份","qty":1,"servingWeight":150.0},{"measure":"克","qty":100,"servingWeight":100.0},{"measure":"瓷勺(满)","qty":1,"servingWeight":12.0}],"servingQty":100,"servingUnit":"g","servingWeightGrams":100.0,"thumb":"https://s.boohee.cn/house/upload_food/2018/3/12/mid_photo_url_326ac77a99719519ec2a28b5d165dc4c.jpg"},{"brandName":"金沙","claims":[],"foodId":"CiyPDi8UTx","foodName":"土豆","fullNutrients":[{"nutrientId":208,"nutrientName":"Energy","nutrientUnit":"kcal","nutrientValue":141.0},{"nutrientId":203,"nutrientName":"Protein","nutrientUnit":"g","nutrientValue":6.3},{"nutrientId":204,"nutrientName":"Total lipid (fat)","nutrientUnit":"g","nutrientValue":7.5},{"nutrientId":205,"nutrientName":"Carbohydrate, by difference","nutrientUnit":"g","nutrientValue":12.6},{"nutrientId":291,"nutrientName":"Fiber, total dietary","nutrientUnit":"g","nutrientValue":0.4},{"nutrientId":320,"nutrientName":"Vitamin A, RAE","nutrientUnit":"μg","nutrientValue":56.3},{"nutrientId":401,"nutrientName":"Vitamin C, total ascorbic acid","nutrientUnit":"mg","nutrientValue":15.4},{"nutrientId":323,"nutrientName":"Vitamin E (alpha-tocopherol)","nutrientUnit":"mg","nutrientValue":3.7},{"nutrientId":328,"nutrientName":"Vitamin D (D2 + D3)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":322,"nutrientName":"Carotene, alpha","nutrientUnit":"μg","nutrientValue":17.1},{"nutrientId":404,"nutrientName":"Thiamin","nutrientUnit":"mg","nutrientValue":0.1},{"nutrientId":405,"nutrientName":"Riboflavin","nutrientUnit":"mg","nutrientValue":0.2},{"nutrientId":406,"nutrientName":"Niacin","nutrientUnit":"mg","nutrientValue":0.7},{"nutrientId":601,"nutrientName":"Cholesterol","nutrientUnit":"mg","nutrientValue":258.2},{"nutrientId":304,"nutrientName":"Magnesium, Mg","nutrientUnit":"mg","nutrientValue":25.2},{"nutrientId":301,"nutrientName":"Calcium, Ca","nutrientUnit":"mg","nutrientValue":52.3},{"nutrientId":303,"nutrientName":"Iron, Fe","nutrientUnit":"mg","nutrientValue":2.0},{"nutrientId":312,"nutrientName":"Copper, Cu","nutrientUnit":"mg","nutrientValue":0.1},{"nutrientId":315,"nutrientName":"Manganese, Mn","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":306,"nutrientName":"Potassium, K","nutrientUnit":"mg","nutrientValue":268.5},{"nutrientId":305,"nutrientName":"Phosphorus, P","nutrientUnit":"mg","nutrientValue":115.4},{"nutrientId":307,"nutrientName":"Sodium, Na","nutrientUnit":"mg","nutrientValue":1234.7},{"nutrientId":317,"nutrientName":"Selenium, Se","nutrientUnit":"μg","nutrientValue":10.0},{"nutrientId":606,"nutrientName":"Fatty acids, total saturated","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":605,"nutrientName":"Fatty acids, total trans","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":645,"nutrientName":"Fatty acids, total monounsaturated","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":693,"nutrientName":"Fatty acids, total trans-monoenoic","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":221,"nutrientName":"Alcohol, ethyl","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":431,"nutrientName":"Folic acid","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":410,"nutrientName":"Pantothenic acid","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":421,"nutrientName":"Choline, total","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":269,"nutrientName":"Sugars, total","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":430,"nutrientName":"Vitamin K (phylloquinone)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":415,"nutrientName":"Vitamin B-6","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":418,"nutrientName":"Vitamin B-12","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":313,"nutrientName":"Fluoride, F","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":309,"nutrientName":"Zinc, Zn","nutrientUnit":"mg","nutrientValue":0.9}],"highres":"https://s.boohee.cn/house/upload_food/2016/10/13/big_photo_url_918354053831446b8a9065e34d09b10b.jpg","imageIndex":4,"isRawFood":0,"lfCalories":141.0,"lfCholesterol":258.2,"lfDietaryFiber":0.4,"lfP":115.4,"lfPotassium":268.5,"lfProtein":6.3,"lfSodium":1234.7,"lfTotalCarbohydrate":12.6,"lfTotalFat":7.5,"locales":"zh_CN","measures":[{"measure":"份","qty":1,"servingWeight":150.0},{"measure":"克","qty":100,"servingWeight":100.0}],"servingQty":100,"servingUnit":"g","servingWeightGrams":100.0,"thumb":"https://s.boohee.cn/house/upload_food/2016/10/13/mid_photo_url_c711981425dc423ead6317349418aba2.jpg"},{"claims":[],"foodId":"v1uZLEprkI","foodName":"牛奶","fullNutrients":[{"nutrientId":208,"nutrientName":"Energy","nutrientUnit":"kcal","nutrientValue":65.0},{"nutrientId":203,"nutrientName":"Protein","nutrientUnit":"g","nutrientValue":3.3},{"nutrientId":204,"nutrientName":"Total lipid (fat)","nutrientUnit":"g","nutrientValue":3.6},{"nutrientId":205,"nutrientName":"Carbohydrate, by difference","nutrientUnit":"g","nutrientValue":4.9},{"nutrientId":291,"nutrientName":"Fiber, total dietary","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":320,"nutrientName":"Vitamin A, RAE","nutrientUnit":"μg","nutrientValue":54.0},{"nutrientId":401,"nutrientName":"Vitamin C, total ascorbic acid","nutrientUnit":"mg","nutrientValue":1.0},{"nutrientId":323,"nutrientName":"Vitamin E (alpha-tocopherol)","nutrientUnit":"mg","nutrientValue":0.1},{"nutrientId":328,"nutrientName":"Vitamin D (D2 + D3)","nutrientUnit":"μg","nutrientValue":1.1},{"nutrientId":322,"nutrientName":"Carotene, alpha","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":404,"nutrientName":"Thiamin","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":405,"nutrientName":"Riboflavin","nutrientUnit":"mg","nutrientValue":0.1},{"nutrientId":406,"nutrientName":"Niacin","nutrientUnit":"mg","nutrientValue":0.1},{"nutrientId":601,"nutrientName":"Cholesterol","nutrientUnit":"mg","nutrientValue":15.0},{"nutrientId":304,"nutrientName":"Magnesium, Mg","nutrientUnit":"mg","nutrientValue":11.0},{"nutrientId":301,"nutrientName":"Calcium, Ca","nutrientUnit":"mg","nutrientValue":107.0},{"nutrientId":303,"nutrientName":"Iron, Fe","nutrientUnit":"mg","nutrientValue":0.3},{"nutrientId":312,"nutrientName":"Copper, Cu","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":315,"nutrientName":"Manganese, Mn","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":306,"nutrientName":"Potassium, K","nutrientUnit":"mg","nutrientValue":109.0},{"nutrientId":305,"nutrientName":"Phosphorus, P","nutrientUnit":"mg","nutrientValue":73.0},{"nutrientId":307,"nutrientName":"Sodium, Na","nutrientUnit":"mg","nutrientValue":37.2},{"nutrientId":317,"nutrientName":"Selenium, Se","nutrientUnit":"μg","nutrientValue":1.9},{"nutrientId":606,"nutrientName":"Fatty acids, total saturated","nutrientUnit":"g","nutrientValue":1.6},{"nutrientId":605,"nutrientName":"Fatty acids, total trans","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":645,"nutrientName":"Fatty acids, total monounsaturated","nutrientUnit":"g","nutrientValue":1.1},{"nutrientId":693,"nutrientName":"Fatty acids, total trans-monoenoic","nutrientUnit":"g","nutrientValue":0.2},{"nutrientId":221,"nutrientName":"Alcohol, ethyl","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":431,"nutrientName":"Folic acid","nutrientUnit":"μg","nutrientValue":3.5},{"nutrientId":410,"nutrientName":"Pantothenic acid","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":421,"nutrientName":"Choline, total","nutrientUnit":"mg","nutrientValue":14.0},{"nutrientId":269,"nutrientName":"Sugars, total","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":430,"nutrientName":"Vitamin K (phylloquinone)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":415,"nutrientName":"Vitamin B-6","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":418,"nutrientName":"Vitamin B-12","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":313,"nutrientName":"Fluoride, F","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":309,"nutrientName":"Zinc, Zn","nutrientUnit":"mg","nutrientValue":0.3}],"highres":"https://s.boohee.cn/house/new_food/big/e4ba04daf3814d06a7d0a25890adde36.jpg","imageIndex":24,"isRawFood":0,"lfCalories":65.0,"lfCholesterol":15.0,"lfDietaryFiber":0.0,"lfP":73.0,"lfPotassium":109.0,"lfProtein":3.3,"lfSaturatedFat":1.6,"lfSodium":37.2,"lfTotalCarbohydrate":4.9,"lfTotalFat":3.6,"locales":"zh_CN","measures":[{"measure":"份","qty":1,"servingWeight":150.0},{"measure":"克","qty":100,"servingWeight":100.0},{"measure":"杯","qty":1,"servingWeight":200.0},{"measure":"盒","qty":1,"servingWeight":250.0}],"servingQty":100,"servingUnit":"g","servingWeightGrams":100.0,"thumb":"https://s.boohee.cn/house/new_food/mid/e4ba04daf3814d06a7d0a25890adde36.jpg"},{"claims":[],"foodId":"t79zwjzDbN","foodName":"香蕉","fullNutrients":[{"nutrientId":208,"nutrientName":"Energy","nutrientUnit":"kcal","nutrientValue":93.0},{"nutrientId":203,"nutrientName":"Protein","nutrientUnit":"g","nutrientValue":1.4},{"nutrientId":204,"nutrientName":"Total lipid (fat)","nutrientUnit":"g","nutrientValue":0.2},{"nutrientId":205,"nutrientName":"Carbohydrate, by difference","nutrientUnit":"g","nutrientValue":22.0},{"nutrientId":291,"nutrientName":"Fiber, total dietary","nutrientUnit":"g","nutrientValue":1.2},{"nutrientId":320,"nutrientName":"Vitamin A, RAE","nutrientUnit":"μg","nutrientValue":5.0},{"nutrientId":401,"nutrientName":"Vitamin C, total ascorbic acid","nutrientUnit":"mg","nutrientValue":8.0},{"nutrientId":323,"nutrientName":"Vitamin E (alpha-tocopherol)","nutrientUnit":"mg","nutrientValue":0.2},{"nutrientId":328,"nutrientName":"Vitamin D (D2 + D3)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":322,"nutrientName":"Carotene, alpha","nutrientUnit":"μg","nutrientValue":60.0},{"nutrientId":404,"nutrientName":"Thiamin","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":405,"nutrientName":"Riboflavin","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":406,"nutrientName":"Niacin","nutrientUnit":"mg","nutrientValue":0.7},{"nutrientId":601,"nutrientName":"Cholesterol","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":304,"nutrientName":"Magnesium, Mg","nutrientUnit":"mg","nutrientValue":43.0},{"nutrientId":301,"nutrientName":"Calcium, Ca","nutrientUnit":"mg","nutrientValue":7.0},{"nutrientId":303,"nutrientName":"Iron, Fe","nutrientUnit":"mg","nutrientValue":0.4},{"nutrientId":312,"nutrientName":"Copper, Cu","nutrientUnit":"mg","nutrientValue":0.1},{"nutrientId":315,"nutrientName":"Manganese, Mn","nutrientUnit":"mg","nutrientValue":0.7},{"nutrientId":306,"nutrientName":"Potassium, K","nutrientUnit":"mg","nutrientValue":256.0},{"nutrientId":305,"nutrientName":"Phosphorus, P","nutrientUnit":"mg","nutrientValue":28.0},{"nutrientId":307,"nutrientName":"Sodium, Na","nutrientUnit":"mg","nutrientValue":0.8},{"nutrientId":317,"nutrientName":"Selenium, Se","nutrientUnit":"μg","nutrientValue":0.9},{"nutrientId":606,"nutrientName":"Fatty acids, total saturated","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":605,"nutrientName":"Fatty acids, total trans","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":645,"nutrientName":"Fatty acids, total monounsaturated","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":693,"nutrientName":"Fatty acids, total trans-monoenoic","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":221,"nutrientName":"Alcohol, ethyl","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":431,"nutrientName":"Folic acid","nutrientUnit":"μg","nutrientValue":20.2},{"nutrientId":410,"nutrientName":"Pantothenic acid","nutrientUnit":"mg","nutrientValue":0.3},{"nutrientId":421,"nutrientName":"Choline, total","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":269,"nutrientName":"Sugars, total","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":430,"nutrientName":"Vitamin K (phylloquinone)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":415,"nutrientName":"Vitamin B-6","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":418,"nutrientName":"Vitamin B-12","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":313,"nutrientName":"Fluoride, F","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":309,"nutrientName":"Zinc, Zn","nutrientUnit":"mg","nutrientValue":0.2}],"highres":"https://s.boohee.cn/house/new_food/big/8f92f8c50c9f44eab94b6a71b5c10e8e.jpg","imageIndex":18,"isRawFood":0,"lfCalories":93.0,"lfCholesterol":0.0,"lfDietaryFiber":1.2,"lfP":28.0,"lfPotassium":256.0,"lfProtein":1.4,"lfSaturatedFat":0.0,"lfSodium":0.8,"lfTotalCarbohydrate":22.0,"lfTotalFat":0.2,"locales":"zh_CN","measures":[{"measure":"份","qty":1,"servingWeight":150.0},{"measure":"克","qty":100,"servingWeight":100.0},{"measure":"根","qty":1,"servingWeight":88.5},{"measure":"根(大)","qty":1,"servingWeight":123.9},{"measure":"根(小)","qty":1,"servingWeight":64.9},{"measure":"根（小）","qty":1,"servingWeight":64.9}],"servingQty":100,"servingUnit":"g","servingWeightGrams":100.0,"thumb":"https://s.boohee.cn/house/new_food/mid/8f92f8c50c9f44eab94b6a71b5c10e8e.jpg"},{"claims":[],"foodId":"5VNPdjHXuM","foodName":"鳄梨(牛油果)","fullNutrients":[{"nutrientId":208,"nutrientName":"Energy","nutrientUnit":"kcal","nutrientValue":171.0},{"nutrientId":203,"nutrientName":"Protein","nutrientUnit":"g","nutrientValue":2.0},{"nutrientId":204,"nutrientName":"Total lipid (fat)","nutrientUnit":"g","nutrientValue":15.3},{"nutrientId":205,"nutrientName":"Carbohydrate, by difference","nutrientUnit":"g","nutrientValue":7.4},{"nutrientId":291,"nutrientName":"Fiber, total dietary","nutrientUnit":"g","nutrientValue":2.1},{"nutrientId":320,"nutrientName":"Vitamin A, RAE","nutrientUnit":"μg","nutrientValue":31.0},{"nutrientId":401,"nutrientName":"Vitamin C, total ascorbic acid","nutrientUnit":"mg","nutrientValue":8.0},{"nutrientId":323,"nutrientName":"Vitamin E (alpha-tocopherol)","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":328,"nutrientName":"Vitamin D (D2 + D3)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":322,"nutrientName":"Carotene, alpha","nutrientUnit":"μg","nutrientValue":366.0},{"nutrientId":404,"nutrientName":"Thiamin","nutrientUnit":"mg","nutrientValue":0.1},{"nutrientId":405,"nutrientName":"Riboflavin","nutrientUnit":"mg","nutrientValue":0.1},{"nutrientId":406,"nutrientName":"Niacin","nutrientUnit":"mg","nutrientValue":1.9},{"nutrientId":601,"nutrientName":"Cholesterol","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":304,"nutrientName":"Magnesium, Mg","nutrientUnit":"mg","nutrientValue":39.0},{"nutrientId":301,"nutrientName":"Calcium, Ca","nutrientUnit":"mg","nutrientValue":11.0},{"nutrientId":303,"nutrientName":"Iron, Fe","nutrientUnit":"mg","nutrientValue":1.0},{"nutrientId":312,"nutrientName":"Copper, Cu","nutrientUnit":"mg","nutrientValue":0.3},{"nutrientId":315,"nutrientName":"Manganese, Mn","nutrientUnit":"mg","nutrientValue":0.2},{"nutrientId":306,"nutrientName":"Potassium, K","nutrientUnit":"mg","nutrientValue":599.0},{"nutrientId":305,"nutrientName":"Phosphorus, P","nutrientUnit":"mg","nutrientValue":41.0},{"nutrientId":307,"nutrientName":"Sodium, Na","nutrientUnit":"mg","nutrientValue":10.0},{"nutrientId":317,"nutrientName":"Selenium, Se","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":606,"nutrientName":"Fatty acids, total saturated","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":605,"nutrientName":"Fatty acids, total trans","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":645,"nutrientName":"Fatty acids, total monounsaturated","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":693,"nutrientName":"Fatty acids, total trans-monoenoic","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":221,"nutrientName":"Alcohol, ethyl","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":431,"nutrientName":"Folic acid","nutrientUnit":"μg","nutrientValue":81.0},{"nutrientId":410,"nutrientName":"Pantothenic acid","nutrientUnit":"mg","nutrientValue":1.4},{"nutrientId":421,"nutrientName":"Choline, total","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":269,"nutrientName":"Sugars, total","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":430,"nutrientName":"Vitamin K (phylloquinone)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":415,"nutrientName":"Vitamin B-6","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":418,"nutrientName":"Vitamin B-12","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":313,"nutrientName":"Fluoride, F","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":309,"nutrientName":"Zinc, Zn","nutrientUnit":"mg","nutrientValue":0.4}],"highres":"https://s.boohee.cn/house/new_food/big/bd5a62a2ea2f41978aa20902bd9cfa35.jpg","imageIndex":20,"isRawFood":0,"lfCalories":171.0,"lfCholesterol":0.0,"lfDietaryFiber":2.1,"lfP":41.0,"lfPotassium":599.0,"lfProtein":2.0,"lfSaturatedFat":0.0,"lfSodium":10.0,"lfTotalCarbohydrate":7.4,"lfTotalFat":15.3,"locales":"zh_CN","measures":[{"measure":"份","qty":1,"servingWeight":150.0},{"measure":"克","qty":100,"servingWeight":100.0},{"measure":"大个","qty":1,"servingWeight":200.0},{"measure":"小个","qty":1,"servingWeight":100.0}],"servingQty":100,"servingUnit":"g","servingWeightGrams":100.0,"thumb":"https://s.boohee.cn/house/new_food/mid/bd5a62a2ea2f41978aa20902bd9cfa35.jpg"},{"claims":[],"foodId":"TOOYJ8dAir","foodName":"西兰花","fullNutrients":[{"nutrientId":208,"nutrientName":"Energy","nutrientUnit":"kcal","nutrientValue":27.0},{"nutrientId":203,"nutrientName":"Protein","nutrientUnit":"g","nutrientValue":3.5},{"nutrientId":204,"nutrientName":"Total lipid (fat)","nutrientUnit":"g","nutrientValue":0.6},{"nutrientId":205,"nutrientName":"Carbohydrate, by difference","nutrientUnit":"g","nutrientValue":3.7},{"nutrientId":291,"nutrientName":"Fiber, total dietary","nutrientUnit":"g","nutrientValue":2.6},{"nutrientId":320,"nutrientName":"Vitamin A, RAE","nutrientUnit":"μg","nutrientValue":13.0},{"nutrientId":401,"nutrientName":"Vitamin C, total ascorbic acid","nutrientUnit":"mg","nutrientValue":56.0},{"nutrientId":323,"nutrientName":"Vitamin E (alpha-tocopherol)","nutrientUnit":"mg","nutrientValue":0.8},{"nutrientId":328,"nutrientName":"Vitamin D (D2 + D3)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":322,"nutrientName":"Carotene, alpha","nutrientUnit":"μg","nutrientValue":151.0},{"nutrientId":404,"nutrientName":"Thiamin","nutrientUnit":"mg","nutrientValue":0.1},{"nutrientId":405,"nutrientName":"Riboflavin","nutrientUnit":"mg","nutrientValue":0.1},{"nutrientId":406,"nutrientName":"Niacin","nutrientUnit":"mg","nutrientValue":0.7},{"nutrientId":601,"nutrientName":"Cholesterol","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":304,"nutrientName":"Magnesium, Mg","nutrientUnit":"mg","nutrientValue":22.0},{"nutrientId":301,"nutrientName":"Calcium, Ca","nutrientUnit":"mg","nutrientValue":50.0},{"nutrientId":303,"nutrientName":"Iron, Fe","nutrientUnit":"mg","nutrientValue":0.9},{"nutrientId":312,"nutrientName":"Copper, Cu","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":315,"nutrientName":"Manganese, Mn","nutrientUnit":"mg","nutrientValue":0.2},{"nutrientId":306,"nutrientName":"Potassium, K","nutrientUnit":"mg","nutrientValue":179.0},{"nutrientId":305,"nutrientName":"Phosphorus, P","nutrientUnit":"mg","nutrientValue":61.0},{"nutrientId":307,"nutrientName":"Sodium, Na","nutrientUnit":"mg","nutrientValue":46.7},{"nutrientId":317,"nutrientName":"Selenium, Se","nutrientUnit":"μg","nutrientValue":0.4},{"nutrientId":606,"nutrientName":"Fatty acids, total saturated","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":605,"nutrientName":"Fatty acids, total trans","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":645,"nutrientName":"Fatty acids, total monounsaturated","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":693,"nutrientName":"Fatty acids, total trans-monoenoic","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":221,"nutrientName":"Alcohol, ethyl","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":431,"nutrientName":"Folic acid","nutrientUnit":"μg","nutrientValue":63.0},{"nutrientId":410,"nutrientName":"Pantothenic acid","nutrientUnit":"mg","nutrientValue":0.5},{"nutrientId":421,"nutrientName":"Choline, total","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":269,"nutrientName":"Sugars, total","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":430,"nutrientName":"Vitamin K (phylloquinone)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":415,"nutrientName":"Vitamin B-6","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":418,"nutrientName":"Vitamin B-12","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":313,"nutrientName":"Fluoride, F","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":309,"nutrientName":"Zinc, Zn","nutrientUnit":"mg","nutrientValue":0.5}],"highres":"https://s.boohee.cn/house/new_food/big/ff5f59e8e1e748af99b0df68322bb608.jpg","imageIndex":10,"isRawFood":0,"lfCalories":27.0,"lfCholesterol":0.0,"lfDietaryFiber":2.6,"lfP":61.0,"lfPotassium":179.0,"lfProtein":3.5,"lfSaturatedFat":0.0,"lfSodium":46.7,"lfTotalCarbohydrate":3.7,"lfTotalFat":0.6,"locales":"zh_CN","measures":[{"measure":"份","qty":1,"servingWeight":150.0},{"measure":"克","qty":100,"servingWeight":100.0},{"measure":"棵(中)","qty":1,"servingWeight":290.5},{"measure":"棵（中）","qty":1,"servingWeight":290.5}],"servingQty":100,"servingUnit":"g","servingWeightGrams":100.0,"thumb":"https://s.boohee.cn/house/new_food/mid/ff5f59e8e1e748af99b0df68322bb608.jpg"},{"claims":[],"foodId":"96SEpV2omw","foodName":"杏仁","fullNutrients":[{"nutrientId":208,"nutrientName":"Energy","nutrientUnit":"kcal","nutrientValue":578.0},{"nutrientId":203,"nutrientName":"Protein","nutrientUnit":"g","nutrientValue":22.5},{"nutrientId":204,"nutrientName":"Total lipid (fat)","nutrientUnit":"g","nutrientValue":45.4},{"nutrientId":205,"nutrientName":"Carbohydrate, by difference","nutrientUnit":"g","nutrientValue":23.9},{"nutrientId":291,"nutrientName":"Fiber, total dietary","nutrientUnit":"g","nutrientValue":8.0},{"nutrientId":320,"nutrientName":"Vitamin A, RAE","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":401,"nutrientName":"Vitamin C, total ascorbic acid","nutrientUnit":"mg","nutrientValue":26.0},{"nutrientId":323,"nutrientName":"Vitamin E (alpha-tocopherol)","nutrientUnit":"mg","nutrientValue":18.5},{"nutrientId":328,"nutrientName":"Vitamin D (D2 + D3)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":322,"nutrientName":"Carotene, alpha","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":404,"nutrientName":"Thiamin","nutrientUnit":"mg","nutrientValue":0.1},{"nutrientId":405,"nutrientName":"Riboflavin","nutrientUnit":"mg","nutrientValue":0.6},{"nutrientId":406,"nutrientName":"Niacin","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":601,"nutrientName":"Cholesterol","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":304,"nutrientName":"Magnesium, Mg","nutrientUnit":"mg","nutrientValue":178.0},{"nutrientId":301,"nutrientName":"Calcium, Ca","nutrientUnit":"mg","nutrientValue":97.0},{"nutrientId":303,"nutrientName":"Iron, Fe","nutrientUnit":"mg","nutrientValue":2.2},{"nutrientId":312,"nutrientName":"Copper, Cu","nutrientUnit":"mg","nutrientValue":0.8},{"nutrientId":315,"nutrientName":"Manganese, Mn","nutrientUnit":"mg","nutrientValue":0.8},{"nutrientId":306,"nutrientName":"Potassium, K","nutrientUnit":"mg","nutrientValue":106.0},{"nutrientId":305,"nutrientName":"Phosphorus, P","nutrientUnit":"mg","nutrientValue":27.0},{"nutrientId":307,"nutrientName":"Sodium, Na","nutrientUnit":"mg","nutrientValue":8.3},{"nutrientId":317,"nutrientName":"Selenium, Se","nutrientUnit":"μg","nutrientValue":15.7},{"nutrientId":606,"nutrientName":"Fatty acids, total saturated","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":605,"nutrientName":"Fatty acids, total trans","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":645,"nutrientName":"Fatty acids, total monounsaturated","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":693,"nutrientName":"Fatty acids, total trans-monoenoic","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":221,"nutrientName":"Alcohol, ethyl","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":431,"nutrientName":"Folic acid","nutrientUnit":"μg","nutrientValue":32.6},{"nutrientId":410,"nutrientName":"Pantothenic acid","nutrientUnit":"mg","nutrientValue":0.5},{"nutrientId":421,"nutrientName":"Choline, total","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":269,"nutrientName":"Sugars, total","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":430,"nutrientName":"Vitamin K (phylloquinone)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":415,"nutrientName":"Vitamin B-6","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":418,"nutrientName":"Vitamin B-12","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":313,"nutrientName":"Fluoride, F","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":309,"nutrientName":"Zinc, Zn","nutrientUnit":"mg","nutrientValue":4.3}],"highres":"https://s.boohee.cn/house/new_food/big/d7b6f9b7bf3a4582802ca5b388f28e5f.jpg","imageIndex":28,"isRawFood":0,"lfCalories":578.0,"lfCholesterol":0.0,"lfDietaryFiber":8.0,"lfP":27.0,"lfPotassium":106.0,"lfProtein":22.5,"lfSaturatedFat":0.0,"lfSodium":8.3,"lfTotalCarbohydrate":23.9,"lfTotalFat":45.4,"locales":"zh_CN","measures":[{"measure":"份","qty":1,"servingWeight":150.0},{"measure":"克","qty":100,"servingWeight":100.0},{"measure":"颗","qty":1,"servingWeight":3.0}],"servingQty":100,"servingUnit":"g","servingWeightGrams":100.0,"thumb":"https://s.boohee.cn/house/new_food/mid/d7b6f9b7bf3a4582802ca5b388f28e5f.jpg"},{"brandName":"佳之选","claims":[],"foodId":"RNZ6CpM8Iy","foodName":"橄榄油","fullNutrients":[{"nutrientId":208,"nutrientName":"Energy","nutrientUnit":"kcal","nutrientValue":823.0},{"nutrientId":203,"nutrientName":"Protein","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":204,"nutrientName":"Total lipid (fat)","nutrientUnit":"g","nutrientValue":91.4},{"nutrientId":205,"nutrientName":"Carbohydrate, by difference","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":291,"nutrientName":"Fiber, total dietary","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":320,"nutrientName":"Vitamin A, RAE","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":401,"nutrientName":"Vitamin C, total ascorbic acid","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":323,"nutrientName":"Vitamin E (alpha-tocopherol)","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":328,"nutrientName":"Vitamin D (D2 + D3)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":322,"nutrientName":"Carotene, alpha","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":404,"nutrientName":"Thiamin","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":405,"nutrientName":"Riboflavin","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":406,"nutrientName":"Niacin","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":601,"nutrientName":"Cholesterol","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":304,"nutrientName":"Magnesium, Mg","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":301,"nutrientName":"Calcium, Ca","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":303,"nutrientName":"Iron, Fe","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":312,"nutrientName":"Copper, Cu","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":315,"nutrientName":"Manganese, Mn","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":306,"nutrientName":"Potassium, K","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":305,"nutrientName":"Phosphorus, P","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":307,"nutrientName":"Sodium, Na","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":317,"nutrientName":"Selenium, Se","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":606,"nutrientName":"Fatty acids, total saturated","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":605,"nutrientName":"Fatty acids, total trans","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":645,"nutrientName":"Fatty acids, total monounsaturated","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":693,"nutrientName":"Fatty acids, total trans-monoenoic","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":221,"nutrientName":"Alcohol, ethyl","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":431,"nutrientName":"Folic acid","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":410,"nutrientName":"Pantothenic acid","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":421,"nutrientName":"Choline, total","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":269,"nutrientName":"Sugars, total","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":430,"nutrientName":"Vitamin K (phylloquinone)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":415,"nutrientName":"Vitamin B-6","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":418,"nutrientName":"Vitamin B-12","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":313,"nutrientName":"Fluoride, F","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":309,"nutrientName":"Zinc, Zn","nutrientUnit":"mg","nutrientValue":0.0}],"highres":"https://s.boohee.cn/house/upload_food/2008/6/14/46884_1213432085.jpg","imageIndex":32,"isRawFood":0,"lfCalories":823.0,"lfCholesterol":0.0,"lfP":0.0,"lfPotassium":0.0,"lfProtein":0.0,"lfSodium":0.0,"lfTotalCarbohydrate":0.0,"lfTotalFat":91.4,"locales":"zh_CN","measures":[{"measure":"份","qty":1,"servingWeight":150.0},{"measure":"克","qty":100,"servingWeight":100.0}],"servingQty":100,"servingUnit":"g","servingWeightGrams":100.0,"thumb":"https://s.boohee.cn/house/upload_food/2008/6/14/46884_1213432085mid.jpg"},{"brandName":"Delibreads","claims":[],"foodId":"WxnkZ7flfc","foodName":"面粉","fullNutrients":[{"nutrientId":208,"nutrientName":"Energy","nutrientUnit":"kcal","nutrientValue":310.0},{"nutrientId":203,"nutrientName":"Protein","nutrientUnit":"g","nutrientValue":7.1},{"nutrientId":204,"nutrientName":"Total lipid (fat)","nutrientUnit":"g","nutrientValue":6.2},{"nutrientId":205,"nutrientName":"Carbohydrate, by difference","nutrientUnit":"g","nutrientValue":55.0},{"nutrientId":291,"nutrientName":"Fiber, total dietary","nutrientUnit":"g","nutrientValue":1.9},{"nutrientId":320,"nutrientName":"Vitamin A, RAE","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":401,"nutrientName":"Vitamin C, total ascorbic acid","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":323,"nutrientName":"Vitamin E (alpha-tocopherol)","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":328,"nutrientName":"Vitamin D (D2 + D3)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":322,"nutrientName":"Carotene, alpha","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":404,"nutrientName":"Thiamin","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":405,"nutrientName":"Riboflavin","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":406,"nutrientName":"Niacin","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":601,"nutrientName":"Cholesterol","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":304,"nutrientName":"Magnesium, Mg","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":301,"nutrientName":"Calcium, Ca","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":303,"nutrientName":"Iron, Fe","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":312,"nutrientName":"Copper, Cu","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":315,"nutrientName":"Manganese, Mn","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":306,"nutrientName":"Potassium, K","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":305,"nutrientName":"Phosphorus, P","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":307,"nutrientName":"Sodium, Na","nutrientUnit":"mg","nutrientValue":507.0},{"nutrientId":317,"nutrientName":"Selenium, Se","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":606,"nutrientName":"Fatty acids, total saturated","nutrientUnit":"g","nutrientValue":3.1},{"nutrientId":605,"nutrientName":"Fatty acids, total trans","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":645,"nutrientName":"Fatty acids, total monounsaturated","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":693,"nutrientName":"Fatty acids, total trans-monoenoic","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":221,"nutrientName":"Alcohol, ethyl","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":431,"nutrientName":"Folic acid","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":410,"nutrientName":"Pantothenic acid","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":421,"nutrientName":"Choline, total","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":269,"nutrientName":"Sugars, total","nutrientUnit":"g","nutrientValue":0.0},{"nutrientId":430,"nutrientName":"Vitamin K (phylloquinone)","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":415,"nutrientName":"Vitamin B-6","nutrientUnit":"mg","nutrientValue":0.0},{"nutrientId":418,"nutrientName":"Vitamin B-12","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":313,"nutrientName":"Fluoride, F","nutrientUnit":"μg","nutrientValue":0.0},{"nutrientId":309,"nutrientName":"Zinc, Zn","nutrientUnit":"mg","nutrientValue":0.0}],"highres":"https://s.boohee.cn/house/upload_food/2018/2/24/big_photo_url_036FD1D8690D.jpg","imageIndex":35,"isRawFood":0,"lfCalories":310.0,"lfDietaryFiber":1.9,"lfProtein":7.1,"lfSaturatedFat":3.1,"lfSodium":507.0,"lfTotalCarbohydrate":55.0,"lfTotalFat":6.2,"locales":"zh_CN","measures":[{"measure":"份","qty":1,"servingWeight":150.0},{"measure":"克","qty":100,"servingWeight":100.0}],"servingQty":100,"servingUnit":"g","servingWeightGrams":100.0,"thumb":"https://s.boohee.cn/house/upload_food/2018/2/24/mid_photo_url_036FD1D8690D.jpg"}]
        """
        //Korre最多支持10个食物
        var defaults = GsonUtil.jsonStirngToObj(dataJson, Array<XinmiaoFoodScaleDefault>::class.java)?.toList() ?: emptyList()
        if (defaults.size > 10) {
            defaults = defaults.subList(0, 10)
        }
        return defaults
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        handleBackPressed()
    }

    private fun handleBackPressed() {
        PPBluetoothPeripheralKorreInstance.disConnect()
        finish()
    }

}