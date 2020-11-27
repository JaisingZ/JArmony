package org.star.jaising.jarmony.ws;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhengjiaxing
 * @description 本地缓存的Session定时任务
 * @date 2020/11/16
 */
@Component
public class LocalSessionTask {

    @Resource
    private LocalSessionHolder localSessionHolder;

    /**
     * 清理不在线的Session，释放内存
     */
    @Scheduled(initialDelay = 0, fixedDelay = 1000 * 60 * 60)
    public void clearDisconnectSession() {
        localSessionHolder.clearDisconnectSession();
    }
}
