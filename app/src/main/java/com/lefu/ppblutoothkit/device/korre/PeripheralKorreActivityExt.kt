package com.lefu.ppblutoothkit.device

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lefu.ppblutoothkit.device.torre.ZipFileUtil

fun PeripheralKorreActivity.requestPermission() {
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

//选择文件
fun PeripheralKorreActivity.performFileSearch() {
    addPrint("have permission")
    addPrint("please select dfu file ,It is a zip file")
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    //允许多选 长按多选
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
    intent.putExtra(Intent.EXTRA_ALARM_COUNT, true)
    //不限制选取类型
    intent.type = "*/*"
    startActivityForResult(Intent.createChooser(intent, "选择DFU文件"), 1)
}

fun PeripheralKorreActivity.handleSingleDocument(data: Intent) {
    val uri = data.data
    dfuFilePath = this.getFilesDir().getAbsolutePath() + "/dfu/"
    dfuFilePath = ZipFileUtil.zipUriToLocalFile(this, uri, dfuFilePath) { filePath ->
        addPrint("DFU 升级文件路径：$filePath\n")
    }
    addPrint("DFU 文件解压路径：$dfuFilePath")
//        String filePath = FileUtils.getRealPath(this, uri);
//        isCopyEnd = true;    isCopyEnd = true;
//        if (filePath.endsWith(".zip")) {
//            String dfuFileName = unZip(filePath, dfuFilePath);
//            dfuFilePath = dfuFilePath + dfuFileName.replace(".zip", "") + File.separator;
//            wifi_name.append("DFU 文件解压路径：" + dfuFilePath + "\n");
//            isCopyEnd = true;
//        } else {
//            isCopyEnd = false;
//            FileUtils.moveDFUFile(this, filePath);
//            isCopyEnd = true;
//        }
}


