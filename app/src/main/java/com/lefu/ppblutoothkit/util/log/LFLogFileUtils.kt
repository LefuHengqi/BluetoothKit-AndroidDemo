package com.lefu.ppblutoothkit.util.log

import android.content.Context
import com.lefu.ppblutoothkit.util.LogUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 *    @author : whs
 *    e-mail : haisilen@163.com
 *    date   : 2023/6/5 11:43
 *    desc   :日志文件相关工具类
 */
object LFLogFileUtils {
    /**
     * 创建文件夹
     *
     */
    fun creatFolder(path: String): File {
        //新建一个File，传入文件夹目录
        val file = File(path)
        //判断文件夹是否存在，如果不存在就创建，否则不创建
        if (!file.exists()) {
            //通过file的mkdirs()方法创建目录中包含却不存在的文件夹
            file.mkdirs();
        }
        return file
    }

    /**
     * 创建文件
     *
     */
    fun creatFile(path: String, name: String): File {
        //新建一个File，传入文件夹目录
        val file = File(path, name)
        //判断文件是否存在，如果不存在就创建，否则不创建
        if (!file.exists()) {
            //创建新文件
            file.createNewFile()
        }

        return file
    }

    /**
     * 创建日志文件
     */
    fun creatLogFile(path: String, date: Date): File {

        val sdfShort = SimpleDateFormat(LogConstant.FORMAT_SHORT, Locale.CHINA)
        //日志写入日期,精确到天
        val curDate = sdfShort.format(date)

        //日志文件父文件夹
        val file = File(path)
        val files = file.listFiles()

        if (files != null && files.size >= 7) {
            //    * 日志类型各占一个文件夹，每个文件夹中包含最近七天(这里的其他不是只生活中的七天时间，而是使用app的七天时间，
            //    如果用户每隔1个月使用一次，那么会记录连着7个月的中每月使用那一天的日志)的日志数据，
            //    每天的日志占一个文件。超过第七天，直接删除最老的那天的日志再新建一个。
            //按文件最后修改时间升序排列文件
            files.sortBy({ it.lastModified() })
            deleteFile(files.get(0))
        }

        //日志文件
//        return File(path, "Android_${BuildConfig.VERSION_NAME}_${curDate}.txt")
        return File(path, "Android_111_${curDate}.txt")

    }


    /**
     * 压缩文件和文件夹
     *
     * @param srcFileString 要压缩的文件或文件夹
     * @param zipFileString 压缩完成的Zip路径
     * @throws Exception
     */
    @Throws(Exception::class)
    fun zipFolder(srcFileString: String, zipFileString: String) {
        //创建ZIP
        val outZip = ZipOutputStream(FileOutputStream(zipFileString))
        //创建文件
        val file = File(srcFileString)
        //压缩
        LogUtils.d(javaClass.simpleName, "---->" + file.parent + "===" + file.absolutePath)
        zipFiles(file.parent + File.separator, file.name, outZip)
        //完成和关闭
        outZip.finish()
        outZip.close()
    }

    /**
     * 压缩文件
     *
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun zipFiles(folderString: String, fileString: String, zipOutputSteam: ZipOutputStream?) {
        LogUtils.d(
            javaClass.simpleName, "folderString:" + folderString + "\n" +
                    "fileString:" + fileString + "\n=========================="
        )
        if (zipOutputSteam == null)
            return
        val file = File(folderString + fileString)
        if (file.isFile) {
            val zipEntry = ZipEntry(fileString)
            val inputStream = FileInputStream(file)
            zipOutputSteam.putNextEntry(zipEntry)
            var len: Int
            val buffer = ByteArray(4096)
            len = inputStream.read(buffer)
            while (len != -1) {
                zipOutputSteam.write(buffer, 0, len)
                len = inputStream.read(buffer)
            }
            zipOutputSteam.closeEntry()
        } else {
            //文件夹
            val fileList = file.list()
            //没有子文件和压缩
            if (fileList.size <= 0) {
                val zipEntry = ZipEntry(fileString + File.separator)
                zipOutputSteam.putNextEntry(zipEntry)
                zipOutputSteam.closeEntry()
            }
            //子文件和递归
            for (i in fileList.indices) {
                zipFiles(folderString + fileString + "/", fileList[i], zipOutputSteam)
            }
        }
    }

    /**
     * 删除文件或文件夹
     */
    fun deleteFile(file: File) {

        if (!file.exists()) return

        if (file.isDirectory) {
            val files = file.listFiles()
            files.forEach {
                //递归删除
                this.deleteFile(it)
            }
            file.delete()
        } else if (file.isFile) {
            file.delete()
        }
    }

    /**
     * 将asset文件写入缓存
     */
    fun copyAssetAndWrite(context: Context, assetsFileName: String, destPath: String): Boolean {
        try {
            //缓存文件路径
//            val cacheDir = context.cacheDir
//            val cacheDir = Environment.getExternalStorageDirectory()
            val cacheDir = File(destPath)
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            val outFile = File(cacheDir, assetsFileName)
            if (!outFile.exists()) {
                val res = outFile.createNewFile()
                if (!res) {
                    return false
                }
            } else {
                if (outFile.length() > 10) {//表示已经写入一次
                    return true
                }
            }
            val inputStream = context.assets.open(assetsFileName)
            val fos = FileOutputStream(outFile)
            val buffer = ByteArray(1024)
            var byteCount: Int = inputStream.read(buffer)
            while (byteCount != -1) {
                fos.write(buffer, 0, byteCount)
                byteCount = inputStream.read(buffer)
            }
            fos.flush()
            inputStream.close()
            fos.close()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return false
    }
}