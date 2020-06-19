package org.star.jaising.jarmony.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

/**
 * @author zhengjiaxing
 * @description MQTT消息接收者
 * @date 2020/6/19
 */
@Slf4j
@Component
public class MqttReceiver {

    /**
     * MQTT消息处理器（消费者）
     *
     * @return {@link MessageHandler}
     */
    @Bean
    @ServiceActivator(inputChannel = MqttConfig.CHANNEL_NAME_IN)
    public MessageHandler handler() {
        return message -> {
            log.info("接收到MQTT消息:{}", message.getPayload());
            // do business logic
        };
    }
}
