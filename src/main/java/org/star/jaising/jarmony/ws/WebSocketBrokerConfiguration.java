package org.star.jaising.jarmony.ws;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author zhengjiaxing
 * @description Stomp Broker配置
 * 1.配置代理站点，支持SockJS
 * 2.添加请求拦截，从请求头中获取业务参数
 * 3.从请求头中获取客户端标识并持有至内存，用于向指定客户端发送消息
 * @date 2020/11/10
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketBrokerConfiguration implements WebSocketMessageBrokerConfigurer {

    @Resource
    private Environment environment;

    @Resource
    private LocalSessionHolder localSessionHolder;

    /**
     * Broker配置Endpoint连接站点
     * 允许调用本地资源，启用SockJS
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // EndPoint不能只是用Servlet上下文，会和Http请求冲突
        String endPointPath = LocalWebSocketConstants.ENDPOINT_RELATIVE;
        registry.addEndpoint(endPointPath)
                .setAllowedOrigins("*")
                .withSockJS();
    }

    /**
     * 配置内存代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 配置Stomp点对点通信，这是Stomp协议固定的，没得商量
        registry.enableSimpleBroker("/user");
    }

    /**
     * 配置请求通道，解析业务参数
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new LocalWebSocketInboundInterceptor());
    }

    /**
     * 新建WebSocket请求拦截器，获取连接时的业务参数
     */
    private class LocalWebSocketInboundInterceptor implements ChannelInterceptor {
        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            // 使用Spring Messaging的SessionId作为用户唯一标识
            String userCode = String.valueOf(message.getHeaders().get("simpSessionId"));
            // 处理CONNECT帧的请求头
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
                if (raw instanceof Map) {
                    Map<String, Object> headers = (Map) raw;
                    Object similarityObj = headers.get("similarity");
                    Object controlIdObj = headers.get("controlIds");
                    if (similarityObj instanceof LinkedList && controlIdObj instanceof LinkedList) {
                        try {
                            // 解析请求头中的业务参数
                            LinkedList<String> similarityList = (LinkedList) similarityObj;
                            Double similarity = Double.valueOf(similarityList.get(0));
                            LinkedList<String> controlIds = (LinkedList) controlIdObj;
                            // 将业务参数加入SessionHolder中管理
                            AlarmArgs alarmArgs = AlarmArgs.init(similarity, Lists.newArrayList(controlIds.get(0).split(",")));
                            LocalSession localSession = localSessionHolder.getSessionByUserCode(userCode);
                            localSession.setAlarmArgs(alarmArgs);
                            log.info("WebSocketBrokerConfiguration监听到有客户端连接接收告警消息，userCode:{}", userCode);
                        } catch (NumberFormatException e) {
                            log.error("LocalWebSocketInboundInterceptor解析客户端连接参数错误", e);
                            return message;
                        }
                    }
                }
            } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                log.error("用户{}断开连接", userCode);
                localSessionHolder.removeSession(userCode);
            }
            return message;
        }
    }
}
