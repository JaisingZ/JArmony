package org.star.jaising.jarmony.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * @description: 文件操作工具类
 * @author: zhengjiaxing
 * @date: 2020/11/26
 */
@Slf4j
public final class FileUtils {

    /**
     * 读取文件内容为json字符串
     */
    public static String readFileAsJson(String path) {
        try {
            ClassPathResource classPathResource = new ClassPathResource(path);
            if (classPathResource.exists()) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JsonNode jsonNode = objectMapper.readTree(classPathResource.getInputStream());
                return jsonNode.toString();
            } else {
                log.error("读取文件错误，路径不存在");
                return null;
            }
        } catch (IOException e) {
            log.error("读取文件错误", e);
            return null;
        }
    }
}
