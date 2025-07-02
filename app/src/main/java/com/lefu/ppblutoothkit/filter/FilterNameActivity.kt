package com.lefu.ppblutoothkit.filter

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.devicelist.ScanDeviceListActivity

class FilterNameActivity : BaseImmersivePermissionActivity() {

    private var et_filterName: EditText? = null
    private var seekBar: SeekBar? = null
    private var seekBarTv: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_name)
        
        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()
        
        // 初始化Toolbar
        initToolbar()

        et_filterName = findViewById(R.id.et_filterName)
        seekBar = findViewById(R.id.seekBar)
        seekBarTv = findViewById(R.id.seekBarTv)
        et_filterName?.setText(ScanDeviceListActivity.inputName)

        seekBarTv?.text = "${ScanDeviceListActivity.filterSign}dBm"
        seekBar?.setProgress(Math.abs(ScanDeviceListActivity.filterSign))
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekBarTv?.text = "-${progress}dBm"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                ScanDeviceListActivity.filterSign = -(seekBar?.progress ?: 80)
            }

        })


    }

    private fun initToolbar() {
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        toolbar?.let {
            setupUnifiedToolbar(
                toolbar = it,
                title = "过滤器名称",
                showBackButton = true
            )
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val inputName = et_filterName?.text?.toString() ?: ""
        ScanDeviceListActivity.inputName = inputName
        closeInputBoard()
    }

    private fun closeInputBoard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }


}