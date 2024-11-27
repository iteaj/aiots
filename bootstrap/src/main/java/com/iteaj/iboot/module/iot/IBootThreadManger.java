package com.iteaj.iboot.module.iot;

import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
@Setter
public class IBootThreadManger implements DisposableBean, InitializingBean {

    private final ThreadPoolTaskScheduler scheduler;
    private final ThreadPoolTaskExecutor taskExecutor;
    private ScheduledExecutorService deviceStatusScheduler;

    public IBootThreadManger(ThreadPoolTaskScheduler scheduler, ThreadPoolTaskExecutor taskExecutor) {
        this.scheduler = scheduler;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void destroy() throws Exception {
        scheduler.destroy();
        if(deviceStatusScheduler != null && !deviceStatusScheduler.isShutdown()) {
            deviceStatusScheduler.shutdownNow();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduler.initialize();
        DefaultThreadFactory factory = new DefaultThreadFactory("IDS-");
        deviceStatusScheduler = Executors.newScheduledThreadPool(2, factory);
    }
}
