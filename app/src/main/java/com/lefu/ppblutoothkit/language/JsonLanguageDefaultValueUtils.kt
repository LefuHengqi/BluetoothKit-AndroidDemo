package com.lefu.toolsutils.language

import android.content.Context
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Locale

/**
 *    @author : whs
 *    e-mail : haisilen@163.com
 *    date   : 2023/3/30 16:36
 *    desc   :语言的默认json值
 */
object JsonLanguageDefaultValueUtils {

    var languageJson: String? = ""

    /**
     * 获取本地语言
     */
    fun initLocalLanguageJson(context: Context): String? {
        if (languageJson.isNullOrEmpty()) {
            var currentLanguageCode = getCurrentLanguageCode()
            if (currentLanguageCode.contains("zh")) {
                currentLanguageCode = "zh"
            } else {
                currentLanguageCode = "en"
            }
            languageJson = readLanguageJsonFromAssets(context, "body_${currentLanguageCode}.json")
            if ((languageJson?.length ?: 0) > 10) {//说明是拿到了本地的语言吧
                return languageJson
            } else {
                return null
            }

        }
        return languageJson
    }

    /**
     * 从 assets 目录中读取指定文件的内容，返回字符串形式的内容。
     *
     * @param context 上下文对象
     * @param filePath 文件路径，相对于 assets 目录，例如 "example.json"
     * @return 文件内容的字符串形式
     * @throws IOException 如果读取文件失败
     */
    fun readLanguageJsonFromAssets(context: Context, filePath: String): String {
        val sb = StringBuilder()
        try {
            context.assets.open(filePath).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        sb.append(line)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return sb.toString()
    }


    /**
     * 获取当前的语言
     */
    fun getCurrentLanguageCode(): String {
        val locale = Locale.getDefault()
        var language = locale.language
        val country = locale.country
        if (language.equals("zh", true)) {
            if (country.equals("HK", true) || country.equals("TW", true) || country.equals(
                    "MO",
                    true
                )
            ) {
                language = "zh_tw" // 繁体中文
            }
        }
        if (language.equals("id", true)) {
            language = "ind"
        }
        return language
    }

    /**
     * 根据key值来获取翻译
     * 多语言适配
     */
    fun getValueFromJson(key: String): String {
        //在线的
        languageJson?.let {
            //文件缓存的
            val json = languageJson?.let { it1 -> JSONObject(it1) }
            return json?.optString(key, key) ?: key
        }
        return key
    }


}