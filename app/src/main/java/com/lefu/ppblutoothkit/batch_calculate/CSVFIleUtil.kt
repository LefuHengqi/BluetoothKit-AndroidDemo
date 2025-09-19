package com.lefu.ppblutoothkit.batch_calculate

import android.content.Context
import android.net.Uri
import android.util.Log
import com.lefu.ppcalculate.PPBodyFatModel
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Serializable
import java.nio.charset.Charset
import kotlin.collections.set
import kotlin.text.contains

/**
 * CSV文件解析工具类
 */
object CSVFIleUtil {

    /**
     * CSV数据行实体类
     * 对应CSV文件中的数据格式：编号、姓名、体重、身高、性别、年龄、阻抗值(可变长度)
     *
     *
     *
     *     //100KHz左手阻抗加密值(下位机上传值)
     *     var z100KhzLeftArmEnCode: Long = 0
     *
     *     //100KHz左腳阻抗加密值(下位机上传值)
     *     var z100KhzLeftLegEnCode: Long = 0
     *
     *     //100KHz右手阻抗加密值(下位机上传值)
     *     var z100KhzRightArmEnCode: Long = 0
     *
     *     //100KHz右腳阻抗加密值(下位机上传值)
     *     var z100KhzRightLegEnCode: Long = 0
     *
     *     //100KHz軀幹阻抗加密值(下位机上传值)
     *     var z100KhzTrunkEnCode: Long = 0
     *
     *     //20KHz左手阻抗加密值(下位机上传值)
     *     var z20KhzLeftArmEnCode: Long = 0
     *
     *     //20KHz左腳阻抗加密值(下位机上传值)
     *     var z20KhzLeftLegEnCode: Long = 0
     *
     *     //20KHz右手阻抗加密值(下位机上传值)
     *     var z20KhzRightArmEnCode: Long = 0
     *
     *     //20KHz右腳阻抗加密值(下位机上传值)
     *     var z20KhzRightLegEnCode: Long = 0
     *
     *     //20KHz軀幹阻抗加密值(下位机上传值)
     *     var z20KhzTrunkEnCode: Long = 0
     */
    data class CSVDataRow(
        val id: String,           // 编号
        val name: String,         // 姓名
        val weight: Float,        // 体重(kg)
        val height: Float,        // 身高(cm)
        val gender: String,       // 性别
        val age: Int,            // 年龄
        val batchNumber: String,  // 批次编号
        val impedanceValues: Map<String, Long> // 阻抗值映射，key为频率标识(如"impedance50Khz", "impedance100Khz")，value为阻抗值
    ) : Serializable {

        // 兼容性方法：获取50KHz阻抗值
        val impedance50Khz: Long
            get() = impedanceValues["impedance50Khz"] ?: impedanceValues["50"] ?: 0L

        // 兼容性方法：获取100KHz阻抗值
        val impedance100Khz: Long
            get() = impedanceValues["impedance100Khz"] ?: impedanceValues["100"] ?: 0L

        // 100KHz左手阻抗加密值(下位机上传值)
        val z100KhzLeftArmEnCode: Long
            get() = impedanceValues["z100KhzLeftArmEnCode"] ?: 0L

        // 100KHz左腳阻抗加密值(下位机上传值)
        val z100KhzLeftLegEnCode: Long
            get() = impedanceValues["z100KhzLeftLegEnCode"] ?: 0L

        // 100KHz右手阻抗加密值(下位机上传值)
        val z100KhzRightArmEnCode: Long
            get() = impedanceValues["z100KhzRightArmEnCode"] ?: 0L

        // 100KHz右腳阻抗加密值(下位机上传值)
        val z100KhzRightLegEnCode: Long
            get() = impedanceValues["z100KhzRightLegEnCode"] ?: 0L

        // 100KHz軀幹阻抗加密值(下位机上传值)
        val z100KhzTrunkEnCode: Long
            get() = impedanceValues["z100KhzTrunkEnCode"] ?: 0L

        // 20KHz左手阻抗加密值(下位机上传值)
        val z20KhzLeftArmEnCode: Long
            get() = impedanceValues["z20KhzLeftArmEnCode"] ?: 0L

        // 20KHz左腳阻抗加密值(下位机上传值)
        val z20KhzLeftLegEnCode: Long
            get() = impedanceValues["z20KhzLeftLegEnCode"] ?: 0L

        // 20KHz右手阻抗加密值(下位机上传值)
        val z20KhzRightArmEnCode: Long
            get() = impedanceValues["z20KhzRightArmEnCode"] ?: 0L

        // 20KHz右腳阻抗加密值(下位机上传值)
        val z20KhzRightLegEnCode: Long
            get() = impedanceValues["z20KhzRightLegEnCode"] ?: 0L

        // 20KHz軀幹阻抗加密值(下位机上传值)
        val z20KhzTrunkEnCode: Long
            get() = impedanceValues["z20KhzTrunkEnCode"] ?: 0L

        // 获取所有阻抗频率
        fun getImpedanceFrequencies(): Set<String> = impedanceValues.keys

        // 获取指定频率的阻抗值
        fun getImpedanceValue(frequency: String): Long? = impedanceValues[frequency]

        // 检查是否包含指定频率的阻抗
        fun hasImpedance(frequency: String): Boolean = impedanceValues.containsKey(frequency)
    }

