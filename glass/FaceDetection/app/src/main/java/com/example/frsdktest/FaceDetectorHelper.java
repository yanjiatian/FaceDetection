package com.example.frsdktest;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import com.jeff.facedetection.R;
import com.jeff.facedetection.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanjiatian on 2017/10/17.
 */

public class FaceDetectorHelper {
    private static final String TAG = FaceDetectorHelper.class.getSimpleName();
    public List<BaseFace> baseFaceList = new ArrayList<>();
    private CaffeMobile caffeMobile;
    private static FaceDetectorHelper instance;
    private static int FACEFEATURESIZE = 512;
    private static int MAXFACECOUNT = 50;
    private static int PARAMSIZE = 22;
    private boolean initFinish = false;

    public static FaceDetectorHelper getInstance() {
        if (instance == null) {
            instance = new FaceDetectorHelper();
        }
        return instance;
    }

    public void init(Context context) {
        Log.d(TAG, "init model ...");
        initResource(context);  //拷贝资源文件
        caffeMobile = new CaffeMobile();
        initBaseFace();
    }

    private void initResource(Context context) {
        Utils.copyFilesFromRaw(context, R.raw.ldmk, "ldmk.bin", Utils.FILE_PATH);
        Utils.copyFilesFromRaw(context, R.raw.fea, "fea.bin", Utils.FILE_PATH);
        Utils.copyFilesFromRaw(context, R.raw.ldmkparam, "ldmkparam.bin", Utils.FILE_PATH);
        Utils.copyFilesFromRaw(context, R.raw.feaparam, "feaparam.bin", Utils.FILE_PATH);
    }

    public void destroyModel() {
        caffeMobile.Destroy();
        Log.d(TAG, "destroy model ...");
    }


    //获取比对结果
    public List<RecFace> getResultFace(Bitmap bitmap) {
        if (initFinish) {
            List<RecFace> recFaceList = getRecFaceList(bitmap);
            if (recFaceList == null) {
                Log.d(TAG, "未检测到人脸");
                return null;
            }
            for (int i = 0; i < recFaceList.size(); i++) {
                int index = 0;
                double tempScore = 0;
                for (int j = 0; j < baseFaceList.size(); j++) {
                    double score = caffeMobile.FFSimilarity(recFaceList.get(i).face_fea, baseFaceList.get(j).face_fea);
                    if (score > tempScore) {
                        index = j;
                        tempScore = score;
                    }
                }
                recFaceList.get(i).face_score = tempScore;
                recFaceList.get(i).face_name = baseFaceList.get(index).face_name;
            }
            return recFaceList;
        } else {
            return null;
        }
    }

    //初始化样本数据
    private void initBaseFace() {
        baseFaceList.clear();
        File[] files = Utils.getBaseFaceModel();
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                BaseFace baseFace = getBaseFace(files[i].getAbsolutePath());
                baseFaceList.add(baseFace);
            }
        }
        initFinish = true;
        Log.d(TAG, "基础数据初始化完成");
    }

    //通过camera里的原始数据来获取
    private List<RecFace> getRecFaceList(Bitmap bitmap) {
        long enter = SystemClock.elapsedRealtime();
        double[] rectArray = new double[PARAMSIZE * MAXFACECOUNT];    //left top width height nQuality
        int faceCount = 0;
        byte[] bgr = Utils.getPixelsBGR(bitmap);
        faceCount = caffeMobile.FDDetect(caffeMobile.DetectHandle[0], bgr, bitmap.getWidth(), bitmap.getHeight(), rectArray);
        Log.d(TAG, "识别人脸个数 ：" + faceCount);
        if (faceCount > 0) {
            List<RecFace> recFaceList = new ArrayList<>();
            for (int i = 0; i < faceCount; i++) {
                double[] fea = new double[FACEFEATURESIZE];
                double[] rect = new double[22];
                System.arraycopy(rectArray, 22 * i, rect, 0, 22);
                caffeMobile.FFFeaExtract(caffeMobile.FeatureHandle[0], bgr, bitmap.getWidth(), bitmap.getHeight(), fea, rect);
                RecFace recFace = new RecFace();
                recFace.face_fea = fea;
                recFace.face_rect = rect;
                recFaceList.add(recFace);
            }
            Log.d(TAG, "总共花费时长:" + (SystemClock.elapsedRealtime() - enter));
            return recFaceList;
        } else {
            Log.d(TAG, "A: detect no face");
            return null;
        }
    }


    private BaseFace getBaseFace(String filePath) {
        Log.d(TAG, "filepath=" + filePath);
        double[] fea = new double[FACEFEATURESIZE];
        double[] rectArray = new double[PARAMSIZE * MAXFACECOUNT];    //left top width height nQuality
        int faceCount = 0;
        long enter = SystemClock.elapsedRealtime();
        //检测人脸，提取特征
        faceCount = caffeMobile.FDDetectpath(caffeMobile.DetectHandle[0], filePath, rectArray);
        Log.d(TAG, "detect face count ：" + faceCount);
        if (faceCount > 0) {
            BaseFace baseFace = new BaseFace();
            caffeMobile.FFFeaExtractPath(caffeMobile.FeatureHandle[0], filePath, fea, rectArray);
            baseFace.face_path = filePath;
            baseFace.face_count = faceCount;
            baseFace.face_name = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf(".jpg"));
            baseFace.face_fea = fea;
            Log.d(TAG, "提取人脸信息：" + baseFace.face_name);
            Log.d(TAG, "总共花费时长:" + (SystemClock.elapsedRealtime() - enter));
            return baseFace;
        } else {
            Log.d(TAG, "detect no face");
            return null;
        }
    }

}
