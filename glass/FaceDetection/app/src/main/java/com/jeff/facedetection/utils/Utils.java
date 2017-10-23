package com.jeff.facedetection.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yanjiatian on 2017/10/17.
 */

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    public static String FILE_PATH = Environment.getExternalStorageDirectory() + "/FaceDetection/";
    public static String BASE_DATA = Environment.getExternalStorageDirectory() + "/FaceDetection/baseface/";
    public static String CAPTURE_FILES_PATH = Environment.getExternalStorageDirectory() + "/FaceDetection/capture/";

    private static final String SEPARATOR = File.separator;//路径分隔符

    public static File[] getBaseFaceModel() {
        File[] base = null;
        try {
            File capture = new File(CAPTURE_FILES_PATH);
            if (!capture.exists()) {
                Log.d(TAG, "要存储的目录不存在");
                if (capture.mkdirs()) {
                    Log.d(TAG, "已经创建文件存储目录");
                } else {
                    Log.d(TAG, "创建目录失败");
                }
            }
            File dir = new File(BASE_DATA);
            if (!dir.exists()) {
                Log.d(TAG, "要存储的目录不存在");
                if (dir.mkdirs()) {
                    Log.d(TAG, "已经创建文件存储目录");
                } else {
                    Log.d(TAG, "创建目录失败");
                }
            }
            base = dir.listFiles();
            if (base.length == 0) {
                Log.d(TAG, "没有原始文件");
            } else {
                for (int i = 0; i < base.length; i++) {
                    Log.d(TAG, "file:" + base[i].getAbsolutePath());
                }
            }
        } catch (Exception e) {

        }
        return base;
    }

    public static byte[] getPixelsBGR(Bitmap image) {
        // calculate how many bytes our image consists of
        int bytes = image.getByteCount();

        ByteBuffer buffer = ByteBuffer.allocate(bytes); // Create a new buffer
        image.copyPixelsToBuffer(buffer); // Move the byte data to the buffer

        byte[] temp = buffer.array(); // Get the underlying array containing the data.

        byte[] pixels = new byte[(temp.length / 4) * 3]; // Allocate for BGR

        // Copy pixels into place
        for (int i = 0; i < temp.length / 4; i++) {

            pixels[i * 3] = temp[i * 4 + 2];        //B
            pixels[i * 3 + 1] = temp[i * 4 + 1];    //G
            pixels[i * 3 + 2] = temp[i * 4];       //R

        }

        return pixels;
    }

    /**
     * 复制res/raw中的文件到指定目录
     *
     * @param context     上下文
     * @param id          资源ID
     * @param fileName    文件名
     * @param storagePath 目标文件夹的路径
     */
    public static void copyFilesFromRaw(Context context, int id, String fileName, String storagePath) {
        InputStream inputStream = context.getResources().openRawResource(id);
        File file = new File(storagePath);
        if (!file.exists()) {//如果文件夹不存在，则创建新的文件夹
            file.mkdirs();
        }
        readInputStream(storagePath + SEPARATOR + fileName, inputStream);
    }

    /**
     * 读取输入流中的数据写入输出流
     *
     * @param storagePath 目标文件路径
     * @param inputStream 输入流
     */
    public static void readInputStream(String storagePath, InputStream inputStream) {
        File file = new File(storagePath);
        try {
            if (!file.exists()) {
                // 1.建立通道对象
                FileOutputStream fos = new FileOutputStream(file);
                // 2.定义存储空间
                byte[] buffer = new byte[inputStream.available()];
                // 3.开始读文件
                int lenght = 0;
                while ((lenght = inputStream.read(buffer)) != -1) {// 循环从输入流读取buffer字节
                    // 将Buffer中的数据写到outputStream对象中
                    fos.write(buffer, 0, lenght);
                }
                fos.flush();// 刷新缓冲区
                // 4.关闭流
                fos.close();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String createFileName() {
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmm")
                .format(new Date());
        return timeStamp;
    }
}
