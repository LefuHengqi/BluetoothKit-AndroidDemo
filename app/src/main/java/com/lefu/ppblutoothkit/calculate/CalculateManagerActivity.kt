package com.lefu.ppblutoothkit.calculate

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.lefu.ppbase.PPScaleDefine
import com.lefu.ppbase.util.PPUtil
import com.lefu.ppbase.vo.PPUnitType
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity
import com.lefu.ppblutoothkit.R

class CalculateManagerActivity : BaseImmersivePermissionActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caculate_manager)
        
        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()
        
        // 初始化Toolbar
        initToolbar()
        
        findViewById<Button>(R.id.dc4).setOnClickListener(this)
        findViewById<Button>(R.id.ac4).setOnClickListener(this)
        findViewById<Button>(R.id.ac4_2channel).setOnClickListener(this)
        findViewById<Button>(R.id.ac8).setOnClickListener(this)

        val weightET = findViewById<EditText>(R.id.weightET);
        val resultTV = findViewById<TextView>(R.id.resultTV);
        val unit_changeBTN = findViewById<Button>(R.id.unit_changeBTN);

        unit_changeBTN?.setOnClickListener{

            val weightD = weightET.text?.toString() ?: "0.0"

            if(weightD.isNullOrEmpty()) return@setOnClickListener

            val toFloat = weightD.toDouble()
            
            // PPDeviceAccuracyTypePoint005 精度算法
            val weightValueST_LB_005 = PPUtil.getWeightValueD(PPUnitType.PPUnitST_LB, toFloat ?: 0.0, PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint005.getType(), true);
            val weightValueD_ST_005 = PPUtil.getWeightValueD(PPUnitType.PPUnitST, toFloat ?: 0.0, PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint005.getType(), true);
            val weightValueD_LB_005 = PPUtil.getWeightValueD(PPUnitType.Unit_LB, toFloat ?: 0.0, PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint005.getType(), true);
            val weightValueD_JIN_005 = PPUtil.getWeightValueD(PPUnitType.PPUnitJin, toFloat ?: 0.0, PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint005.getType(), true);

            // PPDeviceAccuracyTypePoint01 精度算法
            val weightValueST_LB_01 = PPUtil.getWeightValueD(PPUnitType.PPUnitST_LB, toFloat ?: 0.0, PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint01.getType(), true);
            val weightValueD_ST_01 = PPUtil.getWeightValueD(PPUnitType.PPUnitST, toFloat ?: 0.0, PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint01.getType(), true);
            val weightValueD_LB_01 = PPUtil.getWeightValueD(PPUnitType.Unit_LB, toFloat ?: 0.0, PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint01.getType(), true);
            val weightValueD_JIN_01 = PPUtil.getWeightValueD(PPUnitType.PPUnitJin, toFloat ?: 0.0, PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint01.getType(), true);

            val text = "KG:$toFloat\n\n" +
                    "精度0.05:\n" +
                    "ST_LB:$weightValueST_LB_005\n" +
                    "ST:$weightValueD_ST_005\n" +
                    "LB:$weightValueD_LB_005\n" +
                    "JIN:$weightValueD_JIN_005\n\n" +
                    "精度0.1:\n" +
                    "ST_LB:$weightValueST_LB_01\n" +
                    "ST:$weightValueD_ST_01\n" +
                    "LB:$weightValueD_LB_01\n" +
                    "JIN:$weightValueD_JIN_01"

            resultTV.text = text
        }

    }
    
    private fun initToolbar() {
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        toolbar?.let {
            setupUnifiedToolbar(
                toolbar = it,
                title = "计算管理",
                showBackButton = true
            )
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.dc4 -> {
                //4电极直流算法
                startActivity(Intent(this@CalculateManagerActivity, Calculate4DCActivitiy::class.java))
            }
            R.id.ac4 -> {
                //4电极交流算法  24项数据
                startActivity(Intent(this@CalculateManagerActivity, Calculate4ACActivitiy::class.java))
            }
            R.id.ac4_2channel -> {
                //4电极交流双频算法
                startActivity(Intent(this@CalculateManagerActivity, Calculate4AC2ChannelActivitiy::class.java))
            }
            R.id.ac8 -> {
                //8电极交流算法  48项数据
                startActivity(Intent(this@CalculateManagerActivity, Calculate8Activity::class.java))
            }
        }
    }


}