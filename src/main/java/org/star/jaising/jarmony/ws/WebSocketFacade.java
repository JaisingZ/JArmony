package org.star.jaising.jarmony.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhengjiaxing
 * @description WebSocket Broker功能暴露
 * @date 2020/11/10
 */
@Slf4j
@Component
public class WebSocketFacade {

    @Resource
    private LocalSessionHolder localSessionHolder;

    /**
     * 向指定用户发送消息
     */
    public void send2User(String userCode, String message) {
        localSessionHolder.send(message, userCode);
    }

    /**
     * 将告警消息发送给所有在线用户
     */
    public void sendAlarms2AllAlive(String message) {
        List<LocalSession> aliveSessions = localSessionHolder.getAllAlive();
        for (LocalSession localSession : aliveSessions) {
            localSessionHolder.send(message, localSession);
        }
    }

    /**
     * 将告警消息发送给满足订阅条件的在线用户
     */
    public void sendAlarms2AllSuitable(AlarmResp alarmResp) {
        List<LocalSession> aliveSessions = localSessionHolder.getAllAlive();
        for (LocalSession localSession : aliveSessions) {
            // 符合条件的告警才会推送给客户端
            if (localSessionHolder.judgeSuitable(localSession, alarmResp)) {
                localSessionHolder.send(alarmResp.serial(), localSession);
            }
        }
    }

    /**
     * 是否所有客户端均未连接
     */
    public boolean noneClient() {
        List<LocalSession> aliveSessions = localSessionHolder.getAllAlive();
        return CollectionUtils.isEmpty(aliveSessions);
    }
}
