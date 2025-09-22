package com.lefu.ppblutoothkit.batch_calculate

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.lefu.ppbase.PPScaleDefine.PPDeviceCalcuteType
import com.lefu.ppbase.util.Logger
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.batch_calculate.CSVFIleUtil.CSVDataRow
import com.lefu.ppblutoothkit.util.FileUtil
import com.lefu.ppblutoothkit.util.ImpedanceMappingUtil
import com.lefu.ppcalculate.PPBodyFatModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 批量计算基类
 */
abstract class BaseBatchCalculateActivity : AppCompatActivity() {

    protected var toolbar: Toolbar? = null

    override fun onResume() {
        super.onResume()
        // 页面可见时处理待处理的数据
        processPendingDataIfNeeded()
    }

    /**
     * 处理待处理的数据（如果有的话）
     */
    private fun processPendingDataIfNeeded() {
        pendingDataIntent?.let { intent ->
            // 清空待处理数据
            pendingDataIntent = null
            
            // 在后台线程处理数据
            lifecycleScope.launch {
                try {
                    updateLoadingStatus("正在处理数据", "解析CSV文件中...")
                    
                    val result = withContext(Dispatchers.IO) {
                        CSVFIleUtil.parseCSVFromUriEnhanced(this@BaseBatchCalculateActivity, intent.data!!)
                    }
                    addPrint("数据处理失败: ${result.totalRows} 行数据解析完成，成功: ${result.dataRows.size} 行，失败: ${result.errorRows.size} 行")
                    // 在主线程更新UI
                    withContext(Dispatchers.Main) {
                        updateLoadingStatus("处理完成", "数据解析成功")
                        processDataResult(result)
                        
                        // 延迟隐藏弹窗
                        Handler(Looper.getMainLooper()).postDelayed({
                            hideLoadingDialog()
                        }, 1000)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        updateLoadingStatus("处理失败", "错误: ${e.message}")
                        addPrint("数据处理失败: ${e.message}")
                        
                        // 延迟隐藏弹窗
                        Handler(Looper.getMainLooper()).postDelayed({
                            hideLoadingDialog()
                        }, 2000)
                    }
                }
            }
        }
    }

    //批量计算使用的计算类型
    var batchCalculateType: PPDeviceCalcuteType = PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_1//CF577系列

    val REQUEST_CODE = 1024

    var logTxt: TextView? = null

    var dataRows: List<CSVDataRow>? = null

    var dfuFilePath: String? = null//本地文件升级时使用

    // 进度弹窗相关变量
    private var progressDialog: AlertDialog? = null
    private var progressBar: ProgressBar? = null
    private var tvProgress: TextView? = null
    private var tvStatus: TextView? = null

    // 数据加载弹窗相关变量
    private var loadingDialog: AlertDialog? = null
    private var tvLoadingTitle: TextView? = null
    private var tvLoadingStatus: TextView? = null
    
    // 待处理的数据Intent
    private var pendingDataIntent: Intent? = null

    /**
     * 设置沉浸式状态栏 - 需要在 setContentView 之后调用
     */
    /**
     * 设置沉浸式状态栏 - Toolbar延伸到状态栏区域
     */
    protected fun setupImmersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            // 设置状态栏透明，让Toolbar的背景延伸到状态栏
            window.statusBarColor = Color.TRANSPARENT

