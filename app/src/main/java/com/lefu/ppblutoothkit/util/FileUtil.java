package com.lefu.ppblutoothkit.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;

public class FileUtil {

    /**
     * 将日志通过邮箱的方式发送出去
     */
    public static void sendEmail(Context context, String path) {

        //判断文件是否存在
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
//                            SystemUtil.openEmail(this@AboutDeviceActivity, "", file)
                shareForSystemActivity(context, file);
            } else {
                Log.e("文件", "文件不存在");
            }
        }
    }

    /**
     * 弹窗系统分享
     * 文件
     */
    public static void shareForSystemActivity(Context context, File file) {
//    val file = File(filesDir, "${fileName}")
//    var uri = getPathUri(file, this@shareForSystemActivity)
        Uri uri = FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider",
                file
        );
//    }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType(context.getContentResolver().getType(uri));

        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, "分享文件"));
    }


}
