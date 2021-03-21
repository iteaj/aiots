package com.iteaj.core.config;

import com.iteaj.framework.spi.admin.LoggerService;
import com.iteaj.framework.spi.admin.Module;
import com.iteaj.framework.spi.admin.ResourceManager;
import com.iteaj.framework.spi.event.AsyncGuavaEventBus;
import com.iteaj.framework.spi.event.EventPublisher;
import com.iteaj.framework.spi.event.Listener;
import com.iteaj.core.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.List;

/**
 * create time: 2020/4/25
 *
 * @author iteaj
 * @since 1.0
 */
@Configuration
@ComponentScan({
        "com.iteaj.core.service.*"
})
public class CoreAutoConfiguration {

    /**
     * 声明此模块的信息
     * @return
     */
    @Bean
    public Module coreModule() {
        return new Module() {
            @Override
            public int getOrder() {
                return 1;
            }

            @Override
            public String getMsn() {
                return "core";
            }

            @Override
            public String getName() {
                return "核心模块";
            }
        };
    }

    /**
     * 管理端日志记录服务
     * @param logService
     * @param menuService
     * @return
     */
    @Bean
    public LoggerService loggerService(IOperaLogService logService, IMenuService menuService) {
        return new AdminLoggerService(logService, menuService);
    }

    @Bean
    public AdminAuthorizingService authorizingService() {
        return new AdminAuthorizingService();
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    /**
     * 事件通知发布者
     * @param taskExecutor
     * @param listeners
     * @return
     */
    @Bean
    public EventPublisher eventPublisher(TaskExecutor taskExecutor, List<Listener> listeners) {
        return new AsyncGuavaEventBus(listeners, taskExecutor);
    }

    @Bean
    public ResourceManager menuResourceManager(IMenuService menuService
            , IConfigService configService, List<Module> modules) {
        return new MenuResourceManager(menuService, configService, modules);
    }
}
