package com.iteaj.framework.spi.event;

import org.springframework.context.ApplicationContext;

import java.util.EventListener;

/**
 * 应用启动就绪监听
 */
@FunctionalInterface
public interface ApplicationReadyListener extends EventListener {

    /**
     * 启动完成处理
     * @param context
     */
    void started(ApplicationContext context);
}
