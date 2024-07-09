package com.lefu.ppblutoothkit.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lefu.ppblutoothkit.device.torre.ZipFileUtil
import com.lefu.ppbase.util.Logger

object FilePermissionUtil {

    fun requestPermission(activity: Activity, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                performFileSearch(activity, requestCode)
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + activity.packageName)
                activity.startActivityForResult(intent, requestCode)
            }
        } else {
            val permission_read =
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            if (permission_read != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    activity, arrayOf<String>(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), requestCode
                )
            } else {
                performFileSearch(activity, requestCode)
            }
        }
    }

    //选择文件
    fun performFileSearch(activity: Activity, requestCode: Int) {
        Logger.d("have permission" + "please select dfu file ,It is a zip file")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        //允许多选 长按多选
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.putExtra(Intent.EXTRA_ALARM_COUNT, true)
        //不限制选取类型
        intent.type = "*/*"
        activity.startActivityForResult(Intent.createChooser(intent, "选择DFU文件"), requestCode)
    }

    fun handleSingleDocument(activity: Activity, data: Intent): String {
        val uri = data.data
        val dfuFilePath = activity.getFilesDir().getAbsolutePath() + "/dfu/"
        //addPrint("DFU 文件解压路径：$dfuFilePath")
        return ZipFileUtil.zipUriToLocalFile(activity, uri, dfuFilePath) { filePath ->
            Logger.d("DFU 升级文件路径：$filePath\n")
        }
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


}