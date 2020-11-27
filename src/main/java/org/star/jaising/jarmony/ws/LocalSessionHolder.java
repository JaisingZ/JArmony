package org.star.jaising.jarmony.ws;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhengjiaxing
 * @description WebSocket Session管理者
 * @date 2020/10/30
 */
@Slf4j
@Component
public class LocalSessionHolder {

    /**
     * 服务端持有的会话列表
     */
    private ConcurrentHashMap<String, LocalSession> sessionMap = new ConcurrentHashMap<>();

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 服务端发送消息
     */
    public boolean send(String message, String userCode) {
        boolean sendResult = false;
        try {
            simpMessagingTemplate.convertAndSendToUser(userCode, LocalWebSocketConstants.REALTIME_REGISTRY_PATH, message);
            // 使用WebSocketSession.sendMessage会出现unhandled frame
            // LocalSession localSession = sessionMap.get(userId);
            // localSession.getSession().sendMessage(new TextMessage(message));
            sendResult = true;
        } catch (Exception e) {
            log.error("WebSocket服务端主动发送消息错误:", e);
        }
        return sendResult;
    }

    /**
     * 服务端发送消息
     */
    public boolean send(String message, LocalSession localSession) {
        boolean sendResult = false;
        try {
            simpMessagingTemplate.convertAndSendToUser(localSession.getUserCode(), LocalWebSocketConstants.REALTIME_REGISTRY_PATH, message);
            // 使用WebSocketSession.sendMessage会出现unhandled frame
            // localSession.getSession().sendMessage(new TextMessage(message));
            sendResult = true;
        } catch (Exception e) {
            log.error("WebSocket服务端主动发送消息错误:", e);
        }
        return sendResult;
    }

    /**
     * 根据userId获取会话
     *
     * @param userCode 客户端连接时指定的Stomp客户端唯一标识
     */
    public LocalSession getSessionByUserCode(String userCode) {
        LocalSession localSession = sessionMap.get(userCode);
        if (null == localSession) {
            localSession = LocalSession.init(userCode);
            addSession(userCode, localSession);
        }
        return localSession;
    }

    /**
     * 获取所有会话
     */
    public List<LocalSession> getAllSession() {
        Collection<LocalSession> sessions = sessionMap.values();
        if (CollectionUtils.isEmpty(sessions)) {
            return Lists.newArrayList();
        } else {
            return Lists.newArrayList(sessions);
        }
    }

    /**
     * 添加需要维护的会话
     */
    public void addSession(String userCode, LocalSession session) {
        sessionMap.put(userCode, session);
    }

    /**
     * 根据userId删除指定会话
     */
    public void removeSession(String userId) {
        try {
            sessionMap.remove(userId);
        } catch (Exception e) {
            log.error("LocalSessionHolder根据userId删除指定会话失败");
        }
    }

    /**
     * 获取所有连接的会话
     */
    public List<LocalSession> getAllAlive() {
        List<LocalSession> localSessions = Lists.newArrayList();
        for (String userId : sessionMap.keySet()) {
            LocalSession localSession = sessionMap.get(userId);
            if (!localSession.isAlive()) {
                removeSession(userId);
            } else {
                localSessions.add(localSession);
            }
        }
        return localSessions;
    }

    /**
     * 删除不再连接的会话
     */
    public void clearDisconnectSession() {
        try {
            for (String userId : sessionMap.keySet()) {
                LocalSession localSession = sessionMap.get(userId);
                if (!localSession.getSession().isOpen()) {
                    removeSession(userId);
                }
            }
        } catch (Exception e) {
            log.error("LocalSessionHolder删除不再连接的会话失败");
        }
    }

    /**
     * 判断Session是否满足订阅条件
     */
    public boolean judgeSuitable(LocalSession localSession, AlarmResp alarmResp) {
        if (localSession != null && localSession.getAlarmArgs() != null) {
            AlarmArgs alarmArgs = localSession.getAlarmArgs();
            if (alarmArgs != null && alarmArgs.getSimilarity() != null && !CollectionUtils.isEmpty(alarmArgs.getControlIds())) {
                Double subscribeSimilarity = alarmArgs.getSimilarity();
                List<String> subscribeControlIds = alarmArgs.getControlIds();
                if (alarmResp.getSimilarity() >= subscribeSimilarity) {
                    return subscribeControlIds.contains(alarmResp.getControlId());
                }
            }
        }
        return false;
    }
}
