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
     * CSV导出行数据类 - 每行包含原始数据和三组计算结果(data1, data2, data3)
     */
    data class CSVExportRow(
        // 原始数据
        val id: String,
        val name: String,
        val weight: Float,
        val height: Float,
        val gender: String,
        val age: Int,
        val impedanceValues: Map<String, Long>, // 阻抗值映射

        // 兼容性字段
        val impedance50Khz: Long = impedanceValues["impedance50Khz"] ?: impedanceValues["50"] ?: 0L,
        val impedance100Khz: Long = impedanceValues["impedance100Khz"] ?: impedanceValues["100"] ?: 0L,

        // Data1计算结果 (Product0-普通人)
        val data1_errorType: String,
        val data1_ppBMI: Float,
        val data1_ppFat: Float,
        val data1_ppBodyfatKg: Float,
        val data1_ppMusclePercentage: Float,
        val data1_ppMuscleKg: Float,
        val data1_ppBodySkeletal: Float,
        val data1_ppBodySkeletalKg: Float,
        val data1_ppWaterPercentage: Float,
        val data1_ppWaterKg: Float,
        val data1_ppProteinPercentage: Float,
        val data1_ppProteinKg: Float,
        val data1_ppLoseFatWeightKg: Float,
        val data1_ppBodyFatSubCutPercentage: Float,
        val data1_ppBodyFatSubCutKg: Float,

        val data1_ppBMR: Int,
        val data1_ppVisceralFat: Int,
        val data1_ppBoneKg: Float,
        val data1_ppBodyMuscleControl: Float,
        val data1_ppFatControlKg: Float,
        val data1_ppBodyStandardWeightKg: Float,
        val data1_ppIdealWeightKg: Float,
        val data1_ppControlWeightKg: Float,
        val data1_ppBodyType: String,
        val data1_ppFatGrade: String,
        val data1_ppBodyHealth: String,
        val data1_ppBodyAge: Int,
        val data1_ppBodyScore: Int,

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

        // 基础数据字段
        headers.addAll(listOf("编号", "姓名", "体重(kg)", "身高(cm)", "性别", "年龄"))

        // 动态阻抗字段
        impedanceKeys.forEach { key ->
            headers.add("${key}阻抗")
        }

        // 计算结果字段
        headers.addAll(
            listOf(
                // 计算结果字段 - 按字段类型分组，每组包含1、2、3
                "体脂率(%)",
                "BMI",
                "脂肪量(kg)",
                "肌肉率(%)",
                "肌肉量(kg)",
                "骨骼肌率(%)",
                "骨骼肌量(kg)",
                "水分率(%)",
                "水分量(kg)",
                "蛋白质率(%)",
                "蛋白质量(kg)",
                "去脂体重(kg)",
                "皮下脂肪率(%)",
                "皮下脂肪量(kg)",

                "基础代谢",
                "内脏脂肪等级",
                "骨量(kg)",
                "肌肉控制量(kg)",
                "脂肪控制量(kg)",
                "标准体重(kg)",
                "理想体重(kg)",
                "控制体重(kg)",
                "身体类型",
                "肥胖等级",
                "健康评估",
                "身体年龄",
                "身体得分",
                "错误类型"
            )
        )

        return headers.joinToString(",")
    }

    /**
     * 创建CSV数据行
     */
    private fun createCSVDataLine(row: CSVExportRow, impedanceKeys: List<String> = listOf("impedance50Khz", "impedance100Khz")): String {
        val values = mutableListOf<Any>()

        // 基础数据
        values.addAll(listOf(row.id, row.name, row.weight, row.height, row.gender, row.age))

        // 动态阻抗字段
        impedanceKeys.forEach { key ->
            values.add(row.impedanceValues[key] ?: 0L)
        }

        // 计算结果数据
        values.addAll(
            listOf(
                // 计算结果 - 按字段类型分组，每组包含1、2、3
                row.data1_ppFat,
                row.data1_ppBMI,
                row.data1_ppBodyfatKg,
                row.data1_ppMusclePercentage,
                row.data1_ppMuscleKg,
                row.data1_ppBodySkeletal,
                row.data1_ppBodySkeletalKg,
                row.data1_ppWaterPercentage,
                row.data1_ppWaterKg,
                row.data1_ppProteinPercentage,
                row.data1_ppProteinKg,
                row.data1_ppLoseFatWeightKg,
                row.data1_ppBodyFatSubCutPercentage,
                row.data1_ppBodyFatSubCutKg,
                row.data1_ppBMR,
                row.data1_ppVisceralFat,
                row.data1_ppBoneKg,
                row.data1_ppBodyMuscleControl,
                row.data1_ppFatControlKg,
                row.data1_ppBodyStandardWeightKg,
                row.data1_ppIdealWeightKg,
                row.data1_ppControlWeightKg,
                row.data1_ppBodyType,
                row.data1_ppFatGrade,
                row.data1_ppBodyHealth,
                row.data1_ppBodyAge,
                row.data1_ppBodyScore,
                row.data1_errorType
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

            // Data1计算结果 (Product0-普通人)
            data1_errorType = data1Result?.errorType?.toString() ?: "无数据",
            data1_ppBMI = data1Result?.ppBMI ?: 0f,
            data1_ppFat = data1Result?.ppFat ?: 0f,
            data1_ppBodyfatKg = data1Result?.ppBodyfatKg ?: 0f,
            data1_ppMusclePercentage = data1Result?.ppMusclePercentage ?: 0f,
            data1_ppMuscleKg = data1Result?.ppMuscleKg ?: 0f,
            data1_ppBodySkeletal = data1Result?.ppBodySkeletal ?: 0f,
            data1_ppBodySkeletalKg = data1Result?.ppBodySkeletalKg ?: 0f,
            data1_ppWaterPercentage = data1Result?.ppWaterPercentage ?: 0f,
            data1_ppWaterKg = data1Result?.ppWaterKg ?: 0f,
            data1_ppProteinPercentage = data1Result?.ppProteinPercentage ?: 0f,
            data1_ppProteinKg = data1Result?.ppProteinKg ?: 0f,
            data1_ppLoseFatWeightKg = data1Result?.ppLoseFatWeightKg ?: 0f,
            data1_ppBodyFatSubCutPercentage = data1Result?.ppBodyFatSubCutPercentage ?: 0f,
            data1_ppBodyFatSubCutKg = data1Result?.ppBodyFatSubCutKg ?: 0f,
            data1_ppBMR = data1Result?.ppBMR ?: 0,
            data1_ppVisceralFat = data1Result?.ppVisceralFat ?: 0,
            data1_ppBoneKg = data1Result?.ppBoneKg ?: 0f,
            data1_ppBodyMuscleControl = data1Result?.ppBodyMuscleControl ?: 0f,
            data1_ppFatControlKg = data1Result?.ppFatControlKg ?: 0f,
            data1_ppBodyStandardWeightKg = data1Result?.ppBodyStandardWeightKg ?: 0f,
            data1_ppIdealWeightKg = data1Result?.ppIdealWeightKg ?: 0f,
            data1_ppControlWeightKg = data1Result?.ppControlWeightKg ?: 0f,
            data1_ppBodyType = data1Result?.ppBodyType?.toString() ?: "无数据",
            data1_ppFatGrade = data1Result?.ppFatGrade?.toString() ?: "无数据",
            data1_ppBodyHealth = data1Result?.ppBodyHealth?.toString() ?: "无数据",
            data1_ppBodyAge = data1Result?.ppBodyAge ?: 0,
            data1_ppBodyScore = data1Result?.ppBodyScore ?: 0

        )
    }
}

/**
 * CSV解析异常类
 */
class CSVParseException(message: String, cause: Throwable? = null) : Exception(message, cause)

