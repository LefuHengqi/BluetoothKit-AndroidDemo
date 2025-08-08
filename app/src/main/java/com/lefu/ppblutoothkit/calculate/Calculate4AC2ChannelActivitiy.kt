package com.lefu.ppblutoothkit.calculate

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.lefu.ppbase.PPBodyBaseModel
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.PPScaleDefine
import com.lefu.ppbase.util.Logger
import com.lefu.ppbase.vo.PPUserGender
import com.lefu.ppbase.vo.PPUserModel
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.util.UnitUtil
import com.lefu.ppblutoothkit.util.DataUtil
import com.lefu.ppblutoothkit.util.ImpedanceMappingUtil
import com.lefu.ppcalculate.PPBodyFatModel
import com.lefu.ppbase.vo.PPUnitType
import com.lefu.ppblutoothkit.device.PeripheralTorreActivity
import com.lefu.ppblutoothkit.device.handleSingleDocument
import com.lefu.ppblutoothkit.device.performFileSearch
import com.lefu.ppblutoothkit.device.requestPermission
import com.lefu.ppblutoothkit.util.CSVFIleUtil
import com.lefu.ppblutoothkit.util.CSVFIleUtil.CSVDataRow
import com.lefu.ppblutoothkit.util.FileUtil
import com.lefu.ppcalculate.vo.PPBodyDetailModel
import com.peng.ppscale.util.DeviceUtil

/**
 * 4电极交流双频算法
 */
class Calculate4AC2ChannelActivitiy : Activity() {

    var calcuteType: PPScaleDefine.PPDeviceCalcuteType? = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_1//CF577系列
    var deviceName: String = ""
    var spinner: Spinner? = null
    var sportMode: Boolean = false

    val etSex: android.widget.EditText by lazy { findViewById(R.id.etSex) }
    val etHeight: android.widget.EditText by lazy { findViewById(R.id.etHeight) }
    val etAge: android.widget.EditText by lazy { findViewById(R.id.etAge) }
    val etWeight: android.widget.EditText by lazy { findViewById(R.id.etWeight) }
    val etImpedance1: android.widget.EditText by lazy { findViewById(R.id.etImpedance1) }
    val etImpedance2: android.widget.EditText by lazy { findViewById(R.id.etImpedance2) }
    val sportModeEt: android.widget.EditText by lazy { findViewById(R.id.sportModeEt) }

    val REQUEST_CODE = 1024

    private var logTxt: TextView? = null

    var dataRows: List<CSVDataRow>? = null

