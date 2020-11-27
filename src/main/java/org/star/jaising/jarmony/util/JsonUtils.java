package org.star.jaising.jarmony.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengjiaxing
 * @description Json工具类
 * @date 2020/11/18
 */
@Slf4j
public class JsonUtils {

    /**
     * jackson进行对象序列化
     * 使用@JsonProperty生效
     */
    public static String serialize(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("jackson序列化对象失败", e);
            return null;
        }
    }

    /**
     * jackson进行对象反序列化
     * 使用@JsonProperty生效
     */
    public static <T> T deserialize(String content, Class<T> valueType) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            log.error("jackson反序列化对象失败", e);
            return null;
        }
    }
}
