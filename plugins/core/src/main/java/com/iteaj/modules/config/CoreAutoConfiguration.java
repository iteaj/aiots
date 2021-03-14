package com.iteaj.modules.config;

import com.iteaj.framework.captcha.CaptchaConfiguration;
import com.iteaj.framework.spi.admin.LoggerService;
import com.iteaj.framework.spi.admin.Module;
import com.iteaj.framework.spi.admin.ResourceManager;
import com.iteaj.framework.spi.admin.event.OnlineListener;
import com.iteaj.framework.spi.admin.event.OnlineType;
import com.iteaj.framework.spi.event.AsyncGuavaEventBus;
import com.iteaj.framework.spi.event.EventPublisher;
import com.iteaj.framework.spi.event.Listener;
import com.iteaj.modules.core.entity.Admin;
import com.iteaj.modules.core.entity.OnlineUser;
import com.iteaj.modules.core.enums.AppType;
import com.iteaj.modules.core.service.*;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;
import java.util.List;

/**
 * create time: 2020/4/25
 *
 * @author iteaj
 * @since 1.0
 */
@Configuration
@ComponentScan({
        "com.iteaj.modules.core.service.*"
})
@MapperScan(basePackages = {"com.iteaj.modules.core.mapper"})
@ImportAutoConfiguration(classes = {
        CaptchaConfiguration.class,
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
    public ResourceManager resourceManager(IMenuService menuService
            , IConfigService configService, List<Module> modules) {
        return new AdminResourceManager(menuService, configService, modules);
    }
}
