package com.lefu.ppblutoothkit.device.torre;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.lefu.ppbase.util.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipFileUtil {

    /**
     * 在/data/data/下创建一个res文件夹，存放4个文件
     */
    private void copyDbFile(Context context, String fileName) {
        InputStream in = null;
        FileOutputStream out = null;
        String path = "/data/data/" + context.getPackageName() + "/file/res/";
        File file = new File(path + fileName);

        //创建文件夹
        File filePath = new File(path);
        if (!filePath.exists())
            filePath.mkdirs();

        if (file.exists())
            return;

        try {
            in = context.getAssets().open(fileName); // 从assets目录下复制
            out = new FileOutputStream(file);
            int length = -1;
            byte[] buf = new byte[1024];
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void compressFile(String outFilePath) {
        //压缩到此处
//        String path = "/data/data/" + getPackageName() + "/file/";
        //要压缩的文件的路径
        File file = new File(outFilePath + "res.zip");
        //创建文件夹
        File filePath = new File(outFilePath);
        if (!filePath.exists())
            filePath.mkdirs();

        if (file.exists())
            return;

        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new CheckedOutputStream(new FileOutputStream(file), new CRC32()));
            zip(zipOutputStream, "res", new File(outFilePath + "res/"));
            zipOutputStream.flush();
            zipOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("", "压缩完成");
    }

    public static String unZip(Context mContext, String zipFilePath, String outFilePath) {
//        String PATH = "/data/data/" + getPackageName() + "/file/unzip/";
//        File FILE = new File("/data/data/" + getPackageName() + "/file/res.zip");
        Log.d("", "zipFilePath : " + zipFilePath);
        Log.d("", "outFilePath : " + outFilePath);

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(mContext, "SD卡不可用~", Toast.LENGTH_SHORT).show();
            return "";
        }
        String PATH = outFilePath;
        File FILE = new File(zipFilePath);
        File outFile = new File(outFilePath);
        deleteFile(outFile);
        if (!outFile.exists()) {
            outFile.mkdirs();
        }
        try {
            upZipFile(FILE, PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("", "解压完成");
        return FILE.getName();
    }

    private static void deleteFile(File outFile) {
        if (outFile.exists()) {
            if (outFile.isFile()) {
                outFile.delete();
            } else if (outFile.isDirectory()) {
                File[] files = outFile.listFiles();
                if (files == null || files.length == 0) {
                    outFile.delete();
                } else {
                    for (File file : files) {
                        deleteFile(file);
                    }
                    deleteFile(outFile);
                }
            }
        }
    }

    private static void zip(ZipOutputStream zipOutputStream, String name, File fileSrc) throws IOException {
        if (fileSrc.isDirectory()) {
            System.out.println("需要压缩的地址是目录");
            File[] files = fileSrc.listFiles();

            name = name + "/";
            zipOutputStream.putNextEntry(new ZipEntry(name));  // 建一个文件夹
            System.out.println("目录名: " + name);

            for (File f : files) {
                zip(zipOutputStream, name + f.getName(), f);
                System.out.println("目录: " + name + f.getName());
            }
        } else {
            System.out.println("需要压缩的地址是文件");
            zipOutputStream.putNextEntry(new ZipEntry(name));
            System.out.println("文件名: " + name);
            FileInputStream input = new FileInputStream(fileSrc);
            System.out.println("文件路径: " + fileSrc);
            byte[] buf = new byte[1024];
            int len = -1;

            while ((len = input.read(buf)) != -1) {
                zipOutputStream.write(buf, 0, len);
            }

            zipOutputStream.flush();
            input.close();
        }
    }

    /**
     * 解压缩
     * 将zipFile文件解压到folderPath目录下.
     *
     * @param zipFile    zip文件
     * @param folderPath 解压到的地址
     * @throws IOException
     */
    private static void upZipFile(File zipFile, String folderPath) throws IOException {
        ZipFile zfile = new ZipFile(zipFile);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                Log.d("", "ze.getName() = " + ze.getName());
                String dirstr = folderPath + ze.getName();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                Log.d("", "str = " + dirstr);
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            Log.d("", "ze.getName() = " + ze.getName());
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
    }

    public static String zipUriToLocalFile(Context context, Uri uri, String outFilePath, ZipFileCallBack zipFileCallBack) {
        ZipInputStream zipInputStream = null;
        try {
            File outFile = new File(outFilePath);
            deleteFile(outFile);
            if (!outFile.exists()) {
                outFile.mkdirs();
            }
            zipInputStream = new ZipInputStream(context.getContentResolver().openInputStream(uri));
            ZipEntry zipEntry;
            String zipFileName = "";
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                // 处理每一个zip文件条目
                String entryName = zipEntry.getName();
                long entrySize = zipEntry.getSize();
                if (zipFileCallBack != null) {
                    zipFileCallBack.onFilePath(entryName);
                }
                String[] split = entryName.split("/");
                zipFileName = split[0];
                byte[] buffer = new byte[1024];
                int count;
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                while ((count = zipInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, count);
                }
                byte[] data = outputStream.toByteArray();
                FileOutputStream fileOutputStream = null;
                try {
                    File baseFile = new File(outFilePath + zipFileName + "/");
                    if (!baseFile.exists()) {
                        baseFile.mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFilePath + entryName);
                    fileOutputStream.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            String filePath = outFilePath + zipFileName + "/";
            Logger.d("filePath:" + filePath);
            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";


    }


    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    substr = new String(substr.getBytes("8859_1"), "GB2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ret = new File(ret, substr);

            }
            Log.d("", "1ret = " + ret);
            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            try {
                substr = new String(substr.getBytes("8859_1"), "GB2312");
                Log.d("", "substr = " + substr);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ret = new File(ret, substr);
            Log.d("", "2ret = " + ret);
            return ret;
        }
        return ret;
    }

    public interface ZipFileCallBack {

        void onFilePath(String filePath);

    }

}