    /**
     * CSV导出行数据类 - 包含所有需要导出的字段
     */
    data class CSVExportRow(
        // 基础数据字段
        val id: String,
        val name: String,
        val weight: Float,
        val height: Float,
        val gender: String,
        val age: Int,
        val impedanceValues: Map<String, Long>, // 阻抗值映射

        // 从PPBodyFatModel获取的所有字段
        val ppWaterECWKg: Float,                    // 全身体组成:细胞外水量(kg)
        val ppWaterICWKg: Float,                    // 全身体组成:细胞内水量(kg)
        val ppBodyFatKgLeftArm: Float,              // 左手脂肪量(kg), 分辨率0.1
        val ppBodyFatKgLeftLeg: Float,              // 左脚脂肪量(kg), 分辨率0.1
        val ppBodyFatKgRightArm: Float,             // 右手脂肪量(kg), 分辨率0.1
        val ppBodyFatKgRightLeg: Float,             // 右脚脂肪量(kg), 分辨率0.1
        val ppBodyFatKgTrunk: Float,                // 躯干脂肪量(kg), 分辨率0.1
        val ppBodyFatRateLeftArm: Float,            // 左手脂肪率(%), 分辨率0.1
        val ppBodyFatRateLeftLeg: Float,            // 左脚脂肪率(%), 分辨率0.1
        val ppBodyFatRateRightArm: Float,           // 右手脂肪率(%), 分辨率0.1
        val ppBodyFatRateRightLeg: Float,           // 右脚脂肪率(%), 分辨率0.1
        val ppBodyFatRateTrunk: Float,              // 躯干脂肪率(%), 分辨率0.1
        val ppMuscleKgLeftArm: Float,               // 左手肌肉量(kg), 分辨率0.1
        val ppMuscleRateLeftArm: Float,             // 左手肌肉率(%), 分辨率0.1
        val ppMuscleKgLeftLeg: Float,               // 左脚肌肉量(kg), 分辨率0.1
        val ppMuscleRateLeftLeg: Float,             // 左脚肌肉率(%), 分辨率0.1
        val ppMuscleKgRightArm: Float,              // 右手肌肉量(kg), 分辨率0.1
        val ppMuscleRateRightArm: Float,            // 右手肌肉率(%), 分辨率0.1
        val ppMuscleKgRightLeg: Float,              // 右脚肌肉量(kg), 分辨率0.1
        val ppMuscleRateRightLeg: Float,            // 右脚肌肉率(%), 分辨率0.1
        val ppMuscleKgTrunk: Float,                 // 躯干肌肉量(kg), 分辨率0.1
        val ppMuscleRateTrunk: Float,               // 躯干肌肉率(%), 分辨率0.1
        val z100KhzLeftArmEnCode: Long,             // 100KHz左手阻抗加密值
        val z100KhzLeftLegEnCode: Long,             // 100KHz左腳阻抗加密值
        val z100KhzRightArmEnCode: Long,            // 100KHz右手阻抗加密值
        val z100KhzRightLegEnCode: Long,            // 100KHz右腳阻抗加密值
        val z100KhzTrunkEnCode: Long,               // 100KHz軀幹阻抗加密值
        val z20KhzLeftArmEnCode: Long,              // 20KHz左手阻抗加密值
        val z20KhzLeftLegEnCode: Long,              // 20KHz左腳阻抗加密值
        val z20KhzRightArmEnCode: Long,             // 20KHz右手阻抗加密值
        val z20KhzRightLegEnCode: Long,             // 20KHz右腳阻抗加密值
        val z20KhzTrunkEnCode: Long,                // 20KHz軀幹阻抗加密值
        val ppCellMassKg: Float,                    // 身体细胞量(kg)
        val z100KhzLeftArmDeCode: Long,             // 100KHz左手阻抗解密值
        val z100KhzLeftLegDeCode: Long,             // 100KHz左腳阻抗解密值
        val z100KhzRightArmDeCode: Long,            // 100KHz右手阻抗解密值
        val z100KhzRightLegDeCode: Long,            // 100KHz右腳阻抗解密值
        val z100KhzTrunkDeCode: Long,               // 100KHz軀幹阻抗解密值
        val z20KhzLeftArmDeCode: Long,              // 20KHz左手阻抗解密值
        val z20KhzLeftLegDeCode: Long,              // 20KHz左腳阻抗解密值
        val z20KhzRightArmDeCode: Long,             // 20KHz右手阻抗解密值
        val z20KhzRightLegDeCode: Long,             // 20KHz右腳阻抗解密值
        val z20KhzTrunkDeCode: Long,                // 20KHz軀幹阻抗解密值
        val ppBodySkeletalKg: Float,                // 骨骼肌量
        val ppSmi: Float,                           // 骨骼肌质量指数
        val ppWHR: Float,                           // 腰臀比
        val ppRightArmMuscleLevel: Int,             // 右手肌肉标准
        val ppLeftArmMuscleLevel: Int,              // 左手肌肉标准
        val ppTrunkMuscleLevel: Int,                // 躯干肌肉标准
        val ppRightLegMuscleLevel: Int,             // 右脚肌肉标准
        val ppLeftLegMuscleLevel: Int,              // 左脚肌肉标准
        val ppRightArmFatLevel: Int,                // 右手脂肪标准
        val ppLeftArmFatLevel: Int,                 // 左手脂肪标准
        val ppTrunkFatLevel: Int,                   // 躯干脂肪标准
        val ppRightLegFatLevel: Int,                // 右脚脂肪标准
        val ppLeftLegFatLevel: Int,                 // 左脚脂肪标准
        val ppBalanceArmsLevel: Int,                // 上肢肌肉均衡
        val ppBalanceLegsLevel: Int,                // 下肢肌肉均衡
        val ppBalanceArmLegLevel: Int,              // 肌肉-上下均衡度
        val ppBalanceFatArmsLevel: Int,             // 上肢脂肪均衡
        val ppBalanceFatLegsLevel: Int,             // 下肢脂肪均衡
        val ppBalanceFatArmLegLevel: Int,           // 脂肪-上下均衡度
        val dataId: String,                         // 单条数据唯一id
        val channelType: Int,                       // 渠道类型 1 后台接口、2 Android、3 IOS、4 固件
        val batchNumber: String,                    // 批次编号
        val uploadNumber: String,                   // 上传编号
        val dataType: Int,                          // 数据类型 1 计算库原始数据、2 封装数据
        val ppSex: Int,                             // 性别0:女 1:男
        val ppHeightCm: Int,                        // 身高cm
        val ppAge: Int,                             // 年龄
        val accuracy: String,                       // 精度
        val impedance: Long,                        // 阻抗
        val decryptedImpedance: Long,               // 双脚解密阻抗
        val athleteMode: Int,                       // 0普通模式，1运动员模式
        val dataGenerateTime: String,               // 数据生成时间，客户端时间
        val bodyFatDataKey: String,                 // 体脂数据的key,json格式
        val ppWeightKg: Float,                      // 体重数据
        val ppFat: Float,                           // 体脂率(%)
        val ppBMI: Float,                           // 人体质量指数
        val ppBodyfatKg: Float,                     // 脂肪量(kg)
        val ppMusclePercentage: Float,              // 肌肉率(%)
        val ppMuscleKg: Float,                      // 肌肉量(kg)
        val ppVisceralFat: Int,                     // 内脏脂肪等级
        val ppBMR: Int,                             // 基础代谢
        val ppWaterPercentage: Float,               // 体水分率(%)
        val ppBoneKg: Float,                        // 骨量(kg)
        val ppProteinPercentage: Float,             // 蛋白质率(%)
        val ppBodySkeletal: Float,                  // 骨量率/骨骼肌率(%)
        val ppLoseFatWeightKg: Float,               // 去脂体重(%)
        val ppHeartRate: Int,                       // 心率
        val ppBodyScore: Int,                       // 身体得分
        val ppBodyType: String,                     // 身体类型
        val ppBodyAge: Int,                         // 身体年龄
        val ppBodyFatSubCutPercentage: Float,       // 皮下脂肪率
        val ppBodyHealth: String,                   // 健康等级
        val ppFatGrade: String,                     // 肥胖等级
        val ppFatControlKg: Float,                  // 脂肪控制量
        val ppControlWeightKg: Float,               // 体重控制量
        val ppBodyStandardWeightKg: Float,          // 标准体重
        val bodyFatDataValue: String,               // 体脂数据的value
        val ppBodyMuscleControl: Float,             // 肌肉控制量
        val ppProteinKg: Float,                     // 蛋白质量(kg)
        val ppBodyFatSubCutKg: Float,               // 皮下脂肪量(kg)
        val ppDCI: Int,                             // 建议卡路里摄入量 _kcal/day
        val ppMineralKg: Float,                     // 无机盐量(_kg)
        val ppObesity: Float,                       // 肥胖度(%)
        val hasExtendedFields: Boolean,             // 是否存在扩充字段，八电极字段更多
        val errorType: String,                      // 计算库的错误类型
        val deviceMac: String,                      // 设备mac
        val ppSDKVersion: String,                   // sdk版本号
        val ppWaterKg: Float,                       // 水分量
        val organizationId: String,                 // 组织id
        val calculateType: String,                  // 计算类型
        val calculateVersion: String,               // 计算版本
        val ppBodySkeletalKg2: Float,               // 骨骼肌量
        val trunkImpedance1: Long,                  // 躯干阻抗
        val trunkImpedance2: Long,                  // 躯干阻抗
        val trunkImpedance3: Long                   // 躯干阻抗
    ) : Serializable

    /**
     * CSV解析结果类
     */
    data class CSVParseResult(
        val headers: List<String>,      // 表头字段
        val dataRows: List<CSVDataRow>, // 数据行
        val totalRows: Int,             // 总行数（不包括表头）
        val errorRows: List<Pair<Int, String>> // 解析失败的行号和错误信息
    ) : Serializable

