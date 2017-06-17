package com.jc.robot.entity;

import java.io.Serializable;

/**
 * Created by yanjiatian on 2017/6/13.
 */

public class RobotChoices implements Serializable, Comparable<RobotChoices> {
    public String id;

    //navi
    public String name;
    public Integer dis;
    public String addr;
    public RobotPointBean coord;

    //music
    public String title;
    public String artist;
    public String tag;
    public String url;
    public String type;
    public String size;

    //phone
    public String phone;
    public String alias;

    //radio
    public String rtitle;
    public String rurl;
    public String rcode; //0=AM 1=FM 2=Online

    @Override
    public int compareTo(RobotChoices o) {
        return dis - o.dis;
    }
}
