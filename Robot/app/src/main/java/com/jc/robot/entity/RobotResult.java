package com.jc.robot.entity;

import java.io.Serializable;

/**
 * Created by yanjiatian on 2017/6/13.
 */

public class RobotResult implements Serializable {
    public String message;
    public String userid;
    public int status;
    public RobotInfo info;
}