    /**
     * 检测文件的字符编码
     */
    private fun detectCharset(inputStream: InputStream): String {
        val buffer = ByteArray(4096)
        val bytesRead = inputStream.read(buffer)

        if (bytesRead <= 0) return "UTF-8"

        val content = buffer.sliceArray(0..bytesRead - 1)

        // 检测BOM
        if (content.size >= 3 &&
            content[0] == 0xEF.toByte() &&
            content[1] == 0xBB.toByte() &&
            content[2] == 0xBF.toByte()
        ) {
            Log.d("CSVFIleUtil", "检测到UTF-8 BOM")
            return "UTF-8"
        }

        if (content.size >= 2 &&
            content[0] == 0xFF.toByte() &&
            content[1] == 0xFE.toByte()
        ) {
            Log.d("CSVFIleUtil", "检测到UTF-16LE BOM")
            return "UTF-16LE"
        }

        if (content.size >= 2 &&
            content[0] == 0xFE.toByte() &&
            content[1] == 0xFF.toByte()
        ) {
            Log.d("CSVFIleUtil", "检测到UTF-16BE BOM")
            return "UTF-16BE"
        }

        // 优先尝试中文编码，因为CSV文件很可能包含中文表头
        val charsets = listOf("GBK", "GB2312", "UTF-8", "ISO-8859-1", "UTF-16")
        var bestCharset = "UTF-8"
        var bestScore = 0

        for (charset in charsets) {
            try {
                val decoded = String(content, Charset.forName(charset))
                val score = CSVFIleUtil.calculateEncodingScore(decoded)

                Log.d("CSVFIleUtil", "编码 $charset 得分: $score")
                Log.d("CSVFIleUtil", "编码 $charset 解码结果前100字符: ${decoded.take(100)}")

                if (score > bestScore) {
                    bestScore = score
                    bestCharset = charset
                }
            } catch (e: Exception) {
                Log.w("CSVFIleUtil", "编码 $charset 解码失败: ${e.message}")
                continue
            }
        }

        Log.d("CSVFIleUtil", "最终选择编码: $bestCharset (得分: $bestScore)")
        return bestCharset
    }

    /**
     * 计算编码得分，得分越高表示编码越合适
     */
    private fun calculateEncodingScore(text: String): Int {
        var score = 0

        // 检查是否包含过多的替换字符（乱码标志）
        val replacementCharCount = text.count { it == '\uFFFD' }
        if (replacementCharCount > text.length * 0.1) {
            return -1000 // 严重扣分
        }

        // 检查是否包含CSV相关的中文字符
        val csvChineseChars = "编号姓名体重身高性别年龄阻抗男女"
        val chineseCharCount = text.count { it in csvChineseChars }
        score += chineseCharCount * 100 // 中文字符加分很高

        // 检查是否包含常见的CSV分隔符
        val separatorCount = text.count { it in ",\t;|" }
        score += separatorCount * 10

        // 检查是否包含数字
        val digitCount = text.count { it.isDigit() }
        score += digitCount * 2

        // 检查是否包含英文字母
        val letterCount = text.count { it.isLetter() && it.code < 128 }
        score += letterCount

        // 检查是否包含常见的单位字符
        val unitChars = "kgcmKhzΩ"
        val unitCharCount = text.count { it in unitChars }
        score += unitCharCount * 50

        return score
    }

    /**
     * 自动检测CSV分隔符
     */
    private fun detectDelimiter(firstLine: String): String {
        val delimiters = listOf(",", "\t", ";", "|")
        var bestDelimiter = ","
        var maxColumns = 0

        for (delimiter in delimiters) {
            val columns = firstLine.split(delimiter)
            if (columns.size > maxColumns) {
                maxColumns = columns.size
                bestDelimiter = delimiter
            }
        }

        // 如果检测到的列数少于预期，给出警告
        if (maxColumns < 8) {
            Log.w("CSVFIleUtil", "警告: 检测到的列数($maxColumns)少于预期(8)，可能分隔符检测有误")
        }

        return bestDelimiter
    }

    /**
     * 安全解析Float类型
     */
    private fun parseFloat(value: String, fieldName: String): Float {
        return try {
            value.toFloat()
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("${fieldName}格式错误: '$value' 不是有效的数字")
        }
    }

    /**
     * 安全解析Int类型
     */
    private fun parseInt(value: String, fieldName: String): Int {
        return try {
            value.toInt()
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("${fieldName}格式错误: '$value' 不是有效的整数")
        }
    }

    /**
     * 安全解析Long类型
     */
    private fun parseLong(value: String, fieldName: String): Long {
        return try {
            value.toLong()
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("${fieldName}格式错误: '$value' 不是有效的长整数")
        }
    }

