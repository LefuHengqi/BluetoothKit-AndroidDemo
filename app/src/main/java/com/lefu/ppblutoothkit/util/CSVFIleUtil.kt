package com.lefu.ppblutoothkit.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.lefu.ppcalculate.PPBodyFatModel
import com.lefu.ppblutoothkit.util.CSVFIleUtil.CSVDataRow
import com.lefu.ppblutoothkit.util.CSVFIleUtil.CSVParseResult
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

/**
 * CSV文件解析工具类
 */
object CSVFIleUtil {

    /**
     * CSV数据行实体类
     * 对应CSV文件中的数据格式：编号、姓名、体重、身高、性别、年龄、50Khz阻抗、100Khz阻抗
     */
    data class CSVDataRow(
        val id: String,           // 编号
        val name: String,         // 姓名
        val weight: Float,        // 体重(kg)
        val height: Float,        // 身高(cm)
        val gender: String,       // 性别
        val age: Int,            // 年龄
        val impedance50Khz: Long, // 50Khz阻抗
        val impedance100Khz: Long // 100Khz阻抗
    )

    /**
     * CSV导出行数据类 - 每行包含原始数据和一组计算结果
     */
    data class CSVExportRow(
        // 原始数据
        val id: String,
        val name: String,
        val weight: Float,
        val height: Float,
        val gender: String,
        val age: Int,
        val impedance50Khz: Long,
        val impedance100Khz: Long,
        
        // 计算模式标识
        val calculationType: String, // "data1", "data2", "data3"
        val isSportMode: Boolean,
        val ppProduct: Int,
        
        // 计算结果
        val errorType: String,
        val ppBMI: Float,
        val ppFat: Float,
        val ppBodyfatKg: Float,
        val ppMusclePercentage: Float,
        val ppMuscleKg: Float,
        val ppBodySkeletal: Float,
        val ppBodySkeletalKg: Float,
        val ppWaterPercentage: Float,
        val ppWaterKg: Float,
        val ppProteinPercentage: Float,
        val ppProteinKg: Float,
        val ppLoseFatWeightKg: Float,
        val ppBodyFatSubCutPercentage: Float,
        val ppBodyFatSubCutKg: Float,
        val ppHeartRate: Int,
        val ppFootLen: Int,
        val ppBMR: Int,
        val ppVisceralFat: Int,
        val ppBoneKg: Float,
        val ppBodyMuscleControl: Float,
        val ppFatControlKg: Float,
        val ppBodyStandardWeightKg: Float,
        val ppIdealWeightKg: Float,
        val ppControlWeightKg: Float,
        val ppBodyType: String,
        val ppFatGrade: String,
        val ppBodyHealth: String,
        val ppBodyAge: Int,
        val ppBodyScore: Int
    )

    /**
     * CSV解析结果类
     */
    data class CSVParseResult(
        val headers: List<String>,      // 表头字段
        val dataRows: List<CSVDataRow>, // 数据行
        val totalRows: Int,             // 总行数（不包括表头）
        val errorRows: List<Pair<Int, String>> // 解析失败的行号和错误信息
    )

