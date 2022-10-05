package com.iteaj.iboot.msn.iot.collect;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.entity.CollectTask;
import com.iteaj.iboot.msn.iot.service.ICollectTaskService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class CollectService implements InitializingBean {

    private final ThreadPoolTaskScheduler scheduler;
    private final ICollectTaskService collectTaskService;
    private Map<Long, ScheduledFuture> scheduledFutureMap = new ConcurrentHashMap<>();

    public CollectService(ThreadPoolTaskScheduler scheduler
            , ICollectTaskService collectTaskService) {
        this.scheduler = scheduler;
        this.collectTaskService = collectTaskService;
    }

    public void statusSwitch(Long id, String status) {
        CollectTaskDto taskDto = collectTaskService.signalsDetailById(id);

        if(taskDto == null) {
            throw new ServiceException("采集任务不存在");
        }

        if(taskDto.getStatus().equals(status)) {
            throw new ServiceException("状态切换异常");
        }

        if(status.equals("run")) {
            ScheduledFuture future = scheduledFutureMap.get(id);
            if(future == null) {
                createScheduledTask(taskDto);
            } else if(future.isCancelled()) {
                scheduledFutureMap.remove(id);
                createScheduledTask(taskDto);
            }
        } else if(status.equals("stop")){
            ScheduledFuture future = scheduledFutureMap.remove(id);
            if(future != null && !future.isCancelled()) {
                future.cancel(true);
            }
        } else {
            throw new IllegalArgumentException("不支持的状态["+status+"]");
        }

        collectTaskService.update(Wrappers.<CollectTask>lambdaUpdate()
                .set(CollectTask::getStatus, status)
                .eq(CollectTask::getId, id));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        collectTaskService.list(Wrappers.<CollectTask>lambdaQuery().eq(CollectTask::getStatus, "run"))
                .stream().filter(task -> StringUtils.hasText(task.getCron()))
                .map(item -> collectTaskService.signalsDetailById(item.getId()))
                .filter(item -> item != null && !CollectionUtils.isEmpty(item.getDetails()))
                .forEach(item -> createScheduledTask(item));

    }

    private void createScheduledTask(CollectTaskDto taskDto) {
        scheduledFutureMap.put(taskDto.getId(),
                scheduler.schedule(new CollectActionTask(taskDto)
                        , new CronTrigger(taskDto.getCron())));
    }
}
