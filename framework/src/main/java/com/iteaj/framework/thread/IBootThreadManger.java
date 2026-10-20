package com.iteaj.framework.thread;

import com.iteaj.framework.autoconfigure.FrameworkProperties;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.system.JavaVersion;
import org.springframework.boot.task.SimpleAsyncTaskExecutorBuilder;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.boot.task.ThreadPoolTaskSchedulerBuilder;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
@Setter
public class IBootThreadManger implements DisposableBean, InitializingBean {

    private static IBootThreadManger instance;
    private final ThreadPoolTaskScheduler scheduler;
    private final AsyncTaskExecutor collectExecutor;
    private final AsyncTaskExecutor taskExecutor;
    private ScheduledExecutorService deviceStatusScheduler;
    private final static JavaVersion jdkVersion = JavaVersion.getJavaVersion();
    /**
     * 是否支持虚拟线程
     */
    @Getter
    private final static boolean virtualThreadSupported = jdkVersion.isEqualOrNewerThan(JavaVersion.TWENTY_ONE);

    protected IBootThreadManger(ThreadPoolTaskScheduler scheduler, AsyncTaskExecutor taskExecutor, AsyncTaskExecutor collectExecutor) {
        this.scheduler = scheduler;
        this.taskExecutor = taskExecutor;
        this.collectExecutor = collectExecutor;
    }

    public static IBootThreadManger getInstance() {
        if(instance == null) {
            throw new IllegalStateException("未初始化");
        }

        return instance;
    }

    public static IBootThreadManger build(FrameworkProperties properties, AsyncTaskExecutor taskExecutor) {
        if(instance == null) {
            AsyncTaskExecutor collectExecutor;
            ThreadPoolTaskScheduler collectScheduler;
            if(properties.getTask() != null) { // 单独配置了采集任务调度器
                FrameworkProperties.CollectTask task = properties.getTask();
                if(jdkVersion.isEqualOrNewerThan(JavaVersion.TWENTY_ONE) && task.isVirtualThread()) {
                    collectExecutor = new VirtualThreadTaskExecutor(task.getThreadNamePrefix());
                } else {
                    collectExecutor = new ThreadPoolTaskExecutorBuilder()
                            .awaitTermination(true)
                            .corePoolSize(task.getPoolSize())
                            .maxPoolSize(task.getMaxPoolSize())
                            .threadNamePrefix(task.getThreadNamePrefix()).build();
                }
            } else {
                throw new BeanCreationException("未指定采集任务配置(framework.task.[poolSize, maxPoolSize])");
            }

            if(properties.getScheduling() != null) {
                collectScheduler = new ThreadPoolTaskSchedulerBuilder()
                        .awaitTermination(false)
                        .poolSize(properties.getScheduling().getPoolSize())
                        .threadNamePrefix(properties.getScheduling().getThreadNamePrefix())
                        .build();
            } else {
                throw new BeanCreationException("未指定采集任务调度配置(framework.scheduling.poolSize)");
            }

            taskExecutor = taskExecutor == null ? collectExecutor : taskExecutor;
            instance = new IBootThreadManger(collectScheduler, taskExecutor, collectExecutor);
        }

        return instance;
    }

    public static void execute(Runnable task) {
        if(isVirtualThreadSupported()) {
            instance.getCollectExecutor().execute(task);
        } else {
            ThreadPoolTaskExecutor collectExecutor = (ThreadPoolTaskExecutor) instance.getCollectExecutor();
            ThreadPoolTaskExecutor poolTaskExecutor = (ThreadPoolTaskExecutor) instance.getTaskExecutor();
            if(collectExecutor == poolTaskExecutor) { // 同一个对象
                collectExecutor.execute(task);
            } else if(collectExecutor.getQueueSize() - poolTaskExecutor.getQueueSize() > 0){ // 采集任务数量大于其他自行其任务数量
                poolTaskExecutor.execute(task);
            } else { // 使用采集任务执行器
                collectExecutor.execute(task);
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        if(scheduler != null) {
            scheduler.destroy();
        }

        if(this.collectExecutor instanceof DisposableBean) {
            ((DisposableBean) collectExecutor).destroy();
        }

        if(deviceStatusScheduler != null && !deviceStatusScheduler.isShutdown()) {
            deviceStatusScheduler.shutdownNow();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduler.initialize();
        if(this.collectExecutor instanceof InitializingBean) {
            ((InitializingBean) collectExecutor).afterPropertiesSet();
        }

        DefaultThreadFactory factory = new DefaultThreadFactory("IDS-");
        deviceStatusScheduler = Executors.newScheduledThreadPool(2, factory);
    }
}
