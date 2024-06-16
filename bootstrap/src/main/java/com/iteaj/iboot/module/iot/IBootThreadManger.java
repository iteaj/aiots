package com.iteaj.iboot.module.iot;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Getter
@Setter
public class IBootThreadManger implements DisposableBean, InitializingBean {

    private final ThreadPoolTaskScheduler scheduler;

    public IBootThreadManger(ThreadPoolTaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void destroy() throws Exception {
        scheduler.destroy();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduler.initialize();
    }
}
