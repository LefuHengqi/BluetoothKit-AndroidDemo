package com.lefu.ppblutoothkit.util

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.InputStream
import java.nio.charset.Charset

/**
 * CSV编码测试工具类
 * 用于调试和测试CSV文件的字符编码问题
 */
object CSVEncodingTestUtil {
    
    /**
     * 测试不同编码下的文件内容
     */
    fun testEncodings(context: Context, uri: Uri) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val buffer = ByteArray(1024)
                val bytesRead = inputStream.read(buffer)
                val content = buffer.sliceArray(0 until bytesRead)

                val encodings = listOf("GBK", "GB2312", "UTF-8", "ISO-8859-1", "UTF-16")
                
                Log.d("CSVEncodingTest", "=== 编码测试开始 ===")
                for (encoding in encodings) {
                    try {
                        val decoded = String(content, Charset.forName(encoding))
                        val firstLine = decoded.lines().firstOrNull() ?: ""
                        val score = calculateTestScore(decoded)
                        
                        Log.d("CSVEncodingTest", "编码 $encoding (得分: $score):")
                        Log.d("CSVEncodingTest", "  首行: ${firstLine.take(100)}")
                        Log.d("CSVEncodingTest", "  包含中文字符: ${decoded.any { it in "编号姓名体重身高性别年龄阻抗男女" }}")
                        Log.d("CSVEncodingTest", "  乱码字符数: ${decoded.count { it == '\uFFFD' }}")
                        
                    } catch (e: Exception) {
                        Log.d("CSVEncodingTest", "编码 $encoding: 解码失败 - ${e.message}")
                    }
                }
                Log.d("CSVEncodingTest", "=== 编码测试结束 ===")
            }
        } catch (e: Exception) {
            Log.e("CSVEncodingTest", "编码测试失败: ${e.message}")
        }
    }
    
    private fun calculateTestScore(text: String): Int {
        var score = 0
        
        // 检查乱码字符
        val replacementCharCount = text.count { it == '\uFFFD' }
        if (replacementCharCount > 0) {
            score -= replacementCharCount * 1000
        }
        
        // 检查CSV相关中文字符
        val csvChineseChars = "编号姓名体重身高性别年龄阻抗男女"
        val chineseCharCount = text.count { it in csvChineseChars }
        score += chineseCharCount * 100
        
        // 检查分隔符
        val separatorCount = text.count { it in ",\t;|" }
        score += separatorCount * 10
        
        // 检查数字
        val digitCount = text.count { it.isDigit() }
        score += digitCount * 2
        
        return score
    }
    
    /**
     * 检查是否包含中文字符
     */
    private fun containsChinese(text: String): Boolean {
        return text.any { char ->
            char.code in 0x4E00..0x9FFF // 中文字符范围
        }
    }
    
    /**
     * 检查是否包含乱码字符
     */
    private fun hasGarbledText(text: String): Boolean {
        // 检查替换字符
        if (text.contains('\uFFFD')) return true
        
        // 检查是否有过多的特殊字符
        val specialCharCount = text.count { it.code < 32 || it.code > 126 && it.code < 0x4E00 }
        return specialCharCount > text.length * 0.3
    }
    
    /**
     * 分析文件的字节内容
     */
    fun analyzeFileBytes(context: Context, uri: Uri) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val buffer = ByteArray(100) // 只读前100字节
                val bytesRead = inputStream.read(buffer)
                
                Log.d("CSVEncodingTest", "文件字节分析 (前${bytesRead}字节):")
                
                val hexString = buffer.take(bytesRead).joinToString(" ") { 
                    "%02X".format(it.toInt() and 0xFF) 
                }
                Log.d("CSVEncodingTest", "十六进制: $hexString")
                
                // 检查BOM
                if (bytesRead >= 3 && 
                    buffer[0] == 0xEF.toByte() && 
                    buffer[1] == 0xBB.toByte() && 
                    buffer[2] == 0xBF.toByte()) {
                    Log.d("CSVEncodingTest", "检测到UTF-8 BOM")
                }
                
                if (bytesRead >= 2 && 
                    buffer[0] == 0xFF.toByte() && 
                    buffer[1] == 0xFE.toByte()) {
                    Log.d("CSVEncodingTest", "检测到UTF-16LE BOM")
                }
                
                if (bytesRead >= 2 && 
                    buffer[0] == 0xFE.toByte() && 
                    buffer[1] == 0xFF.toByte()) {
                    Log.d("CSVEncodingTest", "检测到UTF-16BE BOM")
                }
            }
        } catch (e: Exception) {
            Log.e("CSVEncodingTest", "字节分析失败: ${e.message}")
        }
    }
}