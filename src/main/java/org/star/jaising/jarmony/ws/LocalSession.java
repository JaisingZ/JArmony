package org.star.jaising.jarmony.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author zhengjiaxing
 * @description WebSocket Session封装
 * @date 2020/11/11
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LocalSession {

    /**
     * 用户id，面向平台的Session唯一标识
     */
    private String userCode;

    /**
     * WebSocket Session
     */
    private WebSocketSession session;

    /**
     * 是否在线
     */
    private boolean isAlive;

    /**
     * 告警参数
     */
    private AlarmArgs alarmArgs;

    /**
     * 初始化
     */
    public static LocalSession init(String userCode, WebSocketSession session, AlarmArgs alarmArgs) {
        return new LocalSession(userCode, session, true, alarmArgs);
    }

    public static LocalSession init(String userCode) {
        return init(userCode, null, null);
    }

    public static LocalSession init(String userCode, WebSocketSession session) {
        return init(userCode, session, null);
    }

    public static LocalSession init(String userCode, AlarmArgs alarmArgs) {
        return init(userCode, null, alarmArgs);
    }

    public static LocalSession empty() {
        return new LocalSession(null, null, true, AlarmArgs.empty());
    }
}
