package com.jc.robot;

import android.app.Application;

import com.iflytek.cloud.SpeechUtility;
import com.jc.robot.utils.LocationHelper;
import com.jc.robot.voice.VoiceRecogniseHelper;

/**
 * Created by yanjiatian on 2017/6/6.
 */

public class RobotApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(RobotApp.this, "appid=" + getString(R.string.app_id));
        VoiceRecogniseHelper.getInstance().init(this);
        LocationHelper.getInstance().initLocation(this);
    }
}
