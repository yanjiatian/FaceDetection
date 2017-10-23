package com.jeff.facedetection;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import com.jeff.facedetection.camera.GlassCameraHelper;
import com.jeff.facedetection.camera.GlassCameraPreview;
import com.jeff.facedetection.utils.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import cn.ceyes.glasswidget.alertview.GlassAlert;
import cn.ceyes.glasswidget.alertview.GlassAlertEntity;
import cn.ceyes.glasswidget.gestures.GlassGestureDetector;
import cn.ceyes.glasswidget.gestures.GlassGestureListener;
import com.example.frsdktest.FaceDetectorHelper;
import com.example.frsdktest.RecFace;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private FrameLayout mParentView = null;
    private GlassCameraPreview mCameraPreview = null;
    private ImageView img_prepare;
    private ImageView mImageResult;
    private final int MSG_TAKE_PICTURE = 1;
    private final int MSG_CAMERA_OPEN_FAILED = 2;
    private final int MSG_CAMERA_OPEN_SUCCESS = 3;
    private final int MSG_CAMERA_OPEN_BUSY = 4;
    public static final int ALERT_ID_CAMERA_ERROR = 5;
    public static final int ALERT_ID_CAMERA_BUSY = 6;
    private final int MSG_RESTART_PREVIEW = 7;
    public GlassAlert mGlassAlert;
    private Bitmap mBitmap;
    private boolean isPreviewStatus = true;
    public double faceFresh = 0.4; //人脸识别的阈值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FaceDetectorHelper.getInstance().init(this);
        initGlassAlert();
        initViews();
        initDetector();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GlassCameraHelper.getInstance().openCamera(new GlassCameraCallBack());
    }

    @Override
    protected void onPause() {
        super.onPause();
        GlassCameraHelper.getInstance().stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        FaceDetectorHelper.getInstance().destroyModel();
    }

    private void initViews() {
        mParentView = (FrameLayout) findViewById(R.id.parent_view);
        mCameraPreview = (GlassCameraPreview) findViewById(R.id.camera_view);
        mCameraPreview.setSurfaceHolderCallback(new GlassCameraPreview.SurfaceHolderCallback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                GlassCameraHelper.getInstance().preview(mCameraPreview.getSurfaceHolder(), MainActivity.this);
            }
        });
        mCameraPreview.setAlpha(1.0f);
        img_prepare = (ImageView) findViewById(R.id.img_prepare);
        mImageResult = (ImageView) findViewById(R.id.rec_result);
    }

    private void initDetector() {
        final GlassGestureDetector detector = new GlassGestureDetector(this, new GlassGestureListener() {
            @Override
            public void onSingleTap(View v) {
                super.onSingleTap(v);
                if (isPreviewStatus) {
                    Toast.makeText(MainActivity.this, "拍照", Toast.LENGTH_SHORT).show();
                    mHandler.sendEmptyMessage(MSG_TAKE_PICTURE);
                } else {
                    Toast.makeText(MainActivity.this, "预览", Toast.LENGTH_SHORT).show();
                    mHandler.sendEmptyMessage(MSG_RESTART_PREVIEW);
                }

            }
        });

        mParentView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(v, event);
            }
        });
    }


    private class GlassCameraPictureCallBack implements GlassCameraHelper.GlassCameraTakePicCallback {
        @Override
        public void onPictureTaken(byte[] data) {
            if (data == null) {
                Log.e(TAG, "data is null .");
                return;
            } else {
                mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length).copy(Bitmap.Config.ARGB_8888, true);//data是字节数据，将其解析成位图
//                String fileName = Utils.createFileName() + ".jpg";
//                saveFile(mBitmap, fileName);
                drawFaceMessage(mBitmap);
                mImageResult.setVisibility(View.VISIBLE);
                mImageResult.setImageBitmap(mBitmap);
                isPreviewStatus = false;
                GlassCameraHelper.getInstance().stopPreview();

            }
        }
    }

    private void drawFaceMessage(Bitmap bitmap) {
        List<RecFace> recFaceList = FaceDetectorHelper.getInstance().getResultFace(bitmap);
        if (recFaceList != null) {
            Canvas canvas = new Canvas(bitmap);//创建一个空画布，并给画布设置位图
            Paint p = new Paint();
            p.setColor(0xffff0000);//设置画笔颜色
            p.setAntiAlias(true);//抗锯齿
            p.setTextSize(24);//设置字体大小
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1);
            for (int i = 0; i < recFaceList.size(); i++) {
                if (recFaceList.get(i).face_score < faceFresh) {
                    Log.d(TAG, "未识别:" + " " + (int) (recFaceList.get(i).face_score * 100) + "%");
                    canvas.drawText(getResources().getString(R.string.stranger), (int) recFaceList.get(i).face_rect[0], (int) recFaceList.get(i).face_rect[1], p);//在画布上绘制文字，即在位图上绘制文字
                } else {
                    Log.d(TAG, recFaceList.get(i).face_name + " " + (int) (recFaceList.get(i).face_score * 100) + "%");
                    canvas.drawText(recFaceList.get(i).face_name + " " + (int) (recFaceList.get(i).face_score * 100) + "%", (int) recFaceList.get(i).face_rect[0], (int) recFaceList.get(i).face_rect[1], p);//在画布上绘制文字，即在位图上绘制文字
                }

                canvas.drawRect((float) recFaceList.get(i).face_rect[0], (float) recFaceList.get(i).face_rect[1],
                        (float) (recFaceList.get(i).face_rect[0] + recFaceList.get(i).face_rect[2]),
                        (float) (recFaceList.get(i).face_rect[1] + recFaceList.get(i).face_rect[3]), p);
            }

        }
    }

    private void saveFile(Bitmap bm, String fileName) {
        try {
        /* 创建文件 */
            File myCaptureFile = new File(Utils.CAPTURE_FILES_PATH, fileName);
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(myCaptureFile));
            /* 采用压缩转档方法 */
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            /* 调用flush()方法，更新BufferStream */
            bos.flush();
            /* 结束OutputStream */
            bos.close();
        } catch (Exception e) {

        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_TAKE_PICTURE:
                    GlassCameraHelper.getInstance().takePicture(new GlassCameraPictureCallBack());
                    break;
                case MSG_CAMERA_OPEN_SUCCESS:
                    img_prepare.setVisibility(View.GONE);
                    isPreviewStatus = true;
                    break;
                case MSG_CAMERA_OPEN_FAILED:
                    mGlassAlert.setAlertEntity(GlassAlertEntity.createVerticalAlert(ALERT_ID_CAMERA_ERROR, R.drawable.ic_failed, R.string.error_open_camera,
                            R.string.tip_restart_open)).show();
                case MSG_CAMERA_OPEN_BUSY:
                    mGlassAlert.setAlertEntity(GlassAlertEntity.createVerticalAlert(ALERT_ID_CAMERA_BUSY, R.drawable.ic_failed, R.string.open_camera_busy,
                            R.string.tip_open_busy)).show();
                case MSG_RESTART_PREVIEW:
                    mImageResult.setVisibility(View.GONE);
                    mBitmap = null;
                    GlassCameraHelper.getInstance().startPreview();
                    isPreviewStatus = true;
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    private class GlassCameraCallBack implements GlassCameraHelper.GlassCameraCallback {
        @Override
        public void onCameraOpened(int status) {
            if (status == GlassCameraHelper.GlassCameraCallback.CAMERA_OPEN_OK) {
                GlassCameraHelper.getInstance().preview(mCameraPreview.getSurfaceHolder(), MainActivity.this);
                mHandler.sendEmptyMessage(MSG_CAMERA_OPEN_SUCCESS);
            } else if (status == GlassCameraHelper.GlassCameraCallback.CAMERA_OPEN_FAILED) {
                mHandler.sendEmptyMessage(MSG_CAMERA_OPEN_FAILED);
            } else if (status == GlassCameraHelper.GlassCameraCallback.CAMERA_OPEN_BUSY) {
                mHandler.sendEmptyMessage(MSG_CAMERA_OPEN_BUSY);
            }
        }

        @Override
        public void onCameraPreviewed(int status) {
            if (status == GlassCameraHelper.GlassCameraCallback.CAMERA_PREVIEW_OK) {
            } else if (status == GlassCameraHelper.GlassCameraCallback.CAMERA_PREVIEW_FAILED) {
                mHandler.sendEmptyMessage(CAMERA_OPEN_FAILED);
            }
        }
    }

    private void initGlassAlert() {
        mGlassAlert = new GlassAlert(this);
        mGlassAlert.setOnAlertDismissCallback(new GlassAlert.IGlassAlertDismissCallback() {
            @Override
            public void onAlertDismissed(int alertEntityId, boolean forced) {
                switch (alertEntityId) {
                    case ALERT_ID_CAMERA_ERROR:
                        break;
                }
            }
        });
    }


}

