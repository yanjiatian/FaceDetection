package com.jc.robot.server;

import android.util.Log;

import com.jc.robot.entity.RobotResult;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by yanjiatian on 2017/6/13.
 */

public class HttpUtils {
    private static final String TAG = HttpUtils.class.getSimpleName();
    private static HttpUtils instance;
    public static final String BASE_URL = "http://211.159.163.116:8989";//登陆和大脑

    public static HttpUtils getInstance() {
        if (instance == null) {
            instance = new HttpUtils();
        }
        return instance;
    }

    public static final String getRobotUrl(String key) {
        String url = BASE_URL + "/brainservice/query?query=" + key + "&uid=111&token=111&x=116.4&y=39.9";
        Log.d(TAG, url);
        return url;
    }


    public void requestServer(String key) {
        // step 1: 创建 OkHttpClient 对象
        OkHttpClient okHttpClient = new OkHttpClient();
        // step 2： 创建一个请求，不指定请求方法时默认是GET。
        Request.Builder requestBuilder = new Request.Builder().url(getRobotUrl(key));
        //可以省略，默认是GET请求
        requestBuilder.method("GET", null);
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // TODO: 2017/6/13 请求失败
                Log.d(TAG, "request failure ...");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // TODO: 2017/6/13 请求成功
                Log.d(TAG, "request success ...");
                ResponseBody body = response.body();
                String json = body.string();
                RobotResult robotResult = GsonUtils.getObject(json, RobotResult.class);

                Log.d(TAG, "status:" + robotResult.status);
                Log.d(TAG, "response:" + json);
            }
        });

    }

}
