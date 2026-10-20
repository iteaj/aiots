package com.iteaj.iboot.module.iot.linkage;

import com.iteaj.iboot.module.iot.dto.EventSourceDto;

/**
 * 执行器定时任务
 */
public class PeriodEventTask implements Runnable {

    private final EventSourceDto source;
    private final LinkageTriggerService triggerService;

    public PeriodEventTask(EventSourceDto source, LinkageTriggerService triggerService) {
        this.source = source;
        this.triggerService = triggerService;
    }

    @Override
    public void run() {
        triggerService.trigger(source);
    }
}
