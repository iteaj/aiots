package com.iteaj.iboot.module.iot.collect.model;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.dto.EventSourceDto;
import com.iteaj.iboot.module.iot.utils.IotLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

public class EventSourceTask implements Runnable {

    private Executor executor;
    private EventSourceDto sourceDto;
    private IotCacheManager cacheManager;
    private static Logger logger = LoggerFactory.getLogger(EventSourceTask.class);

    public EventSourceTask(Executor executor, EventSourceDto sourceDto, IotCacheManager cacheManager) {
        this.executor = executor;
        this.sourceDto = sourceDto;
        this.cacheManager = cacheManager;
    }

    @Override
    public void run() {
        if(CollectionUtil.isNotEmpty(sourceDto.getGroups())) {
            sourceDto.getGroups().forEach(group -> executor
                    .execute(new BatchGroupTask(group, sourceDto, cacheManager)));
        }

        int remainTaskSize = 0, maxPoolSize = 0;
        if(executor instanceof ThreadPoolTaskExecutor) {
            maxPoolSize = ((ThreadPoolTaskExecutor) executor).getMaxPoolSize();
            remainTaskSize = ((ThreadPoolTaskExecutor) executor).getQueueSize();
        } else if(executor instanceof ThreadPoolExecutor) {
            maxPoolSize = ((ThreadPoolExecutor) executor).getMaximumPoolSize();
            remainTaskSize = ((ThreadPoolExecutor) executor).getQueue().size();
        }

        if(remainTaskSize > 100) {
            IotLogger.warn(sourceDto, "采集任务堆积", "请降低采集频率或增加线程数 - 堆积任务数: {} - 最大线程数: {} - 告警阈值: 100", remainTaskSize, maxPoolSize);
        }
    }
}