    /**
     * 从Uri中读取并解析CSV文件
     *
     * @param context 上下文
     * @param uri CSV文件的Uri
     * @param delimiter 分隔符，如果为null则自动检测
     * @return CSV解析结果
     */
    fun parseCSVFromUri(
        context: Context,
        uri: Uri,
        delimiter: String? = null
    ): CSVParseResult {
        val dataRows = mutableListOf<CSVDataRow>()
        val errorRows = mutableListOf<Pair<Int, String>>()
        var headers = emptyList<String>()
        var totalRows = 0
        var detectedDelimiter = delimiter

        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, "UTF-8")).use { reader ->
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
                                validateHeaders(headers)
                            }

                            else -> {
                                // 数据行
                                totalRows++
                                try {
                                    val dataRow = parseDataRow(columns, lineNumber)
                                    dataRows.add(dataRow)
                                } catch (e: Exception) {
                                    errorRows.add(lineNumber to "解析失败: ${e.message}")
                                }
                            }
                        }
                    }
                }
            }

        } catch (e: Exception) {
            throw Throwable("读取CSV文件失败: ${e.message}", e)
        }

        return CSVParseResult(headers, dataRows, totalRows, errorRows)
    }

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
                val score = calculateEncodingScore(decoded)
                
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
     * 检查文本是否有效（不包含乱码）
     */
    private fun isValidText(text: String): Boolean {
        return calculateEncodingScore(text) > 0
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
     * 验证表头格式
     */
    private fun validateHeaders(headers: List<String>) {
        val expectedHeaders = listOf("编号", "姓名", "体重", "身高", "性别", "年龄", "50Khz阻抗", "100Khz阻抗")

        if (headers.size < expectedHeaders.size) {
            Log.e("CSVFIleUtil", "表头字段详情:")
            headers.forEachIndexed { index, header ->
                Log.e("CSVFIleUtil", "  第${index + 1}列: '$header'")
            }
            throw CSVParseException("CSV表头字段不足，期望${expectedHeaders.size}个字段，实际${headers.size}个字段。实际表头: $headers")
        }

        // 检查是否包含必要的字段（支持不同的表头名称）
        val requiredFields = mapOf(
            "编号" to listOf("编号", "ID", "id", "序号", "No", "no"),
            "姓名" to listOf("姓名", "名字", "Name", "name"),
            "体重" to listOf("体重", "Weight", "weight", "重量"),
            "身高" to listOf("身高", "Height", "height"),
            "性别" to listOf("性别", "Gender", "gender", "Sex", "sex"),
            "年龄" to listOf("年龄", "Age", "age"),
            "50Khz阻抗" to listOf("50Khz阻抗", "50KHz阻抗", "50khz阻抗", "50KHZ阻抗", "阻抗50", "impedance50", "Impedance50"),
            "100Khz阻抗" to listOf("100Khz阻抗", "100KHz阻抗", "100khz阻抗", "100KHZ阻抗", "阻抗100", "impedance100", "Impedance100")
        )

        val missingFields = mutableListOf<String>()
        for ((fieldName, alternatives) in requiredFields) {
            val found = headers.any { header ->
                alternatives.any { alt -> header.contains(alt, ignoreCase = true) }
            }
            if (!found) {
                missingFields.add(fieldName)
            }
        }

        if (missingFields.isNotEmpty()) {
            Log.w("CSVFIleUtil", "警告: 以下字段可能缺失或名称不匹配: $missingFields")
            Log.w("CSVFIleUtil", "实际表头: $headers")
        }
    }

    /**
     * 解析数据行
     */
    private fun parseDataRow(columns: List<String>, lineNumber: Int): CSVDataRow {
        if (columns.size < 8) {
            throw IllegalArgumentException("第${lineNumber}行数据字段不足，期望8个字段，实际${columns.size}个字段")
        }

        return try {
            CSVDataRow(
                id = columns[0].trim(),
                name = columns[1].trim(),
                weight = parseFloat(columns[2].trim(), "体重"),
                height = parseFloat(columns[3].trim(), "身高"),
                gender = columns[4].trim(),
                age = parseInt(columns[5].trim(), "年龄"),
                impedance50Khz = parseLong(columns[6].trim(), "50Khz阻抗"),
                impedance100Khz = parseLong(columns[7].trim(), "100Khz阻抗")
            )
        } catch (e: Exception) {
            throw IllegalArgumentException("第${lineNumber}行数据解析失败: ${e.message}")
        }
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
     * 数据验证
     */
    fun validateCSVData(dataRow: CSVDataRow): List<String> {
        val errors = mutableListOf<String>()

        // 验证体重范围 (10-300kg)
        if (dataRow.weight < 10 || dataRow.weight > 300) {
            errors.add("体重超出合理范围(10-300kg): ${dataRow.weight}")
        }

        // 验证身高范围 (50-250cm)
        if (dataRow.height < 50 || dataRow.height > 250) {
            errors.add("身高超出合理范围(50-250cm): ${dataRow.height}")
        }

        // 验证年龄范围 (1-120岁)
        if (dataRow.age < 1 || dataRow.age > 120) {
            errors.add("年龄超出合理范围(1-120岁): ${dataRow.age}")
        }

        // 验证性别
        if (dataRow.gender !in listOf("男", "女", "M", "F", "Male", "Female")) {
            errors.add("性别格式不正确: ${dataRow.gender}")
        }

        // 验证阻抗值范围 (100-2000Ω)
        if (dataRow.impedance50Khz < 100 || dataRow.impedance50Khz > 2000) {
            errors.add("50Khz阻抗超出合理范围(100-2000Ω): ${dataRow.impedance50Khz}")
        }

        if (dataRow.impedance100Khz < 100 || dataRow.impedance100Khz > 2000) {
            errors.add("100Khz阻抗超出合理范围(100-2000Ω): ${dataRow.impedance100Khz}")
        }

        return errors
    }

    /**
     * 批量验证CSV数据
     */
    fun validateAllCSVData(dataRows: List<CSVDataRow>): Map<Int, List<String>> {
        return dataRows.mapIndexed { index, dataRow ->
            index to validateCSVData(dataRow)
        }.filter { it.second.isNotEmpty() }.toMap()
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

        // 打印原始表头用于调试
        Log.d("CSVFIleUtil", "原始表头详情:")
        headers.forEachIndexed { index, header ->
            Log.d("CSVFIleUtil", "  [$index]: '$header' (长度: ${header.length}, 字节: ${header.toByteArray().contentToString()})")
        }

        val fieldPatterns = mapOf(
            "id" to listOf("编号", "ID", "id", "序号", "No", "no", "num", "number", "1"),
            "name" to listOf("姓名", "名字", "Name", "name", "2"),
            "weight" to listOf("体重", "Weight", "weight", "重量", "kg", "3"),
            "height" to listOf("身高", "Height", "height", "cm", "4"),
            "gender" to listOf("性别", "Gender", "gender", "Sex", "sex", "男女", "5"),
            "age" to listOf("年龄", "Age", "age", "岁", "6"),
            "impedance50" to listOf("50Khz阻抗", "50KHz阻抗", "50khz阻抗", "50KHZ阻抗", "阻抗50", "impedance50", "Impedance50", "50", "7"),
            "impedance100" to listOf("100Khz阻抗", "100KHz阻抗", "100khz阻抗", "100KHZ阻抗", "阻抗100", "impedance100", "Impedance100", "100", "8")
        )

        // 首先尝试精确匹配
        for ((fieldKey, patterns) in fieldPatterns) {
            for ((index, header) in headers.withIndex()) {
                val cleanHeader = header.trim()
                if (patterns.any { pattern -> cleanHeader.equals(pattern, ignoreCase = true) }) {
                    fieldMapping[fieldKey] = index
                    Log.d("CSVFIleUtil", "精确匹配: $fieldKey -> [$index] '$cleanHeader'")
                    break
                }
            }
        }

        // 然后尝试包含匹配
        for ((fieldKey, patterns) in fieldPatterns) {
            if (!fieldMapping.containsKey(fieldKey)) {
                for ((index, header) in headers.withIndex()) {
                    val cleanHeader = header.trim()
                    if (patterns.any { pattern -> cleanHeader.contains(pattern, ignoreCase = true) }) {
                        fieldMapping[fieldKey] = index
                        Log.d("CSVFIleUtil", "包含匹配: $fieldKey -> [$index] '$cleanHeader'")
                        break
                    }
                }
            }
        }

        // 检查是否所有表头都是乱码
        val hasGarbledHeaders = headers.all { header ->
            header.contains('\uFFFD') || header.all { char ->
                char.code > 127 && char !in "编号姓名体重身高性别年龄阻抗男女"
            }
        }

        // 最后尝试位置匹配（如果表头数量正确但内容乱码）
        if (fieldMapping.size < fieldPatterns.size && headers.size == 8) {
            Log.d("CSVFIleUtil", "尝试位置匹配（表头可能乱码）")
            Log.d("CSVFIleUtil", "检测到乱码表头: $hasGarbledHeaders")
            
            val fieldKeys = listOf("id", "name", "weight", "height", "gender", "age", "impedance50", "impedance100")
            fieldKeys.forEachIndexed { index, fieldKey ->
                if (!fieldMapping.containsKey(fieldKey) && index < headers.size) {
                    fieldMapping[fieldKey] = index
                    Log.d("CSVFIleUtil", "位置匹配: $fieldKey -> [$index] '${headers[index]}'")
                }
            }
            
            // 如果是乱码表头，给出提示
            if (hasGarbledHeaders) {
                Log.w("CSVFIleUtil", "警告: 检测到表头乱码，已使用位置映射。建议检查文件编码格式。")
            }
        }

        return fieldMapping
    }

    /**
     * 验证字段映射是否包含所有必要字段
     */
    fun validateFieldMapping(fieldMapping: Map<String, Int>) {
        val requiredFields = listOf("id", "name", "weight", "height", "gender", "age", "impedance50", "impedance100")
        val missingFields = requiredFields.filter { !fieldMapping.containsKey(it) }

        if (missingFields.isNotEmpty()) {
            // 检查是否是完整的位置映射（8个字段都有映射）
            val isCompletePositionMapping = fieldMapping.size == 8 && 
                fieldMapping.values.toSet() == (0..7).toSet()
            
            if (isCompletePositionMapping) {
                Log.w("CSVFIleUtil", "使用位置映射，但缺少字段: $missingFields")
                Log.w("CSVFIleUtil", "当前映射: $fieldMapping")
                Log.w("CSVFIleUtil", "将继续处理，但请确认CSV文件列顺序正确")
                return // 允许位置映射继续处理
            }
            
            val errorMessage = "缺少必要字段的映射: $missingFields。" +
                    "当前映射: $fieldMapping。" +
                    "请检查CSV文件的表头格式，确保包含以下字段：编号、姓名、体重、身高、性别、年龄、50Khz阻抗、100Khz阻抗"

            Log.e("CSVFIleUtil", errorMessage)
            throw CSVParseException(errorMessage)
        } else {
            Log.d("CSVFIleUtil", "字段映射验证通过: $fieldMapping")
        }
    }

    /**
     * 使用字段映射解析数据行
     */
    private fun parseDataRowWithMapping(columns: List<String>, lineNumber: Int, fieldMapping: Map<String, Int>): CSVDataRow {
        return try {
            CSVDataRow(
                id = getColumnValue(columns, fieldMapping["id"]!!, "编号").trim(),
                name = getColumnValue(columns, fieldMapping["name"]!!, "姓名").trim(),
                weight = parseFloat(getColumnValue(columns, fieldMapping["weight"]!!, "体重").trim(), "体重"),
                height = parseFloat(getColumnValue(columns, fieldMapping["height"]!!, "身高").trim(), "身高"),
                gender = getColumnValue(columns, fieldMapping["gender"]!!, "性别").trim(),
                age = parseInt(getColumnValue(columns, fieldMapping["age"]!!, "年龄").trim(), "年龄"),
                impedance50Khz = parseLong(getColumnValue(columns, fieldMapping["impedance50"]!!, "50Khz阻抗").trim(), "50Khz阻抗"),
                impedance100Khz = parseLong(getColumnValue(columns, fieldMapping["impedance100"]!!, "100Khz阻抗").trim(), "100Khz阻抗")
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
     * @param data1Results data1计算结果列表
     * @param data2Results data2计算结果列表
     * @param data3Results data3计算结果列表
     * @param fileName 文件名（不包含扩展名）
     * @return 导出文件的Uri，失败返回null
     */
    fun exportCalculationResultsToCSV(
        context: Context,
        dataRows: List<CSVDataRow>,
        data1Results: List<PPBodyFatModel?>,
        data2Results: List<PPBodyFatModel?>,
        data3Results: List<PPBodyFatModel?>,
        fileName: String = "calculation_results"
    ): Uri? {
        return try {
            val csvContent = StringBuilder()
            
            // 添加表头
            csvContent.append(createCSVHeader()).append("\n")
            
            // 为每个原始数据行生成三行输出
            for (i in dataRows.indices) {
                val dataRow = dataRows[i]
                val data1 = if (i < data1Results.size) data1Results[i] else null
                val data2 = if (i < data2Results.size) data2Results[i] else null
                val data3 = if (i < data3Results.size) data3Results[i] else null
                
                // 生成data1行
                val exportRow1 = createCSVExportRowSingle(dataRow, data1, "data1", false, 0)
                csvContent.append(createCSVDataLine(exportRow1)).append("\n")
                
                // 生成data2行
                val exportRow2 = createCSVExportRowSingle(dataRow, data2, "data2", true, 0)
                csvContent.append(createCSVDataLine(exportRow2)).append("\n")
                
                // 生成data3行
                val exportRow3 = createCSVExportRowSingle(dataRow, data3, "data3", true, 1)
                csvContent.append(createCSVDataLine(exportRow3)).append("\n")
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
    private fun createCSVHeader(): String {
        return listOf(
            // 原始数据字段
            "编号", "姓名", "体重(kg)", "身高(cm)", "性别", "年龄", "50Khz阻抗", "100Khz阻抗",
            
            // 计算配置字段
            "计算类型", "运动模式", "产品类型",
            
            // 计算结果字段
            "错误类型", "BMI", "体脂率(%)", "脂肪量(kg)", "肌肉率(%)", "肌肉量(kg)", 
            "骨骼肌率(%)", "骨骼肌量(kg)", "水分率(%)", "水分量(kg)", "蛋白质率(%)", 
            "蛋白质量(kg)", "去脂体重(kg)", "皮下脂肪率(%)", "皮下脂肪量(kg)", 
            "心率", "脚长", "基础代谢", "内脏脂肪等级", "骨量(kg)", 
            "肌肉控制量(kg)", "脂肪控制量(kg)", "标准体重(kg)", "理想体重(kg)", 
            "控制体重(kg)", "身体类型", "肥胖等级", "健康评估", 
            "身体年龄", "身体得分"
        ).joinToString(",")
    }
    
    /**
     * 创建CSV数据行
     */
    private fun createCSVDataLine(row: CSVExportRow): String {
        return listOf(
            // 原始数据
            row.id, row.name, row.weight, row.height, row.gender, row.age, 
            row.impedance50Khz, row.impedance100Khz,
            
            // 计算配置
            row.calculationType, if (row.isSportMode) "是" else "否", row.ppProduct,
            
            // 计算结果
            row.errorType, row.ppBMI, row.ppFat, row.ppBodyfatKg, row.ppMusclePercentage,
            row.ppMuscleKg, row.ppBodySkeletal, row.ppBodySkeletalKg,
            row.ppWaterPercentage, row.ppWaterKg, row.ppProteinPercentage,
            row.ppProteinKg, row.ppLoseFatWeightKg, row.ppBodyFatSubCutPercentage,
            row.ppBodyFatSubCutKg, row.ppHeartRate, row.ppFootLen,
            row.ppBMR, row.ppVisceralFat, row.ppBoneKg,
            row.ppBodyMuscleControl, row.ppFatControlKg, row.ppBodyStandardWeightKg,
            row.ppIdealWeightKg, row.ppControlWeightKg, row.ppBodyType,
            row.ppFatGrade, row.ppBodyHealth, row.ppBodyAge, row.ppBodyScore
        ).joinToString(",") { value ->
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
     * 从PPBodyFatModel创建单行CSVExportRow的辅助方法
     */
    fun createCSVExportRowSingle(
        dataRow: CSVDataRow,
        result: PPBodyFatModel?,
        calculationType: String,
        isSportMode: Boolean,
        ppProduct: Int
    ): CSVExportRow {
        return CSVExportRow(
            // 原始数据
            id = dataRow.id,
            name = dataRow.name,
            weight = dataRow.weight,
            height = dataRow.height,
            gender = dataRow.gender,
            age = dataRow.age,
            impedance50Khz = dataRow.impedance50Khz,
            impedance100Khz = dataRow.impedance100Khz,
            
            // 计算配置
            calculationType = calculationType,
            isSportMode = isSportMode,
            ppProduct = ppProduct,
            
            // 计算结果
            errorType = result?.errorType?.toString() ?: "无数据",
            ppBMI = result?.ppBMI ?: 0f,
            ppFat = result?.ppFat ?: 0f,
            ppBodyfatKg = result?.ppBodyfatKg ?: 0f,
            ppMusclePercentage = result?.ppMusclePercentage ?: 0f,
            ppMuscleKg = result?.ppMuscleKg ?: 0f,
            ppBodySkeletal = result?.ppBodySkeletal ?: 0f,
            ppBodySkeletalKg = result?.ppBodySkeletalKg ?: 0f,
            ppWaterPercentage = result?.ppWaterPercentage ?: 0f,
            ppWaterKg = result?.ppWaterKg ?: 0f,
            ppProteinPercentage = result?.ppProteinPercentage ?: 0f,
            ppProteinKg = result?.ppProteinKg ?: 0f,
            ppLoseFatWeightKg = result?.ppLoseFatWeightKg ?: 0f,
            ppBodyFatSubCutPercentage = result?.ppBodyFatSubCutPercentage ?: 0f,
            ppBodyFatSubCutKg = result?.ppBodyFatSubCutKg ?: 0f,
            ppHeartRate = result?.ppHeartRate ?: 0,
            ppFootLen = result?.ppFootLen ?: 0,
            ppBMR = result?.ppBMR ?: 0,
            ppVisceralFat = result?.ppVisceralFat ?: 0,
            ppBoneKg = result?.ppBoneKg ?: 0f,
            ppBodyMuscleControl = result?.ppBodyMuscleControl ?: 0f,
            ppFatControlKg = result?.ppFatControlKg ?: 0f,
            ppBodyStandardWeightKg = result?.ppBodyStandardWeightKg ?: 0f,
            ppIdealWeightKg = result?.ppIdealWeightKg ?: 0f,
            ppControlWeightKg = result?.ppControlWeightKg ?: 0f,
            ppBodyType = result?.ppBodyType?.toString() ?: "无数据",
            ppFatGrade = result?.ppFatGrade?.toString() ?: "无数据",
            ppBodyHealth = result?.ppBodyHealth?.toString() ?: "无数据",
            ppBodyAge = result?.ppBodyAge ?: 0,
            ppBodyScore = result?.ppBodyScore ?: 0
        )
    }
}

/**
 * CSV解析异常类
 */
class CSVParseException(message: String, cause: Throwable? = null) : Exception(message, cause)