            // 设置布局延伸到状态栏下方
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11+ 使用新的API
                window.setDecorFitsSystemWindows(false)
                window.insetsController?.let { controller ->
                    controller.setSystemBarsAppearance(
                        0, // 清除浅色状态栏标志
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                }
            } else {
                // Android 5-10 使用旧的API
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        )
            }
        }
    }

    /**
     * 初始化统一的Toolbar - 简化版本
     */
    protected fun setupUnifiedToolbar(
        toolbar: Toolbar,
        title: String = "",
        showBackButton: Boolean = false
    ) {
        this.toolbar = toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            this.title = title
            setDisplayHomeAsUpEnabled(showBackButton)
            setDisplayShowHomeEnabled(showBackButton)
        }

        toolbar.setTitleTextColor(Color.WHITE)

        if (showBackButton) {
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }

        // 设置Toolbar的内边距和高度，确保有足够空间显示标题
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // 设置内边距
            view.setPadding(
                view.paddingLeft,
                systemBars.top, // 状态栏高度
                view.paddingRight,
                view.paddingBottom
            )

            // 确保Toolbar有足够的高度
            val actionBarSize = theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
            val minHeight = actionBarSize.getDimensionPixelSize(0, 0)
            actionBarSize.recycle()

            // 设置Toolbar的总高度 = actionBarSize + 状态栏高度
            val layoutParams = view.layoutParams
            layoutParams.height = minHeight + systemBars.top
            view.layoutParams = layoutParams

            insets
        }
    }


    fun addPrint(msg: String) {
        if (msg.isNotEmpty()) {
            Logger.d(msg)
            logTxt?.append("$msg\n")
        }
    }

    /**
     * 处理数据解析结果
     */
    private fun processDataResult(result: CSVFIleUtil.CSVParseResult?) {
        this.dataRows = result?.dataRows

        // 打印前几行的映射结果作为示例
        this.dataRows?.take(3)?.forEachIndexed { index, dataRow ->
            addPrint("第${index + 1}行映射结果:")
            addPrint("  编号: ${dataRow.id}, 姓名: ${dataRow.name}")
            addPrint("  编号: ${dataRow.id}, 性别: ${dataRow.gender}")
            if (dataRow.impedance50Khz > 0) {
                addPrint("  50Khz阻抗: ${dataRow.impedance50Khz}")
            }
            if (dataRow.impedance100Khz > 0) {
                addPrint("  100Khz阻抗: ${dataRow.impedance100Khz}")
            }
            if (dataRow.z100KhzLeftArmEnCode > 0) {
                addPrint("  z100KhzLeftArmEnCode: ${dataRow.z100KhzLeftArmEnCode}")
                addPrint("  z100KhzRightArmEnCode: ${dataRow.z100KhzRightArmEnCode}")
                addPrint("  z100KhzLeftLegEnCode: ${dataRow.z100KhzLeftLegEnCode}")
                addPrint("  z100KhzRightLegEnCode: ${dataRow.z100KhzRightLegEnCode}")
                addPrint("  z100KhzTrunkEnCode: ${dataRow.z100KhzTrunkEnCode}")
                addPrint("  z20KhzLeftArmEnCode: ${dataRow.z20KhzLeftArmEnCode}")
                addPrint("  z20KhzRightArmEnCode: ${dataRow.z20KhzRightArmEnCode}")
                addPrint("  z20KhzLeftLegEnCode: ${dataRow.z20KhzLeftLegEnCode}")
                addPrint("  z20KhzRightLegEnCode: ${dataRow.z20KhzRightLegEnCode}")
                addPrint("  z20KhzTrunkEnCode: ${dataRow.z20KhzTrunkEnCode}")
            }
        }
    }

    /**
     * 显示数据加载弹窗
     */
    private fun showLoadingDialog() {
        if (loadingDialog?.isShowing == true) {
            return
        }
        
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_loading_data, null)
        tvLoadingTitle = dialogView.findViewById(R.id.tvLoadingTitle)
        tvLoadingStatus = dialogView.findViewById(R.id.tvLoadingStatus)
        
        loadingDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
            
        loadingDialog?.show()
    }
    
    /**
     * 更新加载状态
     */
    private fun updateLoadingStatus(title: String, status: String) {
        runOnUiThread {
            tvLoadingTitle?.text = title
            tvLoadingStatus?.text = status
        }
    }
    
    /**
     * 隐藏数据加载弹窗
     */
    private fun hideLoadingDialog() {
        runOnUiThread {
            loadingDialog?.dismiss()
            loadingDialog = null
            tvLoadingTitle = null
            tvLoadingStatus = null
        }
    }

    fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                performFileSearch()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + this.packageName)
                startActivityForResult(intent, REQUEST_CODE)
            }
        } else {
            val permission_read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (permission_read != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf<String>(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), REQUEST_CODE
                )
            } else {
                performFileSearch()
            }
        }
    }

    /**
     * 执行文件搜索
     */
    protected fun performFileSearch() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, REQUEST_CODE)
    }

    /**
     * 开始批量计算
     */
    protected fun startBatchCalculate() {
        dataRows?.let { rows ->
            addPrint("开始批量计算，共${rows.size}条数据")
            
            lifecycleScope.launch {
                try {
                    showProgressDialog()
                    
                    val calculationResults = mutableListOf<PPBodyFatModel?>()
                    
                    rows.forEachIndexed { index, dataRow ->
                        updateProgress(index + 1, rows.size, "正在计算: ${dataRow.name}")
                        
                        val result = withContext(Dispatchers.IO) {
                            calculateCSVDataByProduct0(dataRow)
                        }
                        
                        calculationResults.add(result)
                    }
                    
                    hideProgressDialog()
                    addPrint("批量计算完成")
                    
                    // 导出结果到CSV文件
                    if (calculationResults.isNotEmpty()) {
                        updateProgress(calculationResults.size, calculationResults.size, "正在导出结果...")
                        
                        val exportUri = withContext(Dispatchers.IO) {
                            CSVFIleUtil.exportCalculationResultsToCSV(
                                this@BaseBatchCalculateActivity,
                                rows,
                                calculationResults,
                                "batch_calculation_results_${System.currentTimeMillis()}"
                            )
                        }
                        
                        if (exportUri != null) {
                            runOnUiThread {
                                FileUtil.sendEmail(this@BaseBatchCalculateActivity, exportUri.path)
                                addPrint("导出成功: ${exportUri.path}")
                                Toast.makeText(this@BaseBatchCalculateActivity, "批量计算完成！结果已导出到: ${exportUri.path}", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            runOnUiThread {
                                addPrint("导出失败，请检查存储权限")
                                Toast.makeText(this@BaseBatchCalculateActivity, "导出失败，请检查存储权限", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        runOnUiThread {
                            addPrint("没有计算结果可以导出")
                            Toast.makeText(this@BaseBatchCalculateActivity, "没有计算结果可以导出", Toast.LENGTH_LONG).show()
                        }
                    }
                    
                } catch (e: Exception) {
                    hideProgressDialog()
                    runOnUiThread {
                        addPrint("批量计算失败: ${e.message}")
                        Toast.makeText(this@BaseBatchCalculateActivity, "批量计算失败: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } ?: run {
            addPrint("没有数据可以计算")
        }
    }

    /**
     * 显示进度弹窗
     */
    private fun showProgressDialog() {
        if (progressDialog?.isShowing == true) {
            return
        }
        
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_batch_calculate_progress, null)
        progressBar = dialogView.findViewById(R.id.progressBar)
        tvProgress = dialogView.findViewById(R.id.tvProgress)
        tvStatus = dialogView.findViewById(R.id.tvStatus)
        
        progressDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
            
        progressDialog?.show()
    }
    
    /**
     * 更新进度
     */
    private fun updateProgress(current: Int, total: Int, status: String) {
        runOnUiThread {
            val progress = (current * 100 / total)
            progressBar?.progress = progress
            tvProgress?.text = "$current/$total ($progress%)"
            tvStatus?.text = status
        }
    }
    
    /**
     * 隐藏进度弹窗
     */
    private fun hideProgressDialog() {
        runOnUiThread {
            progressDialog?.dismiss()
            progressDialog = null
            progressBar = null
            tvProgress = null
            tvStatus = null
        }
    }

    /**
     * 处理Activity结果
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                // 保存待处理的数据Intent
                pendingDataIntent = data
                
                // 显示加载弹窗
                showLoadingDialog()
                updateLoadingStatus("准备处理", "正在准备解析数据...")
                
                // 如果页面已经可见，立即处理数据
                // 否则等待onResume时处理
                if (lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) {
                    // 在后台线程处理数据
                    lifecycleScope.launch {
                        try {
                            updateLoadingStatus("正在处理数据", "解析CSV文件中...")
                            
                            val result = withContext(Dispatchers.IO) {
                                CSVFIleUtil.parseCSVFromUriEnhanced(this@BaseBatchCalculateActivity, uri)
                            }

                            addPrint("数据处理失败: ${result.totalRows} 行数据解析完成，成功: ${result.dataRows.size} 行，失败: ${result.errorRows.size} 行")

                            // 在主线程更新UI
                            withContext(Dispatchers.Main) {
                                updateLoadingStatus("处理完成", "数据解析成功")
                                processDataResult(result)
                                pendingDataIntent = null
                                
                                // 延迟隐藏弹窗
                                Handler(Looper.getMainLooper()).postDelayed({
                                    hideLoadingDialog()
                                }, 1000)
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                updateLoadingStatus("处理失败", "错误: ${e.message}")
                                addPrint("数据处理失败: ${e.message}")
                                pendingDataIntent = null
                                
                                // 延迟隐藏弹窗
                                Handler(Looper.getMainLooper()).postDelayed({
                                    hideLoadingDialog()
                                }, 2000)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 抽象方法：计算单行数据
     */
    abstract fun calculateCSVDataByProduct0(dataRow: CSVDataRow): PPBodyFatModel?
}