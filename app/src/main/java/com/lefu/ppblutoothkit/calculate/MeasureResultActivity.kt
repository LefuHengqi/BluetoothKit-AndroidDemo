package com.lefu.ppblutoothkit.calculate

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lefu.ppbase.util.Logger
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.util.DataUtil
import com.lefu.toolsutils.language.JsonLanguageDefaultValueUtils

/**
 * 测量结果页：两列卡片展示 25 项（4 电极）/ 57 项（8 电极）身体数据。
 * 由 [BodyDataDetailActivity] 自动打开，返回时回到计算结果页。
 */
class MeasureResultActivity : BaseImmersivePermissionActivity() {

    private lateinit var adapter: MeasureResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measure_result)

        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()

        // 初始化Toolbar
        initToolbar()

        //指标名称、状态都是语言 json 里的 key，先初始化
        JsonLanguageDefaultValueUtils.initLocalLanguageJson(this)

        val recyclerView = findViewById<RecyclerView>(R.id.measureResultRV)
        adapter = MeasureResultAdapter()
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        val fatModel = DataUtil.bodyDataModel
        if (fatModel == null) {
            Toast.makeText(this, R.string.no_data, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val items = MeasureResultBuilder.build(this, fatModel)
        if (items.isEmpty()) {
            Toast.makeText(this, R.string.no_data, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        Logger.d("MeasureResultActivity calcuteType=${fatModel.ppBodyBaseModel?.deviceModel?.deviceCalcuteType} itemCount=${items.size}")
        adapter.submit(items)
    }

    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setupUnifiedToolbar(
            toolbar = toolbar,
            title = getString(R.string.measure_result),
            showBackButton = true
        )
    }
}