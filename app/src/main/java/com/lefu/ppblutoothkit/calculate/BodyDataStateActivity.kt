package com.lefu.ppblutoothkit.calculate

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.lefu.ppbase.util.PPUtil
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.util.DataUtil.bodyDataModel
import com.lefu.ppcalculate.vo.PPBodyDetailModel
import com.lefu.toolsutils.language.JsonLanguageDefaultValueUtils

class BodyDataStateActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_data_state)

        JsonLanguageDefaultValueUtils.initLocalLanguageJson(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.body_data_status)
        toolbar.setTitleTextColor(Color.WHITE)
        val textView = findViewById<TextView>(R.id.data_detail)
        val bodyData = bodyDataModel
        val buffer: StringBuffer = StringBuffer()
        if (bodyData != null) {
            val ppBodyDetailModel = PPBodyDetailModel(bodyData)
            ppBodyDetailModel.ppBodyDetailInfoModelToJsonVo?.lefuBodyData?.forEach {
                if (it.bodyParamNameString.isNullOrEmpty().not()) {
                    buffer.append("${JsonLanguageDefaultValueUtils.getValueFromJson(it.bodyParamNameString)}:${PPUtil.keepPoint1f(it.currentValue)}${it.unit}")
                    if (it.hasStandard) {
                        buffer.append(" ")
                        buffer.append("standTile:${JsonLanguageDefaultValueUtils.getValueFromJson(it.standardTitle)}")
                    }
                    buffer.append("\n")
                }
            }
            textView.text = buffer.toString()
        }
    }
}