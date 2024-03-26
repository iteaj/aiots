package com.iteaj.iboot.module.iot.collect.model;

import com.iteaj.iboot.module.iot.dto.EventSourceDto;

public class EventSourceTask implements Runnable {

    private EventSourceDto sourceDto;

    public EventSourceTask(EventSourceDto sourceDto) {
        this.sourceDto = sourceDto;
    }

    @Override
    public void run() {

    }
}
