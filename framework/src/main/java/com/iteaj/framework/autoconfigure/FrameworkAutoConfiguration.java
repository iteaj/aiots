package com.iteaj.framework.autoconfigure;

import com.iteaj.framework.spi.event.ApplicationReadyListener;
import com.iteaj.framework.spi.event.EventUtils;
import com.iteaj.framework.spi.file.LocalUploadService;
import com.iteaj.framework.spi.file.UploadService;
import com.iteaj.framework.spring.SpringUtils;
import com.iteaj.framework.thread.IBootThreadManger;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * create time: 2021/3/14
 *  核心框架层的配置信息
 * @author iteaj
 * @since 1.0
 */
@ImportAutoConfiguration({
        PluginAutoConfiguration.class,
        MybatisPlusConfiguration.class,
        RedisJsonAutoConfiguration.class,
        CacheManagerAutoConfiguration.class,
})
@EnableConfigurationProperties({FrameworkProperties.class})
public class FrameworkAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {

    @Bean
    @ConditionalOnMissingBean(UploadService.class)
    public UploadService fileService(FrameworkProperties properties) {
        return new LocalUploadService(properties);
    }

    @Bean
    public EventUtils eventUtils() {
        return new EventUtils();
    }

    @Bean
    public SpringUtils springUtils() {
        return SpringUtils.getInstance();
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler(TaskSchedulerBuilder builder) {
        return builder.build();
    }

    @Bean
    public IBootThreadManger iBootThreadManger(FrameworkProperties properties, @Autowired(required = false) ThreadPoolTaskExecutor taskExecutor) {
        ThreadPoolTaskExecutor collectExecutor;
        ThreadPoolTaskScheduler collectScheduler;
        if(properties.getTask() != null) { // 单独配置了采集任务调度器
            collectExecutor = new TaskExecutorBuilder()
                    .awaitTermination(true)
                    .corePoolSize(properties.getTask().getPoolSize())
                    .maxPoolSize(properties.getTask().getMaxPoolSize())
                    .threadNamePrefix(properties.getTask().getThreadNamePrefix()).build();
        } else {
            throw new BeanCreationException("未指定采集任务配置(framework.task.[poolSize, maxPoolSize])");
        }

        if(properties.getScheduling() != null) {
            collectScheduler = new TaskSchedulerBuilder()
                    .awaitTermination(false)
                    .poolSize(properties.getScheduling().getPoolSize())
                    .threadNamePrefix(properties.getScheduling().getThreadNamePrefix())
                    .build();
        } else {
            throw new BeanCreationException("未指定采集任务调度配置(framework.scheduling.poolSize)");
        }

        taskExecutor = taskExecutor == null ? collectExecutor : taskExecutor;
        return IBootThreadManger.build(collectScheduler, taskExecutor, collectExecutor);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        event.getApplicationContext().getBeanProvider(ApplicationReadyListener.class)
                .orderedStream().forEach(applicationReadyListener -> applicationReadyListener.started(event.getApplicationContext()));
    }
}
