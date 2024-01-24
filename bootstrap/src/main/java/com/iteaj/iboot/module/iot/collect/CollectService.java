package com.iteaj.iboot.module.iot.collect;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.iboot.module.iot.collect.action.CollectAction;
import com.iteaj.iboot.module.iot.collect.store.StoreActionFactory;
import com.iteaj.iboot.module.iot.dto.CollectTaskDto;
import com.iteaj.iboot.module.iot.service.ICollectTaskService;
import com.iteaj.iboot.module.iot.entity.CollectTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 数据采集服务
 */
//@Service
public class CollectService implements InitializingBean {

    private final TaskScheduler scheduler;
    private final CollectActionFactory actionFactory;
    private final StoreActionFactory storeActionFactory;
    private final ICollectTaskService collectTaskService;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Map<Long, ScheduledFuture> scheduledFutureMap = new ConcurrentHashMap<>();

    public CollectService(CollectActionFactory actionFactory, TaskScheduler scheduler
            , StoreActionFactory storeActionFactory, ICollectTaskService collectTaskService) {
        this.actionFactory = actionFactory;
        this.scheduler = scheduler;
        this.storeActionFactory = storeActionFactory;
        this.collectTaskService = collectTaskService;
    }

    public void statusSwitch(Long id, String status) throws CollectException{
        CollectTaskDto taskDto = collectTaskService.collectDetailById(id);

        if(taskDto == null) {
            throw new ServiceException("采集任务不存在");
        }

        if(taskDto.getStatus().equals(status)) {
            throw new ServiceException("状态切换异常");
        }

        if(status.equals("run")) {
            ScheduledFuture future = scheduledFutureMap.get(id);
            Object result = validateTask(taskDto);// 校验采集任务
            if(result instanceof String) { // 任务校验失败
                throw new CollectException((String) result);
            }

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
                .set(CollectTask::getReason, "")
                .set(CollectTask::getUpdateTime, new Date())
                .eq(CollectTask::getId, id));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        collectTaskService.list(Wrappers.<CollectTask>lambdaQuery().eq(CollectTask::getStatus, "run"))
                .stream().map(item -> collectTaskService.collectDetailById(item.getId()))
                    .filter(item -> item != null && !CollectionUtils.isEmpty(item.getDetails()))
                    .map(item -> validateTask(item))
                    .filter(item -> item instanceof CollectTaskDto) // 校验之后重新过滤
                    .forEach(item -> createScheduledTask((CollectTaskDto) item));
    }

    private Object validateTask(CollectTaskDto taskDto) {
        try {
            if(!StringUtils.hasText(taskDto.getCron())) {
                collectTaskService.update(Wrappers.<CollectTask>lambdaUpdate()
                        .set(CollectTask::getStatus, "fail")
                        .set(CollectTask::getReason, "没有配置cron")
                        .eq(CollectTask::getId, taskDto.getId()));

                return "没有配置cron";
            }

            taskDto.getDetails().forEach(item -> {
                String reason = null;
                if(item.getDevice() == null) {
                    reason = "没有配置采集设备";
                }

                if(CollectionUtils.isEmpty(item.getSignals())) {
                    reason = "没有配置采集点位";
                }

                CollectAction collectAction = actionFactory.get(item.getCollectAction());
                if(collectAction == null) {
                    reason = "没有可用的采集动作[action="+item.getCollectAction()+"]";
                } else {
                    try {
                        collectAction.validate(item);
                        item.getSignals().forEach(signal -> collectAction.validate(signal));
                    } catch (CollectException e) {
                        reason = e.getMessage();
                    }
                }

                if(reason != null) {
                    collectTaskService.update(Wrappers.<CollectTask>lambdaUpdate()
                            .set(CollectTask::getStatus, "fail")
                            .set(CollectTask::getReason, reason)
                            .set(CollectTask::getUpdateTime, null)
                            .eq(CollectTask::getId, taskDto.getId()));

                    throw new CollectException(reason);
                }
            });
        } catch (CollectException e) {
            logger.error("校验任务失败 任务:{} - 失败原因: {}", taskDto.getName(), e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            logger.error("校验任务失败 任务:{} - 失败原因: {}", taskDto.getName(), e.getMessage(), e);
            return e.getMessage();
        }

        return taskDto;
    }

    private void createScheduledTask(CollectTaskDto taskDto) {
        scheduledFutureMap.put(taskDto.getId(),
                scheduler.schedule(new CollectActionTask(taskDto)
                        , new CronTrigger(taskDto.getCron())));
    }
}
