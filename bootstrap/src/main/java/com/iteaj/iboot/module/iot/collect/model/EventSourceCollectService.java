package com.iteaj.iboot.module.iot.collect.model;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.spi.event.ApplicationReadyListener;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.dto.EventSourceDto;
import com.iteaj.iboot.module.iot.service.IEventSourceService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
public class EventSourceCollectService implements ApplicationReadyListener {

    private final IEventSourceService eventSourceService;
    private final Map<Long, ScheduledFuture> futures = new ConcurrentHashMap<>();

    public EventSourceCollectService(IEventSourceService eventSourceService) {
        this.eventSourceService = eventSourceService;
    }

    @Override
    public void started(ApplicationContext context) {

    }

    public BooleanResult switchTask(Long id, FuncStatus status) {
        if(status == null) {
            return BooleanResult.buildFalse("未指定切换状态");
        }
        if(id == null) {
            return BooleanResult.buildFalse("未指定事件源id");
        }

        EventSourceDto source = eventSourceService.collectDetailById(id)
                .ifNotPresentThrow("事件源不存在").getData();

        if(source == null) {

        }

        ScheduledFuture scheduledFuture = futures.get(id);
        if(status == FuncStatus.enabled) { // 启用
            if(scheduledFuture != null) { // 已经存在则先移除并取消
                futures.remove(id).cancel(true);
            }
            
        } else { // 禁用
            // 取消定时任务
            if(scheduledFuture != null) {
                futures.remove(id).cancel(true);
            }
        }

        return BooleanResult.buildTrue("");
    }
}
