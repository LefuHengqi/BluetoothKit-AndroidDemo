package com.lefu.ppblutoothkit.producttest.dfu

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.device.instance.PPBlutoothPeripheralTorreInstance
import com.lefu.ppblutoothkit.log.LogActivity
import com.lefu.ppblutoothkit.producttest.TestResultVo
import com.lefu.ppblutoothkit.producttest.devicelist.ProductTestDeviceListActivity
import com.lefu.ppblutoothkit.util.FilePermissionUtil
import com.peng.ppscale.business.ble.listener.PPBleStateInterface
import com.peng.ppscale.business.state.PPBleSwitchState
import com.peng.ppscale.business.state.PPBleWorkState
import com.peng.ppscale.business.torre.dfu.DfuHelper
import com.peng.ppscale.device.PeripheralTorre.PPBlutoothPeripheralTorreController
import com.peng.ppscale.search.PPSearchManager
import com.peng.ppscale.util.Logger
import com.peng.ppscale.vo.PPDeviceModel
import com.peng.ppscale.vo.PPScaleDefine
import kotlinx.android.synthetic.main.product_test_dfu_test_activity.mDeviceMacTv
import kotlinx.android.synthetic.main.product_test_dfu_test_activity.mDeviceNameTv
import kotlinx.android.synthetic.main.product_test_dfu_test_activity.mDfuFirmwareVersionTv
import kotlinx.android.synthetic.main.product_test_dfu_test_activity.mDfuSelectFilePathBtn
import kotlinx.android.synthetic.main.product_test_dfu_test_activity.mDfuTestNumEt
import kotlinx.android.synthetic.main.product_test_dfu_test_activity.mSelectDeviceBtn
import kotlinx.android.synthetic.main.product_test_dfu_test_activity.mTestStateTv
import kotlinx.android.synthetic.main.product_test_dfu_test_activity.startTestBtn


class ProductTestDfuTestActivity : Activity(), View.OnClickListener {

    val REQUEST_CODE = 1024

    private var logTxt: TextView? = null
    var totalNum = 20
    var dfuFilePath: String? = null//本地文件升级时使用
    var deviceNickName: String? = null//设备类型过滤器，所填内容必须包含在蓝牙名称里面
    var controller: PPBlutoothPeripheralTorreController? =
        PPBlutoothPeripheralTorreInstance.instance.controller
    var toolbar: Toolbar? = null
    var isTesting = false//是否正在测试
    var testResultVo = TestResultVo()//测量结果
    var allLen: Long = 1//固件总长

    var ppSearchManager = PPSearchManager()

    companion object {
        var deviceModel: PPDeviceModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.product_test_dfu_test_activity)
        initToolbar()
        startTestBtn?.setOnClickListener(this)
        mDfuSelectFilePathBtn.setOnClickListener(this)
        mSelectDeviceBtn.setOnClickListener(this)

        initLogView()
        initSpinnerView()

