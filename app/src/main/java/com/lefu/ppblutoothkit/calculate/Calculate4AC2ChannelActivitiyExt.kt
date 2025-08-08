package com.lefu.ppblutoothkit.calculate

import android.Manifest
import android.R
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lefu.ppbase.PPBodyBaseModel
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.PPScaleDefine
import com.lefu.ppbase.vo.PPUnitType
import com.lefu.ppbase.vo.PPUserGender
import com.lefu.ppbase.vo.PPUserModel
import com.lefu.ppblutoothkit.util.CSVFIleUtil
import com.lefu.ppblutoothkit.util.CSVEncodingTestUtil
import com.lefu.ppblutoothkit.util.UnitUtil
import com.lefu.ppcalculate.PPBodyFatModel
import com.peng.ppscale.util.DeviceUtil

fun Calculate4AC2ChannelActivitiy.requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // 先判断有没有权限
        if (Environment.isExternalStorageManager()) {
            performFileSearch()
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:" + this.packageName)
            startActivityForResult(intent, REQUEST_CODE)
        }
    } else {
        val permission_read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission_read != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf<String>(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), REQUEST_CODE
            )
        } else {
            performFileSearch()
        }
    }
}

//选择文件
fun Calculate4AC2ChannelActivitiy.performFileSearch() {
    addPrint("have permission")
    addPrint("please select dfu file ,It is a csv file")
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    //允许多选 长按多选
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
    intent.putExtra(Intent.EXTRA_ALARM_COUNT, true)
    //不限制选取类型
    intent.type = "*/*"
    startActivityForResult(Intent.createChooser(intent, "选择CSV文件"), 1)
}

fun Calculate4AC2ChannelActivitiy.handleSingleDocument(data: Intent): CSVFIleUtil.CSVParseResult? {
    val uri = data.data

    uri?.let {
        try {
            // 添加编码调试信息
            addPrint("开始CSV文件编码分析...")
            CSVEncodingTestUtil.analyzeFileBytes(this@handleSingleDocument, it)
            CSVEncodingTestUtil.testEncodings(this@handleSingleDocument, it)

            // 使用增强版的CSV解析方法，支持自动分隔符检测和灵活的字段映射
            val result = CSVFIleUtil.parseCSVFromUriEnhanced(this@handleSingleDocument, it)

            addPrint("CSV解析完成:")
            addPrint("表头: ${result.headers}")
            addPrint("总数据行数: ${result.totalRows}")
            addPrint("成功解析行数: ${result.dataRows.size}")
            addPrint("错误行数: ${result.errorRows.size}")

            if (result.errorRows.isNotEmpty()) {
                addPrint("解析错误:")
                result.errorRows.forEach { (lineNumber, error) ->
                    addPrint("第${lineNumber}行: $error")
                }
            }
            return result
        } catch (e: Exception) {
            addPrint("CSV解析失败: ${e.message}")
            addPrint("错误详情: ${e.stackTraceToString()}")
            e.printStackTrace()
        }
    }
    return null
}

fun Calculate4AC2ChannelActivitiy.calculateCSVDataByProduct0(dataRow: CSVFIleUtil.CSVDataRow, sportMode: Boolean, product: Int): PPBodyFatModel? {
    val sex = if (dataRow.gender.equals("男", false)) {
        PPUserGender.PPUserGenderMale
    } else {
        PPUserGender.PPUserGenderFemale
    }
    val height = dataRow.height.toInt()
    val age = dataRow.age
    val weight = dataRow.weight.toDouble()
    val impedance1 = dataRow.impedance50Khz
    val impedance2 = dataRow.impedance100Khz

    val userModel = PPUserModel.Builder()
        .setSex(sex) //gender
        .setHeight(height)//height 90-220
        .setAge(age)//age 6-99
        .setAthleteMode(sportMode)
        .build()

    val deviceModel = PPDeviceModel("", deviceName)//Select the corresponding Bluetooth name according to your own device
    if (product == 0) {
        deviceModel.deviceCalcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_1
    } else {
        deviceModel.deviceCalcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_1_1
    }

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

    return PPBodyFatModel(bodyBaseModel)
}



