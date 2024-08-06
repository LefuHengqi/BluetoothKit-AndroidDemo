package com.lefu.ppblutoothkit.device

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.okhttp.NetUtil

class SetHostActivity : AppCompatActivity() {
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