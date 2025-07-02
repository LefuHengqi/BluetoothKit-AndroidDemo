package com.lefu.ppblutoothkit.device

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.okhttp.NetUtil

class SetHostActivity : BaseImmersivePermissionActivity() {
    private val mEnterHostEdit by lazy {
        findViewById<EditText>(R.id.mEnterHostEdit)
    }
    private val mEnterHostBtn by lazy {
        findViewById<Button>(R.id.mEnterHostBtn)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_host)
        
        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()
        
        // 初始化Toolbar
        initToolbar()
        
        initView()
    }
    
    private fun initToolbar() {
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        toolbar?.let {
            setupUnifiedToolbar(
                toolbar = it,
                title = "设置主机",
                showBackButton = true
            )
        }
    }

    private fun initView() {
        mEnterHostEdit.setText(NetUtil.getScaleDomain())
        mEnterHostBtn.setOnClickListener {
            mEnterHostEdit.text.trim().toString().let {
                if (it.isNotEmpty()) {
                    NetUtil.setScaleDomain(it)
                    finish()
                }
            }
        }

    }
}