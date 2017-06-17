package com.jc.robot.server;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.util.ArrayList;

/**
 * Created by yanjiatian on 2017/6/13.
 */

public class GsonUtils {
    private static final String TAG = "GsonUtils";

    private static Gson gson = new Gson();

    /**
     * @param json Json字符串eg:{"result":2,"message":"\u5446\u840c\u4e86\u4e00\u4e0b\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5~","data":{}}
     * @param cls  实体类
     * @param <T>  实体类类型
     * @return 含有内容的实体类对象
     */
    public static <T> T getObject(String json, Class<T> cls) {
        T t = null;
        try {
            Log.i(TAG, "getObject" + "json：" + json);
            t = gson.fromJson(json, cls);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return t;
    }

    public static <T> ArrayList<T> jsonArrayToArrayList(JsonArray jsonArray, Class<T> cls) {
        if (null == jsonArray || 0 == jsonArray.size()) {
            return new ArrayList<>();
        }
        int length = jsonArray.size();
        ArrayList<T> arrayList = new ArrayList<>(length);
        for (int i = 0; i < length; ++i) {
            T t = gson.fromJson(jsonArray.get(i), cls);
            arrayList.add(t);
        }
        return arrayList;
    }
}
