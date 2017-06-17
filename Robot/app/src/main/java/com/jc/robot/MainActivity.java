package com.jc.robot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.jc.robot.service.FloatWindowService;
import com.jc.robot.utils.LocationHelper;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btn_start_service;
    private Button btn_stop_service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        LocationHelper.getInstance().startLocation(locationListener);

    }

    private void initViews() {
        btn_start_service = (Button) findViewById(R.id.start_service);
        btn_start_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
                startService(intent);
                finish();
            }
        });
        btn_stop_service = (Button) findViewById(R.id.stop_service);
        btn_stop_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
                stopService(intent);
                finish();
            }
        });
    }

    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    Log.d(TAG, "经    度    : " + location.getLongitude() + "\n" + "纬    度    : " + location.getLatitude() + "\n");
                    LocationHelper.getInstance().stopLocation();
                } else {
                    //定位失败
                    Log.d(TAG, "定位失败" + location.getLocationDetail());
                }

            } else {
                Log.d(TAG, "定位失败，loc is null");
            }
        }
    };
}
