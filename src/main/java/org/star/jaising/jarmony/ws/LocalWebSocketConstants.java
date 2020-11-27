package org.star.jaising.jarmony.ws;

/**
 * @author zhengjiaxing
 * @description WebSocket常量定义
 * @date 2020/11/11
 */
public class LocalWebSocketConstants {

    /**
     * 默认的EndPoint设置
     */
    public static final String ENDPOINT_DEFAULT = "/";

    /**
     * EndPoint设置，组件标识上下文+特定名称
     */
    public static final String ENDPOINT_RELATIVE = "/alarm";

    /**
     * Broker代理路径，和EndPoint不同，以该路径开头的请求将执行WebSocket代理
     */
    public static final String BROKER_PATH = "/alarm";

    /**
     * Broker发送实时告警代理路径
     */
    public static final String REALTIME_REGISTRY_PATH = "/realtime";
}
