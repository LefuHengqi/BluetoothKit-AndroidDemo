package com.lefu.ppblutoothkit.calculate

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.util.DataUtil
import com.lefu.ppcalculate.PPBodyFatModel

class BodyDataDetailActivity : BaseImmersivePermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_data_detail)
        
        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()
        
        // 初始化Toolbar
        initToolbar()

        val textView = findViewById<TextView>(R.id.data_detail)
        val bodyData = DataUtil.bodyDataModel
        if (bodyData != null) {
            textView.text = bodyData.toString()
        }
        
        findViewById<android.view.View>(R.id.copyBodyFatInfo).setOnClickListener {
            if (bodyData != null) {
                copyTextToClip(this, bodyData.toString())
                Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<android.view.View>(R.id.catBodyFatDetailState).setOnClickListener {
            val intent = Intent(this, BodyDataStateActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setupUnifiedToolbar(
            toolbar = toolbar,
            title = getString(R.string.calculation_results),
            showBackButton = true
        )
    }

    private fun copyTextToClip(context: Context, text: String) {
        val myClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        myClipboard.setPrimaryClip(ClipData.newPlainText("text", text))
    }
}
