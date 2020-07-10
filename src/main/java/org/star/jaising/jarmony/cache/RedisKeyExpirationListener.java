package org.star.jaising.jarmony.cache;

import com.hikvision.ebg.qy.ctm02dtenv.constant.CacheConstants;
import com.hikvision.ebg.qy.ctm02dtenv.entity.po.dtenv.BandCachePo;
import com.hikvision.ebg.qy.ctm02dtenv.entity.po.dtenv.WorkerBandPo;
import com.hikvision.ebg.qy.ctm02dtenv.repository.dtenv.WorkerBandRepository;
import com.hikvision.starfish.autoconfigure.redis.HikAutoAddRedisKeyPrefix;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author zhengjiaxing
 * @description 监听redis过期key
 * @date 2020/7/8
 */
@Slf4j
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Value("${component.id}")
    private String componentId;

    @Value("${component.segment.id}")
    private String segmentId;

    @HikAutoAddRedisKeyPrefix
    @Resource
    private RedisTemplate<String, String> hikRedisTemplate;

    @Resource
    private WorkerBandRepository workerBandRepository;

    /**
     * Creates new {@link MessageListener} for {@code __keyevent@*__:expired} messages.
     *
     * @param listenerContainer must not be {@literal null}.
     */
    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * 处理失效key
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String prefix = componentId + "." + segmentId + "-" + CacheConstants.WORKER_BAND_CACHE_PREFIX;
        // 获取失效key，过滤掉非手环业务缓存
        String expireKey = message.toString();
        if (!expireKey.startsWith(prefix)) {
            return;
        }
        log.info("监听到缓存过期key:{}", expireKey);
        // 提取手环唯一编号
        String bandNo = expireKey.substring(prefix.length());
        WorkerBandPo workerBandPo = workerBandRepository.findByBandNo(bandNo);
        if (workerBandPo == null || StringUtils.isEmpty(workerBandPo.getWorkerId())) {
            return;
        }
        BandCachePo bandCachePo = BandCachePo.builder()
                .bandNo(bandNo)
                .workerId(workerBandPo.getWorkerId())
                .workerName(workerBandPo.getWorkerName())
                .state("delete")
                .build();
        // 将过期的key重新加入缓存，用于通知前端失效人员定位
        String timeoutBandNo = CacheConstants.WORKER_BAND_TIME_OUT_CACHE_PREFIX + bandNo;
        hikRedisTemplate.opsForValue().set(
                timeoutBandNo,
                bandCachePo.serialize(),
                CacheConstants.WORKER_BAND_TIME_OUT_CACHE_EXPIRE_TIME,
                TimeUnit.SECONDS);
    }
}
