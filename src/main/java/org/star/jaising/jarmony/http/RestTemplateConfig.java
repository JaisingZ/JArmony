package org.star.jaising.jarmony.http;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

/**
 * @description: 声明外部系统Http接口调用的RestTemplate
 * @author: zhengjiaxing
 * @date: 2020.06.22
 */
@Slf4j
@Configuration
public class RestTemplateConfig {

    /**
     * 配置RestTemplate
     */
    @Bean("exRestTemplate")
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(Objects.requireNonNull(clientHttpRequestFactory()));
        // 使用UTF-8编码， StringHttpMessageConverter的默认字符集是ISO-8859-1
        List<HttpMessageConverter<?>> messageConverters  = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> httpMessageConverter : messageConverters) {
            if (httpMessageConverter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) httpMessageConverter).setSupportedMediaTypes(Lists.newArrayList(
                        MediaType.TEXT_HTML,
                        MediaType.APPLICATION_OCTET_STREAM,
                        MediaType.APPLICATION_JSON
                ));
                ((StringHttpMessageConverter) httpMessageConverter).setDefaultCharset(StandardCharsets.UTF_8);
            }
        }
        // 使用默认异常处理器
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        return restTemplate;
    }

    private ClientHttpRequestFactory clientHttpRequestFactory(){
        try {
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null,
                    (TrustStrategy) (arg0, arg1) -> true).build();
            httpClientBuilder.setSSLContext(sslContext);
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
                    hostnameVerifier);
            // 注册http和https请求
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslConnectionSocketFactory).build();
            // 开始设置连接池
            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(
                    socketFactoryRegistry);
            poolingHttpClientConnectionManager.setMaxTotal(2700); // 最大连接数2700
            poolingHttpClientConnectionManager.setDefaultMaxPerRoute(100); // 同路由并发数100
            httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);
            httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(3, true)); // 重试次数
            HttpClient httpClient = httpClientBuilder.build();
            // httpClient连接配置
            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
            factory.setReadTimeout(120 * 1000); // 连接超时时间，单位ms
            factory.setConnectTimeout(120 * 1000); // 数据读取超时时间
            factory.setConnectionRequestTimeout(120 * 1000);// 连接不够用的等待时间
            return factory;
        }catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            log.error("初始化HTTP连接池出错", e);
        }
        return null;
    }
}