        findViewById<ToggleButton>(R.id.switchModeToggleBtn).setOnCheckedChangeListener { buttonView, isChecked ->
            if (controller?.connectState()?.not() == true) {
                addPrint("maternity mode isChecked:$isChecked")
                /**
                 * 模式切换
                 * @param type 0设置 1获取
                 * @param mode 模式切换 0工厂 1用户
                 * @param sendResultCallBack
                 */
                controller?.getTorreDeviceManager()?.switchMode(0, if (isChecked) 0 else 1, null)
            } else {
                addPrint("设备已连接")
            }
        }
    }

    fun getDeviceList(): List<String> {
        val options = ArrayList<String>()
        options.add("All")
        options.add("eufy")
        options.add("S400 Pro")
        options.add("S400")
        options.add("CF568")
        return options
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar)
        toolbar?.title = "TestDFU"
        toolbar?.setTitleTextColor(Color.WHITE)
        toolbar?.inflateMenu(R.menu.toolbar_menu)
        toolbar?.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_item_export_app_log -> {
//                    startUploadLog()
                    LogActivity.logType = 0
                    val intent = Intent(this, LogActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.menu_item_export_device_log -> {
                    LogActivity.logType = 1
                    val intent = Intent(this, LogActivity::class.java)
                    startActivity(intent)

//                    if (deviceModel != null && controller?.connectState() ?: false) {
//                        //logFilePath 指定文件存储路径，必传例如：val fileFath = context.filesDir.absolutePath + "/Log/DeviceLog"
//                        val fileFath = filesDir.absolutePath + "/Log/DeviceLog"
//                        controller?.getTorreDeviceManager()?.syncLog(fileFath, deviceLogInterface)
//                    } else {
//                        showToast("设备未连接")
//                    }
                    true
                }

                R.id.menu_item_export_test_log -> {
                    addPrint("Export test report")
                    exportTestReport()
                    true
                }

                else -> super.onOptionsItemSelected(it)
            }
        }
    }

    private fun initSpinnerView() {
        val mDfuSpinner = findViewById<Spinner>(R.id.mDfuSpinner)
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getDeviceList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mDfuSpinner.adapter = adapter
        mDfuSpinner.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int,
                id: Long
            ) {
                if (position == 0) {
                    deviceNickName = ""
                } else {
                    deviceNickName = getDeviceList().get(position)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    private fun initLogView() {
        logTxt = findViewById<TextView>(R.id.logTxt)
        val nestedScrollViewLog = findViewById<NestedScrollView>(R.id.nestedScrollViewLog)
        logTxt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                nestedScrollViewLog.fullScroll(View.FOCUS_DOWN)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        updateDeviceStateUI()
        ppSearchManager.registerBluetoothStateListener(bleStateInterface)
    }

    private fun updateDeviceStateUI() {
        if (deviceModel != null) {
            mDeviceMacTv?.text = deviceModel?.deviceMac
            mDeviceNameTv?.text = deviceModel?.deviceName
        } else {
            mTestStateTv?.text = "未选择设备"
            return
        }
        if (dfuFilePath.isNullOrEmpty()) {
            mTestStateTv?.text = "未选择固件"
        } else {
            mTestStateTv?.text = "未开始"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                FilePermissionUtil.performFileSearch(this, REQUEST_CODE)
            } else {
                showToast("存储权限获取失败")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
            //当单选选了一个文件后返回
            if (data.data != null) {
                dfuFilePath = FilePermissionUtil.handleSingleDocument(this@ProductTestDfuTestActivity, data)
                addPrint("DFU 文件解压路径：$dfuFilePath")
                if (dfuFilePath != null && !TextUtils.isEmpty(dfuFilePath)) {
                    val dfuFileVo = DfuHelper.getDfuFile(dfuFilePath)
                    if (dfuFileVo != null && dfuFileVo.packageVersion.isNullOrBlank().not()) {
                        DfuHelper.initDfuData(dfuFileVo)
                        allLen = DfuHelper.allLen //初始化allLen

                        addPrint("DFU File Info: $dfuFileVo")
                        mDfuFirmwareVersionTv?.text = dfuFileVo.packageVersion

                        addPrint("DFU File All Len: $allLen")

                    } else {
                        mTestStateTv?.text = "固件解析失败"
                        addPrint("DFU 固件解析失败")
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mDfuSelectFilePathBtn -> {
                FilePermissionUtil.requestPermission(this, REQUEST_CODE)
            }

            R.id.startTestBtn -> {
                if (!isTesting) {
                    startTest()
                } else {
                    startTestBtn?.setText("开始测试")
                    testResultVo.endTime = System.currentTimeMillis()
                    stopTest()
                }
            }

            R.id.mSelectDeviceBtn -> {
                ProductTestDeviceListActivity.filterName = deviceNickName ?: ""
                ProductTestDeviceListActivity.searchType = 1
                startActivity(Intent(this@ProductTestDfuTestActivity, ProductTestDeviceListActivity::class.java))
            }
        }
    }

    private fun stopTest() {
        isTesting = false
        if (controller?.getTorreDeviceManager()?.isDFU == true) {
            controller?.getTorreDeviceManager()?.stopDFU()
        }
        controller?.stopSeach()
        controller?.disConnect()
    }

    private fun startTest() {
        if (dfuFilePath.isNullOrBlank().not()) {
            if (deviceModel != null && deviceModel?.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralTorre) {
                val totalNum = mDfuTestNumEt?.text?.toString()?.toInt() ?: 0//总次数
                if (totalNum > 0) {
                    deviceModel?.let {
                        isTesting = true
                        currentNum = 0
                        startTestBtn?.setText("停止测试")
                        if (controller?.connectState()?.not() == true) {
                            addPrint("开始连接")
                            mTestStateTv?.text = "开始连接"
                            initResultVo(totalNum)
                            controller?.startConnect(it, bleStateInterface)
                        } else {
                            addPrint("设备已连接")
                        }
                    }
                } else {
                    showToast("请输入正确的OTA次数")
                }
            } else {
                mTestStateTv?.text = "请选择设备"
                addPrint("Error: Device type error or Device is null,Please select device")
            }
        } else {
            mTestStateTv?.text = "未选择升级固件"
            //本地升级时未选则升级文件
            addPrint("Error: Upgrade files if not selected during local upgrade")
        }
    }

    private fun initResultVo(totalNum: Int) {
        testResultVo.initObj()
        testResultVo.deviceModel = deviceModel?.deviceName ?: ""
        testResultVo.deviceMac = deviceModel?.deviceMac ?: ""
        val model = Build.MODEL
        val brand = Build.BRAND
        Logger.d("手机型号 $model 手机品牌 $brand ")
        testResultVo.phoneModel = "$brand $model"
        testResultVo.firmwareVersion = mDfuFirmwareVersionTv?.text?.toString() ?: ""
        testResultVo.packagesSize = allLen
        testResultVo.planNum = totalNum
        testResultVo.startTime = System.currentTimeMillis()
    }

    fun addPrint(msg: String) {
        if (msg.isNotEmpty()) {
            Logger.d(msg)
            logTxt?.append("$msg\n")
        }
    }

    fun showToast(msg: String) {
        addPrint(msg)
        Toast.makeText(this@ProductTestDfuTestActivity, msg, Toast.LENGTH_SHORT).show()
    }

    val bleStateInterface = object : PPBleStateInterface() {
        override fun monitorBluetoothWorkState(ppBleWorkState: PPBleWorkState?, deviceModel: PPDeviceModel?) {
            if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
                mTestStateTv?.text = getString(R.string.device_connected)
                addPrint(getString(R.string.device_connected))
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateCanBeConnected) {
                if (Companion.deviceModel != null) {
                    deviceModel?.let {
                        if (deviceModel.deviceMac.equals(Companion.deviceModel!!.deviceMac)) {
                            mTestStateTv?.text = getString(R.string.device_be_connected)
                            addPrint(getString(R.string.device_be_connected))
                            controller?.startConnect(it, this)
                        }
                    }
                }
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
                mTestStateTv?.text = getString(R.string.device_connecting)
                addPrint(getString(R.string.device_connecting))
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
                mTestStateTv?.text = getString(R.string.device_disconnected)
                addPrint(getString(R.string.device_disconnected))
                if (isTesting) {
                    reSearchAndConnectDevice()
                }
            } else if (ppBleWorkState == PPBleWorkState.PPBleStateSearchCanceled) {
                mTestStateTv?.text = getString(R.string.stop_scanning)
                addPrint(getString(R.string.stop_scanning))
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
                mTestStateTv?.text = getString(R.string.scan_timeout)
                addPrint(getString(R.string.scan_timeout))
                testResultVo.endTime = System.currentTimeMillis()
                if (isTesting) {
                    reSearchAndConnectDevice()
                }
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                mTestStateTv?.text = getString(R.string.scanning)
                addPrint(getString(R.string.scanning))
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateWritable) {
                addPrint(getString(R.string.writable))
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnectFailed) {
                if (isTesting) {
                    addPrint(getString(R.string.connect_fail))
                    reSearchAndConnectDevice()
                }
            }
        }

        override fun monitorBluetoothSwitchState(ppBleSwitchState: PPBleSwitchState?) {
            addPrint("ppBleSwitchState:$ppBleSwitchState isTesting:$isTesting")
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                addPrint(getString(R.string.system_bluetooth_disconnect))
                if (isTesting) {
                    addPrint("dfuFail ppBleSwitchState:$ppBleSwitchState isTesting:$isTesting")
                }
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                addPrint(getString(R.string.system_blutooth_on))
                if (isTesting) {
                    reSearchAndConnectDevice()
                }
            }
        }

        override fun monitorMtuChange(deviceModel: PPDeviceModel?) {
            addPrint("monitorMtuChange mtu:${deviceModel?.mtu}")
            totalNum = mDfuTestNumEt?.text.toString().toInt()//总次数
            dfuFilePath?.let {
                startDfu(it)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        stopTest()
    }

}