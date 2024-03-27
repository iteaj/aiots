package com.iteaj.iboot.module.iot.collect.model;

import cn.hutool.core.collection.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventGroupCollectManager {

    private List<EventGroupCollectListener> listeners;

    private static EventGroupCollectManager instance;

    protected EventGroupCollectManager(List<EventGroupCollectListener> listeners) {
        this.listeners = listeners;
    }

    public static EventGroupCollectManager build(List<EventGroupCollectListener> listeners) {
        if(CollectionUtil.isEmpty(listeners)) {
            listeners = new ArrayList<>();
        }

        return instance = new EventGroupCollectManager(listeners);
    }

    public void register(EventGroupCollectListener listener) {
        listeners.add(listener);
    }

    public void forEach(Consumer<EventGroupCollectListener> consumer) {
        listeners.forEach(consumer);
    }

    public static EventGroupCollectManager getInstance() {
        return instance;
    }
}
