package com.jeff.facedetection.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.view.SurfaceHolder;

import java.text.DecimalFormat;
import java.util.List;

public class GlassCameraHelper {

    private static final String TAG = GlassCameraHelper.class.getSimpleName();
    private static GlassCameraHelper mCameraHelper = new GlassCameraHelper();
    private GlassCameraTakePicCallback pictureCallback;
    private GlassCameraCallback cameraCallback;
    private Camera mCamera;
    private int degrees = 0;
    private boolean isPreviewed = false;
    private boolean isCameraBusy = false;

    public static GlassCameraHelper getInstance() {
        return mCameraHelper;
    }

    public void openCamera(GlassCameraCallback openback) {
        Log.d(TAG, "camera scene openCamera()...");
        Log.i(TAG, "Camera open....");
        if (isCameraBusy) {
            Log.d(TAG, "Camera is busy ...");
            if (openback != null) {
                openback.onCameraOpened(GlassCameraCallback.CAMERA_OPEN_BUSY);
            }
            stopCamera();
        }
        cameraCallback = openback;
        isCameraBusy = true;

        new Thread() {
            @Override
            public synchronized void run() {
                try {
                    Log.d(TAG, "camera scene openCamera()");
                    mCamera = Camera.open();
                } catch (Exception e) {
                    mCamera = null;
                    Log.e(TAG, "Camera open exception " + e.getMessage());
                } finally {
                    if (mCamera == null) {
                        isCameraBusy = false;
                        cameraCallback.onCameraOpened(GlassCameraCallback.CAMERA_OPEN_FAILED);
                    } else {
                        degrees = 0;
                        cameraCallback.onCameraOpened(GlassCameraCallback.CAMERA_OPEN_OK);
                    }
                }
            }
        }.start();
    }


    public void preview(SurfaceHolder holder, Context context) {
        Log.d(TAG, "camera scene preview()");
        if (isPreviewed || mCamera == null || holder == null) {
            Log.d(TAG, "camera scene isPreviewed or mCamera or holder is null");
            return;
        }
        initCamera(context);
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();// 开启预览
        } catch (Exception e) {
            cameraCallback.onCameraPreviewed(GlassCameraCallback.CAMERA_PREVIEW_FAILED);
            return;
        }
        Log.d(TAG, "Camera helper preview: ok");
        isPreviewed = true;
        cameraCallback.onCameraPreviewed(GlassCameraCallback.CAMERA_PREVIEW_OK);
    }

    public void takePicture(GlassCameraTakePicCallback pictureCallBack) {
        if (!isPreviewed || mCamera == null) {
            return;
        }
        Log.i(TAG, "takePicture...");
        try {
            pictureCallback = pictureCallBack;
            mCamera.takePicture(null, null, new PictureCallback() {
                public void onPictureTaken(byte[] data, Camera camera) {
                    pictureCallback.onPictureTaken(data);
                    isPreviewed = false;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "takePicture..." + e.getMessage());
            stopCamera();
        }
    }

    public void stopPreview() {
        try {
            mCamera.stopPreview();
            isPreviewed = false;
        } catch (Exception e) {

        }

    }

    public void startPreview() {
        try {
            mCamera.startPreview();
            isPreviewed = true;
        } catch (Exception e) {

        }

    }

    public void stopCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
        }
        mCamera = null;
        isPreviewed = false;
        isCameraBusy = false;
        Log.d(TAG, "Camera helper stopCamera ");
    }

    public int getCameraDisplayOrientation() {
        return degrees;
    }

    /**
     * @param degress 0 right eye. 180 left eye
     */
    public void setCameraDisplayOrientation(int degress) {
        if (mCamera == null) {
            return;
        }
        try {
            mCamera.setDisplayOrientation(degress);
            degrees = degress;
        } catch (Exception e) {

        }
    }

    public Camera getCamera() {
        return mCamera;
    }

    private void initCamera(Context mContext) {
        Log.d(TAG, "camera scene initCamera()");
        if (mCamera == null) {
            Log.d(TAG, "camera scene mCamera is null");
            return;
        }
        DecimalFormat df = new DecimalFormat("0.00");
        List<Camera.Size> pictureSizes = mCamera.getParameters().getSupportedPictureSizes();
        List<Camera.Size> previewSizes = mCamera.getParameters().getSupportedPreviewSizes();

        for (int i = 0; i < pictureSizes.size(); i++) {
            Camera.Size pSize = pictureSizes.get(i);
            Log.i(TAG, "-----PictureSize.width = " + pSize.width + "-----PictureSize.height = " + pSize.height + " -- " + df.format((float) pSize.width / pSize.height) + " ----- " + df.format((float) 16 / 9));
        }

        for (int i = 0; i < previewSizes.size(); i++) {
            Camera.Size pSize = previewSizes.get(i);
            Log.i(TAG, "-----previewSize.width = " + pSize.width + "-----previewSize.height = " + pSize.height + " -- " + df.format((float) pSize.width / pSize.height) + " ----- " + df.format((float) 16 / 9));
        }
        // 获得相机参数
        Camera.Parameters mParams = mCamera.getParameters();

        mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        mParams.setPreviewSize(1280, 720); // 设置预览大小
        mParams.setPictureSize(1920, 1080);
        mParams.setVideoStabilization(true);
        mParams.setPictureFormat(ImageFormat.JPEG);
        mCamera.setDisplayOrientation(degrees);
        mCamera.setParameters(mParams);
        Log.d(TAG, "camera scene setParameters over");
    }

    public interface GlassCameraTakePicCallback {
        public void onPictureTaken(byte[] data);
    }

    public interface GlassCameraCallback {

        public static final int CAMERA_OPEN_OK = 0;
        public static final int CAMERA_OPEN_FAILED = 1;
        public static final int CAMERA_OPEN_BUSY = CAMERA_OPEN_FAILED + 1;

        public static final int CAMERA_PREVIEW_OK = 0;
        public static final int CAMERA_PREVIEW_FAILED = 1;

        public void onCameraOpened(int status);

        public void onCameraPreviewed(int status);
    }

}
