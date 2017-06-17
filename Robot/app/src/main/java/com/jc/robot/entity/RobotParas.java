package com.jc.robot.entity;

/**
 * Created by yanjiatian on 2017/6/13.
 */

public class RobotParas {
    public String id;

    //car3d
    public String type; //调整类型 1：风速 2：温度 3：暖风 4：凉风
    public String device;
    public String vstep;
    public String vto;

    //music
    public String title;
    public String artist;
    public String tag;

    //navi
    public RobotPoiBean from;
    public RobotPoiBean to;
    public String keyword;

    //phone
    public String name;
    public String alias;
    public String phone;

    //radio
    public String rtitle;
    public String rurl;
    public String rcode; // 0=AM 1=FM 2=Online
}
