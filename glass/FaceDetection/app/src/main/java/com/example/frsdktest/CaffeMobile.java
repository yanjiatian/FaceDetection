package com.example.frsdktest;


import android.os.Environment;
import android.util.Log;

public class CaffeMobile {
    private static final String TAG = CaffeMobile.class.getSimpleName();

    static {
        System.loadLibrary("FaceDetect");
        System.loadLibrary("FaceFeature");
    }

    private String filePath = Environment.getExternalStorageDirectory() + "/FaceDetection";
    public long[] DetectHandle = new long[1];
    public long[] FeatureHandle = new long[1];


    public CaffeMobile() {

        Log.d(CaffeMobile.class.getSimpleName(), "begin to init");
        int flag = FDInit(filePath + "/ldmkparam.bin", filePath + "/ldmk.bin", DetectHandle);
        Log.d(TAG, "FDflag = " + flag);
        if (flag == -66) {
            String sss = "FDInit flag = " + flag;
            Log.d(TAG, sss);
        }
        flag = FFInit(filePath + "/feaparam.bin", filePath + "/fea.bin", FeatureHandle);
        Log.d(TAG, "FFInit flag = " + flag);
        Log.d(TAG, "DetectHandle = " + DetectHandle[0]);
        Log.d(TAG, "FeatureHandle = " + FeatureHandle[0]);
        Log.d(TAG, "FDVersion = " + FDgetVersion());
        Log.d(TAG, "FFVersion = " + FFgetVersion());

        if (flag == -66) {
            String sss = "FFInit flag = " + flag;
            Log.d(TAG, sss);
        }
    }

    public void Destroy() {
        FDDestroy(DetectHandle[0]);
        FFDestroy(FeatureHandle[0]);

    }

    public native void enableLog(boolean enabled);

    public native int FDInit(String ldmkparam, String ldmkbin, long[] handle);

    public native void FDDestroy(long handle);

    public native int FDDetect(long handle, byte[] BGR, int width, int height, double[] rect);

    public native int FDDetectpath(long handle, String imgpath, double[] rect);

    public native String FDgetVersion();

    public native int FFInit(String feaparam, String feabin, long[] handle);

    public native void FFDestroy(long handle);

    public native int FFFeaExtract(long handle, byte[] BGR, int width, int height, double[] fea, double[] rect);

    public native int FFFeaExtractPath(long handle, String imgpath, double[] fea, double[] rect);

    public native double FFSimilarity(double[] feaA, double[] feaB);

    public native String FFgetVersion();


}
