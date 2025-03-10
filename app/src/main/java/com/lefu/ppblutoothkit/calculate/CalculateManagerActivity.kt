package com.lefu.ppblutoothkit.calculate

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.lefu.ppbase.PPScaleDefine
import com.lefu.ppbase.util.PPUtil
import com.lefu.ppbase.vo.PPUnitType
import com.lefu.ppblutoothkit.R

class CalculateManagerActivity : Activity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caculate_manager)

        findViewById<Button>(R.id.dc4).setOnClickListener(this)
        findViewById<Button>(R.id.ac4).setOnClickListener(this)
        findViewById<Button>(R.id.ac4_2channel).setOnClickListener(this)
        findViewById<Button>(R.id.ac8).setOnClickListener(this)

        val weightET = findViewById<EditText>(R.id.weightET);
        val resultTV = findViewById<TextView>(R.id.resultTV);
        val unit_changeBTN = findViewById<Button>(R.id.unit_changeBTN);

        unit_changeBTN?.setOnClickListener{

            val text = weightET.text?.toString() ?: "0.0"

            val toFloat = text.toDouble()
            val weightValueD = PPUtil.getWeightValueD(PPUnitType.PPUnitST_LB, toFloat ?: 0.0, PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint005.getType(), true);
            resultTV.text = weightValueD.toString()
        }


        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        toolbar?.title = "Calculate"
        toolbar?.setTitleTextColor(Color.WHITE)
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
                startActivity(Intent(this@CalculateManagerActivity, Calculate8Activitiy::class.java))
            }
        }
    }


}