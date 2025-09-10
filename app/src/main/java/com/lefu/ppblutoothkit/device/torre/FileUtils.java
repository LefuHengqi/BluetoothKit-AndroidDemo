package com.lefu.ppblutoothkit.device.torre;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.lefu.ppbase.util.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    //把assets目录下的db文件复制到dbpath下
    public static void moveDFUFile(Context context, String dfuFilePath) {

        File filesPath = new File(dfuFilePath);
        if (!filesPath.exists()) {
            filesPath.getParentFile().mkdirs();
//            try {
//                filePath.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        try {
            AssetManager assetManager = context.getAssets();

            String parentFileName = "Torre_ALL_OTA_V009.002.002_20230327";

            String[] filesPathList = assetManager.list(parentFileName);

            if (filesPathList == null || filesPathList.length <= 0) return;

            for (int i = 0; i < filesPathList.length; i++) {
                String inputFile = filesPathList[i];
                String outFilePath = dfuFilePath + inputFile;
                File outFile = new File(outFilePath);
                if (outFile.exists()) {
                    outFile.delete();
                }
                outFile.getParentFile().mkdirs();
                try {
                    outFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Logger.d(" 开始copy filePath: " + inputFile + " outFile：" + outFile);
                FileOutputStream out = new FileOutputStream(outFile);

                InputStream in = assetManager.open(parentFileName + File.separator + inputFile);

                byte[] buffer = new byte[1024];
                int readBytes = 0;
                while ((readBytes = in.read(buffer)) != -1)
                    out.write(buffer, 0, readBytes);
                in.close();
                out.flush();
                out.close();
                Logger.d(" copy inputFile: " + inputFile + " 结束");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getRealPath(Context context, Uri fileUri) {
        String realPath;
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11) {
            realPath = FileUtils.getRealPathFromURI_BelowAPI11(context, fileUri);
        }
        // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19) {
            realPath = FileUtils.getRealPathFromURI_API11to18(context, fileUri);
        }
        // SDK > 19 (Android 4.4) and up
        else {
            realPath = FileUtils.getRealPathFromURI_API19(context, fileUri);
        }
        return realPath;
    }


    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = 0;
        String result = "";
        if (cursor != null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
            return result;
        }
        return result;
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String pathFromUri = getPathFromUri(context, uri);
        if (pathFromUri != null && !TextUtils.isEmpty(pathFromUri)) {
            return pathFromUri;
        }
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                // This is for checking Main Memory
                if ("primary".equalsIgnoreCase(type)) {
                    if (split.length > 1) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    } else {
                        return Environment.getExternalStorageDirectory() + "/";
                    }
                    // This is for checking SD Card
                } else {
                    return "storage" + "/" + docId.replace(":", "/");
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                String fileName = getPathFromUri(context, uri);
                if (fileName != null) {
                    return Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                }
                String id = DocumentsContract.getDocumentId(uri);
                if (id.startsWith("raw:")) {
                    id = id.replaceFirst("raw:", "");
                    File file = new File(id);
                    if (file.exists())
                        return id;
                }

                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static String getFilePath(Context context, Uri uri) {

        Cursor cursor = null;
        final String[] projection = {
                MediaStore.MediaColumns.DISPLAY_NAME
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, null, null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public static String getPathFromUri(Context context, Uri uri) {
        String path = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            //如果是document类型的Uri，通过document id处理，内部会调用Uri.decode(docId)进行解码
            String docId = DocumentsContract.getDocumentId(uri);
            //primary:Azbtrace.txt
            //video:A1283522
            String[] splits = docId.split(":");
            String type = null, id = null;
            if (splits.length == 2) {
                type = splits[0];
                id = splits[1];
            }
            switch (uri.getAuthority()) {
                case "com.android.externalstorage.documents":
                    if ("primary".equals(type)) {
                        path = Environment.getExternalStorageDirectory() + File.separator + id;
                    }
                    break;
                case "com.android.providers.downloads.documents":
                    if ("raw".equals(type)) {
                        path = id;
                    } else {
                        Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                        path = getMediaPathFromUri(context, contentUri, null, null);
                    }
                    break;
                case "com.android.providers.media.documents":
                    Uri externalUri = null;
                    switch (type) {
                        case "image":
                            externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case "video":
                            externalUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case "audio":
                            externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case "document":
                            externalUri = MediaStore.Files.getContentUri("external");
                            break;
                    }
                    if (externalUri != null) {
                        String selection = "_id=?";
                        String[] selectionArgs = new String[]{id};
                        path = getMediaPathFromUri(context, externalUri, selection, selectionArgs);
                    }
                    break;
            }
        } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
            path = getMediaPathFromUri(context, uri, null, null);
        } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的Uri(uri.fromFile)，直接获取图片路径即可
            path = uri.getPath();
        }
        //确保如果返回路径，则路径合法
        return path == null ? null : (new File(path).exists() ? path : null);
    }

    private static String getMediaPathFromUri(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path;
        String authroity = uri.getAuthority();
        path = uri.getPath();
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (!path.startsWith(sdPath)) {
            int sepIndex = path.indexOf(File.separator, 1);
            if (sepIndex == -1) path = null;
            else {
                path = getSDPath() + path.substring(sepIndex);
            }
        }

        if (path == null || !new File(path).exists()) {
            ContentResolver resolver = context.getContentResolver();
            String[] projection = new String[]{MediaStore.MediaColumns.DATA};
            Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    try {
                        int index = cursor.getColumnIndexOrThrow(projection[0]);
                        if (index != -1) path = cursor.getString(index);
                        Log.i("FileUtils: ", "getMediaPathFromUri query " + path);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        path = null;
                    } finally {
                        cursor.close();
                    }
                }
            }
        }
        return path;
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        }
        return sdDir.toString();
    }


}