    /**
     * 增强版CSV解析方法，支持灵活的字段映射
     */
    fun parseCSVFromUriEnhanced(
        context: Context,
        uri: Uri,
        delimiter: String? = null
    ): CSVParseResult {
        val dataRows = mutableListOf<CSVDataRow>()
        val errorRows = mutableListOf<Pair<Int, String>>()
        var headers = emptyList<String>()
        var totalRows = 0
        var detectedDelimiter = delimiter
        var fieldMapping: Map<String, Int>? = null

        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                // 自动检测字符编码
                val detectedCharset = detectCharset(inputStream)
                Log.d("CSVFIleUtil", "检测到字符编码: $detectedCharset")

                // 重新打开输入流，因为检测编码时已经读取了部分内容
                context.contentResolver.openInputStream(uri)?.use { freshInputStream ->
                    BufferedReader(InputStreamReader(freshInputStream, detectedCharset)).use { reader ->
                        var lineNumber = 0
                        var line: String?
                        var firstLine: String? = null

                        while (reader.readLine().also { line = it } != null) {
                            lineNumber++
                            val currentLine = line!!.trim()

                            // 跳过空行
                            if (currentLine.isEmpty()) continue

                            // 保存第一行用于分隔符检测
                            if (firstLine == null) {
                                firstLine = currentLine
                                // 如果没有指定分隔符，则自动检测
                                if (detectedDelimiter == null) {
                                    detectedDelimiter = detectDelimiter(firstLine)
                                    Log.d("CSVFIleUtil", "自动检测到分隔符: '${detectedDelimiter}'")
                                }
                            }

                            val columns = currentLine.split(detectedDelimiter!!)

                            when (lineNumber) {
                                1 -> {
                                    // 第一行作为表头
                                    headers = columns.map { it.trim() }
                                    Log.d("CSVFIleUtil", "解析到表头: $headers (共${headers.size}个字段)")

                                    // 打印原始行内容用于调试
                                    Log.d("CSVFIleUtil", "原始表头行: '$currentLine'")
                                    Log.d("CSVFIleUtil", "原始表头行字节: ${currentLine.toByteArray().contentToString()}")
                                    Log.d("CSVFIleUtil", "使用分隔符: '$detectedDelimiter'")

                                    // 创建字段映射
                                    fieldMapping = createFieldMapping(headers)
                                    Log.d("CSVFIleUtil", "字段映射: $fieldMapping")

                                    // 验证必要字段是否存在
                                    validateFieldMapping(fieldMapping!!)
                                }

                                else -> {
                                    // 数据行
                                    totalRows++
                                    try {
                                        // 创建不可变的局部变量来避免智能转换问题
                                        val currentFieldMapping = fieldMapping
                                        if (currentFieldMapping != null) {
                                            val dataRow = parseDataRowWithMapping(columns, lineNumber, currentFieldMapping)
                                            dataRows.add(dataRow)
                                        } else {
                                            errorRows.add(lineNumber to "字段映射未初始化")
                                        }
                                    } catch (e: Exception) {
                                        errorRows.add(lineNumber to "解析失败: ${e.message}")
                                        Log.w("CSVFIleUtil", "第${lineNumber}行解析失败: ${e.message}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            throw CSVParseException("读取CSV文件失败: ${e.message}", e)
        }

        return CSVParseResult(headers, dataRows, totalRows, errorRows)

    }

    /**
     * 创建字段映射
     */
    fun createFieldMapping(headers: List<String>): Map<String, Int> {
        val fieldMapping = mutableMapOf<String, Int>()
        val impedanceMapping = mutableMapOf<String, Int>() // 存储阻抗字段映射

        // 打印原始表头用于调试
        Log.d("CSVFIleUtil", "原始表头详情:")
        headers.forEachIndexed { index, header ->
            Log.d("CSVFIleUtil", "  [$index]: '$header' (长度: ${header.length}, 字节: ${header.toByteArray().contentToString()})")
        }


        //批次编号	信息id	类型 1 运营后台新增 2 UH导入 3 Excel导入 4 json导入	0普通模式，1运动员模式	性别0:女 1:男	姓名	身高cm	年龄	体重	称重时间戳UTC	躯干阻抗	躯干阻抗	100KHz左手阻抗加密值	100KHz左腳阻抗加密值	100KHz右手阻抗加密值	100KHz右腳阻抗加密值	100KHz躯干阻抗加密值	20KHz左手阻抗加密值	20KHz左腳阻抗加密值	20KHz右手阻抗加密值	20KHz右腳阻抗加密值	20KHz躯干阻抗加密值	躯干阻抗	躯干阻抗

        // 使用固定列号映射新格式的CSV
        // A=0, B=1, C=2, D=3, E=4, F=5, G=6, H=7, I=8, J=9...
        val fixedFieldMapping = mapOf(
            "batchNumber" to 0, // A列：批次编号
            "id" to 1,      // B列：信息id
            "gender" to 4,  // E列：性别0:女 1:男
            "name" to 5,    // F列：姓名
            "height" to 6,  // G列：身高cm
            "age" to 7,     // H列：年龄
            "weight" to 8   // I列：体重
        )

        // 新格式的阻抗字段映射（从J列开始，索引9）
        // J=9(称重时间戳UTC), K=10(impedance50Khz), L=11(impedance100Khz), M=12(100KHz左手阻抗加密值)...
        val fixedImpedanceMapping = mapOf(
            "impedance50Khz" to 10,         // K列：50KHz阻抗
            "impedance100Khz" to 11,        // L列：100KHz阻抗
            "z100KhzLeftArmEnCode" to 12,   // M列：100KHz左手阻抗加密值
            "z100KhzLeftLegEnCode" to 13,   // N列：100KHz左腳阻抗加密值
            "z100KhzRightArmEnCode" to 14,  // O列：100KHz右手阻抗加密值
            "z100KhzRightLegEnCode" to 15,  // P列：100KHz右腳阻抗加密值
            "z100KhzTrunkEnCode" to 16,     // Q列：100KHz躯干阻抗加密值
            "z20KhzLeftArmEnCode" to 17,    // R列：20KHz左手阻抗加密值
            "z20KhzLeftLegEnCode" to 18,    // S列：20KHz左腳阻抗加密值
            "z20KhzRightArmEnCode" to 19,   // T列：20KHz右手阻抗加密值
            "z20KhzRightLegEnCode" to 20,   // U列：20KHz右腳阻抗加密值
            "z20KhzTrunkEnCode" to 21       // V列：20KHz躯干阻抗加密值
        )

        // 直接使用固定列号映射
        for ((fieldKey, columnIndex) in fixedFieldMapping) {
            if (columnIndex < headers.size) {
                fieldMapping[fieldKey] = columnIndex
                Log.d("CSVFIleUtil", "固定列号映射: $fieldKey -> [$columnIndex] '${headers[columnIndex]}'")
            } else {
                Log.w("CSVFIleUtil", "警告: 列 $fieldKey 的索引 $columnIndex 超出表头范围 (${headers.size})")
            }
        }

        // 映射阻抗字段
        for ((fieldKey, columnIndex) in fixedImpedanceMapping) {
            if (columnIndex < headers.size) {
                fieldMapping[fieldKey] = columnIndex
                impedanceMapping[fieldKey] = columnIndex
                Log.d("CSVFIleUtil", "固定阻抗映射: $fieldKey -> [$columnIndex] '${headers[columnIndex]}'")
            } else {
                Log.w("CSVFIleUtil", "警告: 阻抗列 $fieldKey 的索引 $columnIndex 超出表头范围 (${headers.size})")
            }
        }

        // 如果有阻抗映射，添加标记
        if (impedanceMapping.isNotEmpty()) {
            fieldMapping["impedanceMapping"] = -1
        }



        return fieldMapping
    }

    /**
     * 从表头中提取频率信息
     */
    private fun extractFrequencyFromHeader(header: String): String {
        val cleanHeader = header.trim().lowercase()

        // 匹配数字+频率单位的模式
        val frequencyRegex = Regex("(\\d+)\\s*(k?hz|khz|k)", RegexOption.IGNORE_CASE)
        val match = frequencyRegex.find(cleanHeader)

        return if (match != null) {
            val number = match.groupValues[1]
            val unit = match.groupValues[2].lowercase()
            when {
                unit.contains("k") -> "${number}KHz"
                unit == "hz" -> {
                    val freq = number.toIntOrNull() ?: 0
                    if (freq >= 1000) "${freq / 1000}KHz" else "${freq}Hz"
                }

                else -> "${number}KHz"
            }
        } else {
            // 如果没有匹配到标准格式，尝试识别常见的阻抗字段
            when {
                cleanHeader.contains("z100khzleftarm") || cleanHeader.contains("100khzleftarm") -> "z100KhzLeftArmEnCode"
                cleanHeader.contains("z100khzleftleg") || cleanHeader.contains("100khzleftleg") -> "z100KhzLeftLegEnCode"
                cleanHeader.contains("z100khzrightarm") || cleanHeader.contains("100khzrightarm") -> "z100KhzRightArmEnCode"
                cleanHeader.contains("z100khzrightleg") || cleanHeader.contains("100khzrightleg") -> "z100KhzRightLegEnCode"
                cleanHeader.contains("z100khztrunk") || cleanHeader.contains("100khztrunk") -> "z100KhzTrunkEnCode"
                cleanHeader.contains("z20khzleftarm") || cleanHeader.contains("20khzleftarm") -> "z20KhzLeftArmEnCode"
                cleanHeader.contains("z20khzleftleg") || cleanHeader.contains("20khzleftleg") -> "z20KhzLeftLegEnCode"
                cleanHeader.contains("z20khzrightarm") || cleanHeader.contains("20khzrightarm") -> "z20KhzRightArmEnCode"
                cleanHeader.contains("z20khzrightleg") || cleanHeader.contains("20khzrightleg") -> "z20KhzRightLegEnCode"
                cleanHeader.contains("z20khztrunk") || cleanHeader.contains("20khztrunk") -> "z20KhzTrunkEnCode"
                cleanHeader.contains("50") -> "impedance50Khz"
                cleanHeader.contains("100") -> "impedance100Khz"
                else -> ""
            }
        }
    }

    /**
     * 验证字段映射是否包含所有必要字段
     */
    fun validateFieldMapping(fieldMapping: Map<String, Int>) {
        val requiredBasicFields = listOf("id", "name", "weight", "height", "gender", "age")
        val missingBasicFields = requiredBasicFields.filter { !fieldMapping.containsKey(it) }

        // 检查阻抗字段
        val hasImpedanceMapping = fieldMapping.containsKey("impedanceMapping")

        if (missingBasicFields.isNotEmpty()) {
            // 检查是否是完整的位置映射
            val basicFieldCount = requiredBasicFields.size
            val totalMappedFields = fieldMapping.values.filter { it >= 0 }.size

            if (totalMappedFields >= basicFieldCount) {
                Log.w("CSVFIleUtil", "使用位置映射，但缺少基础字段: $missingBasicFields")
                Log.w("CSVFIleUtil", "当前映射: $fieldMapping")
                Log.w("CSVFIleUtil", "将继续处理，但请确认CSV文件列顺序正确")
                return // 允许位置映射继续处理
            }

            val errorMessage = "缺少必要基础字段的映射: $missingBasicFields。" +
                    "当前映射: $fieldMapping。" +
                    "请检查CSV文件的表头格式，确保包含以下字段：编号、姓名、体重、身高、性别、年龄"

            Log.e("CSVFIleUtil", errorMessage)
            throw CSVParseException(errorMessage)
        }

        // 验证阻抗字段
        if (!hasImpedanceMapping) {
            Log.w("CSVFIleUtil", "警告: 未检测到任何阻抗字段，某些计算功能可能无法正常工作")
        }

        Log.d("CSVFIleUtil", "字段映射验证通过: $fieldMapping")
    }

    /**
     * 使用字段映射解析数据行
     */
    private fun parseDataRowWithMapping(columns: List<String>, lineNumber: Int, fieldMapping: Map<String, Int>): CSVDataRow {
        return try {
            // 解析基础字段
            val id = getColumnValue(columns, fieldMapping["id"]!!, "编号").trim()
            val name = getColumnValue(columns, fieldMapping["name"]!!, "姓名").trim()
            val weight = parseFloat(getColumnValue(columns, fieldMapping["weight"]!!, "体重").trim(), "体重")
            val height = parseFloat(getColumnValue(columns, fieldMapping["height"]!!, "身高").trim(), "身高")
            val gender = getColumnValue(columns, fieldMapping["gender"]!!, "性别").trim()
            val age = parseInt(getColumnValue(columns, fieldMapping["age"]!!, "年龄").trim(), "年龄")
            val batchNumber = getColumnValue(columns, fieldMapping["batchNumber"]!!, "批次编号").trim()

            // 解析阻抗字段
            val impedanceValues = mutableMapOf<String, Long>()

            // 定义需要解析的阻抗字段列表
            val impedanceFields = listOf(
                "impedance50Khz",
                "impedance100Khz",
                "z100KhzLeftArmEnCode",
                "z100KhzLeftLegEnCode",
                "z100KhzRightArmEnCode",
                "z100KhzRightLegEnCode",
                "z100KhzTrunkEnCode",
                "z20KhzLeftArmEnCode",
                "z20KhzLeftLegEnCode",
                "z20KhzRightArmEnCode",
                "z20KhzRightLegEnCode",
                "z20KhzTrunkEnCode"
            )

            // 直接使用fieldMapping中已经映射好的阻抗字段
            for (fieldName in impedanceFields) {
                fieldMapping[fieldName]?.let { index ->
                    try {
                        val value = parseLong(getColumnValue(columns, index, fieldName).trim(), fieldName)
                        impedanceValues[fieldName] = value
                    } catch (e: Exception) {
                        Log.w("CSVFIleUtil", "第${lineNumber}行${fieldName}解析失败: ${e.message}，将设为0")
                        impedanceValues[fieldName] = 0L
                    }
                }
            }

            CSVDataRow(
                id = id,
                name = name,
                weight = weight,
                height = height,
                gender = gender,
                age = age,
                batchNumber = batchNumber,
                impedanceValues = impedanceValues
            )
        } catch (e: Exception) {
            throw IllegalArgumentException("第${lineNumber}行数据解析失败: ${e.message}")
        }
    }

    /**
     * 安全获取列值
     */
    private fun getColumnValue(columns: List<String>, index: Int, fieldName: String): String {
        if (index >= columns.size) {
            throw IllegalArgumentException("${fieldName}字段索引($index)超出列数范围(${columns.size})")
        }
        return columns[index]
    }

    /**
     * 导出计算结果到CSV文件
     * @param context 上下文
     * @param dataRows 原始数据行列表
     * @param dataResults data计算结果列表
     * @param fileName 文件名（不包含扩展名）
     * @return 导出文件的Uri，失败返回null
     */
    fun exportCalculationResultsToCSV(
        context: Context,
        dataRows: List<CSVDataRow>,
        data1Results: List<PPBodyFatModel?>,
        fileName: String = "calculation_results"
    ): Uri? {
        return try {
            val csvContent = StringBuilder()

            // 收集所有阻抗字段
            val allImpedanceKeys = dataRows.flatMap { it.impedanceValues.keys }.distinct().sorted()

            // 添加表头
            csvContent.append(createCSVHeader(allImpedanceKeys)).append("\n")

            // 为每个原始数据行生成一行输出，包含原始数据和三组计算结果
            for (i in dataRows.indices) {
                val dataRow = dataRows[i]
                val data1 = if (i < data1Results.size) data1Results[i] else null

                // 创建包含三组计算结果的导出行
                val exportRow = createCSVExportRowCombined(dataRow, data1)
                csvContent.append(createCSVDataLine(exportRow, allImpedanceKeys)).append("\n")
            }

            // 保存文件并返回Uri
            saveCSVToFile(context, csvContent.toString(), fileName)
        } catch (e: Exception) {
            Log.e("CSVFIleUtil", "导出CSV失败: ${e.message}", e)
            null
        }
    }

    /**
     * 创建CSV表头
     */
    private fun createCSVHeader(impedanceKeys: List<String> = listOf("impedance50Khz", "impedance100Khz")): String {
        val headers = mutableListOf<String>()

        // 新的字段列表
        headers.addAll(
            listOf(
                "全身体组成:细胞外水量(kg)",
                "全身体组成:细胞内水量(kg)",
                "左手脂肪量(%) 分辨率0.1",
                "左脚脂肪量(%) 分辨率0.1",
                "右手脂肪量(%) 分辨率0.1",
                "右脚脂肪量(%) 分辨率0.1",
                "躯干脂肪量(%) 分辨率0.1",
                "左手脂肪率(%) 分辨率0.1",
                "左脚脂肪率(%) 分辨率0.1",
                "右手脂肪率(%) 分辨率0.1",
                "右脚脂肪率(%) 分辨率0.1",
                "躯干脂肪率(%) 分辨率0.1",
                "左手肌肉量(kg) 分辨率0.1 范围0.0-200kg",
                "左手肌肉率 分辨率0.1",
                "左脚肌肉量(kg) 分辨率0.1 范围0.0-200kg",
                "左脚肌肉率 分辨率0.1",
                "右手肌肉量(kg) 分辨率0.1 范围0.0-200kg",
                "右手肌肉率 分辨率0.1",
                "右脚肌肉量(kg) 分辨率0.1 范围0.0-200kg",
                "右脚肌肉率 分辨率0.1",
                "躯干肌肉量(kg) 分辨率0.1 范围0.0-200kg",
                "躯干肌肉率 分辨率0.1",
                "100KHz左手阻抗加密值",
                "100KHz左腳阻抗加密值",
                "100KHz右手阻抗加密值",
                "100KHz右腳阻抗加密值",
                "100KHz軀幹阻抗加密值",
                "20KHz左手阻抗加密值",
                "20KHz左腳阻抗加密值",
                "20KHz右手阻抗加密值",
                "20KHz右腳阻抗加密值",
                "20KHz軀幹阻抗加密值",
                "身体细胞量(kg)",
                "100KHz左手阻抗解密值",
                "100KHz左腳阻抗解密值",
                "100KHz右手阻抗解密值",
                "100KHz右腳阻抗解密值",
                "100KHz軀幹阻抗解密值",
                "20KHz左手阻抗解密值",
                "20KHz左腳阻抗解密值",
                "20KHz右手阻抗解密值",
                "20KHz右腳阻抗解密值",
                "20KHz軀幹阻抗解密值",
                "骨骼肌量",
                "骨骼肌质量指数",
                "腰臀比",
                "右手肌肉标准",
                "左手肌肉标准",
                "躯干肌肉标准",
                "右脚肌肉标准",
                "左脚肌肉标准",
                "右手脂肪标准",
                "左手脂肪标准",
                "躯干脂肪标准",
                "右脚脂肪标准",
                "左脚脂肪标准",
                "上肢肌肉均衡",
                "下肢肌肉均衡",
                "肌肉-上下均衡度",
                "上肢脂肪均衡",
                "下肢脂肪均衡",
                "脂肪-上下均衡度",
                "单条数据唯一id",
                "渠道类型 1 后台接口、2 Android、3 IOS、4 固件",
                "批次编号",
                "上传编号",
                "数据类型 1 计算库原始数据、2 封装数据",
                "性别0:女 1:男",
                "身高cm",
                "年龄",
                "精度",
                "阻抗",
                "双脚解密阻抗",
                "0普通模式，1运动员模式",
                "数据生成时间，客户端时间",
                "体脂数据的key json格式",
                "体重数据",
                "体脂率(%)",
                "人体质量指数",
                "脂肪量(kg)",
                "肌肉率(%)",
                "肌肉量(kg)",
                "内脏脂肪等级",
                "基础代谢",
                "体水分率(%)",
                "骨量(kg)",
                "蛋白质率(%)",
                "骨量率/骨骼肌率(%)",
                "去脂体重(%)",
                "心率",
                "身体得分",
                "身体类型",
                "身体年龄",
                "皮下脂肪率",
                "健康等级",
                "肥胖等级",
                "脂肪控制量",
                "体重控制量",
                "标准体重",
                "体脂数据的value",
                "肌肉控制量",
                "蛋白质量(kg)",
                "皮下脂肪量(kg)",
                "建议卡路里摄入量 _kcal/day",
                "无机盐量(_kg)",
                "肥胖度(%)",
                "是否存在扩充字段，八电极字段更多",
                "计算库的错误类型",
                "设备mac",
                "sdk版本号",
                "水分量",
                "组织id",
                "计算类型",
                "计算版本",
                "骨骼肌量",
                "躯干阻抗",
                "躯干阻抗",
                "躯干阻抗"
            )
        )

        return headers.joinToString(",")
    }

    /**
     * 创建CSV数据行
     */
    private fun createCSVDataLine(row: CSVExportRow, impedanceKeys: List<String> = listOf("impedance50Khz", "impedance100Khz")): String {
        val values = mutableListOf<Any>()

        // 按照表头字段顺序输出数据，确保完全对应
        values.addAll(
            listOf(
                row.ppWaterECWKg,                    // 全身体组成:细胞外水量(kg)
                row.ppWaterICWKg,                    // 全身体组成:细胞内水量(kg)
                row.ppBodyFatKgLeftArm,              // 左手脂肪量(%), 分辨率0.1
                row.ppBodyFatKgLeftLeg,              // 左脚脂肪量(%), 分辨率0.1
                row.ppBodyFatKgRightArm,             // 右手脂肪量(%), 分辨率0.1
                row.ppBodyFatKgRightLeg,             // 右脚脂肪量(%), 分辨率0.1
                row.ppBodyFatKgTrunk,                // 躯干脂肪量(%), 分辨率0.1
                row.ppBodyFatRateLeftArm,            // 左手脂肪率(%), 分辨率0.1
                row.ppBodyFatRateLeftLeg,            // 左脚脂肪率(%), 分辨率0.1
                row.ppBodyFatRateRightArm,           // 右手脂肪率(%), 分辨率0.1
                row.ppBodyFatRateRightLeg,           // 右脚脂肪率(%), 分辨率0.1
                row.ppBodyFatRateTrunk,              // 躯干脂肪率(%), 分辨率0.1
                row.ppMuscleKgLeftArm,               // 左手肌肉量(kg), 分辨率0.1
                row.ppMuscleRateLeftArm,             // 左手肌肉率, 分辨率0.1
                row.ppMuscleKgLeftLeg,               // 左脚肌肉量(kg), 分辨率0.1
                row.ppMuscleRateLeftLeg,             // 左脚肌肉率, 分辨率0.1
                row.ppMuscleKgRightArm,              // 右手肌肉量(kg), 分辨率0.1
                row.ppMuscleRateRightArm,            // 右手肌肉率, 分辨率0.1
                row.ppMuscleKgRightLeg,              // 右脚肌肉量(kg), 分辨率0.1
                row.ppMuscleRateRightLeg,            // 右脚肌肉率, 分辨率0.1
                row.ppMuscleKgTrunk,                 // 躯干肌肉量(kg), 分辨率0.1
                row.ppMuscleRateTrunk,               // 躯干肌肉率, 分辨率0.1
                // 注意：这里是第23列，应该对应"100KHz左手阻抗加密值"
                row.z100KhzLeftArmEnCode,            // 100KHz左手阻抗加密值
                row.z100KhzLeftLegEnCode,            // 100KHz左腳阻抗加密值
                row.z100KhzRightArmEnCode,           // 100KHz右手阻抗加密值
                row.z100KhzRightLegEnCode,           // 100KHz右腳阻抗加密值
                row.z100KhzTrunkEnCode,              // 100KHz軀幹阻抗加密值
                row.z20KhzLeftArmEnCode,             // 20KHz左手阻抗加密值
                row.z20KhzLeftLegEnCode,             // 20KHz左腳阻抗加密值
                row.z20KhzRightArmEnCode,            // 20KHz右手阻抗加密值
                row.z20KhzRightLegEnCode,            // 20KHz右腳阻抗加密值
                row.z20KhzTrunkEnCode,               // 20KHz軀幹阻抗加密值
                row.ppCellMassKg,                    // 身体细胞量(kg)
                row.z100KhzLeftArmDeCode,            // 100KHz左手阻抗解密值
                row.z100KhzLeftLegDeCode,            // 100KHz左腳阻抗解密值
                row.z100KhzRightArmDeCode,           // 100KHz右手阻抗解密值
                row.z100KhzRightLegDeCode,           // 100KHz右腳阻抗解密值
                row.z100KhzTrunkDeCode,              // 100KHz軀幹阻抗解密值
                row.z20KhzLeftArmDeCode,             // 20KHz左手阻抗解密值
                row.z20KhzLeftLegDeCode,             // 20KHz左腳阻抗解密值
                row.z20KhzRightArmDeCode,            // 20KHz右手阻抗解密值
                row.z20KhzRightLegDeCode,            // 20KHz右腳阻抗解密值
                row.z20KhzTrunkDeCode,               // 20KHz軀幹阻抗解密值
                row.ppBodySkeletalKg,                // 骨骼肌量
                row.ppSmi,                           // 骨骼肌质量指数
                row.ppWHR,                           // 腰臀比
                row.ppRightArmMuscleLevel,           // 右手肌肉标准
                row.ppLeftArmMuscleLevel,            // 左手肌肉标准
                row.ppTrunkMuscleLevel,              // 躯干肌肉标准
                row.ppRightLegMuscleLevel,           // 右脚肌肉标准
                row.ppLeftLegMuscleLevel,            // 左脚肌肉标准
                row.ppRightArmFatLevel,              // 右手脂肪标准
                row.ppLeftArmFatLevel,               // 左手脂肪标准
                row.ppTrunkFatLevel,                 // 躯干脂肪标准
                row.ppRightLegFatLevel,              // 右脚脂肪标准
                row.ppLeftLegFatLevel,               // 左脚脂肪标准
                row.ppBalanceArmsLevel,              // 上肢肌肉均衡
                row.ppBalanceLegsLevel,              // 下肢肌肉均衡
                row.ppBalanceArmLegLevel,            // 肌肉-上下均衡度
                row.ppBalanceFatArmsLevel,           // 上肢脂肪均衡
                row.ppBalanceFatLegsLevel,           // 下肢脂肪均衡
                row.ppBalanceFatArmLegLevel,         // 脂肪-上下均衡度
                row.dataId,                          // 单条数据唯一id
                row.channelType,                     // 渠道类型 1 后台接口、2 Android、3 IOS、4 固件
                row.batchNumber,                     // 批次编号
                row.uploadNumber,                    // 上传编号
                row.dataType,                        // 数据类型 1 计算库原始数据、2 封装数据
                row.ppSex,                           // 性别0:女 1:男
                row.ppHeightCm,                      // 身高cm
                row.ppAge,                           // 年龄
                row.accuracy,                        // 精度
                row.impedance,                       // 阻抗
                row.decryptedImpedance,              // 双脚解密阻抗
                row.athleteMode,                     // 0普通模式，1运动员模式
                row.dataGenerateTime,                // 数据生成时间，客户端时间
                row.bodyFatDataKey,                  // 体脂数据的key,json格式
                row.ppWeightKg,                      // 体重数据
                row.ppFat,                           // 体脂率(%)
                row.ppBMI,                           // 人体质量指数
                row.ppBodyfatKg,                     // 脂肪量(kg)
                row.ppMusclePercentage,              // 肌肉率(%)
                row.ppMuscleKg,                      // 肌肉量(kg)
                row.ppVisceralFat,                   // 内脏脂肪等级
                row.ppBMR,                           // 基础代谢
                row.ppWaterPercentage,               // 体水分率(%)
                row.ppBoneKg,                        // 骨量(kg)
                row.ppProteinPercentage,             // 蛋白质率(%)
                row.ppBodySkeletal,                  // 骨量率/骨骼肌率(%)
                row.ppLoseFatWeightKg,               // 去脂体重(%)
                row.ppHeartRate,                     // 心率
                row.ppBodyScore,                     // 身体得分
                row.ppBodyType,                      // 身体类型
                row.ppBodyAge,                       // 身体年龄
                row.ppBodyFatSubCutPercentage,       // 皮下脂肪率
                row.ppBodyHealth,                    // 健康等级
                row.ppFatGrade,                      // 肥胖等级
                row.ppFatControlKg,                  // 脂肪控制量
                row.ppControlWeightKg,               // 体重控制量
                row.ppBodyStandardWeightKg,          // 标准体重
                row.bodyFatDataValue,                // 体脂数据的value
                row.ppBodyMuscleControl,             // 肌肉控制量
                row.ppProteinKg,                     // 蛋白质量(kg)
                row.ppBodyFatSubCutKg,               // 皮下脂肪量(kg)
                row.ppDCI,                           // 建议卡路里摄入量 _kcal/day
                row.ppMineralKg,                     // 无机盐量(_kg)
                row.ppObesity,                       // 肥胖度(%)
                row.hasExtendedFields,               // 是否存在扩充字段，八电极字段更多
                row.errorType,                       // 计算库的错误类型
                row.deviceMac,                       // 设备mac
                row.ppSDKVersion,                    // sdk版本号
                row.ppWaterKg,                       // 水分量
                row.organizationId,                  // 组织id
                row.calculateType,                   // 计算类型
                row.calculateVersion,                // 计算版本
                row.ppBodySkeletalKg2,               // 骨骼肌量
                row.trunkImpedance1,                 // 躯干阻抗
                row.trunkImpedance2,                 // 躯干阻抗
                row.trunkImpedance3                  // 躯干阻抗
            )
        )

        return values.joinToString(",") { value ->
            // 处理包含逗号的字段，用双引号包围
            if (value.toString().contains(",")) {
                "\"$value\""
            } else {
                value.toString()
            }
        }
    }

    /**
     * 保存CSV内容到文件
     * 保存到App内部缓存目录下的CSV子目录
     */
    private fun saveCSVToFile(context: Context, csvContent: String, fileName: String): Uri? {
        return try {
            // 创建App缓存目录下的CSV子目录
            val csvDir = File(context.cacheDir, "CSV")
            if (!csvDir.exists()) {
                csvDir.mkdirs()
                Log.d("CSVFIleUtil", "创建CSV目录: ${csvDir.absolutePath}")
            }

            // 创建CSV文件
            val csvFile = File(csvDir, "$fileName.csv")

            // 写入文件内容
            csvFile.outputStream().use { outputStream ->
                // 添加BOM以确保Excel正确识别UTF-8编码
                outputStream.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                outputStream.write(csvContent.toByteArray(Charsets.UTF_8))
            }

            Log.d("CSVFIleUtil", "CSV文件导出成功: ${csvFile.absolutePath}")

            // 返回文件的Uri
            Uri.fromFile(csvFile)
        } catch (e: Exception) {
            Log.e("CSVFIleUtil", "保存CSV文件失败: ${e.message}", e)
            null
        }
    }

    /**
     * 从PPBodyFatModel创建包含计算结果的CSVExportRow的辅助方法
     */
    fun createCSVExportRowCombined(
        dataRow: CSVDataRow,
        data1Result: PPBodyFatModel?
    ): CSVExportRow {
        return CSVExportRow(
            // 原始数据
            id = dataRow.id,
            name = dataRow.name,
            weight = dataRow.weight,
            height = dataRow.height,
            gender = dataRow.gender,
            age = dataRow.age,
            impedanceValues = dataRow.impedanceValues,

            // 从PPBodyFatModel获取所有字段数据
            ppWaterECWKg = data1Result?.ppWaterECWKg ?: 0f,
            ppWaterICWKg = data1Result?.ppWaterICWKg ?: 0f,
            ppBodyFatKgLeftArm = data1Result?.ppBodyFatKgLeftArm ?: 0f,
            ppBodyFatKgLeftLeg = data1Result?.ppBodyFatKgLeftLeg ?: 0f,
            ppBodyFatKgRightArm = data1Result?.ppBodyFatKgRightArm ?: 0f,
            ppBodyFatKgRightLeg = data1Result?.ppBodyFatKgRightLeg ?: 0f,
            ppBodyFatKgTrunk = data1Result?.ppBodyFatKgTrunk ?: 0f,
            ppBodyFatRateLeftArm = data1Result?.ppBodyFatRateLeftArm ?: 0f,
            ppBodyFatRateLeftLeg = data1Result?.ppBodyFatRateLeftLeg ?: 0f,
            ppBodyFatRateRightArm = data1Result?.ppBodyFatRateRightArm ?: 0f,
            ppBodyFatRateRightLeg = data1Result?.ppBodyFatRateRightLeg ?: 0f,
            ppBodyFatRateTrunk = data1Result?.ppBodyFatRateTrunk ?: 0f,
            ppMuscleKgLeftArm = data1Result?.ppMuscleKgLeftArm ?: 0f,
            ppMuscleRateLeftArm = data1Result?.ppMuscleRateLeftArm ?: 0f,
            ppMuscleKgLeftLeg = data1Result?.ppMuscleKgLeftLeg ?: 0f,
            ppMuscleRateLeftLeg = data1Result?.ppMuscleRateLeftLeg ?: 0f,
            ppMuscleKgRightArm = data1Result?.ppMuscleKgRightArm ?: 0f,
            ppMuscleRateRightArm = data1Result?.ppMuscleRateRightArm ?: 0f,
            ppMuscleKgRightLeg = data1Result?.ppMuscleKgRightLeg ?: 0f,
            ppMuscleRateRightLeg = data1Result?.ppMuscleRateRightLeg ?: 0f,
            ppMuscleKgTrunk = data1Result?.ppMuscleKgTrunk ?: 0f,
            ppMuscleRateTrunk = data1Result?.ppMuscleRateTrunk ?: 0f,
            z100KhzLeftArmEnCode = dataRow.z100KhzLeftArmEnCode,
            z100KhzLeftLegEnCode = dataRow.z100KhzLeftLegEnCode,
            z100KhzRightArmEnCode = dataRow.z100KhzRightArmEnCode,
            z100KhzRightLegEnCode = dataRow.z100KhzRightLegEnCode,
            z100KhzTrunkEnCode = dataRow.z100KhzTrunkEnCode,
            z20KhzLeftArmEnCode = dataRow.z20KhzLeftArmEnCode,
            z20KhzLeftLegEnCode = dataRow.z20KhzLeftLegEnCode,
            z20KhzRightArmEnCode = dataRow.z20KhzRightArmEnCode,
            z20KhzRightLegEnCode = dataRow.z20KhzRightLegEnCode,
            z20KhzTrunkEnCode = dataRow.z20KhzTrunkEnCode,
            ppCellMassKg = data1Result?.ppCellMassKg ?: 0f,
            z100KhzLeftArmDeCode = data1Result?.ppBodyBaseModel?.z100KhzLeftArmDeCode?.toLong() ?: 0L,
            z100KhzLeftLegDeCode = data1Result?.ppBodyBaseModel?.z100KhzLeftLegDeCode?.toLong() ?: 0L,
            z100KhzRightArmDeCode = data1Result?.ppBodyBaseModel?.z100KhzRightArmDeCode?.toLong() ?: 0L,
            z100KhzRightLegDeCode = data1Result?.ppBodyBaseModel?.z100KhzRightLegDeCode?.toLong() ?: 0L,
            z100KhzTrunkDeCode = data1Result?.ppBodyBaseModel?.z100KhzTrunkDeCode?.toLong() ?: 0L,
            z20KhzLeftArmDeCode = data1Result?.ppBodyBaseModel?.z20KhzLeftArmDeCode?.toLong() ?: 0L,
            z20KhzLeftLegDeCode = data1Result?.ppBodyBaseModel?.z20KhzLeftLegDeCode?.toLong() ?: 0L,
            z20KhzRightArmDeCode = data1Result?.ppBodyBaseModel?.z20KhzRightArmDeCode?.toLong() ?: 0L,
            z20KhzRightLegDeCode = data1Result?.ppBodyBaseModel?.z20KhzRightLegDeCode?.toLong() ?: 0L,
            z20KhzTrunkDeCode = data1Result?.ppBodyBaseModel?.z20KhzTrunkDeCode?.toLong() ?: 0L,
            ppBodySkeletalKg = data1Result?.ppBodySkeletalKg ?: 0f,
            ppSmi = data1Result?.ppSmi ?: 0f,
            ppWHR = data1Result?.ppWHR ?: 0f,
            ppRightArmMuscleLevel = data1Result?.ppRightArmMuscleLevel ?: 0,
            ppLeftArmMuscleLevel = data1Result?.ppLeftArmMuscleLevel ?: 0,
            ppTrunkMuscleLevel = data1Result?.ppTrunkMuscleLevel ?: 0,
            ppRightLegMuscleLevel = data1Result?.ppRightLegMuscleLevel ?: 0,
            ppLeftLegMuscleLevel = data1Result?.ppLeftLegMuscleLevel ?: 0,
            ppRightArmFatLevel = data1Result?.ppRightArmFatLevel ?: 0,
            ppLeftArmFatLevel = data1Result?.ppLeftArmFatLevel ?: 0,
            ppTrunkFatLevel = data1Result?.ppTrunkFatLevel ?: 0,
            ppRightLegFatLevel = data1Result?.ppRightLegFatLevel ?: 0,
            ppLeftLegFatLevel = data1Result?.ppLeftLegFatLevel ?: 0,
            ppBalanceArmsLevel = data1Result?.ppBalanceArmsLevel ?: 0,
            ppBalanceLegsLevel = data1Result?.ppBalanceLegsLevel ?: 0,
            ppBalanceArmLegLevel = data1Result?.ppBalanceArmLegLevel ?: 0,
            ppBalanceFatArmsLevel = data1Result?.ppBalanceFatArmsLevel ?: 0,
            ppBalanceFatLegsLevel = data1Result?.ppBalanceFatLegsLevel ?: 0,
            ppBalanceFatArmLegLevel = data1Result?.ppBalanceFatArmLegLevel ?: 0,
            dataId = dataRow.id, // 使用计算前的原始数据中的id字段
            channelType = 2, // Android
            batchNumber = dataRow.batchNumber, // 使用源数据中的批次编号
            uploadNumber = "UPLOAD_${System.currentTimeMillis()}",
            dataType = 1, // 计算库原始数据
            ppSex = data1Result?.ppSex?.ordinal ?: 0,
            ppHeightCm = data1Result?.ppHeightCm ?: 100,
            ppAge = data1Result?.ppAge ?: 0,
            accuracy = "0.1", // 默认精度
            impedance = dataRow.impedance50Khz, // 使用50KHz阻抗
            decryptedImpedance = dataRow.impedance50Khz, // 使用50KHz阻抗作为解密阻抗
            athleteMode = 0, // 默认普通模式
            dataGenerateTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()), // 生成当前时间戳
            bodyFatDataKey = "", // 设置为空字符串
            ppWeightKg = data1Result?.ppWeightKg ?: 0f,
            ppFat = data1Result?.ppFat ?: 0f,
            ppBMI = data1Result?.ppBMI ?: 0f,
            ppBodyfatKg = data1Result?.ppBodyfatKg ?: 0f,
            ppMusclePercentage = data1Result?.ppMusclePercentage ?: 0f,
            ppMuscleKg = data1Result?.ppMuscleKg ?: 0f,
            ppVisceralFat = data1Result?.ppVisceralFat ?: 0,
            ppBMR = data1Result?.ppBMR ?: 0,
            ppWaterPercentage = data1Result?.ppWaterPercentage ?: 0f,
            ppBoneKg = data1Result?.ppBoneKg ?: 0f,
            ppProteinPercentage = data1Result?.ppProteinPercentage ?: 0f,
            ppBodySkeletal = data1Result?.ppBodySkeletal ?: 0f,
            ppLoseFatWeightKg = data1Result?.ppLoseFatWeightKg ?: 0f,
            ppHeartRate = data1Result?.ppHeartRate ?: 0,
            ppBodyScore = data1Result?.ppBodyScore ?: 0,
            ppBodyType = data1Result?.ppBodyType?.toString() ?: "",
            ppBodyAge = data1Result?.ppBodyAge ?: 0,
            ppBodyFatSubCutPercentage = data1Result?.ppBodyFatSubCutPercentage ?: 0f,
            ppBodyHealth = data1Result?.ppBodyHealth?.toString() ?: "",
            ppFatGrade = data1Result?.ppFatGrade?.toString() ?: "",
            ppFatControlKg = data1Result?.ppFatControlKg ?: 0f,
            ppControlWeightKg = data1Result?.ppControlWeightKg ?: 0f,
            ppBodyStandardWeightKg = data1Result?.ppBodyStandardWeightKg ?: 0.0f,
            bodyFatDataValue = "{\"firewareReversion\"}", // 生成JSON格式的value
            ppBodyMuscleControl = data1Result?.ppBodyMuscleControl ?: 0f,
            ppProteinKg = data1Result?.ppProteinKg ?: 0f,
            ppBodyFatSubCutKg = data1Result?.ppBodyFatSubCutKg ?: 0f,
            ppDCI = data1Result?.ppDCI ?: 0,
            ppMineralKg = data1Result?.ppMineralKg ?: 0f,
            ppObesity = data1Result?.ppObesity ?: 0f,
            hasExtendedFields = true, // 八电极字段更多
            errorType = data1Result?.errorType?.toString() ?: "",
            deviceMac = "00:00:00:00:00:00", // 默认设备MAC地址
            ppSDKVersion = data1Result?.ppSDKVersion ?: "", // 默认SDK版本
            ppWaterKg = data1Result?.ppWaterKg ?: 0f,
            organizationId = "DEFAULT_ORG", // 默认组织ID
            calculateType = data1Result?.ppBodyBaseModel?.deviceModel?.deviceCalcuteType?.getType().toString() ?: "", // 默认计算类型
            calculateVersion = data1Result?.ppSDKVersion ?: "", // 默认计算版本
            ppBodySkeletalKg2 = data1Result?.ppBodySkeletalKg ?: 0f, // 重复字段
            trunkImpedance1 = dataRow.z100KhzTrunkEnCode, // 使用躯干加密阻抗
            trunkImpedance2 = dataRow.z20KhzTrunkEnCode,
            trunkImpedance3 = dataRow.impedance100Khz
        )
    }
}

/**
 * CSV解析异常类
 */
class CSVParseException(message: String, cause: Throwable? = null) : Exception(message, cause)

