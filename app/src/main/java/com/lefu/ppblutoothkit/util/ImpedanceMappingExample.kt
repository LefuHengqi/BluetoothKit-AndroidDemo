package com.lefu.ppblutoothkit.util

import android.util.Log

/**
 * 阻抗映射工具使用示例
 */
object ImpedanceMappingExample {

    private const val TAG = "ImpedanceMappingExample"

    /**
     * 演示单个阻抗值映射
     */
    fun demonstrateSingleMapping() {
        Log.d(TAG, "=== 单个阻抗值映射示例 ===")
        
        // 测试一些已知的阻抗值
        val testValues = listOf(200L, 250L, 300L, 500L, 680L, 999L, 1200L)
        
        testValues.forEach { impedance ->
            val mappedValue = ImpedanceMappingUtil.mapImpedanceValue(impedance)
            val isFound = ImpedanceMappingUtil.containsImpedanceValue(impedance)
            
            Log.d(TAG, "原始阻抗: $impedance -> 映射值: $mappedValue (${if (isFound) "找到映射" else "未找到映射"})")
        }
    }

    /**
     * 演示批量数据映射
     */
    fun demonstrateBatchMapping() {
        Log.d(TAG, "=== 批量数据映射示例 ===")
        
        // 创建测试数据
        val testDataRows = listOf(
            CSVFIleUtil.CSVDataRow(
                id = "1",
                name = "张三",
                weight = 70.5f,
                height = 175.0f,
                gender = "男",
                age = 30,
                impedance50Khz = 680L,  // 这个值在字典中存在
                impedance100Khz = 650L  // 这个值在字典中存在
            ),
            CSVFIleUtil.CSVDataRow(
                id = "2",
                name = "李四",
                weight = 65.0f,
                height = 168.0f,
                gender = "女",
                age = 25,
                impedance50Khz = 500L,  // 这个值在字典中存在
                impedance100Khz = 480L  // 这个值在字典中存在
            ),
            CSVFIleUtil.CSVDataRow(
                id = "3",
                name = "王五",
                weight = 80.0f,
                height = 180.0f,
                gender = "男",
                age = 35,
                impedance50Khz = 999L,  // 这个值在字典中存在
                impedance100Khz = 1000L // 这个值在字典中存在
            )
        )
        
        Log.d(TAG, "原始数据:")
        testDataRows.forEachIndexed { index, dataRow ->
            Log.d(TAG, "第${index + 1}行: ${dataRow.name} - 50Khz: ${dataRow.impedance50Khz}, 100Khz: ${dataRow.impedance100Khz}")
        }
        
        // 进行批量映射
        val mappedDataRows = ImpedanceMappingUtil.mapImpedanceValues(testDataRows)
        
        Log.d(TAG, "映射后数据:")
        mappedDataRows.forEachIndexed { index, dataRow ->
            Log.d(TAG, "第${index + 1}行: ${dataRow.name} - 50Khz: ${dataRow.impedance50Khz}, 100Khz: ${dataRow.impedance100Khz}")
        }
        
        // 比较映射前后的变化
        Log.d(TAG, "映射变化对比:")
        testDataRows.zip(mappedDataRows).forEachIndexed { index, (original, mapped) ->
            val changed50 = original.impedance50Khz != mapped.impedance50Khz
            val changed100 = original.impedance100Khz != mapped.impedance100Khz
            
            Log.d(TAG, "第${index + 1}行 ${original.name}:")
            Log.d(TAG, "  50Khz: ${original.impedance50Khz} -> ${mapped.impedance50Khz} ${if (changed50) "(已映射)" else "(未变化)"}")
            Log.d(TAG, "  100Khz: ${original.impedance100Khz} -> ${mapped.impedance100Khz} ${if (changed100) "(已映射)" else "(未变化)"}")
        }
    }

    /**
     * 演示字典信息查询
     */
    fun demonstrateDictionaryInfo() {
        Log.d(TAG, "=== 字典信息查询示例 ===")
        
        val dictionarySize = ImpedanceMappingUtil.getDictionarySize()
        Log.d(TAG, "字典总大小: $dictionarySize 个键值对")
        
        val allKeys = ImpedanceMappingUtil.getAllImpedanceKeys()
        Log.d(TAG, "字典键范围: ${allKeys.minOrNull()} - ${allKeys.maxOrNull()}")
        
        // 显示前10个和后10个键值对作为示例
        Log.d(TAG, "前10个键值对:")
        allKeys.take(10).forEach { key ->
            val value = ImpedanceMappingUtil.mapImpedanceValue(key.toLong())
            Log.d(TAG, "  $key -> $value")
        }
        
        Log.d(TAG, "后10个键值对:")
        allKeys.takeLast(10).forEach { key ->
            val value = ImpedanceMappingUtil.mapImpedanceValue(key.toLong())
            Log.d(TAG, "  $key -> $value")
        }
    }

    /**
     * 演示映射统计分析
     */
    fun demonstrateMappingStatistics(dataRows: List<CSVFIleUtil.CSVDataRow>) {
        Log.d(TAG, "=== 映射统计分析 ===")
        
        if (dataRows.isEmpty()) {
            Log.d(TAG, "数据为空，无法进行统计分析")
            return
        }
        
        val mappedDataRows = ImpedanceMappingUtil.mapImpedanceValues(dataRows)
        
        // 统计映射成功率
        var mapped50Count = 0
        var mapped100Count = 0
        
        dataRows.zip(mappedDataRows).forEach { (original, mapped) ->
            if (original.impedance50Khz != mapped.impedance50Khz) mapped50Count++
            if (original.impedance100Khz != mapped.impedance100Khz) mapped100Count++
        }
        
        val total = dataRows.size
        val mapped50Rate = (mapped50Count * 100.0 / total)
        val mapped100Rate = (mapped100Count * 100.0 / total)
        
        Log.d(TAG, "总数据行数: $total")
        Log.d(TAG, "50Khz阻抗映射成功: $mapped50Count 行 (${String.format("%.1f", mapped50Rate)}%)")
        Log.d(TAG, "100Khz阻抗映射成功: $mapped100Count 行 (${String.format("%.1f", mapped100Rate)}%)")
        
        // 统计未映射的阻抗值
        val unmapped50Values = mutableSetOf<Long>()
        val unmapped100Values = mutableSetOf<Long>()
        
        dataRows.zip(mappedDataRows).forEach { (original, mapped) ->
            if (original.impedance50Khz == mapped.impedance50Khz) {
                unmapped50Values.add(original.impedance50Khz)
            }
            if (original.impedance100Khz == mapped.impedance100Khz) {
                unmapped100Values.add(original.impedance100Khz)
            }
        }
        
        if (unmapped50Values.isNotEmpty()) {
            Log.d(TAG, "未映射的50Khz阻抗值: ${unmapped50Values.sorted().take(10)}")
        }
        
        if (unmapped100Values.isNotEmpty()) {
            Log.d(TAG, "未映射的100Khz阻抗值: ${unmapped100Values.sorted().take(10)}")
        }
    }

    /**
     * 运行所有示例
     */
    fun runAllExamples(dataRows: List<CSVFIleUtil.CSVDataRow>? = null) {
        Log.d(TAG, "开始运行阻抗映射示例...")
        
        demonstrateSingleMapping()
        demonstrateBatchMapping()
        demonstrateDictionaryInfo()
        
        dataRows?.let {
            demonstrateMappingStatistics(it)
        }
        
        Log.d(TAG, "阻抗映射示例运行完成")
    }
}