package com.jeff.facedetection.camera;

import android.content.Context;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class GlassCameraPreview extends SurfaceView implements
        SurfaceHolder.Callback {
    private static String TAG = GlassCameraPreview.class.getSimpleName();
    private SurfaceHolder mHolder;
    private SurfaceHolderCallback mCallback;

    public GlassCameraPreview(Context context) {
        this(context, null);
    }

    public GlassCameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSurfaceHolderCallback(SurfaceHolderCallback callback) {
        mCallback = callback;
        getHolder().addCallback(this);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        if (mCallback != null) {
            mCallback.surfaceCreated(mHolder);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        GlassCameraHelper.getInstance().stopCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mHolder = holder;
        Camera camera = GlassCameraHelper.getInstance().getCamera();
        if (holder.getSurface() == null || camera == null) {
            return;
        }
        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();// 开启预览
            Log.d(TAG, "surfaceChanged startPreview");
        } catch (Exception e) {
            Log.i(TAG, "doStartPreview..." + e.getMessage());
        }
    }

    public SurfaceHolder getSurfaceHolder() {
        return mHolder;
    }


    public interface SurfaceHolderCallback {
        public void surfaceCreated(SurfaceHolder holder);
    }
}
