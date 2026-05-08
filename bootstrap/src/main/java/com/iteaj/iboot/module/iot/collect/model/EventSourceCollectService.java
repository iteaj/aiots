package com.iteaj.iboot.module.iot.collect.model;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.spi.event.ApplicationReadyListener;
import com.iteaj.framework.spi.iot.data.IModelAttrRealtimeDataService;
import com.iteaj.framework.thread.IBootThreadManger;
import com.iteaj.iboot.module.iot.consts.EventSourceType;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.dto.EventSourceDto;
import com.iteaj.iboot.module.iot.entity.EventSource;
import com.iteaj.iboot.module.iot.entity.Product;
import com.iteaj.iboot.module.iot.linkage.LinkageTriggerService;
import com.iteaj.iboot.module.iot.linkage.PeriodEventTask;
import com.iteaj.iboot.module.iot.service.IEventSourceService;
import com.iteaj.iboot.module.iot.service.IProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
public class EventSourceCollectService implements ApplicationReadyListener {

    private final IBootThreadManger threadManger;
    private final LinkageTriggerService triggerService;
    private final IEventSourceService eventSourceService;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<Long, ScheduledFuture<?>> futures = new ConcurrentHashMap<>(128);

    public EventSourceCollectService(IBootThreadManger threadManger
            , LinkageTriggerService triggerService, IEventSourceService eventSourceService) {
        this.threadManger = threadManger;
        this.triggerService = triggerService;
        this.eventSourceService = eventSourceService;
    }

    @Override
    public void started(ApplicationContext context) {
        this.eventSourceService.list(Wrappers.<EventSource>lambdaQuery()
                .eq(EventSource::getStatus, FuncStatus.enabled)).forEach(item -> {

            try {
                doSwitchTask(item.getId(), FuncStatus.enabled);
            } catch (ServiceException e) {
                logger.error("启动事件源失败({}) {}", item.getName(), e.getMessage());
                eventSourceService.update(Wrappers.<EventSource>lambdaUpdate()
                        .set(EventSource::getReason, e.getMessage())
                        .eq(EventSource::getId, item.getId()));
            } catch (Exception e) {
                logger.error("启动事件源失败({}) {}", item.getName(), e.getMessage(), e);
                eventSourceService.update(Wrappers.<EventSource>lambdaUpdate()
                        .set(EventSource::getReason, "未知错误")
                        .eq(EventSource::getId, item.getId()));
            }
        });
    }

    public BooleanResult switchTask(Long id, FuncStatus status) {
        if(status == null) {
            return BooleanResult.buildFalse("未指定切换状态");
        }
        if(id == null) {
            return BooleanResult.buildFalse("未指定事件源id");
        }

        doSwitchTask(id, status);

        return this.eventSourceService.update(Wrappers.<EventSource>lambdaUpdate()
                .set(EventSource::getStatus, status)
                .set(EventSource::getReason, "")
                .eq(EventSource::getId, id));
    }

    private void doSwitchTask(Long id, FuncStatus status) {
        EventSourceDto source = eventSourceService.collectDetailById(id)
                .ifNotPresentThrow("事件源不存在").getData();

        if(StrUtil.isBlank(source.getCron())) {
            throw new ServiceException("请输入正确的采集周期(cron或者毫秒值)");
        }

        if(CollectionUtil.isEmpty(source.getApis()))
            throw new ServiceException("未包含任何采集接口");

        if(CollectionUtil.isEmpty(source.getGroups()))
            throw new ServiceException("未包含任何采集组");

        ScheduledFuture<?> scheduledFuture = futures.get(id);
        if(status == FuncStatus.enabled) { // 启用
            Map<Long, Product> productMap = new HashMap<>(8);
            this.eventSourceService.listProductById(source.getId(), null).forEach(product -> {
                if(product.getStatus() == FuncStatus.disabled) {
                    throw new ServiceException("产品["+product.getName()+"]未启用");
                }

                productMap.put(product.getId(), product);
            });

            source.setProductMaps(productMap);
            if(productMap.size() != source.getProductIds().size()) {
                throw new ServiceException("事件源配置的某些产品已被删除");
            }

            if(scheduledFuture != null) { // 已经存在则先移除并取消
                futures.remove(id).cancel(true);
            }

            ThreadPoolTaskScheduler scheduler = threadManger.getScheduler();
            try { // 定时表达式
                CronTrigger trigger = new CronTrigger(source.getCron());
                ScheduledFuture<?> schedule;
                if(source.getType() == EventSourceType.event) {
                    schedule = scheduler.schedule(new EventSourceTask(
                            threadManger.getCollectExecutor(), source), trigger);
                } else {
                    schedule = scheduler.schedule(() -> IBootThreadManger.execute(new PeriodEventTask(source, triggerService)), trigger);
                }

                futures.put(id, schedule);
            } catch (Exception e) { // 数值毫秒
                long timeMills = Long.parseLong(source.getCron());
                if(timeMills <= 0) {
                    throw new ServiceException("采集毫秒值必须大于0");
                }

                ScheduledFuture<?> schedule;
                if(source.getType() == EventSourceType.event) {
                    schedule = scheduler.scheduleAtFixedRate(() -> IBootThreadManger.execute(new EventSourceTask(threadManger.getCollectExecutor(), source)), timeMills);
                } else {
                    schedule = scheduler.scheduleAtFixedRate(() -> IBootThreadManger.execute(new PeriodEventTask(source, triggerService)), timeMills);
                }

                futures.put(id, schedule);
            }

        } else { // 禁用
            // 取消定时任务
            if(scheduledFuture != null) {
                futures.remove(id).cancel(true);
            }
        }
    }
}