    var dfuFilePath: String? = null//本地文件升级时使用

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_4ac2channel)

        findViewById<Button>(R.id.calculateBtn).setOnClickListener {
            startCalculate()
        }
        logTxt = findViewById<TextView>(R.id.logTxt)
        val nestedScrollViewLog = findViewById<NestedScrollView>(R.id.nestedScrollViewLog)

        logTxt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                nestedScrollViewLog.fullScroll(View.FOCUS_DOWN)
            }
        })
        initSpinner()
        initData()

        initbatchCalculation()
    }

    private fun initbatchCalculation() {

        findViewById<Button>(R.id.device_set_getFilePath).setOnClickListener {
            addPrint("check sdCard read and write permission")
            requestPermission()
        }

        findViewById<Button>(R.id.device_set_startDFU).setOnClickListener {
            startBatchCalculate()
        }


    }

    private fun initSpinner() {
        spinner = findViewById<Spinner>(R.id.spinner)
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter.add("Product0-普通人")
        adapter.add("Product0-运动员")
        adapter.add("Product1-运动员")
        spinner?.setAdapter(adapter)
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    sportMode = false
                    calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_1
                } else if (position == 1) {
                    sportMode = true
                    calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_1
                } else if (position == 2) {
                    sportMode = true
                    calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_1_1
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    private fun initData() {
        val tag = intent.getStringExtra("bodyDataModel")
        if (tag != null) {
            //显示称重完成后的数据
            val bodyBaseModel = DataUtil.bodyBaseModel
            deviceName = bodyBaseModel?.deviceModel?.deviceName ?: ""
            etSex.setText(if (bodyBaseModel?.userModel?.sex == PPUserGender.PPUserGenderFemale) "0" else "1")
            sportModeEt.setText(if (bodyBaseModel?.userModel?.isAthleteMode == false) "0" else "1")
            etHeight.setText(bodyBaseModel?.userModel?.userHeight.toString())
            etAge.setText(bodyBaseModel?.userModel?.age.toString())
            etWeight.setText(bodyBaseModel?.getPpWeightKg().toString())
            etImpedance1.setText(bodyBaseModel?.impedance.toString())
            etImpedance2.setText(bodyBaseModel?.ppImpedance100EnCode.toString())
        }
    }

    private fun startCalculate() {
        val sex = if (etSex.text?.toString()?.toInt() == 0) {
            PPUserGender.PPUserGenderFemale
        } else {
            PPUserGender.PPUserGenderMale
        }

        val height = etHeight.text?.toString()?.toInt() ?: 180
        val age = etAge.text?.toString()?.toInt() ?: 28
        val weight = etWeight.text?.toString()?.toDouble() ?: 70.00
        val impedance1 = etImpedance1.text?.toString()?.toLong() ?: 4195332L
        val impedance2 = etImpedance2.text?.toString()?.toLong() ?: 4195332L

        val userModel = PPUserModel.Builder()
            .setSex(sex) //gender
            .setHeight(height)//height 90-220
            .setAge(age)//age 6-99
            .setAthleteMode(sportMode)
            .build()

        val deviceModel = PPDeviceModel("", deviceName)//Select the corresponding Bluetooth name according to your own device
        deviceModel.deviceCalcuteType = calcuteType ?: PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_1
        deviceModel.deviceAccuracyType = if (DeviceUtil.Point2_Scale_List.contains(deviceModel.deviceName)) {
            PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint005
        } else {
            PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint01
        }
        val bodyBaseModel = PPBodyBaseModel()
        bodyBaseModel.weight = UnitUtil.getWeight(weight)
        bodyBaseModel.impedance = impedance1
        bodyBaseModel.ppImpedance100EnCode = impedance2
        bodyBaseModel.deviceModel = deviceModel
        bodyBaseModel.userModel = userModel
        bodyBaseModel.unit = PPUnitType.Unit_KG

        val ppBodyFatModel = PPBodyFatModel(bodyBaseModel)

//        val ppBodyDetailModel = PPBodyDetailModel(ppBodyFatModel)
//        Logger.d("liyp_", ppBodyDetailModel.toString())

        DataUtil.bodyDataModel = ppBodyFatModel
        Logger.d("liyp_", ppBodyFatModel.toString())

        val intent = Intent(this, BodyDataDetailActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                performFileSearch()
            } else {
                Toast.makeText(this@Calculate4AC2ChannelActivitiy, "存储权限获取失败", Toast.LENGTH_SHORT).show()
            }
        } else if (resultCode == RESULT_OK && data != null) {
            //当单选选了一个文件后返回
            if (data.data != null) {
                val result = handleSingleDocument(data)

                // 对dataRows中的impedance50Khz和impedance100Khz进行字典映射
                result?.dataRows?.let { dataRows ->
                    val mappedDataRows = ImpedanceMappingUtil.mapImpedanceValues(dataRows)
                    addPrint("原始数据行数: ${dataRows.size}")
                    addPrint("映射后数据行数: ${mappedDataRows.size}")

                    // 打印前几行的映射结果作为示例
                    mappedDataRows.take(3).forEachIndexed { index, dataRow ->
                        addPrint("第${index + 1}行映射结果:")
                        addPrint("  编号: ${dataRow.id}, 姓名: ${dataRow.name}")
                        addPrint("  50Khz阻抗: ${dataRow.impedance50Khz}")
                        addPrint("  100Khz阻抗: ${dataRow.impedance100Khz}")
                    }

                    this.dataRows = mappedDataRows
                }

            }
        }
    }


    fun startBatchCalculate() {
        if (this.dataRows.isNullOrEmpty().not()) {
            addPrint("开始批量计算")

            val data1Results = mutableListOf<PPBodyFatModel?>()
            val data2Results = mutableListOf<PPBodyFatModel?>()
            val data3Results = mutableListOf<PPBodyFatModel?>()

            this.dataRows?.forEachIndexed { index, dataRow ->
                val data1 = calculateCSVDataByProduct0(dataRow, false, 0)
                val data2 = calculateCSVDataByProduct0(dataRow, true, 0)
                val data3 = calculateCSVDataByProduct0(dataRow, true, 1)

                Logger.d("liyp_", "第${index + 1} 行data1计算结果: ${data1.toString()}")
                Logger.d("liyp_", "第${index + 1} 行data2计算结果: ${data2.toString()}")
                Logger.d("liyp_", "第${index + 1} 行data3计算结果: ${data3.toString()}")

                // 收集计算结果
                data1Results.add(data1)
                data2Results.add(data2)
                data3Results.add(data3)
            }

            // 导出到CSV文件
            addPrint("开始导出计算结果到CSV文件...")
            val fileUri = CSVFIleUtil.exportCalculationResultsToCSV(
                this, 
                this.dataRows!!, 
                data1Results, 
                data2Results, 
                data3Results
            )
            if (fileUri != null) {
                // 从Uri获取文件路径并分享
                val filePath = fileUri.path
                if (filePath != null) {
                    FileUtil.sendEmail(this, filePath)
                    addPrint("CSV文件导出成功！文件已保存到缓存目录并可分享")
                } else {
                    addPrint("CSV文件导出成功，但无法获取文件路径")
                }
            } else {
                addPrint("CSV文件导出失败，请检查存储权限")
            }

        } else {
            addPrint("没有数据行可供计算，请先选择文件")
            return
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            ) {
                performFileSearch()
            } else {
                Toast.makeText(this@Calculate4AC2ChannelActivitiy, "存储权限获取失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addPrint(msg: String) {
        if (msg.isNotEmpty()) {
            Logger.d(msg)
            logTxt?.append("$msg\n")
        }
    }

}