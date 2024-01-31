package com.lefu.ppblutoothkit.producttest

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.lefu.ppblutoothkit.BuildConfig
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.producttest.dfu.ProductTestDfuTestActivity
import com.peng.ppscale.PPBlutoothKit


class ProductTestManagerActivity : Activity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_test_manager_activity)

        findViewById<Button>(R.id.torreDfuTest).setOnClickListener(this)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "ProductTest V" + BuildConfig.VERSION_NAME
        toolbar.setTitleTextColor(Color.WHITE)
        //产测程序，默认开启日志功能
        PPBlutoothKit.setDebug(true)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.torreDfuTest -> {
                //4电极直流算法
                startActivity(Intent(this@ProductTestManagerActivity, ProductTestDfuTestActivity::class.java))
            }
        }
    }


}