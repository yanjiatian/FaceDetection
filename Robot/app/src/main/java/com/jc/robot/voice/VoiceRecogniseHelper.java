package com.jc.robot.voice;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by yanjiatian on 2017/6/6.
 */

public class VoiceRecogniseHelper {
    private static final String TAG = VoiceRecogniseHelper.class.getSimpleName();
    private static VoiceRecogniseHelper instance;
    // 语音听写对象
    private SpeechRecognizer recognizer;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    public static VoiceRecogniseHelper getInstance() {
        if (instance == null) {
            instance = new VoiceRecogniseHelper();
        }
        return instance;
    }

    public void init(Context context) {
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        recognizer = SpeechRecognizer.createRecognizer(context, mInitListener);
        setParam();
    }

    public void startRecognize(RecognizerListener mRecognizerListener) {
        int ret = recognizer.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            Log.d(TAG, "听写失败,错误码：" + ret);
        } else {
            Log.d(TAG, "开始听写");

        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Log.e(TAG, "初始化失败，错误码：" + code);
            }
        }
    };

    public void setParam() {
        // 清空参数
        recognizer.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        recognizer.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        recognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");

        // 设置语言
        recognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        recognizer.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        recognizer.setParameter(SpeechConstant.VAD_EOS, "2000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        recognizer.setParameter(SpeechConstant.ASR_PTT, "0");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        recognizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        recognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }


}
