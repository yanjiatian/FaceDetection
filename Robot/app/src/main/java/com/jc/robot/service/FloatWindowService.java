package com.jc.robot.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.jc.robot.R;
import com.jc.robot.server.HttpUtils;
import com.jc.robot.utils.LocationHelper;
import com.jc.robot.utils.ScreenUtils;
import com.jc.robot.voice.JsonParser;
import com.jc.robot.voice.VoiceRecogniseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by yanjiatian on 2017/6/6.
 */

public class FloatWindowService extends Service {
    private static final String TAG = FloatWindowService.class.getSimpleName();
    private boolean longClick = false;

    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;

    ImageView mFloatView;
    TextView speech_result;
    AnimationDrawable robot_wang;

    // 用HashMap存储听写结果
    private HashMap<String, String> mRecognizeResults = new LinkedHashMap<String, String>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "float service onCreate()");
        createFloatView();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        //获取WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags =
//          LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//          LayoutParams.FLAG_NOT_TOUCHABLE
        ;

        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;

        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 0;
        wmParams.y = 0;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);

        Log.i(TAG, "mFloatLayout-->left" + mFloatLayout.getLeft());
        Log.i(TAG, "mFloatLayout-->right" + mFloatLayout.getRight());
        Log.i(TAG, "mFloatLayout-->top" + mFloatLayout.getTop());
        Log.i(TAG, "mFloatLayout-->bottom" + mFloatLayout.getBottom());

        //浮动窗口按钮
        mFloatView = (ImageView) mFloatLayout.findViewById(R.id.float_id);
        //识别文字
        speech_result = (TextView) mFloatLayout.findViewById(R.id.speech_result);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth() / 2);
        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight() / 2);
        //设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (longClick) {
                    Log.i(TAG, "移动");
                    //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                    wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth() / 2;
                    //25为状态栏的高度
                    wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight() / 2 - ScreenUtils.getStatusBarHeight(FloatWindowService.this);
                    //刷新
                    mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                }
                return false;
            }
        });
        mFloatView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.i(TAG, "长按");
                longClick = true;
                return false;
            }
        });
        mFloatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "点击");
                longClick = false;
                VoiceRecogniseHelper.getInstance().startRecognize(mRecognizerListener);
            }
        });

    }



    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] bytes) {
//            Log.d(TAG, "音量大小:" + volume);
        }

        @Override
        public void onBeginOfSpeech() {
            Log.d(TAG, "开始说话");
            mRecognizeResults.clear();
            mFloatView.setImageResource(R.drawable.robot_wang);
            robot_wang = (AnimationDrawable) mFloatView.getDrawable();
            robot_wang.start();
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "结束说话");
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            Log.d(TAG, "onResult()...");
//            printResult(recognizerResult);
            speech_result.setText(printResult(recognizerResult));
            if (isLast) {
                // TODO 最后的结果
                robot_wang.stop();
                //request server to parse
                HttpUtils.getInstance().requestServer(printResult(recognizerResult));

            }
        }

        @Override
        public void onError(SpeechError speechError) {
            Log.d(TAG, "错误:" + speechError.getErrorDescription());
            robot_wang.stop();
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    private String printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mRecognizeResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mRecognizeResults.keySet()) {
            resultBuffer.append(mRecognizeResults.get(key));
        }
        Log.d(TAG, "result:" + resultBuffer.toString());
        return resultBuffer.toString();
    }
}
