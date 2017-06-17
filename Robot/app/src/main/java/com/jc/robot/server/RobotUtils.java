package com.jc.robot.server;

import com.jc.robot.entity.RobotInfo;
import com.jc.robot.entity.RobotServices;

/**
 * Created by yanjiatian on 2017/6/13.
 */

public class RobotUtils {
    /**
     * 分发消息
     *
     * @param info
     */
    public static void dispServerResult(RobotInfo info) {
        if (info == null || info.services == null || info.services.isEmpty() || info.services.get(0) == null || info.services.isEmpty()) {
            return;
        }

        RobotServices rs = info.services.get(0);
        String domain = rs.domain;

    }
}
